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
public class CurrentDayClickItem implements ClickItem {
    DayView dayView;

    public CurrentDayClickItem(DayView dayView) {
        this.dayView = dayView;
    }

    @Override
    public void click(boolean right) {
        dayView.returnToMonthView();
    }

    @Override
    public ItemStack itemstack() {
        LocalDate localDate = dayView.getDay();
        ItemStackBuilder itemStackBuilder = new ItemStackBuilder(Material.ENCHANTED_BOOK).setName(ChatColor.YELLOW + "•" + localDate.toString("EEEE, MMM")).
                addLore(ChatColor.GRAY + "" + localDate.getDayOfMonth() + DayClickItem.getDayNumberSuffix(localDate.getDayOfMonth()),ChatColor.GRAY + "<Click to return to month view>");
        if (dayView.getDay().isEqual(LocalDate.now())) {
            itemStackBuilder.addLore(ChatColor.YELLOW + "Today");
        }
        return itemStackBuilder.toItemStack();
    }
}
