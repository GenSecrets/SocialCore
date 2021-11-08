package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.election;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.components.courts.elections.Election;
import com.nicholasdoherty.socialcore.components.courts.elections.ElectionManager;
import com.nicholasdoherty.socialcore.utils.CourtUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CourtAddVotesCmd {
    private final CommandSender commandSender;
    private final ElectionManager electionManager;
    private final String[] args;

    public CourtAddVotesCmd(CommandSender commandSender, ElectionManager electionManager, String[] args) {
        this.commandSender = commandSender;
        this.electionManager = electionManager;
        this.args = args;
    }

    public boolean runCommand(){
        Election election = electionManager.getCurrentElection();
        Candidate can = CourtUtil.getCandidate(election, commandSender, args[0], electionManager.isElectionActive());
        boolean approve = true;
        if(args[1].contains("d") || args[1].contains("D")) {
            approve = false;
        }
        final int amountVotes = Integer.parseInt(args[2]);
        for(int i = 0; i < amountVotes; i++) {
            if(approve) {
                can.approve(UUID.randomUUID());
            } else {
                can.disapprove(UUID.randomUUID());
            }
        }
        Courts.getCourts().getElectionManager().checkWin(election, can);
        String approveString = "approve";
        if(!approve) {
            approveString = "disapprove";
        }
        commandSender.sendMessage(ChatColor.GREEN + "Gave " + amountVotes + ' ' + approveString + " votes to " + can.getName());
        return true;
    }
}
