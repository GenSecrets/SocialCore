package com.nicholasdoherty.socialcore.components.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.components.courts.fines.Fine;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/14/15.
 */
public class FineDefendent implements PostCourtAction,ConfigurationSerializable {
    private int amount;
    private int cazeId;

    public FineDefendent(int amount, Case caze) {
        this.amount = amount;
        this.cazeId = caze.getId();
    }

    @Override
    public void doAction() {
        Case caze = Courts.getCourts().getCaseManager().getCase(cazeId);
        Citizen plaintiff = caze.getPlantiff();
        Citizen defendant = caze.getDefendent();
        if (plaintiff == null)
            return;
        Fine fine = Courts.getCourts().getSqlSaveManager().addFine(defendant,plaintiff,amount);
        Courts.getCourts().getFineManager().addFine(fine);
    }

    @Override
    public String prettyDescription() {
        return ChatColor.GOLD + "The defendant will be fined " + amount + " voxels";
    }
    public FineDefendent(Map<String, Object> map) {
        amount = (int) map.get("amount");
        if (map.containsKey("case")) {
            Case caze = (Case) map.get("case");
            cazeId = caze.getId();
        }else if (map.containsKey("case-id")) {
            cazeId = (int) map.get("case-id");
        }
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map =  new HashMap<>();
        map.put("amount",amount);
        map.put("case-id",cazeId);
        return map;
    }
}

