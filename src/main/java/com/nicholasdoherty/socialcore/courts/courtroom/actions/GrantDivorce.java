package com.nicholasdoherty.socialcore.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.marriages.Divorce;
import com.nicholasdoherty.socialcore.marriages.Marriage;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/14/15.
 */
public class GrantDivorce implements PostCourtAction,ConfigurationSerializable {
    private int cazeId;

    public GrantDivorce(Case caze) {
        this.cazeId = caze.getId();
    }

    @Override
    public void doAction() {
        Case caze = Courts.getCourts().getCaseManager().getCase(cazeId);
        if (caze.getPlantiff() == null || caze.getDefendent() == null) {
            return;
        }
        SocialPlayer p1 = SocialCore.plugin.save.getSocialPlayer(caze.getDefendent().getName());
        SocialPlayer p2 = SocialCore.plugin.save.getSocialPlayer(caze.getPlantiff().getName());
        Marriage marriage = SocialCore.plugin.save.getMarriage(p1,p2);
        if (marriage == null) {
            marriage = SocialCore.plugin.save.getMarriage(p2,p1);
        }
        if (marriage == null)
            return;
        SocialCore.plugin.save.removeMarriage(marriage);
        Divorce divorce = Courts.getCourts().getDivorceManager().getDivorce(caze);
        if (divorce != null) {
            SocialCore.plugin.save.removeDivorce(divorce);
        }
        p1.setMarried(false);
        p2.setMarried(false);
        p1.setMarriedTo(null);
        p2.setEngagedTo(null);
    }

    @Override
    public String prettyDescription() {
        return ChatColor.GREEN + "Divorce will be granted";
    }
    public GrantDivorce(Map<String, Object> map) {
        if (map.containsKey("case")) {
            Case caze = (Case) map.get("case");
            cazeId = caze.getId();
        }else if (map.containsKey("case-id")) {
            cazeId = (int) map.get("case-id");
        }
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("case-id",cazeId);
        return map;
    }
}
