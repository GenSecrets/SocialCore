package com.nicholasdoherty.socialcore.courts.judges.gui.approvalgui;

import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.judges.JudgeManager;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/6/15.
 */
public class JudgesApprovalGUI extends InventoryGUI {
    private JudgeManager judgeManager;

    public JudgesApprovalGUI(JudgeManager judgeManager) {
        this.judgeManager = judgeManager;
        setCurrentView(new JudgeApprovalView(this));
    }

    public JudgeManager getJudgeManager() {
        return judgeManager;
    }
    public static void createAndOpen(Player p, JudgeManager judgeManager) {
        JudgesApprovalGUI judgesApprovalGUI = new JudgesApprovalGUI(judgeManager);
        judgesApprovalGUI.setPlayer(p);
        judgesApprovalGUI.open();
    }

}
