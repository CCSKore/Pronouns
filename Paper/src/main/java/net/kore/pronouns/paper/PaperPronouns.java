package net.kore.pronouns.paper;

import net.kore.pronouns.api.PronounsConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.*;
import java.nio.file.Path;
import java.util.logging.Logger;

public class PaperPronouns extends JavaPlugin implements Listener {
    private static Logger LOGGER;
    private static PaperPronouns INSTANCE;

    public static Logger getLog() {return LOGGER;}
    public static PaperPronouns getInstance() {return INSTANCE;}

    @Override
    public void onEnable() {
        INSTANCE = this;
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) new PAPI().register();
        LOGGER = getLogger();

        PaperPronounsAPI.get();

        File configFolder = getDataFolder();
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        File configFile = new File(configFolder, "config.conf");
        if (!configFile.exists()) {
            saveResource("config.conf", false);
        }

        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .file(configFile)
                .build();

        try {
            PronounsConfig.set(loader.load());
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        PaperPronounsAPI.get().getPronouns(e.getPlayer().getUniqueId());
    }
}
