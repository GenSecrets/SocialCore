package com.nicholasdoherty.socialcore.courts.objects;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.courts.notifications.VoteNotification;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Created by john on 1/6/15.
 */
public class ApprovedCitizen extends Citizen {
    private Set<UUID> approvals, disapprovals;
    private long lastVote;
    private int newApprovals,newDisapprovals;

    public ApprovedCitizen(Citizen citizen, Set<UUID> approvals, Set<UUID> disapprovals) {
        super(citizen);
        this.approvals = approvals;
        this.disapprovals = disapprovals;
    }
    public ApprovedCitizen(ApprovedCitizen approvedCitizen) {
        super(approvedCitizen);
        this.approvals = approvedCitizen.getApprovals();
        this.disapprovals = approvedCitizen.getDisapprovals();
    }

    public long getLastVote() {
        return lastVote;
    }

    public int getNewApprovals() {
        return newApprovals;
    }

    public int getNewDisapprovals() {
        return newDisapprovals;
    }

    public Set<UUID> getApprovals() {
        return approvals;
    }

    public Set<UUID> getDisapprovals() {
        return disapprovals;
    }



    public double electPercentage() {
        int votes = votes();
        int requiredVotes = Courts.getCourts().getCourtsConfig().getJudgeRequiredVotes();
        double electPercentage =  (double) votes /requiredVotes;
        if (electPercentage > 1)
            electPercentage = 1;
        electPercentage *= 100;
        return electPercentage;
    }
    public double approvalPercentage() {
        return (double) approvals.size()*100/votes();
    }
    public int votes() {
        return approvals.size() + disapprovals.size();
    }

    public void approve(UUID uuid) {
        if (approvals.contains(uuid))
            return;
        if (disapprovals.contains(uuid))
            disapprovals.remove(uuid);
        approvals.add(uuid);
        onVote(uuid,true);
    }
    public boolean hasVoted(UUID uuid) {
        return (approvals.contains(uuid) || disapprovals.contains(uuid));
    }
    public boolean vote(UUID uuid) {
        return approvals.contains(uuid);
    }
    public void vote(UUID uuid, boolean pos) {
        if (pos) {
            approve(uuid);
        }else {
            disapprove(uuid);
        }
    }

    public void disapprove(UUID uuid) {
        if (disapprovals.contains(uuid))
            return;
        if (approvals.contains(uuid))
            approvals.remove(uuid);
        disapprovals.add(uuid);
        onVote(uuid, false);
    }
    public void resetNew() {
        newApprovals = 0;
        newDisapprovals = 0;
        lastVote = new Date().getTime();
    }
    public void resetVotes() {
        resetNew();
        approvals.clear();
        disapprovals.clear();
    }
    public long ticksSinceLast() {
        long now = new Date().getTime();
        return VoxTimeUnit.TICK.fromMillis(now - lastVote);
    }

    public void onVote(UUID uuid, boolean approved) {
        Courts.getCourts().getSqlSaveManager().updateVote(this,uuid,approved);
        if (approved) {
            newApprovals += 1;
        }else {
            newDisapprovals += 1;
        }
        long now = new Date().getTime();
        if (lastVote > 0) {
            long ticksSinceLast = VoxTimeUnit.TICK.fromMillis(Math.abs(now-lastVote));
            long minTicksSinceLast = Courts.getCourts().getCourtsConfig().getTimeBetweenVoteMessages();
            if (ticksSinceLast < minTicksSinceLast) {
                Courts.getCourts().getNotificationManager().summary(this);
                return;
            }
            lastVote = now;
        }else {
            lastVote = now;
        }
        if (Bukkit.getPlayer(uuid) == null) {
            return;
        }
        boolean summmary = false;
        if (newApprovals > 1 || newDisapprovals > 1) {
            summmary = true;
        }
        if (summmary) {
            Courts.getCourts().getNotificationManager().summary(this);
        }else {
            VoteNotification voteNotification = (VoteNotification) Courts.getCourts().getNotificationManager().getNotificationTypeNotificationMap().get(NotificationType.VOTE_NOTIFICATION);
            voteNotification.vote(this,uuid,approved);
        }
    }
}
