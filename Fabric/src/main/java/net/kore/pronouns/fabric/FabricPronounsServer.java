package net.kore.pronouns.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class FabricPronounsServer implements DedicatedServerModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("Pronouns");

    private static MinecraftServer serverInstance;
    public static MinecraftServer getServerInstance() {
        return serverInstance;
    }

    @Override
    public void onInitializeServer() {
        serverInstance = getServerInstance();
        Path configFile = FabricLoader.getInstance().getConfigDir().resolve("config.conf");

        if (!configFile.toFile().exists()) {
            Optional<ModContainer> o = FabricLoader.getInstance().getModContainer("kore_pronouns");
            if (o.isPresent()) {
                ModContainer modContainer = o.get();
                Optional<Path> p = modContainer.findPath("config.conf");
                if (p.isPresent()) {
                    try {
                        Files.copy(p.get(), configFile, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    LOGGER.error("Could not find config.conf in the mod files!");
                }
            } else {
                LOGGER.error("Huh, that's weird, couldn't find myself as a mod, can't make a config");
            }
        }

        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .path(configFile)
                .build();

        try {
            PronounsConfig.set(loader.load());
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        PronounsLogger.setLogger(LOGGER);

        FabricPronounsAPI.get();
    }
}
