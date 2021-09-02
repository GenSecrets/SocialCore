package com.nicholasdoherty.socialcore.components.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.courtroom.DontChangeStatus;
import com.nicholasdoherty.socialcore.components.courts.courtroom.OnlyAction;
import com.nicholasdoherty.socialcore.components.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.VaultUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/14/15.
 */
public class ThrowoutCase implements PostCourtAction, OnlyAction, DontChangeStatus, ConfigurationSerializable{
    private int cazeId;
    private String judgeName;
    private boolean refund;
    public ThrowoutCase(CourtSession courtSession, boolean refund) {
        judgeName = courtSession.getJudge().getName();
        this.refund = refund;
    }

    @Override
    public void doAction() {
        Case caze = Courts.getCourts().getCaseManager().getCase(cazeId);
        caze.setCaseStatus(CaseStatus.THROWN_OUT, judgeName);
        Citizen submitter = caze.getCaseHistory().getSubmitter();
        if (submitter != null) {
            OfflinePlayer sO = submitter.toOfflinePlayer();
            if (sO != null) {
                try {
                     VaultUtil.give(sO, Courts.getCourts().getCourtsConfig().getCaseFilingCost());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        caze.updateSave();
    }

    @Override
    public String prettyDescription() {
        return ChatColor.RED + "Case will be thrown out due to mistrial with a refund given to the filer.";
    }

    public ThrowoutCase(Map<String, Object> map) {
        if (map.containsKey("case")) {
            Case caze = (Case) map.get("case");
            cazeId = caze.getId();
        }else if (map.containsKey("case-id")) {
            cazeId = (int) map.get("case-id");
        }
        judgeName = (String) map.get("judge-name");
        if (map.containsKey("refund")) {
            refund = (boolean) map.get("refund");
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("case-id",cazeId);
        map.put("judge-name",judgeName);
        map.put("refund",refund);
        return map;
    }
}
