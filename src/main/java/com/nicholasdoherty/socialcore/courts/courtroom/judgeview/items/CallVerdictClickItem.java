package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class CallVerdictClickItem implements ClickItem {
    JudgeBaseView judgeBaseView;
    
    public CallVerdictClickItem(final JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }
    
    @Override
    public void click(final boolean right, final boolean shift) {
        if(right) {
            return;
        }
        judgeBaseView.callVerdict();
    }
    
    @Override
    public ItemStack itemstack() {
        final ItemStackBuilder itemStackBuilder = new ItemStackBuilder(Material.GOLDEN_AXE);
        itemStackBuilder.addEnchant(Enchantment.DURABILITY, 1);
        itemStackBuilder.setName(ChatColor.YELLOW + "Call Verdict");
        itemStackBuilder.addLore(ChatColor.GRAY + "<Left click to announce your verdict>",
                ChatColor.DARK_AQUA + "Pressing this will end the court,",
                ChatColor.DARK_AQUA + "and officiate your verdict.");
        return itemStackBuilder.toItemStack();
    }
}
