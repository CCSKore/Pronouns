package net.kore.pronouns.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class FabricPronounsAPI extends PronounsAPI {
    private FabricPronounsAPI() {
        AtomicInteger ticks = new AtomicInteger(PronounsConfig.get().node("refresh").getInt(5) * 60 * 20);
        AtomicInteger t = new AtomicInteger(ticks.get());

        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) {
            ServerTickEvents.START_WORLD_TICK.register((world) -> {
                t.set(t.get() - 1);

                if (t.get() <= 0) {
                    PronounsLogger.debug("Refreshing cache...");
                    flushCache();
                    List<UUID> uuids = new ArrayList<>();
                    for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                        uuids.add(player.getUuid());
                    }
                    massCacheValues(uuids);
                    t.set(ticks.get());
                }
            });
        }
    }
    private static FabricPronounsAPI INSTANCE;

    public static FabricPronounsAPI get() {
        if (INSTANCE == null) {
            INSTANCE = new FabricPronounsAPI();
            PronounsAPI.setInstance(INSTANCE);
        }
        return INSTANCE;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) {
            ServerPlayerEntity spe = FabricPronouns.getServerInstance().getPlayerManager().getPlayer(uuid);
            if (spe == null) return null;
            Text usedName;
            if (spe.getDisplayName() != null) usedName = spe.getDisplayName();
            else usedName = spe.getName();
            return usedName.getString();
        } else {
            try {
                if (MinecraftClient.getInstance().getNetworkHandler() == null) return null;
                Text usedName = MinecraftClient.getInstance().getNetworkHandler().playerListEntries.get(uuid).getDisplayName();
                return usedName == null ? null : usedName.getString();
            } catch (Exception e) {
                return null;
            }
        }
    }
}
