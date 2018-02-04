package com.nicholasdoherty.socialcore.courts.inventorygui.views.calander;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.joda.time.DateTime;
import org.joda.time.IllegalInstantException;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by john on 1/13/15.
 */
public class HourClickItem implements ClickItem{
    DayView dayView;
    int hour;
    CalendarEvent[] calendarEvents;
    public HourClickItem(DayView dayView, int hour, CalendarEvent[] calendarEvents) {
        this.dayView = dayView;
        this.hour = hour;
        this.calendarEvents = calendarEvents;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        try {
            LocalDate date = dayView.getDay();
            DateTime dateTime = date.toDateTime(LocalTime.fromMillisOfDay(1000 * 60 * 60 * hour));
            if (!dayView.getCalendarGUI().isTimeValid(dateTime)) {
                return;
            }
            long time = dateTime.getMillis();
            dayView.getCalendarGUI().onSelectTime(time);
        }catch (IllegalInstantException e) {

        }
    }

    @Override
    public ItemStack itemstack() {
        try {
            ItemStackBuilder itemStackBuilder;
            DateTime now = DateTime.now();
            LocalDate date = dayView.getDay();
            DateTime dateTime = date.toDateTime(LocalTime.fromMillisOfDay(1000 * 60 * 60 * hour));
            String timeString = dateTime.getHourOfDay() + ":00";
            if (dateTime.getHourOfDay() >=12) {
                timeString += "PM";
            }else {
                timeString += "AM";
            }
            if (isSameHour(dateTime,now)) {
                itemStackBuilder = new ItemStackBuilder(Material.WATCH);
                itemStackBuilder.addEnchant(Enchantment.DURABILITY,1);
                itemStackBuilder.setName(ChatColor.YELLOW + timeString);
                itemStackBuilder.addLore(ChatColor.YELLOW + "Now");
            }else if (dateTime.isAfter(now)) {
                itemStackBuilder = new ItemStackBuilder(Material.WATCH);
                itemStackBuilder.setName(ChatColor.GREEN + timeString);
                itemStackBuilder.addLore(ChatColor.GRAY + "<Click to set time>");
            }else {
                itemStackBuilder = new ItemStackBuilder(Material.COAL);
                itemStackBuilder.setName(ChatColor.GRAY + timeString);
            }
            if (calendarEvents != null && calendarEvents.length > 0) {
                List<CalendarEvent> currentHourCalendarEvents = new ArrayList<>();
                for (CalendarEvent calendarEvent : calendarEvents) {
                    if (isSameHour(dateTime,calendarEvent.exactTime())) {
                        currentHourCalendarEvents.add(calendarEvent);
                    }
                }
                if (currentHourCalendarEvents.size() > 0) {
                    itemStackBuilder.setType(Material.BOOK);
                    if (currentHourCalendarEvents.size() == 1) {
                        itemStackBuilder.addLore(0, ChatColor.RED + "1 " + currentHourCalendarEvents.get(0).prettyName());
                    }else {
                        Map<CalendarEventType, Integer> amounts = new HashMap<>();
                        Map<CalendarEventType, String> names = new HashMap<>();
                        for (CalendarEvent calendarEvent : currentHourCalendarEvents) {
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
                            itemStackBuilder.addLore(0,ChatColor.RED + "" + amount  + " " + prettyName + DayClickItem.getSuffix(amount));
                        }
                    }
                }
            }
            return itemStackBuilder.toItemStack();
        }catch (Exception e) {
            return new ItemStackBuilder(Material.REDSTONE).setName(ChatColor.RED + "Daylight Savings Time").toItemStack();
        }
    }
    public boolean isSameHour(DateTime dateTime, DateTime now) {
        if (now.toLocalDate().isEqual(dateTime.toLocalDate()) && now.getHourOfDay() == dateTime.getHourOfDay()) {
            return true;
        }
        return false;
    }
}
