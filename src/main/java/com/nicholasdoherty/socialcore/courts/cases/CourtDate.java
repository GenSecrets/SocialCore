package com.nicholasdoherty.socialcore.courts.cases;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.CalendarEvent;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.CalendarEventType;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by john on 1/9/15.
 */
public class CourtDate implements CalendarEvent{
    private long time;
    private int judgeId;

    public CourtDate(long time, int judgeId) {
        this.time = time;
        this.judgeId = judgeId;
    }

    public int getJudgeId() {
        return judgeId;
    }
    public Judge getJudge() {
        Judge judge = Courts.getCourts().getJudgeManager().getJudge(judgeId);
        return judge;
    }

    public long getTime() {
        return time;
    }

    public long ticksUntil() {
        long currentTicks = new Date().getTime();
        return (time-currentTicks)/20;
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
