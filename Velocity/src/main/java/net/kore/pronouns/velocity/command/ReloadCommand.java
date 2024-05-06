package net.kore.pronouns.velocity.command;

import com.google.common.io.Resources;
import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.velocity.VelocityPronouns;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Command(name = "pronounsreload")
public class ReloadCommand {
    @Execute
    public void reload(@Context CommandSource sender) {
        File configFile = new File(VelocityPronouns.getInstance().getDataDirectory().toFile(), "config.conf");
        if (!configFile.exists() || !configFile.isDirectory()) {
            try {
                Files.copy(Paths.get(Resources.getResource(getClass(), "config.conf").toURI()), configFile.toPath());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
