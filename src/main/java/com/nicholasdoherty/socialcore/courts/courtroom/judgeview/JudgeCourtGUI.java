package com.nicholasdoherty.socialcore.courts.courtroom.judgeview;

import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import com.voxmc.voxlib.gui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.judges.Judge;

/**
 * Created by john on 1/13/15.
 */
public class JudgeCourtGUI extends InventoryGUI {
    private CourtSession courtSession;
    private boolean specialInterface;

    public JudgeCourtGUI(CourtSession courtSession) {
        this.courtSession = courtSession;
        setCurrentView(new JudgeBaseView(this));
    }

    public Case getCaze() {
        return courtSession.getCaze();
    }

    public Judge getJudge() {
        return courtSession.getJudge();
    }

    public CourtSession getCourtSession() {
        return courtSession;
    }

    @Override
    public boolean inSpecialInterface() {
        return specialInterface;
    }

    public void setSpecialInterface(boolean specialInterface) {
        this.specialInterface = specialInterface;
    }
}
