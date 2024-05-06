package net.kore.pronouns.fabric.client.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kore.pronouns.fabric.FabricPronounsAPI;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.UUID;

import static dev.xpple.clientarguments.arguments.CGameProfileArgumentType.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class PronounsCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("pronouns")
                .then(argument("player", gameProfile())
                        .executes(ctx -> {
                            Collection<GameProfile> gameProfiles = getCProfileArgument(ctx, "player");
                            if (gameProfiles.isEmpty()) {
                                throw new SimpleCommandExceptionType(Text.translatable("pronouns.command.no_players")).create();
                            } else if (gameProfiles.size() > 1) {
                                throw new SimpleCommandExceptionType(Text.translatable("pronouns.command.too_many_players")).create();
                            }
                            GameProfile gameProfile = gameProfiles.iterator().next();
                            UUID uuid = gameProfile.getId();
                            if (FabricPronounsAPI.get().getCached(uuid) != null) {
                                ctx.getSource().sendFeedback(Text.translatable("pronouns.command.feedback", gameProfile.getName(), FabricPronounsAPI.get().getPronouns(uuid)));
                            } else {
                                ctx.getSource().sendFeedback(Text.translatable("pronouns.command.wait"));
                                new Thread(() -> ctx.getSource().sendFeedback(Text.translatable("pronouns.command.feedback", gameProfile.getName(), FabricPronounsAPI.get().getPronouns(uuid)))).start();
                            }
                            return Command.SINGLE_SUCCESS;
                        })));

        dispatcher.register(literal("language_pronouns")
                .executes(ctx -> {
                    switch (ctx.getSource().getClient().getLanguageManager().getLanguage()) {
                        case "en_us" -> {
                            ctx.getSource().sendFeedback(Text.literal("No language notes for the US language."));
                        }
                        case "en_gb" -> {
                            ctx.getSource().sendFeedback(Text.literal("No language notes for the UK language."));
                        }
                        case "en_ud" -> {
                            ctx.getSource().sendFeedback(Text.literal("Notes: Make this text upside down"));
                        }
                        case "en_pt" -> {
                            ctx.getSource().sendFeedback(Text.literal("Arrr, no parchment be holdin' notes fer the pirate tongue, matey."));
                        }
                        case "enws" -> {
                            ctx.getSource().sendFeedback(Text.literal("Verily, there are no annotations for the fancy tongue in our scrolls."));
                        }
                        case "de_de" -> {
                            ctx.getSource().sendFeedback(Text.literal("Meine Deustch ist nicht gut :("));
                        }
                        default -> {
                            ctx.getSource().sendFeedback(Text.literal("Unsupported language ( sorry :< ) "));
                        }
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}
