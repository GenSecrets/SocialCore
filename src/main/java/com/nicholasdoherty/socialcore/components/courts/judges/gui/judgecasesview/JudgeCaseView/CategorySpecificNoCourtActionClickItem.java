package com.nicholasdoherty.socialcore.components.courts.judges.gui.judgecasesview.JudgeCaseView;

import com.nicholasdoherty.socialcore.components.courts.cases.CaseCategory;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 3/2/15.
 */
public class CategorySpecificNoCourtActionClickItem implements ClickItem{
    private JudgeCaseView judgeCaseView;
    private CaseCategory caseCategory;
    public CategorySpecificNoCourtActionClickItem(JudgeCaseView judgeCaseView) {
        this.judgeCaseView = judgeCaseView;
        caseCategory = judgeCaseView.getCase().getCaseCategory();
    }

    @Override
    public void click(boolean right, final boolean shift) {
        CategorySpecificNoCourtActionsView categorySpecificView = new CategorySpecificNoCourtActionsView(judgeCaseView,judgeCaseView.getCase());
        judgeCaseView.getInventoryGUI().setCurrentView(categorySpecificView);
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
