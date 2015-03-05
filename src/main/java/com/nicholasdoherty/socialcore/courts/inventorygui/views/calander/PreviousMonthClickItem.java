package com.nicholasdoherty.socialcore.courts.inventorygui.views.calander;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.MonthView;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.joda.time.YearMonth;


/**
 * Created by john on 1/13/15.
 */
public class PreviousMonthClickItem implements ClickItem {
    MonthView monthView;

    public PreviousMonthClickItem(MonthView monthView) {
        this.monthView = monthView;
    }

    @Override
    public void click(boolean right) {
        monthView.changeMonth(-1);
    }

    @Override
    public ItemStack itemstack() {
        YearMonth yearMonth = monthView.getYearMonth();
        return new ItemStackBuilder(Material.CARPET).setName(ChatColor.RED + "Previous Month").
                addLore(ChatColor.RED + yearMonth.minusMonths(1).toString("MMMM yyyy"),ChatColor.GRAY + "<Click to view the previous month>").toItemStack();
    }
}
