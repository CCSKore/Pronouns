package net.kore.pronouns.api;

import java.util.UUID;

public abstract class PronounsAPI {
    private static PronounsAPI INSTANCE = null;

    @SuppressWarnings("unused") // God damn you, it's an API!
    public static PronounsAPI getInstance() {
        return INSTANCE;
    }

    public static void setInstance(PronounsAPI i) {
        INSTANCE = i;
    }

    abstract public String getPronounsLimit(UUID uuid, int limit);
    abstract public String getPronouns(UUID uuid);
    abstract public String getShortPronouns(UUID uuid);
    abstract public String getPersonal1(UUID uuid);
    abstract public String getPersonal2(UUID uuid);
    abstract public String getPossessive(UUID uuid);
    abstract public String getReflexive(UUID uuid);

    public String formatPlayer(String input, String name) {
        if (name == null) name = "null";
        return input.replace("${player}", name);
    }
}
