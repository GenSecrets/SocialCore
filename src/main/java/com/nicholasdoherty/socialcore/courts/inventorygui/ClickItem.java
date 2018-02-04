package com.nicholasdoherty.socialcore.courts.inventorygui;

import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/3/15.
 */
public interface ClickItem {
    void click(boolean right, final boolean shift);
    
    ItemStack itemstack();
    
    default boolean valid() {
        return true;
    }
}
