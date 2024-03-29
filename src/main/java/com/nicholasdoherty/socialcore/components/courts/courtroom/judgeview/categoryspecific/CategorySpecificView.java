package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.categoryspecific;

import com.nicholasdoherty.socialcore.components.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.gui.clickitems.ChangeViewClickItem;
import com.voxmc.voxlib.gui.views.PaginatedItemView;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/14/15.
 */
public class CategorySpecificView extends PaginatedItemView{
    private JudgeBaseView judgeBaseView;
    private CaseCategory caseCategory;

    public CategorySpecificView(JudgeBaseView judgeBaseView) {
        super(judgeBaseView.getInventoryGUI(),54);
        this.judgeBaseView = judgeBaseView;
        caseCategory = judgeBaseView.getCourtSession().getCaze().getCaseCategory();
        this.setStartEnd(9,53);
        List<ClickItem> paginatedItems = new ArrayList<>();
        if (caseCategory == CaseCategory.DIVORCE) {
            paginatedItems.add(new GrantDivorceItem(judgeBaseView.getCourtSession()));
        }
        if (caseCategory == CaseCategory.ABANDONED || caseCategory == CaseCategory.MAYOR
                || caseCategory == CaseCategory.TRESPASSING || caseCategory == CaseCategory.LAWSUIT
                || caseCategory == CaseCategory.RESIDENT_EVICTION ) {
            paginatedItems.add(new GrantBuildingPermitItem(this));
        }
        this.setPaginatedItems(paginatedItems);
    }

    @Override
    public void initActiveItems() {
        update();
    }

    @Override
    public void update() {
        addActiveItem(0, new ChangeViewClickItem(judgeBaseView) {
            @Override
            public ItemStack itemstack() {
                return new ItemStackBuilder(Material.PAPER)
                        .setName(ChatColor.GREEN + "Back")
                        .addLore(ChatColor.GRAY + "<Click to go back>")
                        .toItemStack();
            }
        });
        super.update();
    }

    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null,54,caseCategory.getName() + " actions");
    }

    public JudgeBaseView getJudgeBaseView() {
        return judgeBaseView;
    }

}
