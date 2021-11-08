package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CourtSilenceCmd {
    private final Courts courts;
    private final CommandSender commandSender;

    public CourtSilenceCmd(Courts courts, CommandSender commandSender) {
        this.courts = courts;
        this.commandSender = commandSender;
    }

    public boolean runCommand(){
        courts.getCourtsConfig().getDefaultCourtRoom().silence();
        commandSender.sendMessage(ChatColor.GREEN + "Sent silence message to the court room.");
        return true;
    }
}
