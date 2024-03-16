package net.kore.pronouns.minestom;

import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;

import java.util.UUID;

public class MinestomPronounsAPI extends PronounsAPI {
    private MinestomPronounsAPI() {
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            PronounsLogger.debug("Refreshing cache...");
            flushCache();
            for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                getPronouns(player.getUuid());
            }
        }, TaskSchedule.nextTick(), TaskSchedule.minutes(PronounsConfig.get().node("refresh").getLong(5)), ExecutionType.ASYNC);
    }
    private static MinestomPronounsAPI INSTANCE;

    public static MinestomPronounsAPI get() {
        if (INSTANCE == null) {
            INSTANCE = new MinestomPronounsAPI();
            PronounsAPI.setInstance(INSTANCE);
        }
        return INSTANCE;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        return MinecraftServer.getConnectionManager().getPlayer(uuid).getUsername();
    }
}
