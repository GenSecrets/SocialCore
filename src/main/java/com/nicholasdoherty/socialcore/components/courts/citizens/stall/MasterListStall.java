package com.nicholasdoherty.socialcore.components.courts.citizens.stall;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.mastercases.MasterCaseGUI;
import com.nicholasdoherty.socialcore.components.courts.stall.Stall;
import com.nicholasdoherty.socialcore.components.courts.stall.StallType;
import com.voxmc.voxlib.VLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by john on 2/15/15.
 */
public class MasterListStall extends Stall{

    public MasterListStall(final int id, final VLocation vLocation) {
        super(id,StallType.MASTERLIST, vLocation);
    }

    @Override
    public void onClick(final Player p) {
        if (!p.hasPermission("courts.mastercases") && !Courts.getCourts().getJudgeManager().isJudge(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "You don't have permission to view the master case list.");
            return;
        }
        MasterCaseGUI.createAndOpen(p);
    }
}
