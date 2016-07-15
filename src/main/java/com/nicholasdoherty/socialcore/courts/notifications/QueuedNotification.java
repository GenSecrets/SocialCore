package com.nicholasdoherty.socialcore.courts.notifications;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by john on 2/20/15.
 */
public abstract class QueuedNotification implements ConfigurationSerializable{
    private int citizenId;
    private long timeoutDate;

    public QueuedNotification(Citizen citizen, long timeoutDate) {
        this.citizenId = citizen.getId();
        this.timeoutDate = timeoutDate;
    }
    public abstract boolean trySend();

public boolean hasTimedOut() {
        if (timeoutDate == -1)
        return false;
        long currentTime = new Date().getTime();
        if (currentTime >= timeoutDate) {
        return true;
        }
        return false;
        }

    public Citizen getCitizen() {
        Citizen citizen = Courts.getCourts().getSqlSaveManager().getCitizen(citizenId);
        return citizen;
    }

    public UUID getRecUUID() {
        return getCitizen().getUuid();
        }
    public QueuedNotification(Map<String, Object> map) {
        citizenId = (int) map.get("citizen-id");
        if (map.containsKey("timeout-date")) {
            timeoutDate = Long.valueOf(map.get("timeout-date")+"");
        }
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("citizen-id",citizenId);
        map.put("timeout-date",timeoutDate);
        return map;
    }
}
