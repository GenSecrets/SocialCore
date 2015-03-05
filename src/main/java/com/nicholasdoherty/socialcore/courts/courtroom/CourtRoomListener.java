package com.nicholasdoherty.socialcore.courts.courtroom;

import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;
import com.nicholasdoherty.socialcore.courts.Courts;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Created by john on 1/19/15.
 */
public class CourtRoomListener implements Listener {
    private CourtSessionManager courtSessionManager;

    public CourtRoomListener(CourtSessionManager courtSessionManager) {
        this.courtSessionManager = courtSessionManager;
        Bukkit.getServer().getPluginManager().registerEvents(this, Courts.getCourts().getPlugin());
    }
    @EventHandler
    public void enter(RegionEnteredEvent event) {
        //rly hap
        Player p = event.getPlayer();
        ProtectedRegion enteredRegion = event.getRegion();
        if (enteredRegion == null)
            return;
        for (CourtSession courtSession : courtSessionManager.getInSession()) {
            if (courtSession != null && courtSession.getCourtRoom() != null) {
                CourtRoom courtRoom = courtSession.getCourtRoom();
                if (courtRoom.getRegion() != null && courtRoom.getRegion().getId().equals(enteredRegion.getId())) {
                    courtSessionManager.removeFromGlobal(p.getName());
                    return;
                }
            }
        }
    }
    @EventHandler
    public void leave(RegionLeftEvent event) {
        //rly hap
        Player p = event.getPlayer();
        ProtectedRegion enteredRegion = event.getRegion();
        if (enteredRegion == null)
            return;
        for (CourtSession courtSession : courtSessionManager.getInSession()) {
            if (courtSession != null && courtSession.getCourtRoom() != null) {
                CourtRoom courtRoom = courtSession.getCourtRoom();
                if (courtRoom.getRegion() != null && courtRoom.getRegion().getId().equals(enteredRegion.getId())) {
                    courtSessionManager.addToGlobal(p.getName());
                    return;
                }
            }
        }
    }
    @EventHandler
    public void judgeLogin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        for (CourtSession courtSession : courtSessionManager.getInSession()) {
            if (courtSession != null && courtSession.isJudgeOffline()) {
                if (courtSession.getJudge().getUuid().equals(uuid)) {
                    courtSession.stopJudgeOfflineTime();
                }
            }
        }
    }

    @EventHandler
    public void judgeLogout(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        for (CourtSession courtSession : courtSessionManager.getInSession()) {
            if (courtSession != null && courtSession.getJudge().getUuid().equals(uuid)) {
                courtSession.startJudgeOfflineTime();
            }
        }
    }


}
