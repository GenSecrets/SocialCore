package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class CallYayNayVoteClickItem implements ClickItem {
    JudgeBaseView judgeBaseView;

    public CallYayNayVoteClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        judgeBaseView.getCourtSession().callYayNay();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder()
                .setPlayerHead("JesusTheCaffeine")
                .setName(ChatColor.GREEN + "Call Yay or Nay Vote")
                .addLore(ChatColor.GRAY  + "<Left click to call a",
                        ChatColor.GRAY +"yay or nay vote>")
                .toItemStack();
    }
}
