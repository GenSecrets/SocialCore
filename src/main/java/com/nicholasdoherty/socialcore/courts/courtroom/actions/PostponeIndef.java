package com.nicholasdoherty.socialcore.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.courts.courtroom.DontChangeStatus;
import com.nicholasdoherty.socialcore.courts.courtroom.OnlyAction;
import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/19/15.
 */
public class PostponeIndef implements PostCourtAction, OnlyAction,DontChangeStatus,ConfigurationSerializable{
    private int cazeId;
    private String judgeName;

    public PostponeIndef(Case caze, String judgeName) {
        this.cazeId = caze.getId();
        this.judgeName = judgeName;
    }

    @Override
    public void doAction() {
        Case caze = Courts.getCourts().getCaseManager().getCase(cazeId);
        caze.setCourtDate(null);
        caze.setCaseStatus(CaseStatus.PROCESSED,judgeName);
        caze.updateSave();
    }

    @Override
    public String prettyDescription() {
        return ChatColor.RED + "Postpone case indefinitely";
    }

    public PostponeIndef(Map<String, Object> map) {
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
