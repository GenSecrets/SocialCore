package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.categoryspecific.genderChanges;

import com.nicholasdoherty.socialcore.components.courts.courtroom.actions.GenderChange;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.components.genders.Gender;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 3/29/15.
 */
public class UnspecifiedGenderChangeItem implements ClickItem{
    PostCourtActionHolder postCourtActionHolder;

    public UnspecifiedGenderChangeItem(PostCourtActionHolder postCourtActionHolder) {
        this.postCourtActionHolder = postCourtActionHolder;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        if (postCourtActionHolder.getCase().getPlantiff() == null) {
            return;
        }
        postCourtActionHolder.addPostCourtAction(new GenderChange(postCourtActionHolder.getCase(),  new Gender("UNSPECIFIED")));
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.LAPIS_BLOCK)
                .setName(ChatColor.GREEN + "Unset Gender")
                .addLore(ChatColor.GRAY + "<Left click to unset"
                        , ChatColor.GRAY + "the plaintiff's gender>").toItemStack();
    }
}
