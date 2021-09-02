package com.nicholasdoherty.socialcore.components.courts.judges.secretaries;


import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;

/**
 * Created by john on 1/3/15.
 */
public class Secretary extends Citizen{
    private int secretaryId;
    private Judge judge;

    public Secretary(int secretaryId, Citizen citizen, Judge judge) {
        super(citizen);
        this.secretaryId = secretaryId;
        this.judge = judge;
    }

    public int getSecretaryId() {
        return secretaryId;
    }

    public Judge getJudge() {
        return judge;
    }

    public void setJudge(Judge judge) {
        this.judge = judge;
    }
}
