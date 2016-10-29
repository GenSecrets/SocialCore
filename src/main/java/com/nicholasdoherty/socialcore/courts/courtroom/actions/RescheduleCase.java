package com.nicholasdoherty.socialcore.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.courts.cases.CourtDate;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.courts.courtroom.DontChangeStatus;
import com.nicholasdoherty.socialcore.courts.courtroom.OnlyAction;
import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.utils.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/14/15.
 */
public class RescheduleCase implements PostCourtAction,OnlyAction,DontChangeStatus,ConfigurationSerializable {
    private int cazeId;
    private CourtDate courtDate;

    public RescheduleCase(CourtSession courtSession, CourtDate courtDate) {
        this.cazeId = courtSession.getCaze().getId();
        this.courtDate = courtDate;
    }

    @Override
    public void doAction() {
        Case caze = Courts.getCourts().getCaseManager().getCase(cazeId);
        caze.setCaseStatus(CaseStatus.COURT_DATE_SET, courtDate.getJudge().getName());
        caze.setCourtDate(courtDate);
        caze.updateSave();
    }
    @Override
    public String prettyDescription() {
        return ChatColor.YELLOW + "Reschedule this case to " + TextUtil.formatDate(courtDate.getTime());
    }
    public RescheduleCase(Map<String, Object> map) {
        if (map.containsKey("case")) {
            Case caze = (Case) map.get("case");
            cazeId = caze.getId();
        }else if (map.containsKey("case-id")) {
            cazeId = (int) map.get("case-id");
        }
        courtDate = (CourtDate) map.get("court-date");
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("case-id",cazeId);
        map.put("court-date",courtDate);
        return map;
    }
}
