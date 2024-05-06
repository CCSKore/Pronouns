package net.kore.pronouns.velocity;

import com.velocitypowered.api.proxy.Player;
import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class VelocityPronounsAPI extends PronounsAPI {
    private VelocityPronounsAPI() {
        VelocityPronouns.getInstance().getServer().getScheduler()
                .buildTask(VelocityPronouns.getInstance(), () -> {
                    PronounsLogger.debug("Refreshing cache...");
                    flushCache();
                    List<UUID> uuids = new ArrayList<>();
                    for (Player player : VelocityPronouns.getInstance().getServer().getAllPlayers()) {
                        uuids.add(player.getUniqueId());
                    }
                    massCacheValues(uuids);
                })
                .repeat(PronounsConfig.get().node("refresh").getLong(5), TimeUnit.MINUTES)
                .schedule();
    }
    private static VelocityPronounsAPI INSTANCE;

    public static VelocityPronounsAPI get() {
        if (INSTANCE == null) {
            INSTANCE = new VelocityPronounsAPI();
            PronounsAPI.setInstance(INSTANCE);
        }
        return INSTANCE;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        return VelocityPronouns.getInstance().getServer().getPlayer(uuid).map(Player::getUsername).orElse(null);
    }
}
