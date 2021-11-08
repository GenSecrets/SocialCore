package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.election;

import com.nicholasdoherty.socialcore.components.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.components.courts.elections.Election;
import com.nicholasdoherty.socialcore.components.courts.elections.ElectionManager;
import com.nicholasdoherty.socialcore.utils.CourtUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CourtRemoveNomineeCmd {
    private final CommandSender commandSender;
    private final ElectionManager electionManager;
    private final String[] args;

    public CourtRemoveNomineeCmd(CommandSender commandSender, ElectionManager electionManager, String[] args) {
        this.commandSender = commandSender;
        this.electionManager = electionManager;
        this.args = args;
    }

    //TODO: Make a command completion for all current people in election database && make the ability to clear election
    public boolean runCommand(){
        Election election = electionManager.getCurrentElection();
        Candidate toRemove = CourtUtil.getCandidate(election, commandSender, args[0], electionManager.isElectionActive());
        if(toRemove != null){
            election.removeCandiate(toRemove);
            commandSender.sendMessage(ChatColor.GREEN + "Removed" + toRemove.getName() + " from the election");
        } else {
            commandSender.sendMessage(ChatColor.RED + "Unable to find candidate in the election.");
        }
        return true;
    }
}
