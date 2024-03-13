package net.kore.pronouns.minestom;

import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.minestom.server.extensions.Extension;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;

public class MinestomPronouns extends Extension {
    @Override
    public void initialize() {
        File configFolder = getDataDirectory().toFile();
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        File configFile = new File(configFolder, "config.conf");
        if (!configFile.exists()) {
            getResource("config.conf");
        }

        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .file(configFile)
                .build();

        try {
            PronounsConfig.set(loader.load());
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        PronounsLogger.setLogger(getLogger());
        MinestomPronounsAPI.get();
    }

    @Override
    public void terminate() {

    }
}