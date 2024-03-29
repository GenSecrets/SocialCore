package com.nicholasdoherty.socialcore.components.courts.judges.gui.approvalgui;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.inventorygui.gui.clickitems.ApprovalItem;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.gui.views.PaginatedItemView;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/6/15.
 */
public class JudgeApprovalView extends PaginatedItemView {
    public JudgeApprovalView(final JudgesApprovalGUI inventoryGUI) {
        super(inventoryGUI, 54);
    }
    
    @Override
    public void initActiveItems() {
        update();
    }
    
    @Override
    public void update() {
        Bukkit.getScheduler().runTaskAsynchronously(SocialCore.getPlugin(), () -> {
            final JudgeManager judgeManager = Courts.getCourts().getJudgeManager();
            final List<ClickItem> clickItemList = new ArrayList<>();
            for (final Judge judge : judgeManager.getJudges()) {
                if (judge != null) {
                    final ClickItem approvalItem = new ApprovalItem(this, judge);
                        clickItemList.add(approvalItem);
                }
            }
            Bukkit.getScheduler().runTask(SocialCore.getPlugin(), () -> {
                setPaginatedItems(clickItemList);
                super.update();
            });
        });
    }
    
    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null, 54, "Court Officials");
    }
}
