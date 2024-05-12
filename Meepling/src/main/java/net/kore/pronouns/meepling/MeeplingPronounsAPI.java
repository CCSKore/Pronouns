package net.kore.pronouns.meepling;

import net.kore.meep.api.Meep;
import net.kore.meep.api.player.Player;
import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MeeplingPronounsAPI extends PronounsAPI {
    private MeeplingPronounsAPI() {
        MeeplingPronouns.thisIsAThread = new Thread(null, () -> { //No Scedular means THREADING
            while (true) {
                List<UUID> uuids = new ArrayList<>();
                for (Player player : Collections.unmodifiableList(Meep.get().getOnlinePlayers())) {
                    uuids.add(player.getUUID());
                }
                massCacheValues(uuids);

                try {
                    Thread.sleep(PronounsConfig.get().node("refresh").getLong(5) * 60 * 1000);
                } catch (InterruptedException ignored) {}
            }
        }, "PronounsDaemonThread", 0);
        MeeplingPronouns.thisIsAThread.setDaemon(true);
        MeeplingPronouns.thisIsAThread.start();
    }

    private static MeeplingPronounsAPI INSTANCE;
    public static MeeplingPronounsAPI get() {
        if (INSTANCE == null) {
            INSTANCE = new MeeplingPronounsAPI();
            PronounsAPI.setInstance(INSTANCE);
        }
        return INSTANCE;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        Player p = Meep.get().getPlayer(uuid);
        if (p != null) {
            return PlainTextComponentSerializer.plainText().serialize(p.displayName());
        }
        return null;
    }
}
