package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.election;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.components.courts.elections.Election;
import com.nicholasdoherty.socialcore.components.courts.elections.ElectionManager;
import com.nicholasdoherty.socialcore.utils.CourtUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CourtResetVotesCmd {
    private final CommandSender commandSender;
    private final ElectionManager electionManager;
    private final String[] args;

    public CourtResetVotesCmd(CommandSender commandSender, ElectionManager electionManager, String[] args) {
        this.commandSender = commandSender;
        this.electionManager = electionManager;
        this.args = args;
    }

    public boolean runCommand(){
        Election election = electionManager.getCurrentElection();
        Candidate can = CourtUtil.getCandidate(election, commandSender, args[0], electionManager.isElectionActive());
        if(can != null) {
            can.resetVotes();
            Courts.getCourts().getElectionManager().checkWin(election, can);
            commandSender.sendMessage(ChatColor.GREEN + "Reset " + can.getName() + "'s votes");
        } else {
            commandSender.sendMessage(ChatColor.RED + "Unable to find candidate in the election.");
        }
        return true;
    }
}
