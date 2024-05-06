package net.kore.pronouns.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public abstract class PronounsAPI {
    private static final AtomicReference<PronounsAPI> INSTANCE = new AtomicReference<>(null);
    public static final List<CachedPronouns> cache = new CopyOnWriteArrayList<>();
    public static void flushCache() {
        cache.clear();
    }

    @SuppressWarnings("unused") // God damn you, it's an API!
    public static PronounsAPI getInstance() {
        return INSTANCE.get();
    }

    public static void setInstance(PronounsAPI i) {
        INSTANCE.set(i);
    }

    public String getPronounsFromJA(JsonArray ja, int limit) {
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

    public JsonObject getObj(UUID uuid) {
        check();
        JsonObject jo = getCached(uuid);
        if (jo != null) {
            JsonObject realJo = JsonParser.parseString("{}").getAsJsonObject();
            realJo.add(uuid.toString(), jo);
            return realJo;
        }
        try {
            PronounsLogger.debug("Sending request to PronounDB API");
            URL url = new URL("https://pronoundb.org/api/v2/lookup?platform=minecraft&ids="+uuid.toString());
            final String response = getResponse(url);

            return JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getResponse(URL url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("x-pronoundb-source", "Pronouns Mod/Plugin (https://modrinth.com/plugin/kore-pronouns)");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return content.toString();
    }

    public void check() {
        cache.removeIf(cp -> Instant.now().toEpochMilli() - cp.timeCached() > 300000L);
    }

    public JsonObject getCached(UUID uuid) {
        Optional<CachedPronouns> ocp = cache.stream().filter(cp -> cp.uuid().equals(uuid)).findFirst();
        return ocp.map(CachedPronouns::data).orElse(null);
    }

    public JsonArray getJsonArray(UUID uuid) {
        check();
        JsonObject jo = getObj(uuid);
        if (jo == null || !jo.has(uuid.toString())) {
            if (cache.stream().noneMatch(cachedPronouns -> cachedPronouns.uuid().equals(uuid))) {
                cache.add(new CachedPronouns(uuid, null, Instant.now().toEpochMilli()));
            }
            return null;
        }

        JsonArray ja = jo.get(uuid.toString()).getAsJsonObject().get("sets").getAsJsonObject().get("en").getAsJsonArray();

        if (cache.size() == PronounsConfig.get().node("max-cache").getLong()) {
            PronounsLogger.debug("Cache has hit max, now flooding cache to prevent max cache hit.");
            cache.clear();
        }
        if (cache.stream().noneMatch(cachedPronouns -> cachedPronouns.uuid().equals(uuid))) {
            cache.add(new CachedPronouns(uuid, jo.get(uuid.toString()).getAsJsonObject(), Instant.now().toEpochMilli()));
        }
        return ja;
    }

    public void massCacheValues(List<UUID> uuids) {
        uuids = new ArrayList<>(uuids);
        if (uuids.size() > 45) {
            PronounUtils.partitionBasedOnSize(uuids, 45).forEach(this::massCacheValues);
            return;
        }
        check();
        uuids.removeIf(uuid -> getCached(uuid) != null);
        List<String> suuids = uuids.stream()
                .map(UUID::toString)
                .toList();
        if (!suuids.isEmpty()) {
           new Thread(() -> {
               try {
                   PronounsLogger.debug("Sending request to PronounDB API");
                   URL url = new URL("https://pronoundb.org/api/v2/lookup?platform=minecraft&ids=" + String.join(",", suuids));
                   HttpURLConnection con = (HttpURLConnection) url.openConnection();
                   con.setRequestMethod("GET");
                   con.setRequestProperty("Content-Type", "application/json");
                   con.setRequestProperty("x-pronoundb-source", "Pronouns Mod/Plugin (https://modrinth.com/plugin/kore-pronouns)");

                   BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                   String inputLine;
                   StringBuilder content = new StringBuilder();
                   while ((inputLine = in.readLine()) != null) {
                       content.append(inputLine);
                   }
                   in.close();
                   con.disconnect();

                   String response = content.toString();

                   JsonObject jo = JsonParser.parseString(response).getAsJsonObject();
                   for (Map.Entry<String, JsonElement> mE : jo.entrySet()) {
                       UUID currentUuid = UUID.fromString(mE.getKey());
                       cache.add(new CachedPronouns(currentUuid, mE.getValue().getAsJsonObject(), Instant.now().toEpochMilli()));
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }).start();
        }
    }

    public String getPronounsLimit(UUID uuid, int limit) {
        JsonArray ja = getJsonArray(uuid);
        if (ja == null) {
            return PronounsConfig.get().node("no-pronouns").getString("\"no-pronouns\" is not defined in config.conf");
        }
        PronounsLogger.debug(ja.toString());
        if (ja.size() == 1 || limit == 1) return getPronounFromShort(ja.get(0).getAsString());

        return getPronounsFromJA(ja, limit);
    }

    public String getPronouns(UUID uuid) {
        String debugedItem = getPronounsLimit(uuid, 3);
        PronounsLogger.debug(debugedItem);
        return debugedItem;
    }

    public String getShortPronouns(UUID uuid) {
        return getPronounsLimit(uuid, 2);
    }

    public String getPersonal1(UUID uuid) {
        JsonArray ja = getJsonArray(uuid);
        if (ja == null) {
            return PronounsConfig.get().node("no-pronouns").getString("\"no-pronouns\" is not defined in config.conf");
        }

        return formatPlayer(PronounsConfig.get().node(ja.get(0).getAsString(), "personal-1").getString("No pronoun defined for " + ja.get(0).getAsString() + "in config.conf"), getPlayerName(uuid));
    }

    public String getPersonal2(UUID uuid) {
        JsonArray ja = getJsonArray(uuid);
        if (ja == null) {
            return PronounsConfig.get().node("no-pronouns").getString("\"no-pronouns\" is not defined in config.conf");
        }

        return formatPlayer(PronounsConfig.get().node(ja.get(0).getAsString(), "personal-2").getString("No pronoun defined for " + ja.get(0).getAsString() + "in config.conf"), getPlayerName(uuid));
    }

    public String getPossessive(UUID uuid) {
        JsonArray ja = getJsonArray(uuid);
        if (ja == null) {
            return PronounsConfig.get().node("no-pronouns").getString("\"no-pronouns\" is not defined in config.conf");
        }

        return formatPlayer(PronounsConfig.get().node(ja.get(0).getAsString(), "possessive").getString("No pronoun defined for " + ja.get(0).getAsString() + "in config.conf"), getPlayerName(uuid));
    }

    public String getReflexive(UUID uuid) {
        JsonArray ja = getJsonArray(uuid);
        if (ja == null) {
            return PronounsConfig.get().node("no-pronouns").getString("\"no-pronouns\" is not defined in config.conf");
        }

        return formatPlayer(PronounsConfig.get().node(ja.get(0).getAsString(), "reflexive").getString("No pronoun defined for " + ja.get(0).getAsString() + "in config.conf"), getPlayerName(uuid));
    }

    public PronounDecoration getDecorationType(UUID uuid) {
        JsonObject jo = getObj(uuid);
        if (jo == null || !jo.has(uuid.toString())) {
            return null;
        }
        return PronounDecoration.getFromID(jo.get(uuid.toString()).getAsJsonObject().get("decoration").getAsString());
    }

    public String getDecorationTypeMMGradientColor(UUID uuid) {
        PronounDecoration type = getDecorationType(uuid);
        return switch (type.getId()) {
            case "pride" -> "<gradient:#F47C7C:#FFC268:#F7F48B:#A1DE93:#70A1D7:#957DAD>";
            case "pride_bi" -> "<gradient:#D872AC:#957DAD:#6AA9ED>";
            case "pride_pan" -> "<gradient:#FF82B1:#F7F48B:#8BD1F9>";
            case "pride_trans" -> "<gradient:#67D4EA:#FCB6B3:#FFFFFF:#FCB6B3:#67D4EA>";
            case "pride_lesbian" -> "<gradient:#EB765A:#FBAB74:#FFFFFF:#F295CC:#A36088>";
            case "nighttime" -> "<gradient:#66757F:#66757F>";
            case "daytime" -> "<gradient:#FFAC33:#FFAC33>";
            case "cogs" -> "<gradient:#C3591D:#C3591D>";

            case "donator_aurora" -> "<gradient:#18F89A:#C243EE>";
            case "donator_warmth" -> "<gradient:#FDD264:#EB5353>";
            case "donator_blossom" -> "<gradient:#F4ABBA:#F4ABBA>";
            case "donator_ribbon" -> "<gradient:#DD2E44:#DD2E44>";
            case "donator_star" -> "<gradient:#FDD264:#FDD264>";
            case "donator_strawberry" -> "<gradient:#77B255:#BE1931:#F4ABBA:#BE1931>";

            case "df_kanin" -> "<gradient:#E08C73:#E08C73>";
            case "df_plume" -> "<gradient:#BAD9B5:#BAD9B5>";

            case "catgirl_chief" -> "<gradient:#F49898:#F49898>";

            default -> "<gradient:#FFFFFF:#FFFFFF>";
        };
    }

    public String getDecorationResourcePack(UUID uuid) {
        PronounDecoration type = getDecorationType(uuid);
        return switch (type.getId()) {
            case "pride" -> "<font:pronouns:decorations> </font>";
            case "pride_bi" -> "<font:pronouns:decorations>!</font>";
            case "pride_pan" -> "<font:pronouns:decorations>\"</font>";
            case "pride_trans" -> "<font:pronouns:decorations>#</font>";
            case "pride_lesbian" -> "<font:pronouns:decorations>$</font>";
            case "nighttime" -> "<font:pronouns:decorations>%</font>";
            case "daytime" -> "<font:pronouns:decorations>&</font>";
            case "cogs" -> "<font:pronouns:decorations>'</font>";

            case "donator_aurora" -> "<font:pronouns:decorations>(</font>";
            case "donator_warmth" -> "<font:pronouns:decorations>)</font>";
            case "donator_blossom" -> "<font:pronouns:decorations>*</font>";
            case "donator_ribbon" -> "<font:pronouns:decorations>+</font>";
            case "donator_star" -> "<font:pronouns:decorations>,</font>";
            case "donator_strawberry" -> "<font:pronouns:decorations>-</font>";

            case "df_kanin" -> "<font:pronouns:decorations>.</font>";
            case "df_plume" -> "<font:pronouns:decorations>/</font>";

            case "catgirl_chief" -> "<font:pronouns:decorations>0</font>";

            default -> null;
        };
    }

    public String formatPlayer(String input, String name) {
        if (name == null) name = "null";
        return input.replace("${player}", name);
    }

    abstract public String getPlayerName(UUID uuid);
}
