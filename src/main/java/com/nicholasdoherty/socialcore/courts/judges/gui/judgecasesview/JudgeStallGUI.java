package com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview;

import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/9/15.
 */
public class JudgeStallGUI extends InventoryGUI {
    private Judge judge;

    public JudgeStallGUI(Judge judge) {
        this.judge = judge;
        setCurrentView(new JudgeProcessedCasesView(this));
    }

    public Judge getJudge() {
        return judge;
    }
    public static void createAndOpen(Player p, Judge judge) {
        JudgeStallGUI judgeStallGUI = new JudgeStallGUI(judge);
        judgeStallGUI.setPlayer(p);
        judgeStallGUI.open();
    }
}
