package com.nicholasdoherty.socialcore.courts.inventorygui.views.calander;

import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryView;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.CalendarGUI;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.MonthView;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.joda.time.LocalDate;

/**
 * Created by john on 1/13/15.
 */
public class DayView extends InventoryView {
    private static int INVENTORY_SIZE = 54;
    private MonthView monthView;
    private LocalDate localDate;
    private CalendarEvent[] calendarEvents;

    public DayView(MonthView monthView, LocalDate localDate, CalendarEvent[] calendarEvents) {
        super(monthView.getInventoryGUI());
        this.monthView = monthView;
        this.localDate = localDate;
        this.calendarEvents = calendarEvents;
    }

    @Override
    public void initActiveItems() {
        update();
    }

    @Override
    public void update() {
        addActiveItem(3,new PreviousDayClickItem(this));
        addActiveItem(4,new CurrentDayClickItem(this));
        addActiveItem(5, new NextDayClickItem(this));

        for (int hour = 0; hour < 24; hour++) {
            int slotId = getSlotId(hour);
            addActiveItem(slotId, new HourClickItem(this,hour,calendarEvents));
        }
    }
    public static int getSlotId(int hour) {
        int width = 5;
        int first = 12;
        int rawSlot = first+hour;
        int zerodSlot = rawSlot-11;
        int rowsToCorrect = (int) Math.ceil(zerodSlot/width);
        int correctionFactor = 9-width;
        int correction = rowsToCorrect * correctionFactor;
        int slot = rawSlot + correction;
        return slot;
    }
    public void returnToMonthView() {
        getInventoryGUI().setCurrentView(monthView);
        getInventoryGUI().open();
    }
    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null,INVENTORY_SIZE,"Day View");
    }

    public void changeDay(int amount) {
        if (amount > 0) {
            localDate = localDate.plusDays(amount);
        }
        if (amount < 0) {
            localDate = localDate.minusDays(-amount);
        }
        reactivate();
    }
    public CalendarGUI getCalendarGUI() {
        return monthView.getCalendarGUI();
    }

    public LocalDate getDay() {
        return localDate;
    }
}
