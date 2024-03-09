package net.kore.pronouns.paper;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kore.pronouns.api.PronounsAPI;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "pronouns";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Kore Team";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.0.1";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getName() {
        return "Pronouns";
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return List.of("%pronouns_get%", "%pronouns_getshort%", "pronouns_getlimit_<limit>");
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String param) {
        PronounsAPI API = PronounsAPI.getInstance();

        String s = switch (param) {
            case "get" -> API.getPronouns(player.getUniqueId());
            case "get_short" -> API.getShortPronouns(player.getUniqueId());
            case "get_personal1" -> API.getPersonal1(player.getUniqueId());
            case "get_personal2" -> API.getPersonal2(player.getUniqueId());
            case "get_possessive" -> API.getPossessive(player.getUniqueId());
            case "get_reflexive" -> API.getReflexive(player.getUniqueId());
            default -> null;
        };

        if (s != null) {
            return s;
        }

        if (param.startsWith("get_limit_")) {
            String limit = param.substring(9);
            Integer lim = Integer.getInteger(limit);
            if (lim != null) return API.getPronounsLimit(player.getUniqueId(), lim);
        }

        return null;
    }
}
