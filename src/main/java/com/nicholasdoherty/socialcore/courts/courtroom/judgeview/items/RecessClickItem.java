package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class RecessClickItem implements ClickItem {
    private JudgeBaseView judgeBaseView;

    public RecessClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        CourtSession courtSession = judgeBaseView.getCourtSession();
        courtSession.setInRecess(true);
        courtSession.getCourtRoom().sendMessage(ChatColor.YELLOW + "Judge " + courtSession.getJudge().getName() + " has called a brief recess.");
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.APPLE)
                .setName(ChatColor.DARK_AQUA + "Call for a brief recess")
                .addLore(ChatColor.GRAY + "Left click to give the",
                        ChatColor.GRAY + "court a brief recess>").toItemStack();
    }
}
