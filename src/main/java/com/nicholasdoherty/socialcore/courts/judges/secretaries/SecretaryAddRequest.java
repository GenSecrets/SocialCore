package com.nicholasdoherty.socialcore.courts.judges.secretaries;

import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;

/**
 * Created by john on 2/18/15.
 */
public class SecretaryAddRequest {
    private Judge judge;
    private Citizen secretary;

    public SecretaryAddRequest(Judge judge, Citizen secretary) {
        this.judge = judge;
        this.secretary = secretary;
    }

    public Judge getJudge() {
        return judge;
    }

    public Citizen getSecretary() {
        return secretary;
    }
}
