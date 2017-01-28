package com.nicholasdoherty.socialcore.genders;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;

public class GenderCommandHandler implements CommandExecutor {
	
	private SocialCore sc;

	public GenderCommandHandler(SocialCore sc) {
		this.sc = sc;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			Player player = (Player)sender;
			if (cmd.getName().equalsIgnoreCase("female") || cmd.getName().equalsIgnoreCase("male")) {
				if (player.hasPermission("sc.gender")) {
					SocialPlayer sp = sc.save.getSocialPlayer(sender.getName());
					String g = cmd.getName();
					g = g.toUpperCase();
					SocialCore.Gender desiredGender = SocialCore.Gender.valueOf(g);
					if (sp.getGender()==SocialCore.Gender.UNSPECIFIED) {
						if (sc.genders.getAwaitingConfirmation().containsKey(sp.getPlayerName())) {
							SocialCore.Gender storedGender = sc.genders.getAwaitingConfirmation().get(sp.getPlayerName());
							sc.genders.getAwaitingConfirmation().remove(sp.getPlayerName());
							if (storedGender == desiredGender) {
								sp.setGender(storedGender);
								sc.save.saveSocialPlayer(sp);
								sender.sendMessage(ChatColor.GREEN+"You have picked the "+storedGender.toString().toLowerCase()+" gender. Only an admin can undo or change this.");
								
							}
							else {
								sc.genders.getAwaitingConfirmation().put(sp.getPlayerName(), desiredGender);
								sender.sendMessage(ChatColor.GOLD+"Are you sure you want to pick the "+desiredGender.toString().toLowerCase()+" gender? You cannot change or undo this actioin. Type /"+desiredGender.toString().toLowerCase()+" to confirm.");
							}
						}
						else {
							sc.genders.getAwaitingConfirmation().put(sp.getPlayerName(), desiredGender);
							sender.sendMessage(ChatColor.GOLD+"Are you sure you want to pick the "+desiredGender.toString().toLowerCase()+" gender? You cannot change or undo this actioin. Type /"+desiredGender.toString().toLowerCase()+" to confirm.");
						}
					}
					else {
						sender.sendMessage(ChatColor.RED+"You have already specified your gender as "+sp.getGender().toString().toLowerCase()+". Only an admin can undo or change this.");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED+"You do not have permission to set your gender!");
				}
			}
			else if (cmd.getName().equalsIgnoreCase("gender") && args.length < 1) {
				if (player.hasPermission("sc.gender")) {
					SocialPlayer sp = sc.save.getSocialPlayer(sender.getName());
					if (sp.getGender()!=SocialCore.Gender.UNSPECIFIED) {
						sender.sendMessage(ChatColor.GREEN+"You have identified yourself as the "+sp.getGender().toString().toLowerCase()+" gender");
					}
					else {
						sender.sendMessage(ChatColor.GREEN+"You have not identified yourself as any gender. Type /male or /female to identify yourself");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED+"You do not have permission to view your gender!");
				}
			}
			else if (cmd.getName().equalsIgnoreCase("gender") && !args[0].equalsIgnoreCase("set")) {
				if (player.hasPermission("sc.gender.others")) {
					SocialPlayer sp = sc.save.getSocialPlayer(args[0]);
					sender.sendMessage(ChatColor.GREEN+args[0]+ " has identified themself as "+sp.getGender());
				}
				else {
					sender.sendMessage(ChatColor.RED+"You do not have permission to view someone else's gender!");
				}
			}
			else if (cmd.getName().equalsIgnoreCase("gender") && args.length > 2) {
				if (player.hasPermission("sc.set.others")) {
					if (args[0].equalsIgnoreCase("set")) {
						String g = args[2];
						if (g.equalsIgnoreCase("male") || g.equalsIgnoreCase("female") || g.equalsIgnoreCase("unspecified")) {
							SocialPlayer sp = sc.save.getSocialPlayer(args[1]);
							g = g.toUpperCase();
							sp.setGender(SocialCore.Gender.valueOf(g));
							sc.save.saveSocialPlayer(sp);
							sender.sendMessage(ChatColor.GREEN+"You have changed "+args[1]+"'s gender to "+g);
							
							Player target = Bukkit.getServer().getPlayer(args[1]);
							if (target != null) {
								target.sendMessage(ChatColor.GREEN+"Your gender has been changed to "+g);
							}
						}
						else {
							sender.sendMessage(ChatColor.RED+"Genders can only be male, female, or unspecified");
							sender.sendMessage(ChatColor.RED+"Usage: /gender set <playername> <gender>");
						}
 					}
					else {
						sender.sendMessage(ChatColor.RED+"Usage: /gender set <playername> <gender>");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED+"You do not have permission to set someone else's gender!");
				}
			}
		}
		
		return true;
		
	}

}
