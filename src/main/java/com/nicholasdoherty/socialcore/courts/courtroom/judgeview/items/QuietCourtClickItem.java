package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class QuietCourtClickItem implements ClickItem {
    JudgeBaseView judgeBaseView;

    public QuietCourtClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }


    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        judgeBaseView.getCourtSession().silence();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.ANVIL)
                .setName(ChatColor.RED + "Quiet in the Court!")
                .addLore(ChatColor.GRAY + "<Left click to silence the"
                , ChatColor.GRAY + "for a short time>").toItemStack();
    }
}
