package net.kore.pronouns.fabric.client;

import dev.isxander.yacl3.api.*;
import net.kore.pronouns.api.PronounsAPI;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class Config {
    public static Screen init(Screen screen) {
        return YetAnotherConfigLib.createBuilder()
        .title(Text.literal("Pronouns Configuration"))
        .category(ConfigCategory.createBuilder()
                .name(Text.literal("Pronouns"))
                .tooltip(Text.literal("The Pronouns mod config :D"))
                .group(OptionGroup.createBuilder()
                        .name(Text.literal("General options"))
                        .description(OptionDescription.of(Text.literal("General things you can do")))
                        .option(ButtonOption.createBuilder()
                                .name(Text.literal("Click to clear cache"))
                                .text(Text.literal(""))
                                .description(OptionDescription.of(Text.literal("Clears the cache so new values can load quicker")))
                                .action((s, b) -> PronounsAPI.flushCache())
                                .build())
                        .build())
                .build())
        .build()
        .generateScreen(screen);
    }
}
