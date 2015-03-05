package com.nicholasdoherty.socialcore.courts.inventorygui.views;

import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.*;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;


/**
 * Created by john on 1/13/15.
 */
public class CalendarGUI extends InventoryGUI {
    private DayGetter dayGetter;
    private CalanderRunnable calanderRunnable;
    private ValidTimeSelector validTimeSelector;
    private CancelAction cancelAction;
    public CalendarGUI(CalanderRunnable calanderRunnable, ValidTimeSelector validTimeSelector, CancelAction cancelAction, DayGetter dayGetter) {
        this.setCurrentView(new MonthView(this, YearMonth.now()));
        this.calanderRunnable = calanderRunnable;
        this.validTimeSelector = validTimeSelector;
        this.cancelAction = cancelAction;
        this.dayGetter = dayGetter;
    }
    public void onSelectTime(long time) {
        if (calanderRunnable == null)
            return;
        calanderRunnable.run(time);
    }
    public boolean isTimeValid(DateTime dateTime) {
        if (validTimeSelector == null)
            return true;
        return validTimeSelector.isValid(dateTime);
    }
    public void onBack() {
        if (cancelAction == null) {
            this.getPlayer().closeInventory();
        }
        cancelAction.onCancel();
    }
    public CalendarEvent[] getEvents(LocalDate localDate) {
        return dayGetter.calendarEvents(localDate);
    }
    public static void createAndOpen(Player p, CalanderRunnable calanderRunnable, ValidTimeSelector validTimeSelector, CancelAction cancelAction, DayGetter daygetter) {
        CalendarGUI calendarGUI = new CalendarGUI(calanderRunnable,validTimeSelector, cancelAction,daygetter);
        calendarGUI.setPlayer(p);
        calendarGUI.open();
    }
}
