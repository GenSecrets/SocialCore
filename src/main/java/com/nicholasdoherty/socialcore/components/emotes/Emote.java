package com.nicholasdoherty.socialcore.components.emotes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.components.emotes.extend.EmoteExtender;
import com.nicholasdoherty.socialcore.components.genders.Gender;
import com.nicholasdoherty.socialcore.utils.NearbyAPI;
import com.nicholasdoherty.socialcore.utils.VanishUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/11/13
 * Time: 23:00
 * To change this template use File | Settings | File Templates.
 */
@CommandAlias("%emote")
public class Emote extends BaseCommand {
	String name,command,permission,emoteString,emoteStringTargeted,usage,emoteStringMale,permissionToBypassTargetBlacklist,emoteStringFemale,emoteStringMaleTargeted,emoteStringFemaleTargeted;
	boolean canBeTargeted;
	boolean canBeUnTargeted;
	List<String> targetBlacklist;
	Map<String, Long> onCooldown = new HashMap<>();
	private final EmoteExtender extender = new EmoteExtender();

	public Emote(String name, String command, String permission, String emoteString, String emoteStringTargeted, String usage, String emoteStringMale, String emoteStringFemale, String emoteStringMaleTargeted, String emoteStringFemaleTargeted, List<String> targetBlacklist, String permissionToBypassTargetBlacklist) {
		this.name = name;
		this.command = command;
		this.permission = permission;
		this.emoteString = emoteString;
		this.emoteStringTargeted = emoteStringTargeted;
		this.usage = usage;
		this.emoteStringMale = emoteStringMale;
		this.emoteStringFemale = emoteStringFemale;
		this.emoteStringMaleTargeted = emoteStringMaleTargeted;
		this.emoteStringFemaleTargeted = emoteStringFemaleTargeted;
		if (emoteStringTargeted != null || (emoteStringMaleTargeted != null && emoteStringFemaleTargeted !=null))
			canBeTargeted = true;
		if (emoteString != null || (emoteStringFemale != null && emoteStringMale != null))
			canBeUnTargeted =true;
		this.targetBlacklist = targetBlacklist;
		this.permissionToBypassTargetBlacklist = permissionToBypassTargetBlacklist;
	}

	public Emote(String name, String command, String permission, String emoteString, String usage) {
		this.name = name;
		this.command = command;
		this.permission = permission;
		this.emoteString = emoteString;
		this.usage = usage;
		canBeTargeted = false;
		canBeUnTargeted = true;
	}

	public boolean isCanBeUntargeted() {
		return canBeUnTargeted;
	}

	public boolean isCanBetargeted() {
		return canBeTargeted;
	}

	public String getEmoteStringMale() {
		return emoteStringMale;
	}

	public String getEmoteStringFemale() {
		return emoteStringFemale;
	}

	public String getEmoteStringMaleTargetted() {
		return emoteStringMaleTargeted;
	}

	public String getEmoteStringFemaleTargeted() {
		return emoteStringFemaleTargeted;
	}

	public String getName() {
		return name;
	}

	public String getCommmand() {
		return command;
	}

	public String getPermission() {
		return permission;
	}

	public String getEmoteString() {
		return emoteString;
	}

	public String getEmoteStringTargetted() {
		return emoteStringTargeted;
	}

