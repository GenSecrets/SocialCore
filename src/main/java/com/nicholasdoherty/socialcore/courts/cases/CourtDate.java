package com.nicholasdoherty.socialcore.courts.cases;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.voxmc.voxlib.gui.inventorygui.views.calander.CalendarEvent;
import com.voxmc.voxlib.gui.inventorygui.views.calander.CalendarEventType;
import com.voxmc.voxlib.libs.org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by john on 1/9/15.
 */
public class CourtDate implements CalendarEvent {
    private final long time;
    private final int judgeId;
    
    public CourtDate(final long time, final int judgeId) {
        this.time = time;
        this.judgeId = judgeId;
    }
    
    public int getJudgeId() {
        return judgeId;
    }
    
    public Judge getJudge() {
        return Courts.getCourts().getJudgeManager().getJudge(judgeId);
    }
    
    public long getTime() {
        return time;
    }
    
    public long ticksUntil() {
        final long currentTicks = new Date().getTime();
        return (time - currentTicks) / 20;
    }
    
    @Override
    public CalendarEventType calendarEventType() {
        return CalendarEventType.COURT_CASE;
    }
    
    @Override
    public String prettyName() {
        return "Court Case";
    }
    
    @Override
    public DateTime exactTime() {
        return new DateTime(time);
    }
}
