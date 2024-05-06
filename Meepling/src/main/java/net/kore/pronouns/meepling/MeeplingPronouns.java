package net.kore.pronouns.meepling;

import net.kore.meep.api.event.EnableEvent;
import net.kore.meep.api.event.EventListener;
import net.kore.meep.api.event.EventManager;
import net.kore.meep.api.event.PlayerJoinEvent;
import net.kore.meep.api.plugin.Meepling;
import net.kore.pronouns.api.PronounsAPI;

public class MeeplingPronouns extends Meepling {
    @Override
    public void init() {
        EventManager.get().registerListener(this);
    }

    @EventListener
    public void onEnable(EnableEvent e) {
        //TODO: Config implementation
        MeeplingPronounsAPI.get();
    }

    @EventListener
    public void onPlayerJoin(PlayerJoinEvent e) {
        new Thread(() -> { //Meep improvement, must apply to other platforms
            PronounsAPI.getInstance().getPronouns(e.getPlayer().getUUID());
        }).start();
    }
}
