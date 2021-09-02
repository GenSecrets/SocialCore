package com.nicholasdoherty.socialcore.components.courts.courtroom.voting;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtRoom;
import com.nicholasdoherty.socialcore.components.courts.courtroom.Restricter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by john on 1/21/15.
 */
public class RegionRestricted implements Restricter {
    private CourtRoom courtRoom;

    public RegionRestricted(CourtRoom courtRoom) {
        this.courtRoom = courtRoom;
    }

    @Override
    public boolean canVote(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null || !p.isOnline())
            return false;
        Location loc = p.getLocation();
        World world = loc.getWorld();
        if (courtRoom.getRegion() == null)
            return false;
        if (!courtRoom.getTpLocation().getLocation().getWorld().equals(world))
            return false;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        if (courtRoom.getRegion().contains(x,y,z)) {
            return true;
        }
        return false;
    }
    public RegionRestricted(Map<String, Object> map) {
        Courts courts = Courts.getCourts();
        String courtRoomId = (String) map.get("court-room-id");
        this.courtRoom = courts.getCourtsConfig().getCourtRoom(courtRoomId);
        if (this.courtRoom == null) {
            this.courtRoom = courts.getCourtsConfig().getDefaultCourtRoom();
        }
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("court-room-id",courtRoom.getName());
        return map;
    }
}