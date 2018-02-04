package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.postactionsview.PostActionsView;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryView;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
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

    public ModifyPostActionsClickItem(InventoryView judgeBaseView, PostCourtActionHolder postCourtActionHolder) {
        this.judgeBaseView = judgeBaseView;
        this.postCourtActionHolder = postCourtActionHolder;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        PostActionsView postActionsView = new PostActionsView(judgeBaseView,postCourtActionHolder);
        judgeBaseView.getInventoryGUI().setCurrentView(postActionsView);
        postActionsView.activate();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.BOOK_AND_QUILL)
                .addEnchant(Enchantment.DURABILITY,1)
                .setName(ChatColor.GOLD + "Modify Verdict Actions")
                .addLore(ChatColor.GRAY + "<Click to modify verdict actions>")
                .toItemStack();
    }
}
