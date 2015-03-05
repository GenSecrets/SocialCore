package com.nicholasdoherty.socialcore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class SCCommandHandler implements CommandExecutor{
	
	private SocialCore sc;

	public SCCommandHandler(SocialCore sc) {
		this.sc = sc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("me")) {
				SocialPlayer sp = sc.save.getSocialPlayer(sender.getName());
				sender.sendMessage(ChatColor.GOLD+"----------=SocialCore - "+sp.getPlayerName()+"=----------");
				sender.sendMessage(ChatColor.AQUA+"Gender: "+sp.getGender().toString().toLowerCase());
				sender.sendMessage(ChatColor.AQUA+"Engaged: "+sp.isEngaged());
				sender.sendMessage(ChatColor.AQUA+"Engaged to: "+sp.getEngagedTo());
				sender.sendMessage(ChatColor.AQUA+"Married: "+sp.isMarried());
				sender.sendMessage(ChatColor.AQUA+"Married to: "+sp.getMarriedTo());
				return true;
			}
		}
		
		sender.sendMessage(ChatColor.GREEN+"This server is running SocialCore v"+sc.getDescription().getVersion()+" by "+sc.getDescription().getAuthors());
		sender.sendMessage(ChatColor.YELLOW+"Type /marriage to view marriage commands.");
		sender.sendMessage(ChatColor.YELLOW+"Type /socialcore me to view your social status");
		return true;
		
	}

}
