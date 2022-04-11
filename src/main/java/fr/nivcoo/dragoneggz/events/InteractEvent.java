package fr.nivcoo.dragoneggz.events;

import fr.nivcoo.dragoneggz.DragonEggZ;
import fr.nivcoo.utilsz.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InteractEvent implements Listener {


    DragonEggZ dragonEggZ;
    Config config;

    public InteractEvent() {

        dragonEggZ = DragonEggZ.get();
        config = dragonEggZ.getConfiguration();

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockFromTo(BlockFromToEvent e) {
        Block b = e.getBlock();
        List<String> worlds_list = config.getStringList("disable_teleport_worlds");
        if (b.getType().equals(Material.DRAGON_EGG) && (worlds_list.size() == 0 || worlds_list.contains(b.getLocation().getWorld().getName()))) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block b = e.getBlock();
        Player p = e.getPlayer();
        if (b.getType().equals(Material.DRAGON_EGG) && !p.hasPermission("dragoneggz.place")) {
            p.sendMessage(config.getString("messages.cannot_place"));
            String startSound = config.getString("sounds.cancel");
            p.playSound(p.getLocation(), Sound.valueOf(startSound), .4f, 1.7f);
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        Player p = e.getPlayer();
        if (b.getType().equals(Material.DRAGON_EGG)) {
            if (!p.hasPermission("dragoneggz.break")) {
                p.sendMessage(config.getString("messages.cannot_break"));
                String cancelSound = config.getString("sounds.cancel");
                p.playSound(p.getLocation(), Sound.valueOf(cancelSound), .4f, 1.7f);
                e.setCancelled(true);
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        ItemStack itemInHand = e.getItem();
        Block b = e.getClickedBlock();
        List<String> worlds_list = config.getStringList("directly_in_inventory_worlds");
        if ((worlds_list.size() != 0 && !worlds_list.contains(b != null ? b.getLocation().getWorld().getName() : null)) || (!a.equals(Action.LEFT_CLICK_BLOCK) && !a.equals(Action.RIGHT_CLICK_BLOCK)) || (itemInHand != null && itemInHand.getType().equals(Material.DRAGON_EGG)) || !Objects.requireNonNull(b).getType().equals(Material.DRAGON_EGG))
            return;

        if (!p.hasPermission("dragoneggz.break")) {
            p.sendMessage(config.getString("messages.cannot_break"));
            e.setCancelled(true);
            return;
        }

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(b, p);
        Bukkit.getPluginManager().callEvent(blockBreakEvent);
        if (!blockBreakEvent.isCancelled()) {
            HashMap<Integer, ItemStack> nope = p.getInventory().addItem(b.getDrops().toArray(new ItemStack[0]));
            String interactSound = config.getString("sounds.interact");
            p.playSound(b.getLocation(), Sound.valueOf(interactSound), .4f, 1.7f);
            p.sendMessage(config.getString("messages.success_break"));
            for (Map.Entry<Integer, ItemStack> entry : nope.entrySet()) {
                p.getWorld().dropItemNaturally(p.getLocation(), entry.getValue());
                p.sendMessage(config.getString("messages.success_break_drop"));
            }
            String pickupSound = config.getString("sounds.pickup");
            p.playSound(p.getLocation(), Sound.valueOf(pickupSound), .4f, 1.7f);
            b.setType(Material.AIR);
            e.setCancelled(true);
        }


    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLoaderPistonRetract(BlockPistonRetractEvent e) {
        List<Block> blocks = e.getBlocks();
        e.setCancelled(cantBePush(blocks));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLoaderPistonExtend(BlockPistonExtendEvent e) {
        List<Block> blocks = e.getBlocks();
        e.setCancelled(cantBePush(blocks));
    }

    public boolean cantBePush(List<Block> blocks) {
        List<String> worlds_list = config.getStringList("disable_piston_push_worlds");
        if (worlds_list.size() == 0 || blocks.size() == 0 || !worlds_list.contains(blocks.get(0).getWorld().getName()))
            return false;
        for (Block block : blocks) {
            if (block.getType().equals(Material.DRAGON_EGG)) {
                return true;
            }
        }
        return false;
    }


}
