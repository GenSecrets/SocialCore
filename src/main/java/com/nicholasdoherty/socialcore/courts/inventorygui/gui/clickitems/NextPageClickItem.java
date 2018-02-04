package com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.PaginatedItemView;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by john on 1/6/15.
 */
public class NextPageClickItem implements ClickItem {
    PaginatedItemView paginatedItemView;

    public NextPageClickItem(PaginatedItemView paginatedItemView) {
        this.paginatedItemView = paginatedItemView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        paginatedItemView.setPage(paginatedItemView.getPage()+1);
        paginatedItemView.update();
    }

    @Override
    public ItemStack itemstack() {
        ItemStack prevItem = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = prevItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "Next Page");
        prevItem.setItemMeta(itemMeta);
        return prevItem;
    }
}
