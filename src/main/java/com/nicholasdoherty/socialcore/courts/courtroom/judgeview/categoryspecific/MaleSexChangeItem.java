package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.categoryspecific;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.courtroom.actions.SexChange;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 3/29/15.
 */
public class MaleSexChangeItem implements ClickItem{
    PostCourtActionHolder postCourtActionHolder;

    public MaleSexChangeItem(PostCourtActionHolder postCourtActionHolder) {
        this.postCourtActionHolder = postCourtActionHolder;
    }

    @Override
    public void click(boolean right) {
        if (right)
            return;
        if (postCourtActionHolder.getCase().getPlantiff() == null) {
            return;
        }
        postCourtActionHolder.addPostCourtAction(new SexChange(postCourtActionHolder.getCase(), SocialCore.Gender.MALE));
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.IRON_HELMET).setDurability((short) 2)
                .setName(ChatColor.GREEN + "Male Sex Change")
                .addLore(ChatColor.GRAY + "<Left click to change"
                        ,ChatColor.GRAY + "the plaintiff's gender"
                        ,ChatColor.GRAY + "to male>").toItemStack();
    }
}
