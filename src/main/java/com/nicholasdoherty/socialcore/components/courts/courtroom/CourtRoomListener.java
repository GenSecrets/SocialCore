package com.nicholasdoherty.socialcore.components.courts.courtroom;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionEnteredEvent;
import com.mewin.WGRegionEvents.events.RegionLeftEvent;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by john on 1/19/15.
 */
public class CourtRoomListener implements Listener {
    private final CourtSessionManager courtSessionManager;
    
    public CourtRoomListener(final CourtSessionManager courtSessionManager) {
        this.courtSessionManager = courtSessionManager;
        Bukkit.getServer().getPluginManager().registerEvents(this, Courts.getCourts().getPlugin());
    }
    
    @EventHandler
    public void contempt(final RegionEnterEvent event) {
        final Player p = event.getPlayer();
        for(final CourtSession courtSession : courtSessionManager.getInSession()) {
            final CourtRoom courtRoom = courtSession.getCourtRoom();
            if(courtRoom != null) {
                if(courtSession.getContempt().contains(p.getUniqueId())) {
                    if(event.getRegion().equals(courtSession.getCourtRoom().getRegion())) {
                        p.teleport(p.getWorld().getSpawnLocation());
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void mute(final AsyncPlayerChatEvent event) {
        final Player sender = event.getPlayer();
        for(final CourtSession courtSession : courtSessionManager.getInSession()) {
            final CourtRoom courtRoom = courtSession.getCourtRoom();
            if(courtRoom == null) {
                continue;
            }
            if(courtSession.isSilenced()) {
                if(!Courts.getCourts().getJudgeManager().isJudge(sender.getUniqueId())) {
                    final Collection<Player> remove = new HashSet<>();
                    for(final Player rec : event.getRecipients()) {
                        if(courtRoom.isInRoom(rec.getLocation())) {
                            remove.add(rec);
                        }
                    }
                    for(final Player rec : remove) {
                        event.getRecipients().remove(rec);
                    }
                }
            }
            if(courtSession.getMuted().contains(sender.getUniqueId())) {
                final Collection<Player> remove = new HashSet<>();
                for(final Player rec : event.getRecipients()) {
                    if(courtRoom.isInRoom(rec.getLocation())) {
                        remove.add(rec);
                    }
                }
                for(final Player rec : remove) {
                    event.getRecipients().remove(rec);
                }
            }
        }
    }
    
    @EventHandler
    public void enter(final RegionEnteredEvent event) {
        //rly hap
        final Player p = event.getPlayer();
        final ProtectedRegion enteredRegion = event.getRegion();
        if(enteredRegion == null) {
            return;
        }
        for(final CourtSession courtSession : courtSessionManager.getInSession()) {
            if(courtSession != null && courtSession.getCourtRoom() != null) {
                final CourtRoom courtRoom = courtSession.getCourtRoom();
                if(courtRoom.getRegion() != null && courtRoom.getRegion().getId().equals(enteredRegion.getId())) {
                    courtSessionManager.removeFromGlobal(p.getName());
                    return;
                }
            }
        }
    }
    
    @EventHandler
    public void leave(final RegionLeftEvent event) {
        //rly hap
        final Player p = event.getPlayer();
        final ProtectedRegion enteredRegion = event.getRegion();
        if(enteredRegion == null) {
            return;
        }
        for(final CourtSession courtSession : courtSessionManager.getInSession()) {
            if(courtSession != null && courtSession.getCourtRoom() != null) {
                final CourtRoom courtRoom = courtSession.getCourtRoom();
                if(courtRoom.getRegion() != null && courtRoom.getRegion().getId().equals(enteredRegion.getId())) {
                    courtSessionManager.addToGlobal(p.getName());
                    return;
                }
            }
        }
    }
    
    @EventHandler
    public void judgeLogin(final PlayerJoinEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        for(final CourtSession courtSession : courtSessionManager.getInSession()) {
            if(courtSession != null && courtSession.isJudgeOffline() && courtSession.getJudge() != null) {
                if(courtSession.getJudge().getUuid().equals(uuid)) {
                    courtSession.stopJudgeOfflineTime();
                }
            }
        }
    }
    
    @EventHandler
    public void judgeLogout(final PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        for(final CourtSession courtSession : courtSessionManager.getInSession()) {
            if(courtSession != null && courtSession.getJudge() != null && courtSession.getJudge().getUuid() != null && courtSession.getJudge().getUuid().equals(uuid)) {
                courtSession.startJudgeOfflineTime();
            }
        }
    }
}
