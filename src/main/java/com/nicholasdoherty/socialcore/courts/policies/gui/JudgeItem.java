package com.nicholasdoherty.socialcore.courts.policies.gui;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 9/12/16.
 */
public class JudgeItem implements ClickItem {
    private Judge judge;
    private boolean hasConfirmed;

    public JudgeItem(Judge judge, boolean hasConfirmed) {
        this.judge = judge;
        this.hasConfirmed = hasConfirmed;
    }

    @Override
    public void click(boolean right) {

    }

    @Override
    public ItemStack itemstack() {
        String lore = ChatColor.GRAY + "Has not confirmed this policy.";
        if (hasConfirmed) {
            lore = ChatColor.GREEN + "Has confirmed this policy.";
        }

        return new ItemStackBuilder().setPlayerHead(judge.getName())
                .setName(judge.getName())
                .addLore(lore)
                .toItemStack();
    }


}
