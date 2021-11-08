package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.postactionsview.PostActionsView;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.gui.InventoryView;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class ModifyPostActionsClickItem implements ClickItem {
    InventoryView judgeBaseView;
    PostCourtActionHolder postCourtActionHolder;
    
    public ModifyPostActionsClickItem(final InventoryView judgeBaseView, final PostCourtActionHolder postCourtActionHolder) {
        this.judgeBaseView = judgeBaseView;
        this.postCourtActionHolder = postCourtActionHolder;
    }
    
    @Override
    public void click(final boolean right, final boolean shift) {
        final PostActionsView postActionsView = new PostActionsView(judgeBaseView, postCourtActionHolder);
        judgeBaseView.getInventoryGUI().setCurrentView(postActionsView);
        postActionsView.activate();
    }
    
    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.WRITABLE_BOOK)
                .addEnchant(Enchantment.DURABILITY, 1)
                .setName(ChatColor.GOLD + "Modify Verdict Actions")
                .addLore(ChatColor.GRAY + "<Click to modify verdict actions>")
                .toItemStack();
    }
}
