package net.kore.pronouns.api;

import java.util.Objects;

public enum PronounDecoration {
    PRIDE("pride"),
    PRIDE_BI("pride_bi"),
    PRIDE_PAN("pride_pan"),
    PRIDE_TRANS("pride_trans"),
    PRIDE_LESBIAN("pride_lesbian"),
    NIGHTTIME("nighttime"),
    DAYTIME("daytime"),
    COGS("cogs"),

    DONATOR_AURORA("donator_aurora"),
    DONATOR_WARMTH("donator_warmth"),
    DONATOR_BLOSSOM("donator_blossom"),
    DONATOR_RIBBON("donator_ribbon"),
    DONATOR_STAR("donator_star"),
    DONATOR_STRAWBERRY("donator_strawberry"),

    DF_KANIN("df_kanin"),
    DF_PLUME("df_plume"),

    CATGIRL_CHIEF("catgirl_chief");

    private final String id;
    public String getId() {
        return id;
    }

    PronounDecoration(String s) {
        id = s;
    }

    public static PronounDecoration getFromID(String id) {
        for (PronounDecoration pd : PronounDecoration.values()) {
            if (Objects.equals(pd.id, id)) {
                return pd;
            }
        }
        return null;
    }
}
