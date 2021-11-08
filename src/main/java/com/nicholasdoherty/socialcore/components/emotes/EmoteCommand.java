package com.nicholasdoherty.socialcore.components.emotes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/11/13
 * Time: 23:51
 * To change this template use File | Settings | File Templates.
 */
@CommandAlias("emotes")
@CommandPermission("socialcore.emotes")
@Description("Manage emotes command and reloading.")
public class EmoteCommand extends BaseCommand {
	SocialCore socialCore;
	Emotes emotes;
	public EmoteCommand(SocialCore socialCore) {
		this.socialCore = socialCore;
		this.emotes = socialCore.emotes;
	}

	@Default
	public boolean onCommand(CommandSender commandSender, String[] strings) {
		if (commandSender.isOp() && strings.length == 1 && strings[0].equalsIgnoreCase("reload")) {
			emotes.reloadEmotes();
			commandSender.sendMessage(ChatColor.GREEN + "Reloaded emotes.");
			return true;
		}
		String base = emotes.getBaseEmoteCommandString();
		int page = 0;
		if (strings.length == 1) {
			try {
				page = Integer.parseInt(strings[0]);
			} catch (Exception e) {
				commandSender.sendMessage(ChatColor.RED + "Usage /emotes <page>");
				return true;
			}
		} else if(strings.length > 1) {
			commandSender.sendMessage(ChatColor.RED + "Usage /emotes <page>");
			return true;
		}

		if (page < 0)
			page = 0;
		base = ChatColor.translateAlternateColorCodes('&', base);
		commandSender.sendMessage(base);

		List<Emote> usableEmotes = new ArrayList<>();
		for (Emote emote : emotes.getEmoteMap().values()) {
			if (emote.getPermission() == null) {
				usableEmotes.add(emote);
			}else if (commandSender.hasPermission(emote.getPermission())) {
				usableEmotes.add(emote);
			}
		}
		Collections.sort(usableEmotes, new Emote.EmoteComparator());

		if (usableEmotes.size() == 0) {
			commandSender.sendMessage(ChatColor.GREEN + "None");
		}else {
			int perPage = 7;
			int lowerbound = (page*perPage);
			int upperbound = lowerbound + perPage;
			if (upperbound >= usableEmotes.size()) {
				upperbound = usableEmotes.size();
				if (upperbound < 0)
					upperbound = 0;
				if (lowerbound >= usableEmotes.size()) {
					lowerbound = upperbound-perPage;
					if (lowerbound < 0)
						lowerbound = 0;
				}
			}
			for (Emote emote : usableEmotes.subList(lowerbound,upperbound)) {
				String usage = emote.getUsage();
				usage = ChatColor.BLUE + ChatColor.translateAlternateColorCodes('&', usage);
				commandSender.sendMessage(usage);
			}
			if (usableEmotes.size()-1 >= upperbound) {
				commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6Type &c/emotes "+(page+1)+"&6 to read the next page."));
			}else {
				commandSender.sendMessage(ChatColor.GOLD + "No more emotes to show.");
			}
		}
		return true;
	}
}
