package com.nicholasdoherty.socialcore.components.courts.judges.gui.judgecasesview.JudgeCaseView;

import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.categoryspecific.GrantCivilMarriageItem;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.categoryspecific.GrantDivorceItem;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.categoryspecific.genderChanges.*;
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
 * Created by john on 3/2/15.
 */
public class CategorySpecificNoCourtActionsView extends PaginatedItemView{
    private Case caze;
    private JudgeCaseView processedCasesView;
    public CategorySpecificNoCourtActionsView(JudgeCaseView judgeProcessedCasesView, Case caze) {
        super(judgeProcessedCasesView.getInventoryGUI(), 54);
        this.caze = caze;
        this.processedCasesView = judgeProcessedCasesView;
        this.setStartEnd(9,54);
    }

    @Override
    public void update() {
        List<ClickItem> clickItemList = new ArrayList<>();
        if (caze.getCaseCategory() ==null)
            return;
        if (caze.getCaseCategory() == CaseCategory.DIVORCE) {
            clickItemList.add(new GrantDivorceItem(processedCasesView));
        }
        if (caze.getCaseCategory() == CaseCategory.GENDER_CHANGE) {
            clickItemList.add(new UnspecifiedGenderChangeItem(processedCasesView));
            clickItemList.add(new FemaleGenderChangeItem(processedCasesView));
            clickItemList.add(new MaleGenderChangeItem(processedCasesView));
            clickItemList.add(new NonBinaryGenderChange(processedCasesView));
            clickItemList.add(new OtherGenderChangeItem(processedCasesView));
        }
        if (caze.getCaseCategory() == CaseCategory.CIVIL_MARRIAGE) {
            clickItemList.add(new GrantCivilMarriageItem(processedCasesView));
        }
        this.setPaginatedItems(clickItemList);
        addActiveItem(0, new ChangeViewClickItem(processedCasesView) {
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
        return Bukkit.createInventory(null,54,"Category Specific Actions");
    }
}
