package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.categoryspecific;

import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseLocation;
import com.nicholasdoherty.socialcore.courts.courtroom.actions.GrantChestPermit;
import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 2/15/15.
 */
public class GrantChestPermitItem implements ClickItem {
    private CategorySpecificView categorySpecificView;
    private Case caze;
    public GrantChestPermitItem(CategorySpecificView categorySpecificView) {
        this.categorySpecificView = categorySpecificView;
        caze = categorySpecificView.getJudgeBaseView().getCourtSession().getCaze();
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        if (caze.getCaseMeta().getCaseLocation() == null) {
            return;
        }
        CaseLocation caseLocation = caze.getCaseMeta().getCaseLocation();
        GrantChestPermit grantChestPermit = new GrantChestPermit(caze,caseLocation,categorySpecificView.getJudgeBaseView().getCourtSession().getJudge());
        categorySpecificView.getJudgeBaseView().getCourtSession().addPostCourtAction(grantChestPermit);
    }

    @Override
    public ItemStack itemstack() {
        CaseLocation caseLocation = caze.getCaseMeta().getCaseLocation();
        if (caseLocation == null) {
            return new ItemStackBuilder(Material.PAPER).setName(ChatColor.RED + "Grant Chest Permit").addLore(ChatColor.GRAY + "Mark a location in order to grant a building permit").toItemStack();
        }
        return new ItemStackBuilder(Material.PAPER).setName(ChatColor.GREEN + "Grant Chest Permit")
                .addLore(ChatColor.GRAY + "<Left click to grant a chest permit to",
                        ChatColor.GRAY + "the plaintiff at " + caseLocation.getvLocation().toPrettyString())
                .toItemStack();
    }
}
