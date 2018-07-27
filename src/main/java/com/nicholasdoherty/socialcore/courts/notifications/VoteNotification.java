package com.nicholasdoherty.socialcore.courts.notifications;

import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.voxmc.voxlib.util.VoxStringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by john on 2/20/15.
 */
@SuppressWarnings("unused")
public class VoteNotification extends Notification {
    String baseApproval;
    String baseDisapproval;
    
    public VoteNotification(final ConfigurationSection section) {
        super(section);
        baseApproval = VoxStringUtils.color(section.getString("approval"));
        baseDisapproval = VoxStringUtils.color(section.getString("disapproval"));
    }
    
    public boolean vote(final ApprovedCitizen citizen, final UUID uuid, final boolean approve) {
        final Player p = citizen.getPlayer();
        if(p == null || !p.isOnline()) {
            return false;
        }
        String message;
        if(approve) {
            message = baseApproval;
        } else {
            message = baseDisapproval;
        }
        message = doReplacements(message, new Object[] {citizen, p}, null);
        sendBasedOnType(citizen, message);
        return true;
    }
}
