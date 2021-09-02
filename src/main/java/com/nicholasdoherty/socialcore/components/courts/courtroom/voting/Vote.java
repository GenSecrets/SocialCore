package com.nicholasdoherty.socialcore.components.courts.courtroom.voting;

import com.nicholasdoherty.socialcore.components.courts.courtroom.Restricter;
import com.voxmc.voxlib.util.SerializableUUID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

/**
 * Created by john on 1/14/15.
 */
public abstract class Vote implements ConfigurationSerializable {
    @SuppressWarnings("unused")
    private final Set<UUID> voted = new HashSet<>();
    private Set<SerializableUUID> approvals;
    private Set<SerializableUUID> disapprovals;
    private Restricter restricter;
    private boolean open = true;
    
    public Vote(final Restricter restricter) {
        this.restricter = restricter;
        approvals = new HashSet<>();
        disapprovals = new HashSet<>();
    }
    
    @SuppressWarnings("unchecked")
    public Vote(final Map<String, Object> map) {
        if(map.containsKey("restricter")) {
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
        final Map<String, Object> map = new HashMap<>();
        map.put("restricter", restricter);
        map.put("approve", approvals);
        map.put("disapprove", disapprovals);
        map.put("open", open);
        return map;
    }
    
    @SuppressWarnings("RedundantCollectionOperation")
    public void vote(final UUID uuid, final VoteValue voteValue) {
        if(voteValue == VoteValue.ABSTAIN) {
            removeVote(uuid);
        }
        boolean approve = false;
        if(voteValue == VoteValue.APPROVE) {
            approve = true;
        }
        final SerializableUUID serializableUUID = new SerializableUUID(uuid);
        if(approve) {
            if(approvals.contains(serializableUUID)) {
                return;
            }
            if(disapprovals.contains(serializableUUID)) {
                disapprovals.remove(serializableUUID);
            }
            approvals.add(serializableUUID);
        } else {
            if(disapprovals.contains(serializableUUID)) {
                return;
            }
            if(approvals.contains(serializableUUID)) {
                approvals.remove(serializableUUID);
            }
            disapprovals.add(serializableUUID);
        }
    }
    
    public boolean isOpen() {
        return open;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
    }
    
    public Result result() {
        if(approvals.size() > disapprovals.size()) {
            return Result.APPROVE;
        } else if(approvals.size() < disapprovals.size()) {
            return Result.DISAPPROVE;
        } else {
            return Result.TIE;
        }
    }
    
    public boolean canVote(final UUID uuid) {
        if(restricter == null) {
            return true;
        }
        return restricter.canVote(uuid);
    }
    
    public boolean hasVoted(final UUID uuid) {
        return approvals.stream().anyMatch(e -> e.asUUID().equals(uuid))
                || disapprovals.stream().anyMatch(e -> e.asUUID().equals(uuid));
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
        final Result result = result();
        final int votes = votes();
        if(result == Result.TIE) {
            return "50%";
        } else if(result == Result.APPROVE) {
            return (int) Math.round(100.0 * approvals() / votes) + "%";
        } else {
            return (int) Math.round(100.0 * disapprovals() / votes) + "%";
        }
    }
    
    public void removeVote(final UUID uuid) {
        final SerializableUUID serializableUUID = new SerializableUUID(uuid);
        if(approvals.stream().anyMatch(e -> e.asUUID().equals(uuid))) {
            approvals.remove(serializableUUID);
        }
        if(disapprovals.stream().anyMatch(e -> e.asUUID().equals(uuid))) {
            disapprovals.remove(serializableUUID);
        }
    }
    
    public void clear() {
        approvals.clear();
        disapprovals.clear();
    }
    
    public abstract String[] helpMessage();
    
    public abstract String summarizeResults();
    
    enum Result {
        APPROVE, TIE, DISAPPROVE
    }
    
    public enum VoteValue {
        APPROVE, DISAPPROVE, ABSTAIN
    }
}
