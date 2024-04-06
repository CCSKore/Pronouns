package net.kore.pronouns.library;

import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LibraryPronounsAPI extends PronounsAPI {
    private LibraryPronounsAPI() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            PronounsLogger.debug("Flushing cache...");
            flushCache();
            PronounsLogger.debug("Running in library mode, can't rebuild cache based on players.");
            PronounsLogger.debug("Consider using the platform specific module to resolve this.");
            PronounsLogger.debug("You may be using this to make use of the MojangAPI providing,");
            PronounsLogger.debug("the ability to get the name offline, this is useful and");
            PronounsLogger.debug("it's understandable if you wanna use this module, to");
            PronounsLogger.debug("prevent this message, just turn off debug logging, you probably");
            PronounsLogger.debug("don't need it on, if you do just ignore the message.");
        }, 0, PronounsConfig.get().node("refresh").getLong(5), TimeUnit.MINUTES);
    }

    private static LibraryPronounsAPI INSTANCE;
    public static LibraryPronounsAPI get() {
        if (INSTANCE == null) {
            INSTANCE = new LibraryPronounsAPI();
        }
        return INSTANCE;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        try {
            InputStream is = new URL("https://sessionserver.mojang.com/session/minecraft/profile/"+uuid.toString()).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            JSONObject json = new JSONObject(sb.toString());
            return json.getString("name");
        } catch (Exception e) {
            return null;
        }
    }
}
