package net.kore.pronouns.fabric;

import com.mojang.authlib.GameProfile;
import eu.pb4.placeholders.api.PlaceholderResult;
import net.kore.pronouns.api.PronounsAPI;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.util.UserCache;

import java.util.Optional;
import java.util.UUID;

public class PAPI {
    public static void register() {
        try {
            Placeholders.register(new Identifier("pronouns", "get"), (ctx, arg) -> {
                if (!ctx.hasPlayer())
                    return PlaceholderResult.invalid("No player given!");

                return PlaceholderResult.value(PronounsAPI.getInstance().getPronouns(ctx.player().getUuid()));
            });

            Placeholders.register(new Identifier("pronouns", "get_short"), (ctx, arg) -> {
                if (!ctx.hasPlayer())
                    return PlaceholderResult.invalid("No player given!");

                return PlaceholderResult.value(PronounsAPI.getInstance().getShortPronouns(ctx.player().getUuid()));
            });

            Placeholders.register(new Identifier("pronouns", "get_personal1"), (ctx, arg) -> {
                if (!ctx.hasPlayer())
                    return PlaceholderResult.invalid("No player given!");

                return PlaceholderResult.value(PronounsAPI.getInstance().getPersonal1(ctx.player().getUuid()));
            });

            Placeholders.register(new Identifier("pronouns", "get_personal2"), (ctx, arg) -> {
                if (!ctx.hasPlayer())
                    return PlaceholderResult.invalid("No player given!");

                return PlaceholderResult.value(PronounsAPI.getInstance().getPersonal2(ctx.player().getUuid()));
            });

            Placeholders.register(new Identifier("pronouns", "get_possessive"), (ctx, arg) -> {
                if (!ctx.hasPlayer())
                    return PlaceholderResult.invalid("No player given!");

                return PlaceholderResult.value(PronounsAPI.getInstance().getPossessive(ctx.player().getUuid()));
            });

            Placeholders.register(new Identifier("pronouns", "get_reflexive"), (ctx, arg) -> {
                if (!ctx.hasPlayer())
                    return PlaceholderResult.invalid("No player given!");

                return PlaceholderResult.value(PronounsAPI.getInstance().getReflexive(ctx.player().getUuid()));
            });

            Placeholders.register(new Identifier("pronouns", "get_limit"), (ctx, arg) -> {
                if (!ctx.hasPlayer())
                    return PlaceholderResult.invalid("No player given!");
                if (arg == null)
                    return PlaceholderResult.invalid("No argument given!");

                Integer lim = Integer.getInteger(arg);
                if (lim != null) return PlaceholderResult.value(PronounsAPI.getInstance().getPronounsLimit(ctx.player().getUuid(), lim));

                return PlaceholderResult.invalid("Invalid argument given.");
            });
        } catch (Throwable ignored) {} // Do anything, PAPI isn't required
    }
}
