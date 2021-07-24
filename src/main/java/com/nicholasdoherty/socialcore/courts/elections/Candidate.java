package com.nicholasdoherty.socialcore.courts.elections;

import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;

/**
 * Created by john on 1/6/15.
 */
public class Candidate extends ApprovedCitizen {
    private int candidateId;

    public Candidate(ApprovedCitizen approvedCitizen, int candidateId) {
        super(approvedCitizen);
        this.candidateId = candidateId;
    }

    public int getCandidateId() {
        return candidateId;
    }
}
