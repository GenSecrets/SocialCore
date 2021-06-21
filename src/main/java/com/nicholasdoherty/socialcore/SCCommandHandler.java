package com.nicholasdoherty.socialcore;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SCCommandHandler implements CommandExecutor{
	
	private SocialCore sc;

	public SCCommandHandler(SocialCore sc) {
		this.sc = sc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		StringBuilder authors = new StringBuilder();
		for (String author: sc.getDescription().getAuthors()) {
			authors.append(author).append(", ");
		}
		if (args.length > 0) {
			String name = args[0];
			SocialPlayer sp = SocialCore.plugin.save.getSocialPlayer(name);
			if (sp == null) {
				sender.sendMessage(ChatColor.RED + "Could not find player: " + name);
			} else {
				OfflinePlayer op = sc.getServer().getOfflinePlayer(sp.getPlayerName());
				SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd, y @ h:mma");
				String join = sdf.format(new Date(op.getFirstPlayed()));
				sender.sendMessage(ChatColor.GOLD+"----------=Social - "+sp.getPlayerName()+"=----------");

				// PERSONAL PLAYER INFO
				sender.sendMessage(ChatColor.YELLOW +"Player info");
				if(op.isOnline() && ((Player)sender).canSee(op.getPlayer())){
					sender.sendMessage(ChatColor.AQUA+"Online: "+ChatColor.GREEN+"true");
				} else {
					sender.sendMessage(ChatColor.AQUA+"Online: "+ChatColor.RED+"false");
				}
				sender.sendMessage(ChatColor.AQUA+"Gender: "+sp.getGender().toString().toLowerCase());
				sender.sendMessage(ChatColor.AQUA+"Join Date: "+join);


				sender.sendMessage("");

				// MARITAL STATUS
				sender.sendMessage(ChatColor.YELLOW +"Marital status");
				if(sp.isEngaged()){
					sender.sendMessage(ChatColor.AQUA+"This player is currently engaged!");
					sender.sendMessage(ChatColor.AQUA+"Fiance: "+sp.getEngagedTo());
				} else if(sp.isMarried()){
					sender.sendMessage(ChatColor.AQUA+"This player is currently married!");
					sender.sendMessage(ChatColor.AQUA+"Spouse: "+sp.getMarriedTo());
				} else {
					sender.sendMessage(ChatColor.AQUA+"This player is neither engaged nor married!");
				}
			}
			return true;
		}

		sender.sendMessage(ChatColor.GOLD+"----------=Social - "+sender.getName()+"=----------");
		sender.sendMessage(ChatColor.YELLOW+"Plugin Info");
		sender.sendMessage(ChatColor.AQUA+"-Name: "+sc.getDescription().getName());
		sender.sendMessage(ChatColor.AQUA+"-Authors: "+authors.substring(0, authors.length()-2));
		sender.sendMessage(ChatColor.AQUA+"-Version: "+sc.getDescription().getVersion());
		sender.sendMessage(ChatColor.AQUA+"-Depends: "+sc.getDescription().getDepend());
		sender.sendMessage("");
		sender.sendMessage(ChatColor.YELLOW+"Commands");
		sender.sendMessage(ChatColor.AQUA+"-View your profile: "+ChatColor.GREEN+"/socialcore <name>");
		sender.sendMessage(ChatColor.AQUA+"-View marriage commands: "+ChatColor.GREEN+"/marriage");
		sender.sendMessage(ChatColor.AQUA+"-View gender commands: "+ChatColor.GREEN+"/gender");
		return true;
		
	}

}
