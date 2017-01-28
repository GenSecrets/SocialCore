package com.nicholasdoherty.socialcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by john on 2/15/15.
 */
public class PlayerUtil {
    public static long lastOnlineTime(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer == null)
            return -1;
        return offlinePlayer.getLastPlayed();
    }
    public static long timeSinceLastOnline(UUID uuid) {
        long lastOnline = lastOnlineTime(uuid);
        if (lastOnline == -1)
            return -1;
        return new Date().getTime() - lastOnline;
    }
    public static List<Player> playersAround(Location loc, double rad) {
        double radSquared = rad*rad;
        List<Player> players = new ArrayList<>();
        for (Player p : loc.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(loc) <= radSquared) {
                players.add(p);
            }
        }
        return players;
    }
}
