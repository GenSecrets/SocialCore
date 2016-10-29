package com.nicholasdoherty.socialcore.courts.courtroom.voting;

import com.nicholasdoherty.socialcore.courts.courtroom.Restricter;
import com.nicholasdoherty.socialcore.utils.SerializableUUID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

/**
 * Created by john on 1/14/15.
 */
public abstract class Vote implements ConfigurationSerializable {
    private Set<UUID> voted = new HashSet<>();
    private Set<SerializableUUID> approvals;
    private Set<SerializableUUID> disapprovals;
    private Restricter restricter;
    private boolean open = true;

    public Vote(Restricter restricter) {
        this.restricter = restricter;
        approvals = new HashSet<>();
        disapprovals = new HashSet<>();
    }
    public Vote(Map<String, Object> map) {
        if (map.containsKey("restricter")) {
            restricter = (Restricter) map.get("restricter");
        }
        approvals = (Set<SerializableUUID>) map.get("approve");
        approvals = new HashSet<>(approvals);
        disapprovals = (Set<SerializableUUID>) map.get("disapprove");
        disapprovals = new HashSet<>(disapprovals);
        open = (boolean) map.get("open");
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("restricter",restricter);
        map.put("approve",approvals);
        map.put("disapprove",disapprovals);
        map.put("open",open);
        return map;
    }

    public void vote(UUID uuid, VoteValue voteValue) {
        if (voteValue == VoteValue.ABSTAIN) {
            removeVote(uuid);
        }
        boolean approve = false;
        if (voteValue == VoteValue.APPROVE) {
            approve = true;
        }
        SerializableUUID serializableUUID = new SerializableUUID(uuid);
        if (approve) {
            if (approvals.contains(serializableUUID))
                return;
            if (disapprovals.contains(serializableUUID))
                disapprovals.remove(serializableUUID);
            approvals.add(serializableUUID);
        }else {
            if (disapprovals.contains(serializableUUID))
                return;
            if (approvals.contains(serializableUUID))
                approvals.remove(serializableUUID);
            disapprovals.add(serializableUUID);
        }
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Result result() {
        if (approvals.size() > disapprovals.size()) {
            return Result.APPROVE;
        }else if (approvals.size() < disapprovals.size()) {
            return Result.DISAPPROVE;
        }else {
            return Result.TIE;
        }
    }
    public boolean canVote(UUID uuid) {
        if (restricter == null)
            return true;
        return restricter.canVote(uuid);
    }
    public boolean hasVoted(UUID uuid) {
        return (approvals.contains(uuid) || disapprovals.contains(uuid));
    }
    public int votes() {
        return approvals.size() + disapprovals.size();
    }
    public int approvals() {
        return approvals.size();
    }
    public int disapprovals() {
        return disapprovals.size();
    }
    public String percentWinString() {
        Result result = result();
        int votes = votes();
        if (result == Result.TIE) {
            return "50%";
        }else if (result == Result.APPROVE) {
            return ((int) Math.round((100.0*approvals())/votes)) +"%";
        }else {
            return ((int) Math.round((100.0*disapprovals())/votes)) +"%";
        }
    }
    public void removeVote(UUID uuid) {
        SerializableUUID serializableUUID = new SerializableUUID(uuid);
        if (approvals.contains(serializableUUID)) {
            approvals.remove(serializableUUID);
        }
        if (disapprovals.contains(serializableUUID)) {
            disapprovals.remove(serializableUUID);
        }
    }
    public void clear() {
        approvals.clear();
        disapprovals.clear();
    }

    public abstract String[] helpMessage();

    enum Result {
        APPROVE,TIE,DISAPPROVE;
    }
    public enum VoteValue {
        APPROVE,DISAPPROVE,ABSTAIN;
    }
    public abstract String summarizeResults();
}
