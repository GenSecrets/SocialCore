package com.nicholasdoherty.socialcore.courts.inventorygui.views.calander;

import org.joda.time.LocalDate;

/**
 * Created by john on 1/13/15.
 */
public interface DayGetter {
    public CalendarEvent[] calendarEvents(LocalDate localDate);
}
