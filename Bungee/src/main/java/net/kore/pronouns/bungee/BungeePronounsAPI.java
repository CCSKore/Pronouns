package net.kore.pronouns.bungee;

import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BungeePronounsAPI extends PronounsAPI {
    private BungeePronounsAPI() {
        BungeePronouns.getInstance().getProxy().getScheduler().schedule(BungeePronouns.getInstance(), () -> {
            PronounsLogger.debug("Refreshing cache...");
            flushCache();
            for (ProxiedPlayer player : BungeePronouns.getInstance().getProxy().getPlayers()) {
                getPronouns(player.getUniqueId());
            }
        }, 0L, PronounsConfig.get().node("refresh").getLong(5), TimeUnit.MINUTES);
    }
    private static BungeePronounsAPI INSTANCE;

    public static BungeePronounsAPI get() {
        if (INSTANCE == null) {
            INSTANCE = new BungeePronounsAPI();
            PronounsAPI.setInstance(INSTANCE);
        }
        return INSTANCE;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        return BungeePronouns.getInstance().getProxy().getPlayer(uuid).getName();
    }
}