package com.nicholasdoherty.socialcore.components.courts.courtroom;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseStatus;

import java.util.Date;

/**
 * Created by john on 1/19/15.
 */
public class StartSessionRunnable implements Runnable {
    private Case caze;

    public StartSessionRunnable(Case caze) {
        this.caze = caze;
    }

    public Case getCaze() {
        return caze;
    }

    @Override
    public void run() {
        if (caze.getCaseStatus() != CaseStatus.COURT_DATE_SET)
            return;
        if (caze.getCourtDate() == null || caze.getCourtDate().getJudge() == null) {
            return;
        }
        if (caze.getCourtDate().getTime() > new Date().getTime()+5000) {
            return;
        }
        try {
            CourtSession courtSession = new CourtSession(caze,caze.getCourtDate().getJudge(), Courts.getCourts().getCourtsConfig().getDefaultCourtRoom());
            courtSession.startSession();
        }catch (Exception e) {e.printStackTrace();}
    }
}
