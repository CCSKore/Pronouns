package net.kore.pronouns.meepling;

import net.kore.meep.api.Meep;
import net.kore.meep.api.player.Player;
import net.kore.pronouns.api.PronounsAPI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.UUID;

public class MeeplingPronounsAPI extends PronounsAPI {
    private MeeplingPronounsAPI() {
        //TODO: Implement a schedular in Meep
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
