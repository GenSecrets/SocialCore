package com.nicholasdoherty.socialcore.courts.courtroom;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.voxmc.voxlib.VLocation;
import com.voxmc.voxlib.WorldGuardUtils;
import com.voxmc.voxlib.util.VoxEffects;
import com.voxmc.voxlib.util.title.TitleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by john on 1/11/15.
 */
public class CourtRoom {
    private final String name;
    private final String regionName;
    private final VLocation tpLocation;
    private final VLocation center;
    private final VLocation judgeChairLoc;
    
    public CourtRoom(final String name, final String regionName, final VLocation tpLocation, final VLocation center,
                     final VLocation judgeChairLoc) {
        this.name = name;
        this.regionName = regionName;
        this.tpLocation = tpLocation;
        this.center = center;
        this.judgeChairLoc = judgeChairLoc;
    }
    
    public VLocation getJudgeChairLoc() {
        return judgeChairLoc;
    }
    
    public String getName() {
        return name;
    }
    
    public ProtectedRegion getRegion() {
        return WorldGuardUtils.getRegion(center.getLocation(), regionName);
    }
    
    public VLocation getTpLocation() {
        return tpLocation;
    }
    
    public VLocation getCenter() {
        return center;
    }
    
    public void teleportTo(final CourtSession courtSession) {
        if(courtSession.getJudge() != null) {
            final Player judgeP = courtSession.getJudge().getPlayer();
            if(judgeP != null) {
                judgeP.teleport(judgeChairLoc.getLocation());
            }
        }
        if(courtSession.getCaze().getPlantiff() != null) {
            final Player plaintiffPlayer = courtSession.getCaze().getPlantiff().getPlayer();
            if(plaintiffPlayer != null) {
                plaintiffPlayer.teleport(tpLocation.getLocation());
            }
        }
        if(courtSession.getCaze().getDefendent() != null) {
            final Player defendantPlayer = courtSession.getCaze().getDefendent().getPlayer();
            if(defendantPlayer != null) {
                defendantPlayer.teleport(tpLocation.getLocation());
            }
        }
    }
    
    public void silence() {
        final long length = Courts.getCourts().getCourtsConfig().getSilenceLength();
        final VoxEffects voxEffects = Courts.getCourts().getCourtsConfig().getSilenceCourtEffects();
        voxEffects.play(center.getLocation());
        final ProtectedRegion region = getRegion();
        if(region == null) {
            System.out.println("Could not find region: " + regionName);
            return;
        }
        //noinspection ConstantConditions
        for(final Player p : center.getLocation().getWorld().getPlayers()) {
            final Location pLoc = p.getLocation();
            final int x = pLoc.getBlockX();
            final int y = pLoc.getBlockY();
            final int z = pLoc.getBlockZ();
            if(region.contains(x, y, z)) {
                TitleUtil.sendTitle(p, ChatColor.RED + "Order!", "Quiet in the court!", 5, (int) length, 10);
            }
        }
    }
    
    public List<Player> playersInRoom() {
        final List<Player> inRoom = new ArrayList<>();
        final ProtectedRegion region = getRegion();
        if(region == null) {
            System.out.println("Could not find region: " + regionName);
            return inRoom;
        }
        if(center != null && center.getLocation() != null) {
            //noinspection ConstantConditions
            for(final Player p : center.getLocation().getWorld().getPlayers()) {
                if(p != null && p.isOnline()) {
                    p.getLocation();
                    final Location pLoc = p.getLocation();
                    final int x = (int) Math.round(pLoc.getX());
                    final int y = (int) Math.round(pLoc.getY());
                    final int z = (int) Math.round(pLoc.getZ());
                    if(region.contains(x, y, z)) {
                        inRoom.add(p);
                    }
                }
            }
        }
        return inRoom;
    }
    
    public void sendMessage(final String... message) {
        if(message == null) {
            return;
        }
        for(final Player p : playersInRoom()) {
            p.sendMessage(message);
        }
    }
    
    public boolean isInRoom(final Location location) {
        final ProtectedRegion region = getRegion();
        if(region == null) {
            System.out.println("region for " + name + " is null");
            return false;
        }
        if(location == null) {
            return false;
        }
        if(!Objects.equals(location.getWorld(), center.getLocation().getWorld())) {
            return false;
        }
        return region.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
