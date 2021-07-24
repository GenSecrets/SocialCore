package com.nicholasdoherty.socialcore.marriages;

import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by john on 2/1/15.
 */
public class PurgeInvalidCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Thread thread = new Thread(){
            public void run(){
                SocialCore.plugin.save.purgeInvalids();
                commandSender.sendMessage(ChatColor.GREEN + "Removed invalid marriages.");
            }
        };
        thread.start();
        return true;
    }
}
