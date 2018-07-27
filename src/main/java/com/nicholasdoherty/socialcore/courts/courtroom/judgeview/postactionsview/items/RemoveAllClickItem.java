package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.postactionsview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.PostCourtActionHolder;
import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.gui.inventorygui.InventoryView;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class RemoveAllClickItem implements ClickItem {
    InventoryView prevView;
    PostCourtActionHolder postCourtActionHolder;

    public RemoveAllClickItem(InventoryView prevView, PostCourtActionHolder postCourtActionHolder) {
        this.prevView = prevView;
        this.postCourtActionHolder = postCourtActionHolder;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        postCourtActionHolder.getPostCourtActions().clear();
        prevView.reactivate();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.REDSTONE)
                .setName(ChatColor.RED + "Remove all")
                .addLore(ChatColor.GRAY +"<Left click to remove"
                ,ChatColor.GRAY + "all verdict actions>")
                .toItemStack();
    }
}
