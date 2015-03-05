package com.nicholasdoherty.socialcore.courts.citizens.stall;

import com.nicholasdoherty.socialcore.courts.mastercases.MasterCaseGUI;
import com.nicholasdoherty.socialcore.courts.stall.Stall;
import com.nicholasdoherty.socialcore.courts.stall.StallType;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Created by john on 2/15/15.
 */
public class MasterListStall extends Stall implements ConfigurationSerializable{

    public MasterListStall(VLocation vLocation) {
        super(StallType.MASTERLIST, vLocation);
    }
    public MasterListStall(Map<String, Object> map) {
        super(map);
    }

    @Override
    public void onClick(Player p) {
        if (!p.hasPermission("courts.mastercases")) {
            p.sendMessage(ChatColor.RED +"You don't have permission to view the master case list.");
            return;
        }
        MasterCaseGUI.createAndOpen(p);
    }
}
