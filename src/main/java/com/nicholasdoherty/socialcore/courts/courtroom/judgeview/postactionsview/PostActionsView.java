package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.postactionsview;

import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.postactionsview.items.PostActionRemoveClickItem;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.postactionsview.items.RemoveAllClickItem;
import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.gui.inventorygui.InventoryView;
import com.voxmc.voxlib.gui.inventorygui.gui.clickitems.ChangeViewClickItem;
import com.voxmc.voxlib.gui.inventorygui.views.PaginatedItemView;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/14/15.
 */
public class PostActionsView extends PaginatedItemView {
    private InventoryView judgeBaseView;
    private PostCourtActionHolder postCourtActionHolder;

    public PostActionsView(InventoryView judgeBaseView, PostCourtActionHolder postCourtActionHolder) {
        super(judgeBaseView.getInventoryGUI(),54);
        this.judgeBaseView = judgeBaseView;
        this.postCourtActionHolder = postCourtActionHolder;
        setStartEnd(9,53);
    }

    @Override
    public void initActiveItems() {
        update();
    }

    public PostCourtActionHolder getPostCourtActionHolder() {
        return postCourtActionHolder;
    }

    @Override
    public void update() {
        clearActiveItems();
        List<ClickItem> clickItems = new ArrayList<>();
        for (PostCourtAction postCourtAction : postCourtActionHolder.getPostCourtActions()) {
            clickItems.add(new PostActionRemoveClickItem(this,postCourtAction,postCourtActionHolder));
        }
        this.setPaginatedItems(clickItems);
        addActiveItem(0, new ChangeViewClickItem(judgeBaseView) {
            @Override
            public ItemStack itemstack() {
                return new ItemStackBuilder(Material.PAPER)
                        .setName(ChatColor.AQUA + "Back")
                        .addLore(ChatColor.GRAY + "<Click to go back>")
                        .toItemStack();
            }
        });
        addActiveItem(1, new RemoveAllClickItem(this,postCourtActionHolder));
        super.update();
    }

    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null,54, "Edit Verdict Actions");
    }
}
