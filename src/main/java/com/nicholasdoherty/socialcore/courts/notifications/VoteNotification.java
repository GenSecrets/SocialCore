package com.nicholasdoherty.socialcore.courts.notifications;

import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import com.nicholasdoherty.socialcore.utils.VoxStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by john on 2/20/15.
 */
public class VoteNotification extends Notification {
    String baseApproval,baseDisapproval;
    public VoteNotification(ConfigurationSection section) {
        super(section);
        this.baseApproval = VoxStringUtils.color(section.getString("approval"));
        this.baseDisapproval = VoxStringUtils.color(section.getString("disapproval"));
    }
    public boolean vote(ApprovedCitizen citizen, UUID uuid, boolean approve) {
        Player p = citizen.getPlayer();
        if (p == null || !p.isOnline()) {
            return false;
        }
        String message;
        if (approve) {
            message = baseApproval;
        }else {
            message = baseDisapproval;
        }
        message = this.doReplacements(message,new Object[]{citizen,p}, null);
        sendBasedOnType(citizen,message);
        return true;
    }
}
