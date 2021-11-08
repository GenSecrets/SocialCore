package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.categoryspecific.genderChanges;

import com.nicholasdoherty.socialcore.components.courts.courtroom.actions.GenderChange;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.components.genders.Gender;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 3/29/15.
 */
public class MaleGenderChangeItem implements ClickItem{
    PostCourtActionHolder postCourtActionHolder;

    public MaleGenderChangeItem(PostCourtActionHolder postCourtActionHolder) {
        this.postCourtActionHolder = postCourtActionHolder;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        if (postCourtActionHolder.getCase().getPlantiff() == null) {
            return;
        }
        postCourtActionHolder.addPostCourtAction(new GenderChange(postCourtActionHolder.getCase(), new Gender("MALE")));
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.IRON_HELMET).addEnchant(Enchantment.DURABILITY, 1)
                .setName(ChatColor.GREEN + "Male Gender Change")
                .addLore(ChatColor.GRAY + "<Left click to change"
                        ,ChatColor.GRAY + "the plaintiff's gender"
                        ,ChatColor.GRAY + "to male>").toItemStack();
    }
}
