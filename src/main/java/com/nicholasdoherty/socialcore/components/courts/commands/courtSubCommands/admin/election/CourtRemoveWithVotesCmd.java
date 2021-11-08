package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.election;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.components.courts.elections.Election;
import com.nicholasdoherty.socialcore.components.courts.elections.ElectionManager;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class CourtRemoveWithVotesCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final ElectionManager electionManager;
    private final String[] args;

    public CourtRemoveWithVotesCmd(Courts courts, CommandSender commandSender, ElectionManager electionManager, String[] args) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.electionManager = electionManager;
        this.args = args;
    }

    public boolean runCommand(){
        final String playerName = args[0];
        final Citizen citizen = courts.getCitizenManager().getCitizen(playerName);
        if(citizen == null) {
            commandSender.sendMessage("could not find " + playerName);
            return true;
        }
        final Election election = electionManager.getCurrentElection();
        if(election != null) {
            final Optional<Candidate> cand = election.getCandidateSet().stream().filter(candidate -> candidate.getUuid().equals(citizen.getUuid())).findAny();
            cand.ifPresent(ApprovedCitizen::resetVotes);
            cand.ifPresent(election::removeCandiate);
        }
        final Judge judge = courts.getJudgeManager().getJudge(citizen.getUuid());
        if(judge != null) {
            courts.getJudgeManager().demoteJudge(judge);
        }
        courts.getSqlSaveManager().removeVotes(citizen);
        commandSender.sendMessage("removed all votes and elected ranks from " + citizen.getName());
        return true;

    }
}
