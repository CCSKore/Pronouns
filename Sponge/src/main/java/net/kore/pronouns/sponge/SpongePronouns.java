package net.kore.pronouns.sponge;

import com.google.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("pronouns")
public class SpongePronouns {
    @Inject
    private static Logger logger;
    @Inject
    private static PluginContainer pluginContainer;
    @Inject
    private static Game game;
    @Inject
    @ConfigDir(
            sharedRoot = false
    )
    private Path configDir;
    private static CommentedConfigurationNode node;

    public SpongePronouns() {
    }

    public static Logger getLogger() {
        return logger;
    }

    public static PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public static Server getServer() {
        return game.server();
    }

    public static CommentedConfigurationNode getNode() {
        return node;
    }

    @Listener
    public void onServerStart(StartedEngineEvent<Server> event) {
        HoconConfigurationLoader loader = ((HoconConfigurationLoader.Builder)HoconConfigurationLoader.builder().file(new File(this.configDir.toFile(), "config.conf"))).build();

        try {
            node = (CommentedConfigurationNode)loader.load();
        } catch (ConfigurateException var4) {
            throw new RuntimeException(var4);
        }

        SpongePronounsAPI.get();
    }
}