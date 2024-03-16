package net.kore.pronouns.velocity;

import com.google.gson.*;
import com.velocitypowered.api.proxy.Player;
import net.kore.pronouns.api.CachedPronouns;
import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class VelocityPronounsAPI extends PronounsAPI {
    private VelocityPronounsAPI() {
        VelocityPronouns.getInstance().getServer().getScheduler()
                .buildTask(VelocityPronouns.getInstance(), () -> {
                    PronounsLogger.debug("Refreshing cache...");
                    flushCache();
                    for (Player player : VelocityPronouns.getInstance().getServer().getAllPlayers()) {
                        getPronouns(player.getUniqueId());
                    }
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
        return VelocityPronouns.getInstance().getServer().getPlayer(uuid).orElseGet(null).getUsername();
    }
}
