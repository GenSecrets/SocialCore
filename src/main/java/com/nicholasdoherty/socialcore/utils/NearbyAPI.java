package com.nicholasdoherty.socialcore.utils;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by john on 4/29/15.
 */
public class NearbyAPI {
    public static Set<Player> getNearbyPlayers(Location loc, double distance) {
        Set<Player> nearby = new HashSet<Player>();
        double distanceSquared = Math.pow(distance,2);
        if (loc == null)
            return nearby;
        for (Player p : loc.getWorld().getPlayers()) {
            Location pLoc = p.getLocation();
            if (pLoc != null && pLoc.getWorld().equals(loc.getWorld()) && pLoc.distanceSquared(loc) <= distanceSquared) {
                nearby.add(p);
            }
        }
        return nearby;
    }
    public static Set<LivingEntity> getNearbyLivingEntities(Location loc, double distance) {
        Set<LivingEntity> nearby = new HashSet<LivingEntity>();
        double distanceSquared = Math.pow(distance,2);
        if (loc == null)
            return nearby;
        for (LivingEntity p : loc.getWorld().getLivingEntities()) {
            Location pLoc = p.getLocation();
            if (pLoc != null && pLoc.getWorld().equals(loc.getWorld()) && pLoc.distanceSquared(loc) <= distanceSquared) {
                nearby.add(p);
            }
        }
        return nearby;
    }
}
