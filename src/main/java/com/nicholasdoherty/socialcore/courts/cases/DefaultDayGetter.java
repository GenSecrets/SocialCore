package com.nicholasdoherty.socialcore.courts.cases;

import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.CalendarEvent;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.DayGetter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.*;

/**
 * Created by john on 1/13/15.
 */
public class DefaultDayGetter implements DayGetter{
    private CaseManager caseManager;
    private Map<LocalDate, Set<CourtDate>> courtDatesByDay = new HashMap<>();
    private Map<CourtDate, LocalDate> currentCourtDate = new HashMap<>();
    private Map<CourtDate, DateTime> times = new HashMap<>();
    public DefaultDayGetter(CaseManager caseManager) {
        this.caseManager = caseManager;
        init();
    }
    public void init() {
        for (Case caze : caseManager.getCases()) {
            if (caze.getCourtDate() != null) {
                update(caze.getCourtDate());
            }
        }
    }
    public void update(CourtDate courtDate) {
        if (currentCourtDate.containsKey(courtDate)) {
            LocalDate localDate = currentCourtDate.get(courtDate);
            courtDatesByDay.get(localDate).remove(courtDate);
            currentCourtDate.remove(courtDate);
            if (courtDatesByDay.get(localDate).isEmpty()) {
                courtDatesByDay.remove(localDate);
            }
        }
        LocalDate localDate = new LocalDate(courtDate.getTime());
        currentCourtDate.put(courtDate, localDate);
        if (!courtDatesByDay.containsKey(localDate)) {
            courtDatesByDay.put(localDate, new HashSet<CourtDate>());
        }
        courtDatesByDay.get(localDate).add(courtDate);
    }
    public void remove(CourtDate courtDate) {
        if (currentCourtDate.containsKey(courtDate)) {
            LocalDate localDate = currentCourtDate.get(courtDate);
            if (courtDatesByDay.containsKey(localDate)) {
                Set<CourtDate> set = courtDatesByDay.get(localDate);
                if (set.contains(courtDate)) {
                    set.remove(courtDate);
                    if (set.isEmpty()) {
                        courtDatesByDay.remove(localDate);
                    }
                }
            }
        }
    }
    public boolean hasCorutDate(DateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        for (CourtDate courtDate : currentCourtDate.keySet()) {
            if (courtDate != null) {
                if (Math.abs(dateTime.toDate().getTime() - courtDate.getTime()) < 1800000) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public CalendarEvent[] calendarEvents(LocalDate localDate) {
        if (courtDatesByDay.containsKey(localDate)) {
            return courtDatesByDay.get(localDate).toArray(new CalendarEvent[courtDatesByDay.get(localDate).size()]);
        }else {
            return new CalendarEvent[0];
        }
    }
}
