package com.nicholasdoherty.socialcore.welcomer;

import co.aikar.commands.BaseCommand;
import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class WelcomeCommandHandler extends BaseCommand {

    private final SocialCore sc;

    public WelcomeCommandHandler(SocialCore plugin){
        sc = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        Player player = (Player)sender;
        if (sc.welcomerLastJoined == null) {
            player.sendMessage(ChatColor.RED + "No one to be welcomed!");
            return true;
        }

        List<String> welcomeMessageList = sc.getWelcomerConfig().getStringList("welcome-messages");
        String welcomeMessage = ChatColor.translateAlternateColorCodes('&', (String) welcomeMessageList.get((new Random()).nextInt(welcomeMessageList.size())));
        if (player != null) {
            player.chat(welcomeMessage.replace("%player%", sc.welcomerLastJoined));
        }
        return true;
    }
}