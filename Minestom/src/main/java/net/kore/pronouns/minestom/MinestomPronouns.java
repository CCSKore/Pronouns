package net.kore.pronouns.minestom;

import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extensions.Extension;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.*;

@SuppressWarnings("unused")
public class MinestomPronouns extends Extension {
    @Override
    public void initialize() {
        PronounsLogger.setLogger(getLogger());
        File configFolder = getDataDirectory().toFile();
        if (!configFolder.exists()) {
            if (!configFolder.mkdirs()) {
                throw new RuntimeException("Unable to create needed directories for the config file.");
            }
        }

        File configFile = new File(configFolder, "config.conf");
        if (!configFile.exists()) {
            try {
                OutputStream os = new FileOutputStream(configFile);
                os.write(getResource("config.conf").readAllBytes());
                os.close();
            } catch (IOException e) {
                throw new RuntimeException("Unable to write config.", e);
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

        MinestomPronounsAPI.get();

        getEventNode().addListener(PlayerLoginEvent.class, (event) -> {
            PronounsAPI.getInstance().getPronouns(event.getPlayer().getUuid());
        });
    }

    @Override
    public void terminate() {

    }
}