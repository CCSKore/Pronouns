package net.kore.pronouns.velocity;

import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.velocity.LiteVelocityFactory;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.kore.pronouns.velocity.command.ReloadCommand;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Plugin(id = "kore-pronouns", name = "Pronouns", version = "0.2.0",
        description = "PronounDB in Minecraft!", authors = {"Nova", "Kore Team"})
public class VelocityPronouns {
    private final ProxyServer server;
    public ProxyServer getServer() {
        return server;
    }
    private Path dataDirectory;
    public Path getDataDirectory() {
        return dataDirectory;
    }
    private static VelocityPronouns INSTANCE;
    public static VelocityPronouns getInstance() {
        return INSTANCE;
    }

    private LiteCommands<CommandSource> liteCommands;

    @Inject
    public VelocityPronouns(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.dataDirectory = dataDirectory;
        PronounsLogger.setLogger(logger);

        File configFile = new File(dataDirectory.toFile(), "config.conf");
        if (!configFile.exists() || !configFile.isDirectory()) {
            try {
                Files.copy(Paths.get(Resources.getResource(getClass(), "config.conf").toURI()), configFile.toPath());
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

        this.liteCommands = LiteVelocityFactory.builder(server)
                .commands(
                        new ReloadCommand()
                )
                .build();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        INSTANCE = this;
        VelocityPronounsAPI.get();
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent e) {
        VelocityPronounsAPI.get().getPronouns(e.getPlayer().getUniqueId());
    }
}
