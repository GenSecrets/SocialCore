package com.nicholasdoherty.socialcore.components.courts.judges.gui.judgecasesview;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseManager;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseStatus;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.gui.views.PaginatedItemView;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/9/15.
 */
public class JudgeProcessedCasesView extends PaginatedItemView {
    private JudgeStallGUI judgeStallGUI;

    public JudgeProcessedCasesView(JudgeStallGUI judgeStallGUI) {
        super(judgeStallGUI, 54);
        this.judgeStallGUI = judgeStallGUI;
    }

    @Override
    public void update() {
        updateCases();
        super.update();
    }

    public void updateCases() {
        CaseManager caseManager = Courts.getCourts().getCaseManager();
        List<Case> unprocessed = caseManager.casesByStatus(CaseStatus.PROCESSED);
        unprocessed.addAll(caseManager.casesByStatus(CaseStatus.COURT_DATE_SET));
        List<ClickItem> secCaseClickItems = new ArrayList<>();
        for (Case caze : unprocessed) {
            secCaseClickItems.add(new JudgeCaseClickItem(caze, this));
        }

        this.setPaginatedItems(secCaseClickItems);
    }

    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null, getSize(),"Judge Desk");
    }
}
