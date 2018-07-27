package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.actions.AffirmNay;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class AffirmNayClickItem implements ClickItem {
    private JudgeBaseView judgeBaseView;

    public AffirmNayClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        judgeBaseView.getCourtSession().addPostCourtAction(new AffirmNay());
        judgeBaseView.reactivate();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder()
                .setColoredWool(DyeColor.RED)
                .setName(ChatColor.DARK_AQUA + "Affirm Nay Vote")
                .addLore(ChatColor.GRAY + "<Left click to affirm nay",
                        ChatColor.GRAY + "into court summary>").toItemStack();
    }
}