	@Default
	@CommandCompletion("@players")
	public void onCmd(final CommandSender commandSender, final String[] args) {
		final Emote emote = SocialCore.getPlugin().emotes.getEmote(getCommmand());
		if(emote == null) {
			return;
		}

		Player p = null;
		if(commandSender instanceof Player) {
			p = (Player) commandSender;
		}
		if (p == null){
			return;
		}

		if(onCooldown.containsKey(p.getName()) && !p.hasPermission("sc.emotes.bypasscooldown")) {
			final long time = new Date().getTime();
			final int secondsElapsed = Math.round((time - onCooldown.get(p.getName())) / 1000);
			final int secondsLeft = SocialCore.getPlugin().emotes.cooldown - secondsElapsed;
			p.sendMessage(ChatColor.RED + "You must wait " + secondsLeft + " seconds before using another emote.");
			return;
		}
		if(!p.hasPermission("sc.emotes")) {
			p.sendMessage(ChatColor.RED + "You do not have permission to use emotes!");
			return;
		}
		if(emote.getPermission() != null) {
			if(!p.hasPermission(emote.getPermission())) {
				p.sendMessage(ChatColor.RED + "You do not have permission: " + emote.getPermission() + " required to use this emote!");
				return;
			}
		}
		if(args.length == 1) {
			if(!emote.isCanBetargeted()) {
				p.sendMessage(ChatColor.RED + "This emote may be not be targeted.");
				return;
			}
			final Player target = Bukkit.getPlayer(args[0]);
			if(target == null || !target.isOnline() || VanishUtil.isVanished(target)) {
				p.sendMessage(ChatColor.RED + "Target " + args[0] + " is not currently online.");
				return;
			}
			if(target.equals(p)) {
				p.sendMessage(ChatColor.RED + "You may not target yourself.");
				return;
			}
			if(!target.getWorld().getName().equals(p.getWorld().getName())) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat player is too far away from you."));
				return;
			}
			if(SocialCore.getPlugin().emotes.targetMustBeInRange) {
				if(p.getLocation().distanceSquared(target.getLocation()) > Math.pow(SocialCore.getPlugin().emotes.radius, 2)) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat player is too far away from you."));
					return;
				}
			}
			if(emote.targetBlacklist.contains(target.getName())) {
				boolean hasPermission = false;
				if(emote.permissionToBypassTargetBlacklist != null) {
					if(p.hasPermission(emote.permissionToBypassTargetBlacklist)) {
						hasPermission = true;
					}
				}
				if(!hasPermission) {
					p.sendMessage(ChatColor.RED + "You are not allowed to target " + target.getName() + " for this emote.");
					return;
				}
			}
			final String message = emote.getEmoteMessageTargeted(p, target);
			sendMessage(p, message);
			return;
		}
		if(args.length == 0) {
			if(!emote.isCanBeUntargeted()) {
				p.sendMessage(ChatColor.RED + "This emote must be targeted.");
				return;
			}
			final String message = emote.getEmoteMessage(p);
			sendMessage(p, extender.process(message));
			return;
		}
		p.sendMessage("Usage: " + emote.getUsage());
	}

	public String getEmoteMessage(Player p){
		SocialPlayer socialPlayer = SocialCore.plugin.save.getSocialPlayer(p.getUniqueId().toString());
		Gender gender = socialPlayer.getGender();
		String message = emoteString;
		if (gender.getName().equalsIgnoreCase("male") && emoteStringMale != null)
			message = emoteStringMale;
		else if (gender.getName().equalsIgnoreCase("female") && emoteStringFemale != null)
			message = emoteStringFemale;
	    message = ChatColor.translateAlternateColorCodes('&', message);
		message = message.replace("<player>", p.getName());
		for (Replacement replacement : SocialCore.plugin.emotes.getReplacements()) {
			message = message.replace(replacement.getKey(), replacement.getReplacement(gender));
		}
		return message;
	}
	public String getEmoteMessageTargeted(Player p, Player targeted) {
		SocialPlayer sender = SocialCore.plugin.save.getSocialPlayer(p.getUniqueId().toString());
		SocialPlayer reciv = SocialCore.plugin.save.getSocialPlayer(targeted.getUniqueId().toString());
		Gender gender = new Gender("UNSPECIFIED");
		String message = emoteStringTargeted;
		if (!sender.getGender().getName().equalsIgnoreCase("unspecified") || !sender.getGender().getName().equalsIgnoreCase("null"))
			gender = sender.getGender();
		else if (!reciv.getGender().equals(gender))
			gender = reciv.getGender();
		if (sender.getGender().getName().equalsIgnoreCase("male") && emoteStringMaleTargeted != null)
			message = emoteStringMaleTargeted;
		else if (sender.getGender().getName().equalsIgnoreCase("female") && emoteStringFemaleTargeted != null)
			message = emoteStringFemaleTargeted;
		message = ChatColor.translateAlternateColorCodes('&', message);
		message = message.replace("<player>", p.getName());
		message = message.replace("<target>", targeted.getName());
		for (Replacement replacement : SocialCore.plugin.emotes.getReplacements()) {
			message = message.replace(replacement.getKey(), replacement.getReplacement(gender));
		}
		return message;
	}

	@SuppressWarnings("TypeMayBeWeakened")
	public void sendMessage(final Player p, final String message) {
		final int radius = SocialCore.getPlugin().emotes.getRadius();
		final World world = p.getWorld();
		if(radius == 0) {
			for(final Player toSend : world.getPlayers()) {
				toSend.sendMessage(message);
			}
		} else {
			for(final Player e : NearbyAPI.getNearbyPlayers(p.getLocation(), radius)) {
				if(e.getType() == EntityType.PLAYER) {
					e.sendMessage(message);
				}
			}
			final String pName = p.getName();
			final long time = new Date().getTime();
			onCooldown.put(p.getName(), time);
			new BukkitRunnable() {
				@Override
				public void run() {
					onCooldown.remove(pName);
				}
			}.runTaskLater(SocialCore.getPlugin(), SocialCore.getPlugin().emotes.cooldown * 20);
		}
	}

	public String getUsage() {
		return usage;
	}

	public static class EmoteComparator implements Comparator<Emote> {
		@Override
		public int compare(Emote o1, Emote o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
}
