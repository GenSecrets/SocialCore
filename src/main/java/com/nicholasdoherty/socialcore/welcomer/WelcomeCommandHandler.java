package com.nicholasdoherty.socialcore.welcomer;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;


@CommandAlias("welcome|wel")
@CommandPermission("socialcore.welcome")
@Description("Welcome new players to the server!")
public class WelcomeCommandHandler extends BaseCommand {

    private final SocialCore sc;

    public WelcomeCommandHandler(SocialCore plugin){
        sc = plugin;
    }

    @Default
    public boolean onCommand(CommandSender sender) {
        if(sender instanceof Player){
            Player player = (Player)sender;
            if (sc.welcomerLastJoined == null) {
                player.sendMessage(ChatColor.RED + "No one to be welcomed!");
                return true;
            }

            List<String> welcomeMessageList = sc.getWelcomerConfig().getStringList("welcome-messages");
            String randomWelcomeMsg =welcomeMessageList.get((new Random()).nextInt(welcomeMessageList.size()));
            String welcomeMessage = ChatColor.translateAlternateColorCodes('&', randomWelcomeMsg);
            player.chat(welcomeMessage.replace("%player%", sc.welcomerLastJoined));
        } else {
            sender.sendMessage("This command must be ran by a player in game!");
        }
        return true;
    }
}