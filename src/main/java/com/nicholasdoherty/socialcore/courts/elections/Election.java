package com.nicholasdoherty.socialcore.courts.elections;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

/**
 * Created by john on 1/6/15.
 */
public class Election implements ConfigurationSerializable{
    Set<Candidate> candidateSet = new HashSet<>();

    public Election() {
    }

    public Set<Candidate> getCandidateSet() {
        return candidateSet;
    }

    public void setCandidateSet(Set<Candidate> candidateSet) {
        this.candidateSet = candidateSet;
    }
    public boolean isInElection(UUID uuid) {
        for (Candidate candidate : candidateSet) {
            if (candidate.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }
    public void addCandidate(Candidate candidate) {
        candidateSet.add(candidate);
    }
    public int amountElected() {
        int amount = 0;
        for (Candidate candidate : candidateSet) {
            if (candidate.isElected())
                amount += 1;
        }
        return amount;
    }
    public void removeCandiate(Candidate candidate) {
        candidateSet.remove(candidate);
    }
    public Election(Map<String, Object> map) {
        this.candidateSet = new HashSet<>((Set<Candidate>)map.get("cs"));
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("cs",candidateSet);
        return map;
    }
}
