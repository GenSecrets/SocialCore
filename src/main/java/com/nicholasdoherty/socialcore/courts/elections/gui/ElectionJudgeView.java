package com.nicholasdoherty.socialcore.courts.elections.gui;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.courts.elections.Election;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.ApprovalItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.PaginatedItemView;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/6/15.
 */
public class ElectionJudgeView extends PaginatedItemView {
    public ElectionJudgeView(ElectionGUI inventoryGUI) {
        super(inventoryGUI,54);
    }

    @Override
    public void initActiveItems() {
        update();
    }

    @Override
    public void update() {
        Election election = Courts.getCourts().getElectionManager().getCurrentElection();
        if (election == null || election.getCandidateSet() == null) {
            return;
        }
        List<ClickItem> clickItemList = new ArrayList<>();
        for (Candidate candidate : election.getCandidateSet()) {
            if (candidate != null) {
                ApprovalItem approvalItem = new JudgeApprovalItem(this,candidate);
                clickItemList.add(approvalItem);
            }
        }
        this.setPaginatedItems(clickItemList);
        super.update();
    }

    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null, 54,"Electing Judges");
    }
}
