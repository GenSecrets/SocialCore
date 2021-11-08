package com.nicholasdoherty.socialcore.components.courts.judges;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.components.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by john on 1/6/15.
 */
public class Judge extends ApprovedCitizen{
    private int judgeId;
    private Set<com.nicholasdoherty.socialcore.components.courts.judges.secretaries.Secretary> secretaries;

    public Judge(ApprovedCitizen approvedCitizen, int judgeId) {
        super(approvedCitizen);
        this.judgeId = judgeId;
    }

    public void setSecretaries(Set<Secretary> secretaries) {
        this.secretaries = secretaries;
    }

    public int getJudgeId() {
        return judgeId;
    }

    public Set<Secretary> getSecretaries() {
        return secretaries;
    }

    public long getJoinDateTime() {
        return SocialCore.plugin.getCourts().getSqlSaveManager().getJudgeJoinDate(judgeId);
    }

    public long getJudgeLastOnlineDate() {
        return SocialCore.plugin.getCourts().getSqlSaveManager().getJudgeLastOnlineDate(judgeId);
    }

    public long getSecretaryLastOnlineDate(int secId) {
        return SocialCore.plugin.getCourts().getSqlSaveManager().getSecretaryLastOnlineDate(secId);
    }

    public boolean isSecretary(UUID uuid) {
        for (com.nicholasdoherty.socialcore.components.courts.judges.secretaries.Secretary secretary : secretaries) {
            if (secretary != null && secretary.getUuid() != null && secretary.getUuid().equals(uuid))
                return true;
        }
        return false;
    }

    public Secretary getSecretary(UUID uuid) {
        for (com.nicholasdoherty.socialcore.components.courts.judges.secretaries.Secretary secretary : secretaries) {
            if (secretary != null && secretary.getUuid() != null && secretary.getUuid().equals(uuid))
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
        Courts.getCourts().getSqlSaveManager().removeSecretary(sec);
        secretaries.remove(sec);
        Courts.getCourts().getJudgeManager().setPerms(sec.getUuid());
    }
    public void addSecretary(Citizen citizen) {
        Secretary secretary = Courts.getCourts().getSqlSaveManager().createSecretary(this,citizen);
        secretaries.add(secretary);
        Courts.getCourts().getJudgeManager().setPerms(secretary.getUuid());
    }

}
