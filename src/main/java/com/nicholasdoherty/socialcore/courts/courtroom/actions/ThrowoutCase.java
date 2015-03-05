package com.nicholasdoherty.socialcore.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.courts.courtroom.DontChangeStatus;
import com.nicholasdoherty.socialcore.courts.courtroom.OnlyAction;
import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/14/15.
 */
public class ThrowoutCase implements PostCourtAction, OnlyAction, DontChangeStatus, ConfigurationSerializable{
    private int cazeId;
    private String judgeName;

    public ThrowoutCase(CourtSession courtSession) {
        judgeName = courtSession.getJudge().getName();
    }

    @Override
    public void doAction() {
        Case caze = Courts.getCourts().getCaseManager().getCase(cazeId);
        caze.setCaseStatus(CaseStatus.THROWN_OUT, judgeName);
    }

    @Override
    public String prettyDescription() {
        return ChatColor.RED + "Case will be thrown out due to mistrial";
    }

    public ThrowoutCase(Map<String, Object> map) {
        if (map.containsKey("case")) {
            Case caze = (Case) map.get("case");
            cazeId = caze.getId();
        }else if (map.containsKey("case-id")) {
            cazeId = (int) map.get("case-id");
        }
        judgeName = (String) map.get("judge-name");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("case-id",cazeId);
        map.put("judge-name",judgeName);
        return map;
    }
}
