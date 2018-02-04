package com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/9/15.
 */
public class ThrowoutClickItem implements ClickItem {
    ThrowoutCase throwOut;

    public ThrowoutClickItem(ThrowoutCase throwOut) {
        this.throwOut = throwOut;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        throwOut.throwOut(false);
    }

    @Override
    public ItemStack itemstack() {
        ItemStack itemStack = new ItemStack(Material.REDSTONE_COMPARATOR);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "<Left click to throw case out>");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Throw out");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    public interface ThrowoutCase {
        public void throwOut(boolean refund);
    }
}