package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CourtReloadCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    public CourtReloadCmd(Courts courts, CommandSender commandSender) {
        this.courts = courts;
        this.commandSender = commandSender;
    }

    public boolean runCommand(){
        courts.reloadConfig();
        commandSender.sendMessage(ChatColor.GREEN + "Reloaded config.yml for courts");
        return true;
    }
}
