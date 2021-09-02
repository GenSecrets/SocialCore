package com.nicholasdoherty.socialcore.components.courts.judges.secretaries.gui.caseview;

import com.voxmc.voxlib.gui.ClickItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/9/15.
 */
public class ProcessClickItem implements ClickItem {
    SecCaseView secCaseView;

    public ProcessClickItem(SecCaseView secCaseView) {
        this.secCaseView = secCaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        secCaseView.process();
    }

    @Override
    public ItemStack itemstack() {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Process");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "<Left click to process this case>");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
