package com.nicholasdoherty.socialcore.courts.inventorygui.views.calander;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.MonthView;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by john on 1/13/15.
 */
public class DayClickItem implements ClickItem {
    private MonthView monthView;
    private LocalDate localDate;
    private CalendarEvent[] calendarEvents;

    public DayClickItem(MonthView monthView, LocalDate localDate) {
        this.monthView = monthView;
        this.localDate = localDate;
    }

    @Override
    public void click(boolean right) {
        monthView.switchToDayView(localDate,calendarEvents);
    }

    @Override
    public ItemStack itemstack() {
        ItemStackBuilder itemStackBuilder = new ItemStackBuilder(Material.PAINTING);
        LocalDate now = LocalDate.now();
        String modifier = "";
        ChatColor color = ChatColor.GRAY;
        calendarEvents = monthView.getCalendarGUI().getEvents(localDate);
        if (localDate.isBefore(now)) {
            itemStackBuilder.setType(Material.ITEM_FRAME);
        }
        if (localDate.getMonthOfYear() != monthView.getYearMonth().getMonthOfYear()) {
            itemStackBuilder.setType(Material.PAPER);
        }
        if (localDate.equals(now)) {
            modifier += "•";
            color = ChatColor.YELLOW;
            itemStackBuilder.setType(Material.ENCHANTED_BOOK);
        }
        if (localDate.isAfter(now)) {
            modifier += "•";
            color = ChatColor.GREEN;
        }
        itemStackBuilder.setName(color + modifier + localDate.toString("EEEE, MMM"));
        itemStackBuilder.addLore(color +""+ localDate.getDayOfMonth() +getDayNumberSuffix(localDate.getDayOfMonth()));
        if (calendarEvents.length > 0) {
            itemStackBuilder.setType(Material.BOOK);
            if (calendarEvents.length == 1) {
                itemStackBuilder.addLore(ChatColor.RED + "1 " + calendarEvents[0].prettyName());
            }else {
                Map<CalendarEventType, Integer> amounts = new HashMap<>();
                Map<CalendarEventType, String> names = new HashMap<>();
                for (CalendarEvent calendarEvent : calendarEvents) {
                    CalendarEventType calendarEventType = calendarEvent.calendarEventType();
                    int cur;
                    if (amounts.containsKey(calendarEventType)) {
                        cur = amounts.get(calendarEventType);
                    }else {
                        cur = 0;
                    }
                    amounts.put(calendarEventType,cur+1);
                    //todo fix ugly
                    if (!names.containsKey(calendarEventType)) {
                        names.put(calendarEventType,calendarEvent.prettyName());
                    }
                }
                for (CalendarEventType calendarEventType : amounts.keySet()) {
                    int amount = amounts.get(calendarEventType);
                    String prettyName = names.get(calendarEventType);
                    itemStackBuilder.addLore(ChatColor.RED + "" + amount  + " " + prettyName + getSuffix(amount));
                }

            }
        }
        itemStackBuilder.addLore(ChatColor.GRAY + "<Click to view day details>");
        return itemStackBuilder.toItemStack();
    }
    public static String getSuffix(int amount) {
        if (amount > 1) {
            return "s";
        }
        return "";
    }
    public static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
