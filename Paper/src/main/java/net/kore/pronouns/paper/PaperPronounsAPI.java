package net.kore.pronouns.paper;

import com.google.common.collect.ImmutableList;
import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class PaperPronounsAPI extends PronounsAPI {
    private PaperPronounsAPI() {
        if (isFolia()) {
            Bukkit.getAsyncScheduler().runAtFixedRate(
                    PaperPronouns.getInstance(),
                    this::handleRefresh,
                    0L,
                    PronounsConfig.get().node("refresh").getLong(5),
                    TimeUnit.MINUTES
            );
        } else { // Support legacy Bukkit "just in case"
            Bukkit.getScheduler().runTaskTimerAsynchronously(
                    PaperPronouns.getInstance(),
                    this::handleRefresh,
                    0L,
                    PronounsConfig.get().node("refresh").getLong(5) * 60 * 20
            );
        }
    }
    private static PronounsAPI INSTANCE;

    private void handleRefresh(Object ignored) {
        PronounsLogger.debug("Refreshing cache...");
        flushCache();
        for (Player player : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
            getPronouns(player.getUniqueId());
        }
    }

    public static PronounsAPI get() {
        if (INSTANCE == null) {
            INSTANCE = new PaperPronounsAPI();
            PronounsAPI.setInstance(INSTANCE);
        }
        return INSTANCE;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
