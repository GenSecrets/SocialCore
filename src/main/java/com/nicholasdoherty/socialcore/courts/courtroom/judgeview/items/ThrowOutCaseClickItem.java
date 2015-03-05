package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class ThrowOutCaseClickItem implements ClickItem {
    JudgeBaseView judgeBaseView;

    public ThrowOutCaseClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right) {
        if (right)
            return;
        judgeBaseView.throwOut();
        judgeBaseView.reactivate();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.REDSTONE)
                .setName(ChatColor.RED + "Throw Out Case: Mistrial")
                .addLore(ChatColor.GRAY + "<Left click to remove"
                ,ChatColor.GRAY + "due to error>").toItemStack();
    }
}
