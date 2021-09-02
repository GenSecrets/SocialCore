package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.categoryspecific;

import com.nicholasdoherty.socialcore.components.courts.courtroom.actions.GrantCivilMarriage;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.PostCourtActionHolder;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 3/29/15.
 */
public class GrantCivilMarriageItem implements ClickItem{
    private PostCourtActionHolder categorySpecificView;

    public GrantCivilMarriageItem(PostCourtActionHolder categorySpecificView) {
        this.categorySpecificView = categorySpecificView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        categorySpecificView.addPostCourtAction(new GrantCivilMarriage(categorySpecificView.getCase()));
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.GOLD_INGOT).setDurability((short) 2)
                .setName(ChatColor.GREEN + "Grant Divorce")
                .addLore(ChatColor.GRAY + "<Left click to grant"
                        ,ChatColor.GRAY + "divorce between the plaintiff"
                        ,ChatColor.GRAY + "and defendant>").toItemStack();
    }
}
