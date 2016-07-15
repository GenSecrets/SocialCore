package com.nicholasdoherty.socialcore.courts.citizens.stall;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.mastercases.MasterCaseGUI;
import com.nicholasdoherty.socialcore.courts.stall.Stall;
import com.nicholasdoherty.socialcore.courts.stall.StallType;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by john on 2/15/15.
 */
public class MasterListStall extends Stall{

    public MasterListStall(int id, VLocation vLocation) {
        super(id,StallType.MASTERLIST, vLocation);
    }

    @Override
    public void onClick(Player p) {
        if (!p.hasPermission("courts.mastercases") && !Courts.getCourts().getJudgeManager().isJudge(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED +"You don't have permission to view the master case list.");
            return;
        }
        MasterCaseGUI.createAndOpen(p);
    }
}
