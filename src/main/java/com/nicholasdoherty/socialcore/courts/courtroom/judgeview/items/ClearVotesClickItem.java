package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class ClearVotesClickItem implements ClickItem {
    JudgeBaseView judgeBaseView;

    public ClearVotesClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (!right)
            return;
        judgeBaseView.getCourtSession().clearVotes();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.TNT)
                .setName(ChatColor.RED + "Clear Votes")
                .addLore(ChatColor.GRAY + "<Left click to"
                ,ChatColor.GRAY + "clear the votes")
                .toItemStack();
    }
}
