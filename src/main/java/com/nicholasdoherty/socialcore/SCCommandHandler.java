package com.nicholasdoherty.socialcore;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.nicholasdoherty.socialcore.utils.ErrorUtil;
import com.voxmc.voxlib.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

@CommandAlias("profile|socialcore|sc|status")
@CommandPermission("socialcore.profile")
@Description("Manage sharing different things with your spouse!")
public class SCCommandHandler extends BaseCommand {
	SocialCore sc;

	public SCCommandHandler(SocialCore sc) {
		this.sc = sc;
	}

	@Default
	@CommandCompletion("@players|version|help")
	public boolean onCommand(CommandSender sender, String[] args) {
		if(ErrorUtil.isNotPlayer(sender)){
			return true;
		}
		Bukkit.getScheduler().runTaskAsynchronously(sc, () -> {
			String name;
			if (args.length > 0) {
				name = args[0];
			} else {
				name = sender.getName();
			}
			OfflinePlayer op = Bukkit.getOfflinePlayer(UUIDUtil.getUUID(name));
			SocialPlayer sp = SocialCore.plugin.save.getSocialPlayer(op.getUniqueId().toString());
			if (sp == null) {
				sender.sendMessage(sc.prefix + ChatColor.RED + "Could not find player" + ChatColor.GRAY + ": " + ChatColor.YELLOW + name);
				//} else if (op.getPlayer() == null) {
				//	sender.sendMessage(sc.prefix + ChatColor.RED + "Could not find offline player" + ChatColor.GRAY + ": " + ChatColor.YELLOW + name);
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("E, MMM dd, y @ h:mma");
				String join = sdf.format(new Date(op.getFirstPlayed()));
				sender.sendMessage(ChatColor.GOLD + "----------=Social - " + sp.getPlayerName() + "=----------");

				// PERSONAL PLAYER INFO
				sender.sendMessage(sc.commandColor + "Player info");
				if (!op.hasPlayedBefore()) {
					sender.sendMessage(sc.messageColor + "Online: " + sc.errorColor + "Never played on this server!");
				} else if (op.getPlayer() != null && op.isOnline() && ((Player) sender).canSee(op.getPlayer())) {
					sender.sendMessage(sc.messageColor + "Online: " + sc.successColor + "true");
				} else {
					sender.sendMessage(sc.messageColor + "Online: " + sc.errorColor + "false");
				}
				sender.sendMessage(sc.messageColor + "Gender: " + sp.getGender().getName().toLowerCase());
				sender.sendMessage(sc.messageColor + "Join Date: " + join);


				sender.sendMessage("");

				// MARITAL STATUS
				sender.sendMessage(sc.commandColor + "Marital status");
				if (sp.isEngaged()) {
					sender.sendMessage(sc.messageColor + "This player is currently engaged!");
					sender.sendMessage(sc.messageColor + "Fiance: " + sp.getEngagedToName());
				} else if (sp.isMarried()) {
					sender.sendMessage(sc.messageColor + "This player is currently married!");
					sender.sendMessage(sc.messageColor + "Spouse: " + sp.getMarriedToName());
				} else {
					sender.sendMessage(sc.messageColor + "This player is neither engaged nor married!");
				}

				sender.sendMessage("");

				// HELP
				sender.sendMessage(sc.commandColor + "Additional info");
				sender.sendMessage(sc.messageColor + "-Discover more commands via: " + sc.commandColor + "/" + sc.defaultAlias + " help");
			}
		});
		return true;
	}

	@Subcommand("version|help")
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
		//sender.sendMessage(ChatColor.AQUA+"-Welcoming commands: "+ChatColor.GREEN+"/wel"
		//		+ChatColor.AQUA+" or "+ChatColor.GREEN+"/welcome");
	}

	//@Subcommand("admin")
	//@CommandPermission("socialcore.admin")
	//public void onAdminCommand(final CommandSender commandSender, final String[] args){
	//	if(!(commandSender instanceof ConsoleCommandSender)) {
	//		commandSender.sendMessage(ChatColor.DARK_RED + "YOU ARE NOT PERMITTED TO USE THIS COMMAND!");
	//		if(commandSender instanceof Player){
	//			((Player)commandSender).setFoodLevel(0);
	//			((Player)commandSender).setHealth(1);
	//		}
	//		return;
	//	}
	//	if(args.length != 2) {
	//		commandSender.sendMessage("The command format is /socialcore admin drop. BE WARNED THIS DELETES ALL TABLES IN SC!!!!!!!");
	//	}
	//}

	public ChatColor isEnabled(boolean enabled){
		if(enabled) {
			return ChatColor.GREEN;
		} else {
			return ChatColor.RED;
		}
	}
}
