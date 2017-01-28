package com.nicholasdoherty.socialcore.courts.inventorygui;

import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/3/15.
 */
public interface ClickItem{
    public void click(boolean right);
    public ItemStack itemstack();
    public default boolean valid() {
        return true;
    }
}
