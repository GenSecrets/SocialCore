package com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/21/15.
 */
public class BookPageItem implements ClickItem {
    private List<String> lines;

    public BookPageItem(List<String> lines) {
        this.lines = whiteWash(lines);
    }

    @Override
    public void click(boolean right, final boolean shift) {

    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.PAPER).setName(" ").addLore(lines).toItemStack();
    }
    private static List<String> whiteWash(List<String> in) {
        List<String> white = new ArrayList<>();
        for (String inL : in) {
            white.add(ChatColor.WHITE + inL);
        }
        return white;
    }
}
