package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.courtroom.actions.FinePlantiff;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeBaseView;
import com.nicholasdoherty.socialcore.components.courts.inputlib.InputLib;
import com.nicholasdoherty.socialcore.components.courts.inputlib.IntegerInputRunnable;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by john on 1/14/15.
 */
public class FinePlaintiffClickItem implements ClickItem {
    private JudgeBaseView judgeBaseView;

    public FinePlaintiffClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        final Player p = judgeBaseView.getInventoryGUI().getPlayer();
        UUID uuid = p.getUniqueId();
        judgeBaseView.getInventoryGUI().specialClose();
        final InputLib inputLib = SocialCore.plugin.getInputLib();
        inputLib.add(uuid, new IntegerInputRunnable(uuid) {
            @Override
            public boolean valid(int num) {
                if (num > 0)
                    return true;
                return false;
            }

            @Override
            public void run(int num) {
                judgeBaseView.getCourtSession().addPostCourtAction(new FinePlantiff(num,judgeBaseView.getCourtSession().getCaze()));
                judgeBaseView.getInventoryGUI().open();
            }
        });
        inputLib.clearChat(p);
        inputLib.sendMessage(p, ChatColor.GREEN + "To exit, type cancel");
        inputLib.sendMessage(p, ChatColor.GREEN + "Enter the number of voxels to fine the defendant: ");
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.GOLD_INGOT)
                .setName(ChatColor.GREEN + "Fine Plaintiff")
                .addLore(ChatColor.GRAY + "<Left click to enter a voxel",
                        ChatColor.GRAY + "amount to fine the plaintiff>")
                .toItemStack();
    }
}
