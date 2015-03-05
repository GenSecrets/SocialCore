package com.nicholasdoherty.socialcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by john on 1/11/15.
 */
public class UUIDUtil {
    public static UUID getUUID(String name) {
        Player p = Bukkit.getPlayer(name);
        if (p != null)
            return p.getUniqueId();
        try {
            UUID uuid = UUIDFetcher.getUUIDOf(name);
            return uuid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String prettyName(String name, UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            return p.getName();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer != null && offlinePlayer.getName() != null) {
            return offlinePlayer.getName();
        }
        return name;
    }
}
