package com.nicholasdoherty.socialcore.courts.stall;

import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/6/15.
 */
public abstract class Stall implements ConfigurationSerializable{
    private StallType stallType;
    private VLocation vLocation;

    public Stall(StallType stallType, VLocation vLocation) {
        this.stallType = stallType;
        this.vLocation = vLocation;
    }

    public StallType getStallType() {
        return stallType;
    }

    public Location getLocation() {
        return vLocation.getLocation();
    }
    public boolean isStall(Location loc) {
        if (loc == null)
            return false;
        Location stallLoc = vLocation.getLocation();
        if (stallLoc == null)
            return false;
        return stallLoc.equals(loc);
    }
    public abstract void onClick(Player p);
    public Stall(Map<String, Object> map) {
        this.stallType = StallType.valueOf((String) map.get("stall-type"));
        this.vLocation = (VLocation) map.get("loc");
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("stall-type",stallType.toString());
        map.put("loc",vLocation);
        return map;
    }
}
