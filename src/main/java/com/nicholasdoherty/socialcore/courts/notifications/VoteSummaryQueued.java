package com.nicholasdoherty.socialcore.courts.notifications;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;

import java.util.Map;

/**
 * Created by john on 2/20/15.
 */
public class VoteSummaryQueued extends QueuedNotification {
    private int citizenId;

    public VoteSummaryQueued(ApprovedCitizen approvedCitizen, long timeout) {
        super(approvedCitizen,timeout);
        this.citizenId = approvedCitizen.getId();
    }

    @Override
    public boolean trySend() {
        Citizen citizen = Courts.getCourts().getSqlSaveManager().getCitizen(citizenId);
        ApprovedCitizen approvedCitizen = Courts.getCourts().getSqlSaveManager().getApprovedCitizen(citizen);
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
        this.citizenId = (int) map.get("citizen-id");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map =  super.serialize();
        map.put("citizen-id",citizenId);
        return map;
    }
}
