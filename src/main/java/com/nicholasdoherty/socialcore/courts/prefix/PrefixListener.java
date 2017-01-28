package com.nicholasdoherty.socialcore.courts.prefix;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

/**
 * Created by john on 3/1/15.
 */
public class PrefixListener implements Listener {
    private PrefixManager prefixManager;

    public PrefixListener(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
        Bukkit.getServer().getPluginManager().registerEvents(this,prefixManager.getCourts().getPlugin());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void chatEvent(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Courts courts = prefixManager.getCourts();
        String format = event.getFormat();
        for (CourtSession courtSession : courts.getCourtSessionManager().getInSession()) {
            if (courtSession.getCaze() != null) {
                Case caze = courtSession.getCaze();
                if (caze.getPlantiff() != null && caze.getPlantiff().getUuid().equals(uuid)) {
                    format = format.replace("%1",courts.getCourtsLangManager().getCourtSessionPlaintiffPrefix() + "%1");
                    event.setFormat(format);
                    return;
                }
                if (caze.getDefendent() != null && caze.getDefendent().getUuid().equals(uuid)) {
                    format = format.replace("%1",courts.getCourtsLangManager().getCourtSessionDefendantPrefix() + "%1");
                    event.setFormat(format);
                    return;
                }
            }
        }
    }

}
