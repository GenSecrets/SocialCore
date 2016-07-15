package com.nicholasdoherty.socialcore.emotes;

import com.nicholasdoherty.socialcore.NearbyAPI;
import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/11/13
 * Time: 23:16
 * To change this template use File | Settings | File Templates.
 */
public class EmoteListener implements Listener{
	SocialCore plugin;
	Emotes emotes;
	Map<String, Long> onCooldown = new HashMap<String, Long>();
	public EmoteListener(SocialCore plugin) {
		this.plugin = plugin;
		emotes = plugin.emotes;
		plugin.getServer().getPluginManager().registerEvents(this,plugin);
	}
	@EventHandler(priority =  EventPriority.LOWEST)
	public void emoteCommand(PlayerCommandPreprocessEvent event) {
		String commandString = event.getMessage();
		if (commandString.length() < 1)
			return;
		String[] args = commandString.split(" ");
		if (args.length == 0)
			return;
		String command = args[0].toLowerCase();
		Emote emote = emotes.getEmote(command);
		if (emote == null)
			return;
		event.setCancelled(true);
		Player p = event.getPlayer();
		if (onCooldown.containsKey(p.getName()) && !p.hasPermission("sc.emotes.bypasscooldown")) {
			long time = new Date().getTime();
			int secondsElapsed = Math.round((time - onCooldown.get(p.getName()))/1000);
			int secondsLeft = plugin.emotes.cooldown - secondsElapsed;
			p.sendMessage(ChatColor.RED + "You must wait " + secondsLeft + " seconds before using another emote.");
			return;
		}
		if (!p.hasPermission("sc.emotes")) {
			p.sendMessage(ChatColor.RED + "You do not have permission to use emotes!");
			return;
		}
		if (emote.getPermission() != null) {
			if (!p.hasPermission(emote.getPermission())) {
				p.sendMessage(ChatColor.RED + "You do not have permission: "+emote.getPermission()+" required to use this emote!");
				return;
			}
		}
		if (args.length == 2) {
			if (!emote.isCanBetargeted()) {
				p.sendMessage(ChatColor.RED + "This emote may be not be targeted.");
				return;
			}
			Player target = Bukkit.getPlayer(args[1]);
			if (target == null || !target.isOnline() || VanishWrapper.isVanished(target)) {
				p.sendMessage(ChatColor.RED + "Target " + args[1] + " is not currently online.");
				return;
			}
			if (target.equals(p)) {
				p.sendMessage(ChatColor.RED + "You may not target yourself.");
				return;
			}
			if (!target.getWorld().getName().equals(p.getWorld().getName()))  {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat player is too far away from you."));
				return;
			}
			if (emotes.targetMustBeInRange) {
				if (p.getLocation().distanceSquared(target.getLocation()) > Math.pow(emotes.radius,2)) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat player is too far away from you."));
					return;
				}
			}
			if (emote.targetBlacklist.contains(target.getName())) {
				boolean hasPermission = false;
				if (emote.permissionToBypassTargetBlacklist != null) {
					if (p.hasPermission(emote.permissionToBypassTargetBlacklist))
						hasPermission = true;
				}
				if (!hasPermission) {
					p.sendMessage(ChatColor.RED + "You are not allowed to send " + target.getName() + " this emote.");
					return;
				}
			}
			String message = emote.getEmoteMessageTargeted(p, target);
			sendMessage(p,message);
			return;
		}
		if (args.length == 1) {
			if (!emote.isCanBeUntargeted()) {
				p.sendMessage(ChatColor.RED + "This emote must be targeted.");
				return;
			}
			String message = emote.getEmoteMessage(p);
			sendMessage(p,message);
			return;
		}
		p.sendMessage("Usage: " + emote.getUsage());
		return;
	}
	public void sendMessage(Player p, String message) {
		int radius = SocialCore.plugin.emotes.getRadius();
		World world = p.getWorld();
		if (radius == 0) {
			for (Player toSend : world.getPlayers()) {
				toSend.sendMessage(message);
			}
		} else {
			for (Player e : NearbyAPI.getNearbyPlayers(p.getLocation(), radius)) {
				if (e.getType().equals(EntityType.PLAYER)) {
					Player toSend = (Player) e;
					toSend.sendMessage(message);
				}
			}
			//p.sendMessage(message);
			final String pName = p.getName();
			long time = new Date().getTime();
			onCooldown.put(p.getName(), time);
			new BukkitRunnable() {
				@Override
				public void run() {
					onCooldown.remove(pName);
				}
			}.runTaskLater(plugin, plugin.emotes.cooldown*20);
			return;
		}
	}
}
