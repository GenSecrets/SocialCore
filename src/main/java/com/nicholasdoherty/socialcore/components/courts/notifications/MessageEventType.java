package com.nicholasdoherty.socialcore.components.courts.notifications;

/**
 * Created by john on 1/21/15.
 */
public enum MessageEventType {
    ON_LOGIN;
    public static MessageEventType byName(String in) {
        in = in.trim().toLowerCase().replace("_","").replace("-","");
        for (MessageEventType notificationType : values()) {
            String cleanN = notificationType.toString().replace("_","").toLowerCase();
            if (cleanN.equals(in)) {
                return notificationType;
            }
        }
        return null;
    }
}
