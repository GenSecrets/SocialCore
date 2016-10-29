package com.nicholasdoherty.socialcore.courts.notifications;

import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.utils.VoxStringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Created by john on 2/20/15.
 */
public class VoteSummary extends Notification {
    private String baseApproval,baseDisapproval,baseBoth;
    public VoteSummary(ConfigurationSection section) {
        super(section);
        this.baseApproval = VoxStringUtils.color(section.getString("approval"));
        this.baseDisapproval = VoxStringUtils.color(section.getString("disapproval"));
        this.baseBoth = VoxStringUtils.color(section.getString("both"));
    }

    public boolean send(ApprovedCitizen approvedCitizen) {
        Player p = approvedCitizen.getPlayer();
        if (p == null || !p.isOnline()) {
            return false;
        }
        boolean newApprove = false;
        boolean newDisapproval = false;
        if (approvedCitizen.getNewApprovals() > 0) {
            newApprove = true;
        }
        if (approvedCitizen.getNewDisapprovals() > 0) {
            newDisapproval = true;
        }
        if (!newApprove && !newDisapproval)
            return false;
        String baseMessage;
        if (newApprove && newDisapproval) {
            baseMessage = baseBoth;
        }else if (newApprove) {
            baseMessage = baseApproval;
        }else {
            baseMessage = baseDisapproval;
        }
        baseMessage = doReplacements(baseMessage,new Object[]{p,approvedCitizen},null);
        approvedCitizen.resetNew();
        sendBasedOnType(approvedCitizen,baseMessage);
        return true;
    }
}
