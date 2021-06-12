package com.nicholasdoherty.socialcore.emotes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

/**
 * Created by john on 3/2/14.
 */
public class VanishWrapper {
	public static boolean isVanished(Player p) {
		for (MetadataValue meta : p.getMetadata("vanished")) {
			if (meta.asBoolean()) return true;
		}
		return false;
	}
}
