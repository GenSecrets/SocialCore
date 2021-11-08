package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import org.bukkit.command.CommandSender;

public class CourtEndSessionsCmd {
    private final Courts courts;
    private final CommandSender commandSender;

    public CourtEndSessionsCmd(Courts courts, CommandSender commandSender) {
        this.courts = courts;
        this.commandSender = commandSender;
    }

    public boolean runCommand(){
        courts.getCourtSessionManager().endAll();
        commandSender.sendMessage("ended");
        return true;
    }
}
