package com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeCaseView;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/9/15.
 */
public class UpdateClickItem implements ClickItem{
    JudgeCaseView judgeCaseView;

    public UpdateClickItem(JudgeCaseView judgeCaseView) {
        this.judgeCaseView = judgeCaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        judgeCaseView.notifyAndUpdate();
    }

    @Override
    public ItemStack itemstack() {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY,1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Update and Notify");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "<Left click to update the case");
        lore.add(ChatColor.GRAY + "and notify the participants>");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
