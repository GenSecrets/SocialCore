package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.components.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeBaseView;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.categoryspecific.CategorySpecificView;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class CategorySpecificClickItem implements ClickItem {
    private JudgeBaseView judgeBaseView;
    private CaseCategory caseCategory;
    public CategorySpecificClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
        caseCategory = judgeBaseView.getCourtSession().getCaze().getCaseCategory();
    }

    @Override
    public void click(boolean right, final boolean shift) {
        CategorySpecificView categorySpecificView = new CategorySpecificView(judgeBaseView);
        judgeBaseView.getInventoryGUI().setCurrentView(categorySpecificView);
        categorySpecificView.activate();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.COMPASS)
                .setName(ChatColor.GREEN + caseCategory.getName() + " specific actions")
                .addLore(ChatColor.GRAY + "<Left click to view",
                        ChatColor.GRAY + caseCategory.getName() + " specific actions>")
                .toItemStack();
    }
}
