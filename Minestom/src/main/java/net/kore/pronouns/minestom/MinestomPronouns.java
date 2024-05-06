package net.kore.pronouns.minestom;

import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.*;

@SuppressWarnings("unused")
public class MinestomPronouns {
    public static void init() {
        PronounsLogger.setLogger(MinecraftServer.LOGGER);
        File configFolder = new File("./");
        if (!configFolder.exists()) {
            if (!configFolder.mkdirs()) {
                throw new RuntimeException("Unable to create needed directories for the config file.");
            }
        }

        File configFile = new File(configFolder, "pronouns-config.conf");
        if (!configFile.exists()) {
            try {
                OutputStream os = new FileOutputStream(configFile);
                InputStream is = MinestomPronouns.class.getResourceAsStream("config.conf");
                if (is == null) throw new RuntimeException("Unable to read default config");
                os.write(is.readAllBytes());
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

        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, (event) -> {
            MinestomPronounsAPI.get().getPronouns(event.getPlayer().getUuid());
        });
    }
}