package com.nicholasdoherty.socialcore;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

@CommandAlias("profile|socialcore|sc|status")
@CommandPermission("socialcore.profile")
@Description("Manage sharing different things with your spouse!")
public class SCCommandHandler extends BaseCommand {
	
	private SocialCore sc;

	public SCCommandHandler(SocialCore sc) {
		this.sc = sc;
	}

	@Default
	public boolean onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("You can only use this command in game!");
			return true;
		}
		Player player = (Player)sender;
		String name;
		if(args.length > 0){
			name = args[0];
		} else {
			name = sender.getName();
		}
		SocialPlayer sp = SocialCore.plugin.save.getSocialPlayer(name);
		if (sp == null) {
			sender.sendMessage(ChatColor.RED + "Could not find player: " + name);
		} else {
			OfflinePlayer op = sc.getServer().getOfflinePlayer(sp.getPlayerName());
			SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd, y @ h:mma");
			String join = sdf.format(new Date(op.getFirstPlayed()));
			sender.sendMessage(ChatColor.GOLD+"----------=Social - "+sp.getPlayerName()+"=----------");

			// PERSONAL PLAYER INFO
			sender.sendMessage(sc.commandColor +"Player info");
			if(op.isOnline() && ((Player)sender).canSee(op.getPlayer())){
				sender.sendMessage(sc.messageColor+"Online: "+sc.successColor+"true");
			} else {
				sender.sendMessage(sc.messageColor+"Online: "+sc.errorColor+"false");
			}
			sender.sendMessage(sc.messageColor+"Gender: "+sp.getGender().getName().toLowerCase());
			sender.sendMessage(sc.messageColor+"Join Date: "+join);


			sender.sendMessage("");

			// MARITAL STATUS
			sender.sendMessage(sc.commandColor +"Marital status");
			if(sp.isEngaged()){
				sender.sendMessage(sc.messageColor+"This player is currently engaged!");
				sender.sendMessage(sc.messageColor+"Fiance: "+sp.getEngagedTo());
			} else if(sp.isMarried()){
				sender.sendMessage(sc.messageColor+"This player is currently married!");
				sender.sendMessage(sc.messageColor+"Spouse: "+sp.getMarriedTo());
			} else {
				sender.sendMessage(sc.messageColor+"This player is neither engaged nor married!");
			}

			sender.sendMessage("");

			// HELP
			sender.sendMessage(sc.commandColor + "Additional info");
			sender.sendMessage(sc.messageColor + "-Discover more commands via: " + sc.commandColor + "/"+sc.defaultAlias+" help");
		}
		return true;
		
	}

	@Subcommand("version")
	@CommandAlias("help")
	@CommandPermission("socialcore.profile.version")
	public void onVersion(CommandSender sender){
		StringBuilder authors = new StringBuilder();
		for (String author: sc.getDescription().getAuthors()) {
			authors.append(author).append(", ");
		}
		StringBuilder depends = new StringBuilder();
		for (String depend: sc.getDescription().getDepend()) {
			depends.append(depend).append(", ");
		}

		sender.sendMessage(ChatColor.GOLD+"----------=Social - "+sender.getName()+"=----------");
		sender.sendMessage(ChatColor.YELLOW+"Plugin Info");
		sender.sendMessage(ChatColor.AQUA+"-Name: "+ChatColor.GRAY+sc.getDescription().getName());
		sender.sendMessage(ChatColor.AQUA+"-Authors: "+ChatColor.GRAY+authors.substring(0, authors.length()-2));
		sender.sendMessage(ChatColor.AQUA+"-Version: "+ChatColor.GRAY+sc.getDescription().getVersion());
		sender.sendMessage(ChatColor.AQUA+"-Depends: "+ChatColor.GRAY+depends.substring(0, depends.length()-2));
		sender.sendMessage(ChatColor.AQUA+"-Components ("+ChatColor.GREEN+"Enabled "+ChatColor.RED+"Disabled"+ChatColor.AQUA+")");
		sender.sendMessage((isEnabled(sc.isCourtsEnabled))+"<Courts> " + (isEnabled(sc.isMarriagesEnabled))+"<Marriages> " + (isEnabled(sc.isGendersEnabled))+"<Genders> " + (isEnabled(sc.isWelcomerEnabled))+"<Welcomer>");
		sender.sendMessage("");
		sender.sendMessage(ChatColor.YELLOW+"Commands");
		sender.sendMessage(ChatColor.AQUA+"-View your profile: "+ChatColor.GREEN+"/"+sc.defaultAlias+" <name>");
		sender.sendMessage(ChatColor.AQUA+"-View court commands: "+ChatColor.GREEN+"/court");
		sender.sendMessage(ChatColor.AQUA+"-View marriage commands: "+ChatColor.GREEN+"/marriage");
		sender.sendMessage(ChatColor.AQUA+"-View gender commands: "+ChatColor.GREEN+"/gender");
		sender.sendMessage(ChatColor.AQUA+"-Welcoming commands: "+ChatColor.GREEN+"/wel"
				+ChatColor.AQUA+" or "+ChatColor.GREEN+"/welcome");
	}

	public ChatColor isEnabled(boolean enabled){
		if(enabled) {
			return ChatColor.GREEN;
		} else {
			return ChatColor.RED;
		}
	}
}
