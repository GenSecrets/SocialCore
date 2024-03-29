package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.components.courts.courtroom.actions.AffirmPlaintiffGuilty;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class FindPlantiffGuiltyClickItem implements ClickItem {
    JudgeBaseView judgeBaseView;

    public FindPlantiffGuiltyClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        judgeBaseView.getCourtSession().addPostCourtAction(new AffirmPlaintiffGuilty());
        judgeBaseView.reactivate();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder()
                .setColoredWool(DyeColor.WHITE)
                .setName(ChatColor.DARK_AQUA + "Find Plaintiff Guilty")
                .addLore(ChatColor.GRAY + "<Left click to find",
                        ChatColor.GRAY + "the plaintiff guilty>").toItemStack();
    }
}
