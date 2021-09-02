package com.nicholasdoherty.socialcore.components.courts.policies;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

/**
 * Created by john on 9/11/16.
 */
public class Policy {
    private boolean isStale;
    private int id;
    private String text;
    private Citizen author;
    private Set<Citizen> confirmApprovals;
    private Set<Citizen> approvals;
    private Set<Citizen> disapprovals;
    private State state;
    private Timestamp creationTime;
    private Optional<Timestamp> confirmTime;
    public void checkStale() {
        if (isStale) {
            throw new RuntimeException("Attempt made on stale policy");
        }
    }

    public void setStale(boolean stale) {
        isStale = stale;
    }

    public Policy(int id, String text, Citizen author, Set<Citizen> confirmApprovals, Set<Citizen> approvals,
                  Set<Citizen> disapprovals, State state, Timestamp creationTime, Optional<Timestamp> confirmTime) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.confirmApprovals = confirmApprovals;
        this.approvals = approvals;
        this.disapprovals = disapprovals;
        this.state = state;
        this.creationTime = creationTime;
        this.confirmTime = confirmTime;
    }

    public boolean isStale() {
        return isStale;
    }
    public int totalVotes() {
        return approvals.size() + disapprovals.size();
    }
    public int getId() {
        return id;
    }
    public int approvalRating() {
        if (totalVotes() ==0) {
            return 0;
        }
        return (approvals.size() *100) /totalVotes();
    }
    public int voteProgress() {
        int requiredConfirms = Courts.getCourts().getPolicyManager().getPolicyConfig().getJudgesRequiredToConfirm();
        if (state == State.UNCONFIRMED && requiredConfirms != 0) {
            return (getConfirmApprovals().size() *100)/requiredConfirms;
        }
        if (totalVotes() == 0) {
            return 0;
        }
        int required = Courts.getCourts().getCourtsConfig().getJudgeRequiredVotes();
        if (required == 0) {
            return 0;
        }

        return (totalVotes()*100)/required;
    }
    public String getText() {
        return text;
    }

    public Set<Citizen> getApprovals() {
        return approvals;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public Set<Citizen> getDisapprovals() {
        return disapprovals;
    }

    public Optional<Timestamp> getConfirmTime() {
        return confirmTime;
    }

    public State getState() {
        return state;
    }

    public Citizen getAuthor() {
        return author;
    }

    public Set<Citizen> getConfirmApprovals() {
        return confirmApprovals;
    }

    public enum State {
        UNFINISHED,UNCONFIRMED,MAIN_VOTING,FAILED,
        IN_EFFECT,REPEALED;
    }
}
