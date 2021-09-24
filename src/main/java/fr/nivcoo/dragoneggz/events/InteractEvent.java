package fr.nivcoo.dragoneggz.events;

import fr.nivcoo.dragoneggz.DragonEggZ;
import fr.nivcoo.utilsz.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractEvent implements Listener {


    DragonEggZ dragonEggZ;
    Config config;

    public InteractEvent() {

        dragonEggZ = DragonEggZ.get();
        config = dragonEggZ.getConfiguration();

    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e) {
        Block b = e.getBlock();
        List<String> worlds_list = config.getStringList("disable_teleport_worlds");
        if (b.getType().equals(Material.DRAGON_EGG) && worlds_list.contains(b.getLocation().getWorld().getName())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block b = e.getBlock();
        Player p = e.getPlayer();
        if (b.getType().equals(Material.DRAGON_EGG) && !p.hasPermission("dragoneggz.place")) {
            p.sendMessage(config.getString("messages.cannot_place"));
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        Player p = e.getPlayer();
        if (b.getType().equals(Material.DRAGON_EGG)) {
            if (!p.hasPermission("dragoneggz.break"))
                p.sendMessage(config.getString("messages.cannot_break"));
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        ItemStack itemInHand = e.getItem();
        Block b = e.getClickedBlock();
        if (a != Action.PHYSICAL || (itemInHand != null && !itemInHand.getType().equals(Material.DRAGON_EGG)) || !b.getType().equals(Material.DRAGON_EGG))
            return;

        if (!p.hasPermission("dragoneggz.break")) {
            p.sendMessage(config.getString("messages.cannot_break"));
            return;
        }

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(b, p);
        Bukkit.getPluginManager().callEvent(blockBreakEvent);
        if (!blockBreakEvent.isCancelled()) {
            HashMap<Integer, ItemStack> nope = p.getInventory().addItem(b.getDrops().toArray(new ItemStack[0]));
            for (Map.Entry<Integer, ItemStack> entry : nope.entrySet()) {
                p.getWorld().dropItemNaturally(p.getLocation(), entry.getValue());
            }
            b.setType(Material.AIR);
        }


    }

}
