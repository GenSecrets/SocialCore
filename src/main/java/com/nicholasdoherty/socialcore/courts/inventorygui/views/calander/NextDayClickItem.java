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
public class NextDayClickItem implements ClickItem {
    DayView dayView;

    public NextDayClickItem(DayView dayView) {
        this.dayView = dayView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        dayView.changeDay(1);
    }

    @Override
    public ItemStack itemstack() {
        LocalDate day = dayView.getDay();
        int dayOfMonth = day.getDayOfMonth();
        return new ItemStackBuilder(Material.CARPET).setName(ChatColor.GREEN + "Next Day").
                addLore(ChatColor.GREEN + day.minusDays(1).toString("EEEE") + " " + dayOfMonth + DayClickItem.getDayNumberSuffix(dayOfMonth),ChatColor.GRAY + "<Click to view the next day>").toItemStack();
    }
}
