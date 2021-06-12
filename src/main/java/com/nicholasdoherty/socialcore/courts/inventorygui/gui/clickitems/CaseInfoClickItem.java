package com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems;

import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseHistory;
import com.nicholasdoherty.socialcore.courts.cases.CourtDate;
import com.voxmc.voxlib.gui.ClickItem;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.SecCaseView;
import com.voxmc.voxlib.util.TextUtil;
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
    public void click(boolean right, final boolean shift) {

    }

    @Override
    public ItemStack itemstack() {
        ItemStack itemStack = caze.getCaseBook().clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + caze.caseName());
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
        String filedTime = caze.getCaseHistory().getSubmitterEntry().map(entry -> TextUtil.formatDate(entry.getDate())).orElse("N/A");
        String resolvedTime = caze.getCaseHistory().getResolveEntry().map(entry -> TextUtil.formatDate(entry.getDate())).orElse("N/A");
        if (caze.getPlantiff() != null) {
            lore.add(SecCaseView.replaceNouns(ChatColor.RED + "Plaintiff: " + caze.getPlantiff().getName(),caze.getCaseCategory()));
        }
        if (caze.getDefendent() != null) {
            lore.add(SecCaseView.replaceNouns(ChatColor.BLUE + "Defendant: " + caze.getDefendent().getName(),caze.getCaseCategory()));
        }
        lore.add(ChatColor.DARK_AQUA + "Filed: " + filedTime);
        CaseHistory.HistoryEntry processing = caze.getCaseHistory().getProcessingEntry();
        if (processing != null) {
            lore.add(ChatColor.GOLD + "Processed by: " + processing.getResponsible());
            lore.add(ChatColor.DARK_AQUA + TextUtil.formatDate(processing.getDate()));
        }
        lore.add(ChatColor.DARK_AQUA + "Resolved: " + resolvedTime);
        if (caze.getCaseCategory() != null) {
            lore.add(ChatColor.LIGHT_PURPLE + "Category: " + caze.getCaseCategory().getName());
        }
        if (caze.getCourtDate() != null) {
            CourtDate courtDate = caze.getCourtDate();
            if (courtDate.getJudge() != null) {
                lore.add(ChatColor.DARK_GREEN + "Court date with Judge " + courtDate.getJudge().getName() +" at");
            }else {
                caze.removeCourtDate();
            }
            lore.add(ChatColor.DARK_GREEN + TextUtil.formatDate(courtDate.getTime()));
        }
        if (caze.getResolve() != null && caze.getResolve().getPostCourtActionList().size() > 0) {
            lore.add(ChatColor.GREEN + "Resolution: ");
            for (String postCourtAction : caze.getResolve().getPostCourtActionList()) {
                lore.add(clean(postCourtAction));
            }
        }
        return lore;
    }
    private static String clean(String in) {
        if (in == null)
            return null;
        if (in.contains("A building permit will be granted to the plaintiff at")) {
            String[] split = in.split("A building permit will be granted to the plaintiff at");
            if (split.length > 1) {
                String numbers = split[1];
                String newNumbers = "";
                for (String coord : numbers.split(",")) {
                    try {
                        double number = Double.valueOf(coord);
                        int norm = (int) Math.round(number);
                        newNumbers = newNumbers + norm + ",";
                    }catch (Exception e) {
                        newNumbers = newNumbers + coord +",";
                    }
                }
                if (newNumbers.length() > 2) {
                    newNumbers = newNumbers.substring(0,newNumbers.length()-2);
                }
                in = ChatColor.GREEN + "A building permit will be granted to the plaintiff at " + newNumbers;
            }
        }
        return in;
    }
    public Case getCaze() {
        return caze;
    }
}
