package com.nicholasdoherty.socialcore.courts.elections;

import com.google.common.io.Files;
import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.time.condition.RealTimeCondition;
import com.nicholasdoherty.socialcore.time.condition.TimeCondition;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by john on 1/6/15.
 */
public class ElectionManager{
    private Election currentElection;

    public ElectionManager(Election currentElection) {
        this.currentElection = currentElection;
        new BukkitRunnable(){
            @Override
            public void run() {
                checkShouldScheduleFile();
            }
        }.runTaskLater(Courts.getCourts().getPlugin(),5);
    }
    public void tryStartElection() {
        if (!requirementsForScheduleElectionMet()) {
            return;
        }
        if (currentElection != null) {
            return;
        }
        startElection();
    }
    public long judgeNeededTime() {
        File file = new File(Courts.getCourts().getPlugin().getDataFolder(), "courts-election-flag-set");
        boolean flagSet = file.exists();
        if (!flagSet) {
            return -1;
        }
        if (!requirementsForScheduleElectionMet()) {
            file.delete();
        }
        try {
            return Long.parseLong(Files.readFirstLine(file, Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public void deleteJudgeNeededFile() {
        File file = new File(Courts.getCourts().getPlugin().getDataFolder(), "courts-election-flag-set");
        if (file.exists()) {
            file.delete();
        }
    }
    public void setJudgeNeededFile() {
        if (!requirementsForScheduleElectionMet()) {
            return;
        }
        File file = new File(Courts.getCourts().getPlugin().getDataFolder(), "courts-election-flag-set");
        if (file.exists()) {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Files.write(new Date().getTime() +"",file,Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void checkShouldScheduleFile() {
        if (requirementsForScheduleElectionMet() && judgeNeededTime() == -1) {
            setJudgeNeededFile();
        }
    }

    public boolean requirementsForScheduleElectionMet() {
          return Courts.getCourts().getJudgeManager().getJudges().size() < Courts.getCourts().getCourtsConfig().getMaxJudges() && currentElection == null;
    }
    public boolean isScheduled() {
        return judgeNeededTime() != -1 && requirementsForScheduleElectionMet();
    }
    public boolean hasBeenWaitTime() {
        long judgeNeededTime = judgeNeededTime();
        if (judgeNeededTime == -1) {
            return false;
        }
        return new Date().getTime() - judgeNeededTime >= Courts.getCourts().getCourtsConfig().getMinElectionWaitMillis() - 100;
    }
    public boolean requirementsToStartElectionMet() {
        return Courts.getCourts().getJudgeManager().getJudges().size() < Courts.getCourts().getCourtsConfig().getMaxJudges() && currentElection == null
                && hasBeenWaitTime();
    }
    public void startElection() {
        Courts.getCourts().getSqlSaveManager().startElection();
        currentElection = new Election();
        deleteJudgeNeededFile();
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
        currentElection = null;
        Courts.getCourts().getSqlSaveManager().endElection();
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

}
