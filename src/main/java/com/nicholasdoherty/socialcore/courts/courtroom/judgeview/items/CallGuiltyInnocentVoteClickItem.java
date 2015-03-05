package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class CallGuiltyInnocentVoteClickItem implements ClickItem{
    private JudgeBaseView judgeBaseView;

    public CallGuiltyInnocentVoteClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right) {
        if (right)
            return;
        judgeBaseView.getCourtSession().callInnocentGuilty();
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder()
                .setPlayerHead("Zombiemold")
                .setName(ChatColor.GREEN + "Call Guilty or Innocent Vote")
                .addLore(ChatColor.GRAY + "<Left click to call a",
                        ChatColor.GRAY + "guilty or innocent vote>").toItemStack();
    }
}
