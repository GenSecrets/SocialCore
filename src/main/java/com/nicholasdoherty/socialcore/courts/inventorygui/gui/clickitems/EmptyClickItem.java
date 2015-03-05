package com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/9/15.
 */
public class EmptyClickItem implements ClickItem {
    ItemStack itemStack;

    public EmptyClickItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void click(boolean right) {

    }

    @Override
    public ItemStack itemstack() {
        return itemStack;
    }
}
