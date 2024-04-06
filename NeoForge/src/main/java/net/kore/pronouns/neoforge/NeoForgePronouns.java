package net.kore.pronouns.neoforge;

import com.mojang.logging.LogUtils;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Mod(NeoForgePronouns.MODID)
public class NeoForgePronouns {
    public static final String MODID = "korepronouns";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static MinecraftServer SERVER;
    public static MinecraftServer getServer() {
        if (SERVER == null) {
            throw new IllegalStateException("SERVER has not been defined.");
        }
        return SERVER;
    }

    public NeoForgePronouns(IEventBus modEventBus) {
        modEventBus.addListener(this::clientSetup);
        NeoForge.EVENT_BUS.register(this);

        PronounsLogger.setLogger(LOGGER);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent e) {
        LOGGER.warn("Pronouns is not for client use, nothing changes.");
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onServerStarting(ServerStartingEvent event) {
        SERVER = event.getServer();
        LOGGER.info("Pronouns starting...");
        File configFile = new File(new File(new File(event.getServer().getServerDirectory(), "config"), "pronouns"), "config.conf");
        if (!configFile.mkdirs() || !configFile.isFile()) {
            LOGGER.error("The config file is not a file or a directory couldn't be made.");
            event.getServer().close();
        }
        if (!configFile.exists()) {
            if (getClass().getClassLoader().getResourceAsStream("config.conf") == null) {
                throw new RuntimeException("Could not find config file.");
            }
            try {
                Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.conf")), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
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

        NeoForgePronounsAPI.get();
    }
}