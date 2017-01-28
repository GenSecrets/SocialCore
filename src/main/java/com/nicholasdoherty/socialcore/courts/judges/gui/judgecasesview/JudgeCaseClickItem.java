package com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview;

import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.CaseInfoClickItem;
import com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeCaseView.JudgeCaseView;
import org.bukkit.ChatColor;

import java.util.List;

/**
 * Created by john on 1/9/15.
 */
public class JudgeCaseClickItem extends CaseInfoClickItem {
    private JudgeProcessedCasesView judgeProcessedCasesView;
    public JudgeCaseClickItem(Case caze, JudgeProcessedCasesView judgeProcessedCasesView) {
        super(caze);
        this.judgeProcessedCasesView = judgeProcessedCasesView;
    }
    @Override
    public void click(boolean right) {
        if (getCaze().isLocked()) {
            judgeProcessedCasesView.getInventoryGUI().getPlayer().sendMessage(ChatColor.RED + "That case is currently locked.");
        }else {
            JudgeCaseView judgeCaseView = new JudgeCaseView(getCaze(),judgeProcessedCasesView);
            judgeProcessedCasesView.getInventoryGUI().setCurrentView(judgeCaseView);
            judgeCaseView.activate();
        }
    }

    @Override
    public List<String> lore() {
        List<String> lore = super.lore();
        lore.add(ChatColor.GRAY + "<Left click to view>");
        return lore;
    }
}
