package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.categoryspecific.genderChanges;

import com.nicholasdoherty.socialcore.components.courts.courtroom.actions.GenderChange;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.components.genders.Gender;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NonBinaryGenderChange implements ClickItem {
    PostCourtActionHolder postCourtActionHolder;

    public NonBinaryGenderChange(PostCourtActionHolder postCourtActionHolder) {
        this.postCourtActionHolder = postCourtActionHolder;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        if (postCourtActionHolder.getCase().getPlantiff() == null) {
            return;
        }
        postCourtActionHolder.addPostCourtAction(new GenderChange(postCourtActionHolder.getCase(),  new Gender("NONBINARY")));
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.REDSTONE)
                .setName(ChatColor.GREEN + "Non-Binary Gender Change")
                .addLore(ChatColor.GRAY + "<Left click to change"
                        ,ChatColor.GRAY + "the plaintiff's gender"
                        ,ChatColor.GRAY + "to non-binary>").toItemStack();
    }
}