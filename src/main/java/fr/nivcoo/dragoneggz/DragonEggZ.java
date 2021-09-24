package fr.nivcoo.dragoneggz;

import fr.nivcoo.dragoneggz.events.InteractEvent;
import fr.nivcoo.utilsz.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class DragonEggZ extends JavaPlugin {

    private static DragonEggZ INSTANCE;
    private Config config;

    @Override
    public void onEnable() {
        INSTANCE = this;
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        config = new Config(configFile);
        Bukkit.getPluginManager().registerEvents(new InteractEvent(), this);

    }

    @Override
    public void onDisable() {
    }

    public Config getConfiguration() {
        return config;
    }

    public static DragonEggZ get() {
        return INSTANCE;
    }

}
