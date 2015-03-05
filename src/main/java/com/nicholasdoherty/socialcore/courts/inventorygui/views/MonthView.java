package com.nicholasdoherty.socialcore.courts.inventorygui.views;

import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryView;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by john on 1/13/15.
 */
public class MonthView extends InventoryView {
    private static int INVENTORY_SIZE = 54;
    CalendarGUI calendarGUI;
    YearMonth yearMonth;

    public MonthView(CalendarGUI calendarGUI, YearMonth yearMonth) {
        super(calendarGUI);
        this.calendarGUI = calendarGUI;
        this.yearMonth = yearMonth;
    }

    @Override
    public void initActiveItems() {
        update();
    }

    @Override
    public void update() {
        clearActiveItems();
        addActiveItem(3, new PreviousMonthClickItem(this));
        addActiveItem(4, new CurrentMonthClickItem(this));
        addActiveItem(5, new NextMonthClickItem(this));

        List<LocalDate> days = datesToDisplay(yearMonth);
        for (int i = 0; i < days.size(); i++) {
            int slotId = getSlotId(i);
            if (slotId >= INVENTORY_SIZE)
                break;
            LocalDate localDate = days.get(i);
            addActiveItem(slotId, new DayClickItem(this,localDate));
        }

    }
    public static int getSlotId(int hour) {
        int width = 7;
        int first = 10;
        int rawSlot = first+hour;
        int zerodSlot = rawSlot-first;
        int rowsToCorrect = (int) Math.ceil(zerodSlot/width);
        int correctionFactor = 9-width;
        int correction = rowsToCorrect * correctionFactor;
        int slot = rawSlot + correction;
        return slot;
    }
    public void changeMonth(int change) {
        if (change > 0) {
            yearMonth = yearMonth.plusMonths(change);
        }
        if (change < 0) {
            yearMonth = yearMonth.minusMonths(-change);
        }
        reactivate();
    }

    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null,54,"Month View");
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public CalendarGUI getCalendarGUI() {
        return calendarGUI;
    }
    public void switchToDayView(LocalDate localDate, CalendarEvent[] calendarEvents) {
        DayView dayView = new DayView(this,localDate, calendarEvents);
        getInventoryGUI().setCurrentView(dayView);
        getCalendarGUI().open();
    }
    private static List<LocalDate> datesToDisplay(YearMonth yearMonth) {
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate firstDay = yearMonth.toLocalDate(1);
        if (yearMonth.toLocalDate(1).getDayOfWeek() != 1) {
            LocalDate firstDayOfWeek = yearMonth.toLocalDate(1).withDayOfWeek(1);
            while (firstDayOfWeek.getMonthOfYear() != firstDay.getMonthOfYear()) {
                localDates.add(firstDayOfWeek);
                firstDayOfWeek = firstDayOfWeek.plusDays(1);
            }
        }
        while (firstDay.getMonthOfYear() == yearMonth.getMonthOfYear()) {
            localDates.add(firstDay);
            firstDay = firstDay.plusDays(1);
        }
        return localDates;
    }
}
