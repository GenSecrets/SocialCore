package com.nicholasdoherty.socialcore.courts.notifications;

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
    private Citizen citizen;
    private long timeoutDate;

    public QueuedNotification(Citizen citizen, long timeoutDate) {
        this.citizen = citizen;
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
        return citizen;
    }

    public UUID getRecUUID() {
        return citizen.getUuid();
        }
    public QueuedNotification(Map<String, Object> map) {
        if (map.containsKey("citizen")) {
            citizen = (Citizen) map.get("citizen");
        }
        if (map.containsKey("timeout-date")) {
            timeoutDate = Long.valueOf(map.get("timeout-date")+"");
        }
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("citizen",citizen);
        map.put("timeout-date",timeoutDate);
        return map;
    }
}
