package com.nicholasdoherty.socialcore.courts.notifications;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;

import java.util.Map;

/**
 * Created by john on 2/20/15.
 */
public class VoteSummaryQueued extends QueuedNotification {
    private ApprovedCitizen approvedCitizen;

    public VoteSummaryQueued(ApprovedCitizen approvedCitizen, long timeout) {
        super(approvedCitizen,timeout);
        this.approvedCitizen = approvedCitizen;
    }

    @Override
    public boolean trySend() {
        if (approvedCitizen.ticksSinceLast() < Courts.getCourts().getCourtsConfig().getTimeBetweenVoteMessages()) {
            return false;
        }
        if (Courts.getCourts() == null || Courts.getCourts().getNotificationManager() == null)
            return false;
        VoteSummary voteSummary = (VoteSummary) Courts.getCourts().getNotificationManager().getNotificationTypeNotificationMap().get(NotificationType.VOTE_SUMMARY);
        return voteSummary.send(approvedCitizen);
    }

    public VoteSummaryQueued(Map<String, Object> map) {
        super(map);
        this.approvedCitizen = (ApprovedCitizen) map.get("approved-citizen");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map =  super.serialize();
        map.put("approved-citizen",approvedCitizen);
        return map;
    }
}
