package com.nicholasdoherty.socialcore.courts.courtroom;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.utils.VLocation;
import com.nicholasdoherty.socialcore.utils.VoxEffects;
import com.nicholasdoherty.socialcore.utils.title.TitleUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/11/15.
 */
public class CourtRoom {
    private String name;
    private String regionName;
    private VLocation tpLocation;
    private VLocation center;
    private VLocation judgeChairLoc;
    public CourtRoom(String name,String regionName, VLocation tpLocation, VLocation center, VLocation judgeChairLoc) {
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
        return WGBukkit.getRegionManager(center.getLocation().getWorld()).getRegion(regionName);
    }
    public void onEnter(Player p) {

    }

    public VLocation getTpLocation() {
        return tpLocation;
    }

    public VLocation getCenter() {
        return center;
    }
    public void teleportTo(CourtSession courtSession) {
        if (courtSession.getJudge() != null) {
            Player judgeP = courtSession.getJudge().getPlayer();
            if (judgeP != null ) {
                judgeP.teleport(judgeChairLoc.getLocation());
            }
        }
        if (courtSession.getCaze().getPlantiff() != null) {
            Player plaintiffPlayer = courtSession.getCaze().getPlantiff().getPlayer();
            if (plaintiffPlayer != null) {
                plaintiffPlayer.teleport(tpLocation.getLocation());
            }
        }
        if (courtSession.getCaze().getDefendent() != null) {
            Player defendantPlayer = courtSession.getCaze().getDefendent().getPlayer();
            if (defendantPlayer != null) {
                defendantPlayer.teleport(tpLocation.getLocation());
            }
        }
    }
    public void silence() {
        long length = Courts.getCourts().getCourtsConfig().getSilenceLength();
        VoxEffects voxEffects = Courts.getCourts().getCourtsConfig().getSilenceCourtEffects();
        voxEffects.play(center.getLocation());
        ProtectedRegion region = getRegion();
        if (region == null) {
            System.out.println("Could not find region: " + regionName);
            return;
        }
        for (Player p : center.getLocation().getWorld().getPlayers()) {
            Location pLoc = p.getLocation();
            int x = pLoc.getBlockX();
            int y = pLoc.getBlockY();
            int z = pLoc.getBlockZ();
            if (region.contains(x,y,z)) {
                TitleUtil.sendTitle(p, ChatColor.RED + "Order!","Quiet in the court!",5, (int) length,10);
            }
        }
    }
    public List<Player> playersInRoom() {
        List<Player> inRoom = new ArrayList<>();
        ProtectedRegion region = getRegion();
        if (region == null) {
            System.out.println("Could not find region: " + regionName);
            return inRoom;
        }
        if (center != null && center.getLocation() != null) {
            for (Player p : center.getLocation().getWorld().getPlayers()) {
                if (p != null && p.isOnline() && p.getLocation() != null) {
                    Location pLoc = p.getLocation();
                    int x = (int) Math.round(pLoc.getX());
                    int y = (int) Math.round(pLoc.getY());
                    int z = (int) Math.round(pLoc.getZ());
                    if (region.contains(x,y,z)) {
                        inRoom.add(p);
                    }
                }
            }
        }
        return inRoom;
    }
    public void sendMessage(String... message) {
        if (message == null)
            return;
        for (Player p : playersInRoom()) {
            p.sendMessage(message);
        }
    }
    public boolean isInRoom(Location location) {
        ProtectedRegion region = getRegion();
        if (region == null) {
            System.out.println("region for " + name + " is null");
            return false;
        }
        if (location == null)
            return false;
        if (!location.getWorld().equals(center.getLocation().getWorld()))
            return false;
        return region.contains(location.getBlockX(),location.getBlockY(),location.getBlockZ());
    }
}
