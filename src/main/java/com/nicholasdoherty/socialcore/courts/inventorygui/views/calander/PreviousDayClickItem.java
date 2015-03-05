package com.nicholasdoherty.socialcore.courts.inventorygui.views.calander;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.joda.time.LocalDate;

/**
 * Created by john on 1/13/15.
 */
public class PreviousDayClickItem implements ClickItem {
    DayView dayView;

    public PreviousDayClickItem(DayView dayView) {
        this.dayView = dayView;
    }

    @Override
    public void click(boolean right) {
        dayView.changeDay(-1);
    }

    @Override
    public ItemStack itemstack() {
        LocalDate day = dayView.getDay();
        int dayOfMonth = day.getDayOfMonth();
        return new ItemStackBuilder(Material.CARPET).setName(ChatColor.RED + "Previous Day").
                addLore(ChatColor.RED + day.minusDays(1).toString("EEEE") + " " + dayOfMonth + DayClickItem.getDayNumberSuffix(dayOfMonth), ChatColor.GRAY + "<Click to view the previous day>").toItemStack();
    }
}
