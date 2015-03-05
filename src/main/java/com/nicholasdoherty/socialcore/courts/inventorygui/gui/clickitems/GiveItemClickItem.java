package com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/21/15.
 */
public class GiveItemClickItem implements ClickItem{
    private InventoryGUI inventoryGUI;
    private ItemStack itemStack;

    public GiveItemClickItem(InventoryGUI inventoryGUI, ItemStack itemStack) {
        this.inventoryGUI = inventoryGUI;
        this.itemStack = itemStack;
    }

    @Override
    public void click(boolean right) {
        Player p = inventoryGUI.getPlayer();
        if (p != null) {
            p.getInventory().addItem(itemStack.clone());
        }
    }

    @Override
    public ItemStack itemstack() {
        ItemStack clone = itemStack.clone();
        String name = "Get copy of item";
        if (clone.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            String displayName = clone.getItemMeta().getDisplayName();
            name = ChatColor.YELLOW + "Get copy of " + ChatColor.WHITE + displayName;
        }
        return new ItemStackBuilder(clone).setName(name).toItemStack();
    }
}
