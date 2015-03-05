package com.nicholasdoherty.socialcore.marriages;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by john on 2/1/15.
 */
public class StatusCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 1) {
            return false;
        }
        String name = strings[0];
        SocialPlayer socialPlayer = SocialCore.plugin.save.getSocialPlayer(name);
        if (socialPlayer == null) {
            commandSender.sendMessage(ChatColor.RED + "Could not find player: " + name);
            return true;
        }
        String message;
        if (socialPlayer.isMarried()) {
            message = ChatColor.GREEN + socialPlayer.getPlayerName() + " is lawfully married to " + socialPlayer.getMarriedTo();
        }else if (socialPlayer.isEngaged()) {
            message = ChatColor.GREEN + socialPlayer.getPlayerName() + " is engaged to " + socialPlayer.getEngagedTo();
        }else {
            message=  ChatColor.YELLOW + socialPlayer.getPlayerName() + " is not engaged or married";
        }
        commandSender.sendMessage(message);
        return true;
    }
}
