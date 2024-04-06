package net.kore.pronouns.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FabricPronounsAPI extends PronounsAPI {
    private FabricPronounsAPI() {
        AtomicInteger ticks = new AtomicInteger(PronounsConfig.get().node("refresh").getInt(5) * 60 * 20);
        AtomicInteger t = new AtomicInteger(ticks.get());

        ServerTickEvents.START_WORLD_TICK.register((world) -> {
            t.set(t.get() - 1);

            if (t.get() <= 0) {
                PronounsLogger.debug("Refreshing cache...");
                flushCache();
                for (ServerPlayerEntity player : world.getServer().getPlayerManager().getPlayerList()) {
                    getPronouns(player.getUuid());
                }
                t.set(ticks.get());
            }
        });
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
        return FabricPronounsServer.getServerInstance().getPlayerManager().getPlayer(uuid).getName().getString();
    }
}
