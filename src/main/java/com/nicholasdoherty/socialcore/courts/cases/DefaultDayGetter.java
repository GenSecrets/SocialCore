package com.nicholasdoherty.socialcore.courts.cases;

import com.voxmc.voxlib.gui.inventorygui.views.calendar.CalendarEvent;
import com.voxmc.voxlib.gui.inventorygui.views.calendar.DayGetter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by john on 1/13/15.
 */
@SuppressWarnings("unused")
public class DefaultDayGetter implements DayGetter {
    private final CaseManager caseManager;
    private final Map<LocalDate, Set<CourtDate>> courtDatesByDay = new HashMap<>();
    private final Map<CourtDate, LocalDate> currentCourtDate = new HashMap<>();
    private final Map<CourtDate, DateTime> times = new HashMap<>();
    
    public DefaultDayGetter(final CaseManager caseManager) {
        this.caseManager = caseManager;
        init();
    }
    
    public void init() {
        for(final Case caze : caseManager.getCases()) {
            if(caze.getCourtDate() != null) {
                update(caze.getCourtDate());
            }
        }
    }
    
    public void update(final CourtDate courtDate) {
        if(currentCourtDate.containsKey(courtDate)) {
            final LocalDate localDate = currentCourtDate.get(courtDate);
            courtDatesByDay.get(localDate).remove(courtDate);
            currentCourtDate.remove(courtDate);
            if(courtDatesByDay.get(localDate).isEmpty()) {
                courtDatesByDay.remove(localDate);
            }
        }
        final LocalDate localDate = new LocalDate(courtDate.getTime());
        currentCourtDate.put(courtDate, localDate);
        if(!courtDatesByDay.containsKey(localDate)) {
            courtDatesByDay.put(localDate, new HashSet<>());
        }
        courtDatesByDay.get(localDate).add(courtDate);
    }
    
    public void remove(final CourtDate courtDate) {
        if(currentCourtDate.containsKey(courtDate)) {
            final LocalDate localDate = currentCourtDate.get(courtDate);
            if(courtDatesByDay.containsKey(localDate)) {
                final Set<CourtDate> set = courtDatesByDay.get(localDate);
                if(set.contains(courtDate)) {
                    set.remove(courtDate);
                    if(set.isEmpty()) {
                        courtDatesByDay.remove(localDate);
                    }
                }
            }
        }
    }
    
    public boolean hasCorutDate(final DateTime dateTime) {
        if(dateTime == null) {
            return false;
        }
        for(final CourtDate courtDate : currentCourtDate.keySet()) {
            if(courtDate != null) {
                if(Math.abs(dateTime.toDate().getTime() - courtDate.getTime()) < 1800000) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public CalendarEvent[] calendarEvents(final LocalDate localDate) {
        if(courtDatesByDay.containsKey(localDate)) {
            return courtDatesByDay.get(localDate).toArray(new CalendarEvent[0]);
        } else {
            return new CalendarEvent[0];
        }
    }
}
