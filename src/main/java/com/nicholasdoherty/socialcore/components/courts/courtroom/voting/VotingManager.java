package com.nicholasdoherty.socialcore.components.courts.courtroom.voting;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by john on 1/16/15.
 */
public class VotingManager implements ConfigurationSerializable{
    Set<Vote> votes = new HashSet<>();

    public VotingManager() {
        new VotingListener(this);
    }

    public synchronized Set<Vote> getVotes() {
        return votes;
    }
    public synchronized void addVote(Vote vote) {
        votes.add(vote);
    }
    public synchronized void removeVote(Vote vote) {
        votes.remove(vote);
    }

    public VotingManager(Map<String, Object> map) {
        votes = (Set<Vote>) map.get("votes");
        votes = new HashSet<>(votes);
        new VotingListener(this);
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("votes",votes);
        return map;
    }
}
