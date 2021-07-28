package com.nicholasdoherty.socialcore.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ErrorUtil {

    /**
     * @param sender - The object that triggered the command to be sent, typically console or player
     * @return - a boolean flag; false if is a player, true if they're not
     */
    public static boolean isNotPlayer(CommandSender sender) {
        if((sender instanceof Player)){
            return false;
        }
        sender.sendMessage("Only players may use this command from in game!");
        return true;
    }
}
