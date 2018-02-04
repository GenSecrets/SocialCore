package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 3/14/15.
 */
public class RefundCaseClickItem implements ClickItem{
    JudgeBaseView judgeBaseView;

    public RefundCaseClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        judgeBaseView.throwOut(true);
        judgeBaseView.reactivate();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.REDSTONE)
                .setName(ChatColor.RED + "Throw Out Case: with refund")
                .addLore(ChatColor.GRAY + "<Left click to remove"
                        ,ChatColor.GRAY + "due to error and also refund submitter>").toItemStack();
    }
}
