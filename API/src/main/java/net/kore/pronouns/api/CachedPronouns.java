package net.kore.pronouns.api;

import com.google.gson.JsonArray;

import java.util.UUID;

public record CachedPronouns(UUID uuid, JsonArray pronouns, Long timeCached) {
}
