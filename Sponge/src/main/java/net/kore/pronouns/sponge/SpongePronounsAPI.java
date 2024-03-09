//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.kore.pronouns.sponge;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.kore.pronouns.api.CachedPronouns;
import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

public class SpongePronounsAPI extends PronounsAPI {
    private static SpongePronounsAPI INSTANCE;
    private static final List<CachedPronouns> cache = new ArrayList<>();

    private SpongePronounsAPI() {
        Task.Builder taskBuilder = Task.builder();
        taskBuilder.execute(() -> {
            PronounsLogger.debug("Refreshing cache...");
            cache.clear();
            for (ServerPlayer serverPlayer : SpongePronouns.getServer().onlinePlayers()) {
                this.getPronouns(serverPlayer.uniqueId());
            }

        }).delay(0L, TimeUnit.MICROSECONDS).interval(Ticks.of(PronounsConfig.get().node("refresh").getLong(5) * 60 * 20)).plugin(SpongePronouns.getPluginContainer()).build();
    }

    public static SpongePronounsAPI get() {
        if (INSTANCE == null) {
            INSTANCE = new SpongePronounsAPI();
            PronounsAPI.setInstance(INSTANCE);
        }

        return INSTANCE;
    }

    private String getPronounsFromJA(JsonArray ja, int limit) {
        List<String> ls = new ArrayList<>();

        for (JsonElement je : ja) {
            if (ls.size() == limit) break;
            String pronoun = je.getAsString();
            String p1 = PronounsConfig.get().node(pronoun, "overridep1").getString(PronounsConfig.get().node(pronoun, "personal-1").getString("No pronoun for "+pronoun+" defined in config.conf"));
            ls.add(p1);
        }

        return String.join("/", ls);
    }

    public String getPronounFromShort(String pronoun) {
        String p1 = PronounsConfig.get().node(pronoun, "overridep1").getString(PronounsConfig.get().node(pronoun, "personal-1").getString("No pronoun for "+pronoun+" defined in config.conf"));
        String p2 = PronounsConfig.get().node(pronoun, "overridep2").getString(PronounsConfig.get().node(pronoun, "personal-2").getString("No pronoun for "+pronoun+" defined in config.conf"));
        return p1+"/"+p2;
    }

    private JsonObject getObj(UUID uuid) {
        try {
            URL url = new URL("https://pronoundb.org/api/v2/lookup?platform=minecraft&ids="+uuid.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            String response = content.toString();

            return JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void check() {
        cache.removeIf(cp -> Instant.now().toEpochMilli() - cp.timeCached() > 300000L);
    }

    private JsonArray getCached(UUID uuid) {
        Optional<CachedPronouns> ocp = cache.stream().filter(cp -> cp.uuid().equals(uuid)).findFirst();
        return ocp.map(CachedPronouns::pronouns).orElse(null);
    }

    private JsonArray getJsonArray(UUID uuid) {
        check();
        JsonArray cached = getCached(uuid);
        if (cached != null) {
            return cached;
        }
        JsonObject jo = getObj(uuid);
        SpongePronouns.getLogger().info(String.valueOf(jo));
        if (jo == null) {
            return null;
        }
        if (!jo.has(uuid.toString())) {
            return null;
        }
        JsonArray ja = new JsonArray();
        for (JsonElement je : jo.get(uuid.toString()).getAsJsonObject().get("sets").getAsJsonObject().get("en").getAsJsonArray()) {
            ja.add(formatPlayer(je.getAsString(), Sponge.server().player(uuid).get().name()));
        }
        if (cache.size() == PronounsConfig.get().node("max-cache").getLong()) {
            PronounsLogger.debug("Cache has hit max, now flooding cache to prevent max cache hit.");
            cache.clear();
        }
        cache.add(new CachedPronouns(uuid, ja, Instant.now().toEpochMilli()));
        return ja;
    }

    public String getPronounsLimit(UUID uuid, int limit) {
        JsonArray ja = getJsonArray(uuid);
        if (ja == null) {
            return PronounsConfig.get().node("no-pronouns").getString("\"no-pronouns\" is not defined in config.conf");
        }
        if (ja.size() == 1 || limit == 1) return getPronounFromShort(ja.get(0).getAsString());

        return getPronounsFromJA(ja, limit);
    }

    public String getPronouns(UUID uuid) {
        return getPronounsLimit(uuid, 3);
    }

    public String getShortPronouns(UUID uuid) {
        return getPronounsLimit(uuid, 2);
    }

    public String getPersonal1(UUID uuid) {
        JsonArray ja = getJsonArray(uuid);
        if (ja == null) {
            return PronounsConfig.get().node("no-pronouns").getString("\"no-pronouns\" is not defined in config.conf");
        }

        return PronounsConfig.get().node(ja.get(0).getAsString(), "personal-1").getString("No pronoun defined for " + ja.get(0).getAsString() + "in config.conf");
    }

    public String getPersonal2(UUID uuid) {
        JsonArray ja = getJsonArray(uuid);
        if (ja == null) {
            return PronounsConfig.get().node("no-pronouns").getString("\"no-pronouns\" is not defined in config.conf");
        }

        return PronounsConfig.get().node(ja.get(0).getAsString(), "personal-2").getString("No pronoun defined for " + ja.get(0).getAsString() + "in config.conf");
    }

    public String getPossessive(UUID uuid) {
        JsonArray ja = getJsonArray(uuid);
        if (ja == null) {
            return PronounsConfig.get().node("no-pronouns").getString("\"no-pronouns\" is not defined in config.conf");
        }

        return PronounsConfig.get().node(ja.get(0).getAsString(), "possessive").getString("No pronoun defined for " + ja.get(0).getAsString() + "in config.conf");
    }

    public String getReflexive(UUID uuid) {
        JsonArray ja = getJsonArray(uuid);
        if (ja == null) {
            return PronounsConfig.get().node("no-pronouns").getString("\"no-pronouns\" is not defined in config.conf");
        }

        return PronounsConfig.get().node(ja.get(0).getAsString(), "reflexive").getString("No pronoun defined for " + ja.get(0).getAsString() + "in config.conf");
    }
}