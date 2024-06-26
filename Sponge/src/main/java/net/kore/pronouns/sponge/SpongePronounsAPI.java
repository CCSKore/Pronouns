package net.kore.pronouns.sponge;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Nameable;
import org.spongepowered.api.util.Ticks;

public class SpongePronounsAPI extends PronounsAPI {
    private static SpongePronounsAPI INSTANCE;

    private SpongePronounsAPI() {
        Task.Builder taskBuilder = Task.builder();
        taskBuilder.execute(() -> {
            PronounsLogger.debug("Refreshing cache...");
            flushCache();
            List<UUID> uuids = new ArrayList<>();
            for (ServerPlayer serverPlayer : SpongePronouns.getServer().onlinePlayers()) {
                uuids.add(serverPlayer.uniqueId());
            }
            massCacheValues(uuids);
        }).delay(0L, TimeUnit.MICROSECONDS)
                .interval(Ticks.of(PronounsConfig.get().node("refresh").getLong(5) * 60 * 20))
                .plugin(SpongePronouns.getPluginContainer())
                .build();
    }

    public static SpongePronounsAPI get() {
        if (INSTANCE == null) {
            INSTANCE = new SpongePronounsAPI();
            PronounsAPI.setInstance(INSTANCE);
        }

        return INSTANCE;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        return Sponge.server().player(uuid).map(Nameable::name).orElse(null);
    }
}