package com.nicholasdoherty.socialcore.courts.elections;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationType;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/6/15.
 */
public class ElectionManager implements ConfigurationSerializable{
    private Election currentElection;
    private Election previousElection;

    public ElectionManager(Election currentElection, Election previousElection) {
        this.currentElection = currentElection;
        this.previousElection = previousElection;
        if (currentElection == null)
            startElection();
    }

    public void startElection() {
        if (currentElection != null) {
            previousElection = currentElection;
        }
        currentElection = new Election();
        //if (previousElection != null) {
        //    for (Candidate candidate : previousElection.getCandidateSet()) {
        //        if (!candidate.isElected()) {
        //            UUID uuid = candidate.getUuid();
        //            String name = candidate.getName();
        //            currentElection.getCandidateSet().add(new Candidate(name,uuid));
        //        }
        //    }
        //}
    }
    public boolean isElectionActive() {
        return currentElection != null;
    }

    public Election getCurrentElection() {
        return currentElection;
    }
    public boolean hasConditionsForWin(Candidate candidate) {
        if (candidate.electPercentage() < 100)
            return false;
        if (candidate.approvalPercentage() < Courts.getCourts().getCourtsConfig().getJudgeApprovalRateRequired())
            return false;
        return true;
    }
    public void endCurrentElection() {
        previousElection = currentElection;
        currentElection = null;
    }
    public void checkWin(Election election, Candidate candidate) {
        if (Courts.getCourts().getJudgeManager().getJudges().size() >= Courts.getCourts().getCourtsConfig().getMaxJudges()) {
            return;
        }
        int requiredVotes = Courts.getCourts().getCourtsConfig().getJudgeRequiredVotes();
        if (candidate.votes() < requiredVotes) {
            return;
        }
        if (candidate.electPercentage() >= 100 && candidate.approvalPercentage() > Courts.getCourts().getCourtsConfig().getJudgeApprovalRateRequired()) {
            Judge judge = Courts.getCourts().getJudgeManager().promoteJudge(candidate);
            Courts.getCourts().getNotificationManager().notification(NotificationType.JUDGE_ELECTED_ALL,new Object[]{judge});
        }
        election.removeCandiate(candidate);
    }
    public ElectionManager(Map<String, Object> map) {
        currentElection = (Election) map.get("currentElection");
        previousElection = (Election) map.get("prevElection");
        if (currentElection == null)
            startElection();
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("currentElection",currentElection);
        map.put("prevElection",previousElection);
        return map;
    }
}
