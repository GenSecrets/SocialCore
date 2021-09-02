package com.nicholasdoherty.socialcore.components.courts.elections;

import com.nicholasdoherty.socialcore.components.courts.Courts;
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
        if (candidateSet == null) {
            candidateSet = new HashSet<>();
        }else {
            this.candidateSet = candidateSet;
        }
    }
    public boolean isInElection(UUID uuid) {
        if (candidateSet == null) {
            return false;
        }
        for (Candidate candidate : candidateSet) {
            if (candidate != null && candidate.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }
    public void addCandidate(Candidate candidate) {
        candidateSet.add(candidate);
    }

    public void removeCandiate(Candidate candidate) {
        candidateSet.remove(candidate);
        Courts.getCourts().getSqlSaveManager().removeCandidate(candidate);
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
