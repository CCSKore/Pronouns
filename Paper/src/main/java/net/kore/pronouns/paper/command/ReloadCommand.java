package net.kore.pronouns.paper.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.paper.PaperPronouns;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;

@Command(name = "pronounsreload")
public class ReloadCommand {
    @Execute
    public void reload(@Context CommandSender sender) {
        File configFolder = PaperPronouns.getInstance().getDataFolder();
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        File configFile = new File(configFolder, "config.conf");
        if (!configFile.exists()) {
            PaperPronouns.getInstance().saveResource("config.conf", false);
        }

        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .file(configFile)
                .build();

        try {
            PronounsConfig.set(loader.load());
            sender.sendMessage(Component.text("Config reloaded!").color(NamedTextColor.GREEN));
        } catch (ConfigurateException e) {
            e.printStackTrace();
            sender.sendMessage(Component.text("Error loading config, check console for errors.").color(NamedTextColor.DARK_RED));
        }
    }
}
