package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.PostCourtActionHolder;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class PostCourtActionsClickItem implements ClickItem {
    PostCourtActionHolder courtSession;

    public PostCourtActionsClickItem(PostCourtActionHolder courtSession) {
        this.courtSession = courtSession;
    }

    @Override
    public void click(boolean right, final boolean shift) {

    }

    @Override
    public ItemStack itemstack() {
        ItemStackBuilder itemStackBuilder = new ItemStackBuilder(Material.BOOK);
        itemStackBuilder.setName("Case Summary");
        for (PostCourtAction postCourtAction : courtSession.getPostCourtActions()) {
            itemStackBuilder.addLore(ChatColor.YELLOW + postCourtAction.prettyDescription());
        }
        return itemStackBuilder.toItemStack();
    }
}
