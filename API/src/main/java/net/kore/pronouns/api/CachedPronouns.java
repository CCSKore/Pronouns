package net.kore.pronouns.api;

import com.google.gson.JsonObject;

import java.util.UUID;

public record CachedPronouns(UUID uuid, JsonObject data, Long timeCached) {
}
