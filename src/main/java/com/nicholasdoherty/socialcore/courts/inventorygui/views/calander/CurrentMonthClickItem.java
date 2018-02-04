package com.nicholasdoherty.socialcore.courts.inventorygui.views.calander;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.MonthView;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.joda.time.YearMonth;


/**
 * Created by john on 1/13/15.
 */
public class CurrentMonthClickItem implements ClickItem{
    MonthView monthView;

    public CurrentMonthClickItem(MonthView monthView) {
        this.monthView = monthView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        monthView.getCalendarGUI().onBack();
    }

    @Override
    public ItemStack itemstack() {
        YearMonth yearMonth = monthView.getYearMonth();
        ItemStack itemStack = new ItemStackBuilder(Material.PAPER)
                .setName(ChatColor.YELLOW + yearMonth.toString("MMMM yyyy")).addLore(ChatColor.YELLOW + "This month", ChatColor.GRAY + "<Click to go back>")
                .addEnchant(Enchantment.DURABILITY,1).toItemStack();
        return itemStack;
    }
}
