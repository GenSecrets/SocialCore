package com.nicholasdoherty.socialcore.emotes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishCheck;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.VanishUser;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;


/**
 * Created by john on 3/2/14.
 */
public class VanishWrapper {
	public static boolean isVanished(Player p) {
		try {
			return VanishNoPacket.isVanished(p.getName());
		} catch (VanishNotLoadedException e) {
			e.printStackTrace();
		}
		return false;
	}
}
