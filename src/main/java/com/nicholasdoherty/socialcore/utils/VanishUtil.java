package com.nicholasdoherty.socialcore.utils;

import com.earth2me.essentials.Essentials;
import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.entity.Player;

public class VanishUtil {
    public static boolean isVanished(final Player player) {
        if (player == null) return false;

        Essentials ess = (Essentials) SocialCore.getPlugin().getServer().getPluginManager().getPlugin("Essentials");

        return ess.getUser(player.getUniqueId()).isVanished();
    }

}
