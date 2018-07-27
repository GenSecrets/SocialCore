package com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview;

import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 3/15/15.
 */
public class RefundClickItem implements ClickItem{
    private ThrowoutClickItem.ThrowoutCase throwOut;

    public RefundClickItem(ThrowoutClickItem.ThrowoutCase throwOut) {
        this.throwOut = throwOut;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        throwOut.throwOut(true);
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.REDSTONE)
                .setName(ChatColor.RED + "Throw Out Case: with refund")
                .addLore(ChatColor.GRAY + "<Left click to remove"
                        ,ChatColor.GRAY + "due to error and also refund submitter>").toItemStack();
    }
}
