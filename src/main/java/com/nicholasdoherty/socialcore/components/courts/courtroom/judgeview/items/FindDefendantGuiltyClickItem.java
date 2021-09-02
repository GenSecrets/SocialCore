package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.components.courts.courtroom.actions.AffirmDefendantGuilty;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class FindDefendantGuiltyClickItem implements ClickItem {
    private JudgeBaseView judgeBaseView;

    public FindDefendantGuiltyClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        judgeBaseView.getCourtSession().addPostCourtAction(new AffirmDefendantGuilty());
        judgeBaseView.reactivate();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder()
                .setColoredWool(DyeColor.BLACK)
                .setName(ChatColor.DARK_AQUA + "Find Defendant Guilty")
                .addLore(ChatColor.GRAY + "<Left click to find",
                        ChatColor.GRAY + "the defendant guilty>").toItemStack();
    }

}
