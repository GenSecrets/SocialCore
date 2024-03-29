package com.nicholasdoherty.socialcore.components.courts.citizens.stall;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.gui.SecretaryGUI;
import com.nicholasdoherty.socialcore.components.courts.stall.Stall;
import com.nicholasdoherty.socialcore.components.courts.stall.StallType;
import com.voxmc.voxlib.VLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/6/15.
 */
public class SecretaryStall extends Stall {
    public SecretaryStall(final int id, final VLocation vLocation) {
        super(id,StallType.SECRETARY, vLocation);
    }

    @Override
    public void onClick(final Player p) {
        final Courts courts = Courts.getCourts();
        final Secretary secretary = courts.getJudgeManager().getSecretary(p.getUniqueId());
        if (secretary == null) {
            p.sendMessage(ChatColor.RED + "You are not a secretary.");
            return;
        }
        SecretaryGUI.createAndOpen(p, secretary);
    }
}
