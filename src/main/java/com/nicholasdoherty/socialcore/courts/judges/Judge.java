package com.nicholasdoherty.socialcore.courts.judges;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.utils.SerializableUUID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by john on 1/6/15.
 */
public class Judge extends ApprovedCitizen implements ConfigurationSerializable{
    private Set<com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary> secretaries;

    public Judge(ApprovedCitizen approvedCitizen) {
        super(approvedCitizen);
        this.secretaries = new HashSet<>();
    }

    public Set<Secretary> getSecretaries() {
        return secretaries;
    }

    public Judge(String name, UUID uuid, Set<SerializableUUID> approvals, Set<SerializableUUID> disapprovals) {
        super(name, uuid, approvals, disapprovals);
        this.secretaries = new HashSet<>();
    }

    public Judge(String name, UUID uuid, Set<SerializableUUID> approvals, Set<SerializableUUID> disapprovals, Set<com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary> secretaries) {
        super(name, uuid, approvals, disapprovals);
        this.secretaries = secretaries;
    }
    public boolean isSecretary(UUID uuid) {
        for (com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary secretary : secretaries) {
            if (secretary.getUuid().equals(uuid))
                return true;
        }
        return false;
    }

    public Secretary getSecretary(UUID uuid) {
        for (com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary secretary : secretaries) {
            if (secretary.getUuid().equals(uuid))
                return secretary;
        }
        return null;
    }
    public List<Case> upcomingCases() {
        List<Case> upcoming = new ArrayList<>();
        for (Case casze : Courts.getCourts().getCaseManager().getCases()) {
            if (casze != null && casze.getCaseStatus() != null && casze.getCaseStatus() == CaseStatus.COURT_DATE_SET && casze.getCourtDate() != null && casze.getCourtDate().getJudge() != null && casze.getCourtDate().getJudge().equals(this)) {
                upcoming.add(casze);
            }
        }
        return upcoming;
    }
    public void removeSecretary(Secretary sec) {
        secretaries.remove(sec);
        Courts.getCourts().getJudgeManager().setPerms(sec.getUuid());
    }
    public void addSecretary(Secretary secretary) {
        secretaries.add(secretary);
        Courts.getCourts().getJudgeManager().setPerms(secretary.getUuid());
    }
    public Judge(Map<String, Object> map) {
        super(map);
        secretaries = new HashSet<>((Collection<? extends Secretary>) map.get("secretaries"));
        for (Secretary secretary : secretaries) {
            secretary.setJudge(this);
        }
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("secretaries",secretaries);
        return map;
    }
    public static Judge adminJudge(Player p) {
        return new Judge(p.getName(),p.getUniqueId(),new HashSet<SerializableUUID>(),new HashSet<SerializableUUID>());
    }
}
