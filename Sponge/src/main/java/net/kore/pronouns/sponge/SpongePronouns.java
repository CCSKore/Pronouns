package net.kore.pronouns.sponge;

import com.google.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("pronouns")
public class SpongePronouns {
    private static Logger logger;
    private static PluginContainer pluginContainer;
    private static Game game;
    private final Path configDir;

    @Inject
    public SpongePronouns(PluginContainer plugin, Game game, Logger logger, @ConfigDir(sharedRoot = false) Path folder) {
        SpongePronouns.pluginContainer = plugin;
        SpongePronouns.game = game;
        SpongePronouns.logger = logger;
        this.configDir = folder;
    }

    public static PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public static Server getServer() {
        return game.server();
    }

    @Listener
    public void onServerStart(StartedEngineEvent<Server> event) {
        File configFile = new File(this.configDir.toFile(), "config.conf");

        if (!configFile.exists()) {
            Optional<URI> ois = pluginContainer.locateResource(Path.of("config.conf").toUri());
            if (ois.isEmpty()) {
                logger.info("Couldn't find default config.");
                game.server().shutdown(Component.text("Server stopping due to plugin issue. (default config is currently broken)"));
                return;
            }
            URI u = ois.get();
            try {
                Files.copy(Paths.get(u), configFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().file(configFile).build();

        try {
            PronounsConfig.set(loader.load());
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        PronounsLogger.setLogger(logger);

        SpongePronounsAPI.get();
    }
}