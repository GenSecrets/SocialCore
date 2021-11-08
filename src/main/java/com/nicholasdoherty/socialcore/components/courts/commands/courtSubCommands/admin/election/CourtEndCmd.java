package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.election;

import com.nicholasdoherty.socialcore.components.courts.elections.ElectionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CourtEndCmd {
    private final CommandSender commandSender;
    private final ElectionManager electionManager;

    public CourtEndCmd(CommandSender commandSender, ElectionManager electionManager) {
        this.commandSender = commandSender;
        this.electionManager = electionManager;
    }

    public boolean runCommand(){
        if(!electionManager.isElectionActive()) {
            commandSender.sendMessage(ChatColor.RED + "There is not an active election at this time.");
            return true;
        }
        electionManager.endCurrentElection();
        commandSender.sendMessage(ChatColor.GREEN + "Election ended.");
        return true;
    }
}
