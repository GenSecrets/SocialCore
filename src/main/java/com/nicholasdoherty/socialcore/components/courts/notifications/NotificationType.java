package com.nicholasdoherty.socialcore.components.courts.notifications;

/**
 * Created by john on 1/21/15.
 */
public enum NotificationType {
    JUDGE_UPCOMING_CASE,PARTICIPANT_UPCOMING_CASE,JUDGE_INACTIVE_JUDGE,JUDGE_INACTIVE_ALL,ONGOING_CASE_ALL,ONGOING_CASE_PARTICIPANT,
    JUDGE_REMOVED_RATING_JUDGE,JUDGE_REMOVED_RATING_ALL,JUDGE_TERM_REACHED,JUDGE_TERM_REACHED_ALL,
    

    JUDGE_NOMINATED_SELF,JUDGE_NOMINATED_ALL,

    JUDGE_ELECTED_ALL,

    SECRETARY_REMOVED,

    CASE_ASSIGNED_TIME,
    VOTE_NOTIFICATION,VOTE_SUMMARY,

    COURT_SESSION_END;
    public static NotificationType byName(String in) {
        in = in.trim().toLowerCase().replace("_","").replace("-","");
        for (NotificationType notificationType : values()) {
            String cleanN = notificationType.toString().replace("_","").toLowerCase();
            if (cleanN.equals(in)) {
                return notificationType;
            }
        }
        return null;
    }
}
