package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
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
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        CourtSession courtSession = judgeBaseView.getCourtSession();
        courtSession.setInRecess(false);
        courtSession.getCourtRoom().sendMessage(ChatColor.YELLOW + "Judge " + courtSession.getJudge().getName() + " has called the court back together.");
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
