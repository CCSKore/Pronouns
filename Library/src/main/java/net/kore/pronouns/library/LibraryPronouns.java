package net.kore.pronouns.library;

import net.kore.pronouns.api.PronounsConfig;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class LibraryPronouns {
    public static void start() {
        start(LibraryPronouns.class.getClassLoader().getResource("config.conf"), true);
    }

    public static void start(URL url) {
        start(url, false);
    }

    public static void start(URL url, boolean internal) {
        HoconConfigurationLoader loader;
        if (internal) {
            try {
                loader = HoconConfigurationLoader.builder()
                        .file(Paths.get(url.toURI()).toFile())
                        .build();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            loader = HoconConfigurationLoader.builder()
                    .url(url)
                    .build();
        }

        try {
            PronounsConfig.set(loader.load());
        } catch (ConfigurateException e) {
            throw new RuntimeException("Config unable to load.", e);
        }
        init();
    }

    public static void start(File file) {
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .file(file)
                .build();

        try {
            PronounsConfig.set(loader.load());
        } catch (ConfigurateException e) {
            throw new RuntimeException("Config unable to load.", e);
        }
        init();
    }

    private static void init() {
        LibraryPronounsAPI.get();
    }
}
