package net.kore.pronouns.neoforge;

import net.kore.pronouns.api.PronounsAPI;
import net.kore.pronouns.api.PronounsConfig;
import net.kore.pronouns.api.PronounsLogger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

import java.util.UUID;

public class NeoForgePronounsAPI extends PronounsAPI {
    private int ticks;
    private NeoForgePronounsAPI() {
        ticks = 0;
        NeoForge.EVENT_BUS.addListener(event -> {
            if (event instanceof TickEvent.ServerTickEvent e) {
                ticks++;
                if (ticks >= PronounsConfig.get().node("refresh").getInt(5) * 60 * 20){
                    PronounsLogger.debug("Refreshing cache...");
                    flushCache();
                    for (ServerPlayer player : e.getServer().getPlayerList().getPlayers()) {
                        getPronouns(player.getUUID());
                    }
                    ticks = 0;
                }
            }
        });
    }
    private static NeoForgePronounsAPI INSTANCE;

    public static NeoForgePronounsAPI get() {
        if (INSTANCE == null) {
            INSTANCE = new NeoForgePronounsAPI();
            PronounsAPI.setInstance(INSTANCE);
        }
        return INSTANCE;
    }

    @Override
    public String getPlayerName(UUID uuid) {
        ServerPlayer p = NeoForgePronouns.getServer().getPlayerList().getPlayer(uuid);
        if (p == null) return null;
        else return p.getName().getString();
    }
}
