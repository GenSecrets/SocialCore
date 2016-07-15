package com.nicholasdoherty.socialcore.courts.citizens.stall;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.SecretaryGUI;
import com.nicholasdoherty.socialcore.courts.stall.Stall;
import com.nicholasdoherty.socialcore.courts.stall.StallType;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/6/15.
 */
public class SecretaryStall extends Stall {
    public SecretaryStall(int id, VLocation vLocation) {
        super(id,StallType.SECRETARY, vLocation);
    }

    @Override
    public void onClick(Player p) {
        Courts courts = Courts.getCourts();
        Secretary secretary = courts.getJudgeManager().getSecretary(p.getUniqueId());
        if (secretary == null) {
            p.sendMessage(ChatColor.RED + "You are not a secretary.");
            return;
        }
        SecretaryGUI.createAndOpen(p,secretary);
    }
}
