package com.nicholasdoherty.socialcore.courts.notifications;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Created by john on 2/15/15.
 */
public class BasicQueuedNotification extends QueuedNotification implements ConfigurationSerializable{
    private String message;
    private NotificationType notificationType;
    public BasicQueuedNotification(Citizen citizen, String message, long timeoutDate, NotificationType notificationType) {
        super(citizen,timeoutDate);
        this.message = message;
        this.notificationType = notificationType;
    }

    public BasicQueuedNotification(Map<String, Object> map) {
        super(map);
        if (map.containsKey("message"))
             this.message = (String) map.get("message");
        if (map.containsKey("type")) {
            this.notificationType = (NotificationType) map.get("type");
        }
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public boolean trySend() {
        Player p = Bukkit.getPlayer(this.getRecUUID());
        if (p == null || !p.isOnline())
            return false;
        sendBasedOnType(this.getCitizen(),message);
        return true;
    }
    public boolean sendBasedOnType(Citizen citizen, String message) {
        Notification notification = Courts.getCourts().getNotificationManager().getNotificationTypeNotificationMap().get(notificationType);
        if (notification != null) {
            return notification.sendBasedOnType(citizen,message);
        }
        Player p = citizen.getPlayer();
        if (p == null || !p.isOnline()) {
            return false;
        }
        p.sendMessage(message);
        return true;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("message",message);
        map.put("type",notificationType);
        return map;
    }
}
