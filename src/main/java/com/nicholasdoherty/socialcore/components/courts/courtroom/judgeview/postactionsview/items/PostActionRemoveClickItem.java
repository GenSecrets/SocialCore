package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.postactionsview.items;

import com.nicholasdoherty.socialcore.components.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.PostCourtActionHolder;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.gui.InventoryView;
import com.voxmc.voxlib.util.ItemStackBuilder;
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
    public void click(boolean right, final boolean shift) {
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
