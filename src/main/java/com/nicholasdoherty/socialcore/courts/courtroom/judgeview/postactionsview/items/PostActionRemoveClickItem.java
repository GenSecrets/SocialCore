package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.postactionsview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryView;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class PostActionRemoveClickItem implements ClickItem {
    private InventoryView prevView;
    private PostCourtAction postCourtAction;
    private PostCourtActionHolder postCourtActionHolder;
    public PostActionRemoveClickItem(InventoryView prevView, PostCourtAction postCourtAction, PostCourtActionHolder postCourtActionHolder) {
        this.prevView = prevView;
        this.postCourtAction = postCourtAction;
        this.postCourtActionHolder = postCourtActionHolder;
    }

    @Override
    public void click(boolean right) {
        if (right)
            return;
        postCourtActionHolder.removePostCourtAction(postCourtAction);
        prevView.reactivate();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.IRON_PICKAXE)
                .setName(postCourtAction.prettyDescription())
                .addLore(ChatColor.RED + "<Left click to remove",
                        ChatColor.RED + "this verdict action>")
                .toItemStack();
    }
}
