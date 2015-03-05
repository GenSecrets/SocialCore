package com.nicholasdoherty.socialcore.courts.objects;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.courts.notifications.VoteNotification;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import com.nicholasdoherty.socialcore.utils.SerializableUUID;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

/**
 * Created by john on 1/6/15.
 */
public class ApprovedCitizen extends Citizen implements ConfigurationSerializable{
    private Set<SerializableUUID> approvals, disapprovals;
    private long lastVote;
    private int newApprovals,newDisapprovals;

    public ApprovedCitizen(ApprovedCitizen approvedCitizen) {
        super(approvedCitizen.getName(), approvedCitizen.getUuid());
        approvals = approvedCitizen.getApprovals();
        disapprovals = approvedCitizen.getDisapprovals();
        this.lastVote = approvedCitizen.getLastVote();
        this.newApprovals = approvedCitizen.getNewApprovals();
        this.newDisapprovals = approvedCitizen.getNewDisapprovals();
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

    public Set<SerializableUUID> getApprovals() {
        return approvals;
    }

    public Set<SerializableUUID> getDisapprovals() {
        return disapprovals;
    }

    public ApprovedCitizen(String name, UUID uuid, Set<SerializableUUID> approvals, Set<SerializableUUID> disapprovals) {
        super(name, uuid);
        this.approvals = approvals;
        this.disapprovals = disapprovals;
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

    public void approve(UUID Nuuid) {
        SerializableUUID uuid = new SerializableUUID(Nuuid);
        if (approvals.contains(uuid))
            return;
        if (disapprovals.contains(uuid))
            disapprovals.remove(uuid);
        approvals.add(uuid);
        onVote(Nuuid,true);
    }
    public boolean hasVoted(UUID nUUID) {
        SerializableUUID uuid = new SerializableUUID(nUUID);
        return (approvals.contains(uuid) || disapprovals.contains(uuid));
    }
    public boolean vote(UUID uuid) {
        return approvals.contains(new SerializableUUID(uuid));
    }
    public void vote(UUID uuid, boolean pos) {
        if (pos) {
            approve(uuid);
        }else {
            disapprove(uuid);
        }
    }

    public void disapprove(UUID Nuuid) {
        SerializableUUID uuid = new SerializableUUID(Nuuid);
        if (disapprovals.contains(uuid))
            return;
        if (approvals.contains(uuid))
            approvals.remove(uuid);
        disapprovals.add(uuid);
        onVote(Nuuid, false);
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
    public ApprovedCitizen(Map<String, Object> map) {
        super(map);
        this.approvals = new HashSet<>((Set<SerializableUUID>) map.get("approvals"));
        this.disapprovals = new HashSet<>((Set<SerializableUUID>) map.get("disapprovals"));
        this.lastVote = -1;
        if (map.containsKey("lastvote")) {
            lastVote = Long.parseLong(map.get("lastvote") + "");
        }
        if (map.containsKey("new-approvals")) {
            newApprovals = (int) map.get("new-approvals");
        }
        if (map.containsKey("new-disapprovals")) {
            newDisapprovals = (int) map.get("new-disapprovals");
        }
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("approvals", approvals);
        map.put("disapprovals",disapprovals);
        map.put("lastvote",lastVote);
        map.put("new-approvals",newApprovals);
        map.put("new-disapprovals",newDisapprovals);
        return map;
    }
}
