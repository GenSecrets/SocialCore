package com.nicholasdoherty.socialcore.components.courts.judges.secretaries.gui.caseview;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseCategory;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.VoxStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/8/15.
 */
public class AssignCategoryClickItem implements ClickItem{
    AssignCategory assignCategory;
    CaseCategory caseCategory;
    AssignCategory secCaseView;
    public AssignCategoryClickItem(AssignCategory secCaseView, AssignCategory assignCategory, CaseCategory caseCategory) {
        this.secCaseView = secCaseView;
        this.assignCategory = assignCategory;
        this.caseCategory = caseCategory;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (!right) {
            assignCategory.assignCategory(caseCategory);
        }
    }

    @Override
    public ItemStack itemstack() {
        ItemStack itemStack = new ItemStack(caseCategory.getMat());
        String name = ChatColor.GREEN + "Assign as " + caseCategory.getName();
        List<String> lore = new ArrayList<>();
        lore.addAll(VoxStringUtils.splitLoreFormat(Courts.getCourts().getCourtsLangManager().caseCategoryDescription(caseCategory)));
        lore.add(ChatColor.GRAY + "<Left click to assign>");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    public interface AssignCategory {
        public void assignCategory(CaseCategory caseCategory);
    }
}
