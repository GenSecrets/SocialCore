package com.nicholasdoherty.socialcore.courts.cases;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtRoom;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.CalendarEvent;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.CalendarEventType;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/9/15.
 */
public class CourtDate implements ConfigurationSerializable, CalendarEvent{
    private long time;
    private Judge judge;
    private CourtRoom courtRoom;

    public CourtDate(long time, Judge judge, CourtRoom courtRoom) {
        this.time = time;
        this.judge = judge;
        this.courtRoom = courtRoom;
    }

    public long getTime() {
        return time;
    }

    public Judge getJudge() {
        return judge;
    }
    public long ticksUntil() {
        long currentTicks = new Date().getTime();
        return (time-currentTicks)/20;
    }

    public CourtDate(Map<String, Object> map) {
        this.time = (long) map.get("time");
        this.judge = (Judge) map.get("judge");
        String courtRoomId = (String) map.get("court-room-id");
        courtRoom = Courts.getCourts().getCourtsConfig().getCourtRoom(courtRoomId);
        if (courtRoom == null) {
            courtRoom = Courts.getCourts().getCourtsConfig().getDefaultCourtRoom();
        }
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("time",time);
        map.put("judge",judge);
        map.put("court-room-id",courtRoom.getName());
        return map;
    }

    public CourtRoom getCourtRoom() {
        return courtRoom;
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
