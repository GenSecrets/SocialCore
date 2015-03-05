package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.categoryspecific;

import com.nicholasdoherty.socialcore.courts.courtroom.actions.GrantSameSexMarriage;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 3/2/15.
 */
public class GrantSameSexMarriageItem implements ClickItem {
    PostCourtActionHolder postCourtActionHolder;

    public GrantSameSexMarriageItem(PostCourtActionHolder postCourtActionHolder) {
        this.postCourtActionHolder = postCourtActionHolder;
    }

    @Override
    public void click(boolean right) {
        if (right)
            return;
        if (postCourtActionHolder.getCase().getDefendent() == null) {
            return;
        }
        postCourtActionHolder.addPostCourtAction(new GrantSameSexMarriage(postCourtActionHolder.getCase()));
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.ANVIL).setDurability((short) 2)
                .setName(ChatColor.GREEN + "Grant SameSex Marriage")
                .addLore(ChatColor.GRAY + "<Left click to grant"
                        ,ChatColor.GRAY + "same-sex marriage between petitioner"
                        ,ChatColor.GRAY + "and spouse>").toItemStack();
    }
}
