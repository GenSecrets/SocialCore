package com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems;

import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseHistory;
import com.nicholasdoherty.socialcore.courts.cases.CourtDate;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.SecCaseView;
import com.nicholasdoherty.socialcore.utils.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/9/15.
 */
public class CaseInfoClickItem implements ClickItem{
    Case caze;

    public CaseInfoClickItem(Case caze) {
        this.caze = caze;
    }

    @Override
    public void click(boolean right) {

    }

    @Override
    public ItemStack itemstack() {
        ItemStack itemStack = caze.getCaseBook().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public List<String> lore() {
        List<String> lore = new ArrayList<>();
        if (caze.isLocked()) {
            lore.add(ChatColor.RED + ""+ChatColor.BOLD + "LOCKED");
        }
        lore.add(ChatColor.GRAY + "Status: " + caze.getCaseStatus());
        if (caze.getPlantiff() != null) {
            lore.add(SecCaseView.replaceNouns(ChatColor.RED + "Plaintiff: " + caze.getPlantiff().getName(),caze.getCaseCategory()));
        }
        if (caze.getDefendent() != null) {
            lore.add(SecCaseView.replaceNouns(ChatColor.BLUE + "Defendant: " + caze.getDefendent().getName(),caze.getCaseCategory()));
        }
        CaseHistory.HistoryEntry processing = caze.getCaseHistory().getProcessingEntry();
        if (processing != null) {
            lore.add(ChatColor.GOLD + "Processed by: " + processing.getResponsible());
            lore.add(ChatColor.DARK_AQUA + TextUtil.formatDate(processing.getDate()));
        }
        if (caze.getCaseCategory() != null) {
            lore.add(ChatColor.LIGHT_PURPLE + "Category: " + caze.getCaseCategory().getName());
        }
        if (caze.getCourtDate() != null) {
            CourtDate courtDate = caze.getCourtDate();
            lore.add(ChatColor.DARK_GREEN + "Court date with Judge " + courtDate.getJudge().getName() +" at");
            lore.add(ChatColor.DARK_GREEN + TextUtil.formatDate(courtDate.getTime()));
        }
        if (caze.getResolve() != null && caze.getResolve().getPostCourtActionList().size() > 0) {
            lore.add(ChatColor.GREEN + "Resolution: ");
            for (String postCourtAction : caze.getResolve().getPostCourtActionList()) {
                lore.add(postCourtAction);
            }
        }
        return lore;
    }

    public Case getCaze() {
        return caze;
    }
}
