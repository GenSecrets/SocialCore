package com.nicholasdoherty.socialcore.utils;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by john on 1/19/15.
 */
public class RegionUtil {
    public static Location center(ProtectedRegion protectedRegion) {
        World world = getRegionWorld(protectedRegion);
        BlockVector max = protectedRegion.getMaximumPoint();
        BlockVector min = protectedRegion.getMinimumPoint();
        int x = (max.getBlockX()+min.getBlockX())/2;
        int y = (max.getBlockY()+min.getBlockY())/2;
        int z = (max.getBlockZ()+min.getBlockZ())/2;
        return new Location(world,x,y,z);
    }
    public static World getRegionWorld(ProtectedRegion region) {
        for (World world : Bukkit.getWorlds()) {
            RegionManager regionManager = WGBukkit.getRegionManager(world);
            if (regionManager.getRegions().containsKey(region.getId())) {
                return world;
            }
        }
        return null;
    }
}
