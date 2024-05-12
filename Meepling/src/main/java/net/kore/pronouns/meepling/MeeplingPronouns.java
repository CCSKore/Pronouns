package net.kore.pronouns.meepling;

import net.kore.meep.api.event.*;
import net.kore.meep.api.plugin.Meepling;
import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.*;

public class MeeplingPronouns extends Meepling {
    protected static Thread thisIsAThread = null;

    @Override
    public void init() {
        EventManager.get().registerListener(this);
    }

    @EventListener
    public void onEnable(EnableEvent e) {
        //Config implemented!
        File configFile = new File(getConfigDir(), "config.conf");
        if (!configFile.exists()) {
            try {
                OutputStream os = new FileOutputStream(configFile);
                InputStream is = MeeplingPronouns.class.getResourceAsStream("config.conf");
                if (is == null) throw new RuntimeException("Unable to read default config");
                os.write(is.readAllBytes());
                os.close();
            } catch (IOException ex) {
                throw new RuntimeException("Unable to write config.", ex);
            }
        }

        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .file(configFile)
                .build();

        try {
            PronounsConfig.set(loader.load());
        } catch (ConfigurateException ex) {
            throw new RuntimeException(ex);
        }

        MeeplingPronounsAPI.get();
    }

    public void onDisable(DisableEvent e) {
        if (thisIsAThread != null && thisIsAThread.isAlive()) {
            thisIsAThread.interrupt();
        }
    }

    @EventListener
    public void onPlayerJoin(PlayerJoinEvent e) {
        new Thread(() -> { //Meep improvement, must apply to other platforms
            PronounsAPI.getInstance().getPronouns(e.getPlayer().getUUID());
        }).start();
    }
}
