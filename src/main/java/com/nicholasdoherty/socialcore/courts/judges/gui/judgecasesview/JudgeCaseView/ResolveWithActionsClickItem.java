package com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeCaseView;

import com.voxmc.voxlib.gui.ClickItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 3/2/15.
 */
public class ResolveWithActionsClickItem implements ClickItem {
    private JudgeCaseView judgeCaseView;

    public ResolveWithActionsClickItem(JudgeCaseView judgeCaseView) {
        this.judgeCaseView = judgeCaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        judgeCaseView.resolveWithAction();
    }

    @Override
    public ItemStack itemstack() {
        ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE);
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY,1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Resolve With Actions");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "<Left click to resolve the case");
        lore.add(ChatColor.GRAY + "and with specified actions>");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
