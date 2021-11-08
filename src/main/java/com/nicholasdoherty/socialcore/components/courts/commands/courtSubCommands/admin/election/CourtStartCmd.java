package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.election;

import com.nicholasdoherty.socialcore.components.courts.elections.ElectionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CourtStartCmd {
    private final CommandSender commandSender;
    private final ElectionManager electionManager;
    private final String[] args;

    public CourtStartCmd(CommandSender commandSender, ElectionManager electionManager, String[] args) {
        this.commandSender = commandSender;
        this.electionManager = electionManager;
        this.args = args;
    }

    public boolean runCommand(){
        if(!electionManager.requirementsToStartElectionMet() && Arrays.stream(args).noneMatch(m -> m.equalsIgnoreCase("force"))) {
            commandSender.sendMessage("Requirements not met... use force to force start");
        }
        electionManager.startElection();
        commandSender.sendMessage(ChatColor.GREEN + "A new election has begun.");
        return true;
    }
}
