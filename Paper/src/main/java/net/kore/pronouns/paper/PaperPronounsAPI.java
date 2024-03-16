package net.kore.pronouns.paper;

import com.google.common.collect.ImmutableList;
import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("unused")
public class PaperPronounsAPI extends PronounsAPI {
    private PaperPronounsAPI() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(PaperPronouns.getInstance(), () -> {
            PronounsLogger.debug("Refreshing cache...");
            flushCache();
            for (Player player : ImmutableList.copyOf(Bukkit.getOnlinePlayers())) {
                getPronouns(player.getUniqueId());
            }
        }, 0L, PronounsConfig.get().node("refresh").getLong(5) * 60 * 20);
    }
    private static PronounsAPI INSTANCE;

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
}
