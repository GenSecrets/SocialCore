package com.nicholasdoherty.socialcore.misc;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.nicholasdoherty.socialcore.SocialCore;

/**
 * A global mute.
 *
 * @author Incomp
 * @since Oct 29, 2016
 */
public class GlobalMute implements CommandExecutor, Listener {
	
	public static final String PERM_USE = "sc.globalmute.use";
	public static final String PERM_BYPASS = "sc.globalmute.bypass";
	
	// Commands here will be blocked if a mute is active. Don't include the slash.
	private final List<String> blacklistedCommands = Arrays.asList("");
	
	private boolean active = false;
	
	public GlobalMute(SocialCore core){
		Bukkit.getPluginManager().registerEvents(this, core);
		core.getCommand("globalmute").setExecutor(this);
	}
	
	public boolean isActive(){
		return active;
	}
	
	@Override
	public boolean onCommand(CommandSender sn, Command cmd, String lbl, String[] args) {
		if(sn.hasPermission(PERM_USE)){
			if(args.length > 0){
				sn.sendMessage(this.color("&4GlobalMute &l> &7Too many arguments. Just use &e/globalmute&7."));
				return true;
			}else{
				final String sendName = (sn instanceof Player) ? ((Player) sn).getName() : "Console";
				if(this.active){ // Chat is globally muted
					this.active = false;
					Bukkit.broadcastMessage(this.color("&aGlobalMute &l> &7Chat has been &aunmuted &7by &e" + sendName + "&7."));
					return true;
				}else{ // Chat is not globally muted
					this.active = true;
					Bukkit.broadcastMessage(this.color("&cGlobalMute &l> &7Chat has been &amuted &7by &e" + sendName + "&7."));
					return true;
				}
			}
		}else{
			sn.sendMessage(this.color("&4GlobalMute &l> &7No access."));
			return true;
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		if(this.active){
			if(event.getPlayer().hasPermission(PERM_BYPASS)) return;
			// Check for blacklisted commands and block whatever is necessary.
			if(event.getMessage().startsWith("/")){
				String check = event.getMessage().replaceFirst("/", "");
				if(this.blacklistedCommands.contains(check)){
					event.setCancelled(true);
					event.getPlayer().sendMessage(this.color("&4GlobalMute &l> &7Chat is currently muted."));
					return;
				}else return;
			}
			if(!event.isCancelled()){
				event.setCancelled(true);
				event.getPlayer().sendMessage(this.color("&4GlobalMute &l> &7Chat is currently muted."));
				return;
			}else return;
		}else return;
	}
	
	// For the sake of convenience. I'll delete this and use whatever util class has a similar method whenever I find one.
	private String color(String input){
		return ChatColor.translateAlternateColorCodes('&', input);
	}
}
