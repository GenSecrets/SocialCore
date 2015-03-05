import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.SupernaturalUtils;
import com.nicholasdoherty.socialcore.races.Race;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/*package com.nicholasdoherty.socialcore.races;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.SupernaturalUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/14/13
 * Time: 18:56
 * To change this template use File | Settings | File Templates.
 */
/*
public class RaceCommand implements CommandExecutor {
	SocialCore plugin;

	public RaceCommand(SocialCore plugin) {
		this.plugin = plugin;
		plugin.getCommand("races").setExecutor(this);
		plugin.getCommand("race").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if (!(commandSender instanceof Player)) {
			commandSender.sendMessage("This command only available to players.");
			return true;
		}
		Player p = (Player) commandSender;
		if (args.length == 1) {
			String subCommand = args[0];
			if (subCommand.equalsIgnoreCase("join")) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.races.commandStrings.get("join").get("usage")));
				return true;
			}else if (subCommand.equalsIgnoreCase("leave")) {
				SocialPlayer socialPlayer = plugin.save.getSocialPlayer(p.getName());
				if (socialPlayer.getRace() != null) {
					Race race = socialPlayer.getRace();
					socialPlayer.setRace(null);
					if (socialPlayer.getRace() != null) {
						if (socialPlayer.getRace().equals(race)) {
							p.sendMessage(ChatColor.RED + "You may not leave the default race!");
							return true;
						}
					}
					SocialCore.plugin.store.syncSocialPlayer(socialPlayer);
					race.unapplyRace(p);
					String message = plugin.races.commandStrings.get("leave").get("success");
					message = message.replace("<race>", race.getLoreName());
					message = ChatColor.translateAlternateColorCodes('&',message);
					p.sendMessage(message);
				} else {
					String message = plugin.races.commandStrings.get("leave").get("fail");
					message = ChatColor.translateAlternateColorCodes('&', message);
					p.sendMessage(message);
				}
				return true;
			}
		}
		if (args.length == 2) {
			String subCommand = args[0];
			if (subCommand.equalsIgnoreCase("join")) {
				if (commandSender.hasPermission("sc.race.issupernatural") || SupernaturalUtils.isSupernatural(p.getName())) {
					String message = plugin.races.commandStrings.get("join").get("supernatural");
					message = ChatColor.translateAlternateColorCodes('&', message);
					commandSender.sendMessage(message);
					return true;
				}
				String raceString = args[1].toLowerCase();
				Race race = plugin.races.getRaceByName(raceString);
				if (race == null) {
					String message = plugin.races.commandStrings.get("join").get("fail");
					message = message.replace("<race>", raceString);
					message = ChatColor.translateAlternateColorCodes('&', message);
					p.sendMessage(message);
					return true;
				}
				SocialPlayer socialPlayer = plugin.save.getSocialPlayer(p.getName());
				if (socialPlayer.getRace() != null) {
					if (socialPlayer.getRace().equals(race)) {
						p.sendMessage(plugin.races.commandStrings.get("join").get("already-race"));
						return true;
					}
				}

				if (race.getPermissionToChose() != null && !race.getPermissionToChose().equals("")) {
					if (!p.hasPermission(race.getPermissionToChose())) {
						String message = plugin.races.commandStrings.get("join").get("not-owned");
						message = message.replace("<race>", race.getLoreName());
						message = ChatColor.translateAlternateColorCodes('&', message);
						p.sendMessage(message);
						return true;
					}
				}
				long secondsSinceLast = (new Date().getTime()/1000)-SocialCore.plugin.save.getSocialPlayer(p.getName()).getLastRaceChange();
				if (secondsSinceLast < plugin.races.cooldown) {
					String message = plugin.races.commandStrings.get("join").get("cooldown");
					message = message.replace("<race>", race.getLoreName());
					long seconds = plugin.races.cooldown - secondsSinceLast;
					String time = String.format("%d Hours %d Minutes, %d Seconds",
							TimeUnit.SECONDS.toHours(seconds),
							TimeUnit.SECONDS.toMinutes(seconds - TimeUnit.HOURS.toSeconds(TimeUnit.SECONDS.toHours(seconds))),
							TimeUnit.SECONDS.toSeconds(seconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds - TimeUnit.HOURS.toSeconds(TimeUnit.SECONDS.toHours(seconds)))) - TimeUnit.SECONDS.toHours(seconds))
					);
					message = message.replace("<time>", time);
					message = ChatColor.translateAlternateColorCodes('&', message);
					p.sendMessage(message);
					return true;
				}
				if (socialPlayer.getRace() != null) {
					socialPlayer.getRace().unapplyRace(p);
				}
				socialPlayer.setRace(race.getName());
				socialPlayer.applyRace();
				long curTime = new Date().getTime()/1000;
				socialPlayer.setLastRaceChange(curTime);
				plugin.store.syncSocialPlayer(socialPlayer);
				String message = plugin.races.commandStrings.get("join").get("success");
				message = message.replace("<race>", race.getLoreName());
				message = ChatColor.translateAlternateColorCodes('&', message);
				p.sendMessage(message);
				if (race.getJoinInfo() != null) {
					p.sendMessage(race.getJoinInfo());
				}
				return true;
			} else if (subCommand.equalsIgnoreCase("info")) {
			 	String raceString = args[1].toLowerCase();
				Race race = plugin.races.getRace(raceString);
				if (race == null) {
					String message = plugin.races.commandStrings.get("info").get("no-race");
					message = ChatColor.translateAlternateColorCodes('&', message);
					message = message.replace("<race>", args[2]);
					p.sendMessage(message);
					return true;
				}
				String longInfo = race.getLongInfo();
				if (longInfo == null)
					longInfo = race.getShortInfo();
				if (longInfo == null)
					longInfo = "This race does not have info set.";
				longInfo = ChatColor.translateAlternateColorCodes('&', longInfo);
				p.sendMessage(longInfo);
				return true;
			}
		}
		if ((command.getName().equalsIgnoreCase("races") && args.length <= 2) || (command.getName().equalsIgnoreCase("race") && args.length >= 1 && args[0].equalsIgnoreCase("list"))) {
			List<Race> races = plugin.races.getRaces();
			Map<String, String> listCommand = plugin.races.commandStrings.get("list");
			int page = 0;
			if (command.getName().equalsIgnoreCase("races") && args.length ==1 && isInteger(args[0])) {
				page = Integer.valueOf(args[0])-1;
			} else if ((command.getName().equalsIgnoreCase("race") || command.getName().equalsIgnoreCase("races")) && args.length == 2 && args[0].equalsIgnoreCase("list") && isInteger(args[1])) {
				page = Integer.valueOf(args[1])-1;
			}
			if (page < 0)
				page = 0;
			int perPage = 5;
			int lowerbound = (page*perPage);
			int upperbound = lowerbound + perPage;
			if (upperbound >= races.size()) {
				upperbound = races.size();
				if (upperbound < 0)
					upperbound = 0;
				if (lowerbound >= races.size()) {
					lowerbound = upperbound-perPage;
					if (lowerbound < 0)
						lowerbound = 0;
				}
			}
			String baseMessage = listCommand.get("base-message");
			baseMessage = ChatColor.translateAlternateColorCodes('&', baseMessage);
			p.sendMessage(baseMessage);
			for (Race race : races.subList(lowerbound,upperbound)) {
				String message = listCommand.get("race-message");
				message = ChatColor.translateAlternateColorCodes('&', message);
				message = message.replace("<race>", ChatColor.BLUE + ChatColor.translateAlternateColorCodes('&',race.getLoreName()));
				message = message.replace("<short-info>", race.getShortInfo());
				commandSender.sendMessage(message);
			}
			if (races.size()-1 >= upperbound) {
				String message = listCommand.get("next-page");
				message = message.replace("<page>", Integer.toString(page+2));
				message = ChatColor.translateAlternateColorCodes('&', message);
				commandSender.sendMessage(message);
			}else {
				String message = listCommand.get("no-more");
				message = ChatColor.translateAlternateColorCodes('&', message);
				commandSender.sendMessage(message);
			}
			return true;
		}
		SocialPlayer socialPlayer = plugin.save.getSocialPlayer(p.getName());
		if (socialPlayer.getRace() != null)
			p.sendMessage(ChatColor.GOLD + "You are the race: " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&',socialPlayer.getRace().getLoreName()));
		p.sendMessage(ChatColor.BLUE + "Race commands:");
		for (Map<String, String> map : plugin.races.commandStrings.values()) {
			String message = map.get("usage");
			message = ChatColor.translateAlternateColorCodes('&', message);
			p.sendMessage(message);
		}
		return true;
	}
	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

}    */
