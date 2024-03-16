package net.kore.pronouns.bungee;

import com.google.common.io.Resources;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.md_5.bungee.api.plugin.Plugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BungeePronouns extends Plugin {
    private static BungeePronouns INSTANCE;
    public static BungeePronouns getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        PronounsLogger.setLogger(getLogger());

        File configFile = new File(getDataFolder(), "config.conf");
        if (!configFile.exists() || !configFile.isDirectory()) {
            try {
                Files.copy(Paths.get(Resources.getResource("config.conf").toURI()), configFile.toPath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        File pathFile = new File(getDataFolder(), "Pronouns.zip");
        if (!pathFile.exists() || !pathFile.isDirectory()) {
            try {
                Files.copy(Paths.get(Resources.getResource("Pronouns.zip").toURI()), pathFile.toPath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .file(configFile)
                .build();

        try {
            PronounsConfig.set(loader.load());
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        INSTANCE = this;

        BungeePronounsAPI.get();
    }
}