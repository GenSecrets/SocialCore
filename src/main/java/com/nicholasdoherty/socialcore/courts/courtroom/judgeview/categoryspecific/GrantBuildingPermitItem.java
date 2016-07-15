package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.categoryspecific;

import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseLocation;
import com.nicholasdoherty.socialcore.courts.courtroom.actions.GrantBuildingPermit;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 2/15/15.
 */
public class GrantBuildingPermitItem implements ClickItem {
    private CategorySpecificView categorySpecificView;
    private Case caze;
    public GrantBuildingPermitItem(CategorySpecificView categorySpecificView) {
        this.categorySpecificView = categorySpecificView;
        caze = categorySpecificView.getJudgeBaseView().getCourtSession().getCaze();
    }

    @Override
    public void click(boolean right) {
        if (right)
            return;
        if (caze != null && caze.getCaseMeta() != null && caze.getCaseMeta().getCaseLocation() == null) {
            return;
        }
        CaseLocation caseLocation = caze.getCaseMeta().getCaseLocation();
        if (caseLocation != null) {
            GrantBuildingPermit grantBuildingPermit = new GrantBuildingPermit(caze,caseLocation,categorySpecificView.getJudgeBaseView().getCourtSession().getJudge());
            categorySpecificView.getJudgeBaseView().getCourtSession().addPostCourtAction(grantBuildingPermit);
        }else {
            categorySpecificView.getJudgeBaseView().getInventoryGUI().getPlayer().sendMessage(ChatColor.RED + "No case location.");
        }
    }

    @Override
    public ItemStack itemstack() {
        CaseLocation caseLocation = caze.getCaseMeta().getCaseLocation();
        if (caseLocation == null) {
            return new ItemStackBuilder(Material.PAPER).setName(ChatColor.RED + "Grant Building Permit").addLore(ChatColor.GRAY + "Mark a location in order to grant a building permit").toItemStack();
        }
        return new ItemStackBuilder(Material.PAPER).setName(ChatColor.GREEN + "Grant Building Permit")
                .addLore(ChatColor.GRAY + "<Left click to grant a building permit to",
                        ChatColor.GRAY + "the plaintiff at " + caseLocation.getvLocation().toPrettyString())
                .toItemStack();
    }
}
