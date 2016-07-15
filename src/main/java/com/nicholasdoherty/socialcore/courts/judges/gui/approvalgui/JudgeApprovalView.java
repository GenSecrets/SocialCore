package com.nicholasdoherty.socialcore.courts.judges.gui.approvalgui;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.ApprovalItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.PaginatedItemView;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.judges.JudgeManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/6/15.
 */
public class JudgeApprovalView extends PaginatedItemView {
    public JudgeApprovalView(JudgesApprovalGUI inventoryGUI) {
        super(inventoryGUI,54);
    }
    @Override
    public void initActiveItems() {
        update();
    }

    @Override
    public void update() {
        JudgeManager judgeManager = Courts.getCourts().getJudgeManager();
        List<ClickItem> clickItemList = new ArrayList<>();
        for (Judge judge : judgeManager.getJudges()) {
            if (judge != null) {
                ApprovalItem approvalItem = new ApprovalItem(this,judge);
                clickItemList.add(approvalItem);
            }
        }
        this.setPaginatedItems(clickItemList);
        super.update();
    }

    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null,54,"Judges");
    }
}
