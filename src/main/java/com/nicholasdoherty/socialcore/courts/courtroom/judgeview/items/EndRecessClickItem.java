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
public class EndRecessClickItem implements ClickItem {
    private JudgeBaseView judgeBaseView;

    public EndRecessClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right) {
        if (right)
            return;
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.APPLE)
                .setName(ChatColor.GREEN + "End recess early")
                .addLore(ChatColor.GRAY + "<Left click to end",
                        ChatColor.GRAY + "recess early>")
                .toItemStack();
    }
}
