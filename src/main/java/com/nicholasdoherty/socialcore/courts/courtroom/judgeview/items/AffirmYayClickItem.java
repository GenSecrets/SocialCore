package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.actions.AffirmYay;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class AffirmYayClickItem implements ClickItem {
    private JudgeBaseView judgeBaseView;

    public AffirmYayClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        judgeBaseView.getCourtSession().addPostCourtAction(new AffirmYay());
        judgeBaseView.reactivate();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder()
                .setColoredWool(DyeColor.GREEN)
                .setName(ChatColor.DARK_AQUA + "Affirm Yay Vote")
                .addLore(ChatColor.GRAY + "<Left click to affirm yay",
                        ChatColor.GRAY + "into court summary>").toItemStack();
    }
}
