package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview;

import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.voxmc.voxlib.gui.InventoryGUI;
import org.bukkit.Bukkit;

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
