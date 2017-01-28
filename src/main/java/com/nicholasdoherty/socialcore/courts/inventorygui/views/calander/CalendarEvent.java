package com.nicholasdoherty.socialcore.courts.inventorygui.views.calander;

import org.joda.time.DateTime;

/**
 * Created by john on 1/13/15.
 */
public interface CalendarEvent {
    public CalendarEventType calendarEventType();
    public String prettyName();
    public DateTime exactTime();
}
