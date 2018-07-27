package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.categoryspecific;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.courtroom.actions.SexChange;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.PostCourtActionHolder;
import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 3/29/15.
 */
public class UngenderedSexChangeItem implements ClickItem{
    PostCourtActionHolder postCourtActionHolder;

    public UngenderedSexChangeItem(PostCourtActionHolder postCourtActionHolder) {
        this.postCourtActionHolder = postCourtActionHolder;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        if (postCourtActionHolder.getCase().getPlantiff() == null) {
            return;
        }
        postCourtActionHolder.addPostCourtAction(new SexChange(postCourtActionHolder.getCase(), SocialCore.Gender.UNSPECIFIED));
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.LAPIS_BLOCK).setDurability((short) 2)
                .setName(ChatColor.GREEN + "Unset Gender")
                .addLore(ChatColor.GRAY + "<Left click to unset"
                        , ChatColor.GRAY + "the plaintiff's gender>").toItemStack();
    }
}
