package com.nicholasdoherty.socialcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 8/10/14.
 */
public class VLocation implements ConfigurationSerializable{
    private String worldName;
    private double x,y,z;
    private Location location;

    public VLocation(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public VLocation(Location location) {
        this.location = location;
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public Location getLocation() {
        if (location == null) {
            World world = Bukkit.getWorld(worldName);
            if (world == null)
                return null;
            location = new Location(world,x,y,z);
        }
        return location;
    }
    public static VLocation fromString(String in) {
        String[] split = in.trim().split(",");
        String worldName = split[0];
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        return new VLocation(worldName,x,y,z);
    }
    public String toPrettyString() {
        return worldName +","+ (int)Math.round(x) +","+ (int)Math.round(y) +","+ (int)Math.round(z);
    }
    @Override
    public String toString() {
        return worldName+","+x+","+y+","+z;
    }
    public static VLocation valueOf(Map<String, Object> map) {
        return fromString((String) map.get("loc"));
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("loc",toString());
        return map;
    }
}
