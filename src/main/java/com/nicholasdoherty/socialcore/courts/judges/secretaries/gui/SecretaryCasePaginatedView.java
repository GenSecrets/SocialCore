package com.nicholasdoherty.socialcore.courts.judges.secretaries.gui;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseManager;
import com.nicholasdoherty.socialcore.courts.cases.CaseStatus;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.gui.views.PaginatedItemView;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/6/15.
 */
public class SecretaryCasePaginatedView extends PaginatedItemView{
    SecretaryGUI secretaryGUI;
    public SecretaryCasePaginatedView(SecretaryGUI inventoryGUI) {
        super(inventoryGUI, 54);
        this.secretaryGUI = inventoryGUI;
        //this.setStartEnd(9,44);
    }

    @Override
    public void update() {
        updateCases();
        super.update();
    }

    public void updateCases() {
        CaseManager caseManager = Courts.getCourts().getCaseManager();
        List<Case> cases = caseManager.casesByStatus(CaseStatus.UNPROCESSED);
        //cases.addAll(caseManager.casesByStatus(CaseStatus.COURT_DATE_SET));
        List<ClickItem> secCaseClickItems = new ArrayList<>();
        for (Case caze : cases) {
            secCaseClickItems.add(new SecCaseClickItem(secretaryGUI,caze,this));
        }
        this.setPaginatedItems(secCaseClickItems);
    }

    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null, this.getSize(), "Secretary: Cases");
    }
}
