package net.kore.pronouns.api;

import org.spongepowered.configurate.CommentedConfigurationNode;

public class PronounsConfig {
    private static CommentedConfigurationNode config;

    public static void set(CommentedConfigurationNode node) {
        config = node;
    }

    public static CommentedConfigurationNode get() {
        return config;
    }
}
