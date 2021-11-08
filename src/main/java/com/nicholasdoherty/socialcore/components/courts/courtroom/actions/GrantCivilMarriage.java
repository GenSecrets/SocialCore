package com.nicholasdoherty.socialcore.components.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.components.marriages.types.Engagement;
import com.nicholasdoherty.socialcore.components.marriages.types.Marriage;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.nicholasdoherty.socialcore.utils.MarriagesUtil.getMonth;

/**
 * Created by john on 3/29/15.
 */
public class GrantCivilMarriage implements PostCourtAction,ConfigurationSerializable {
    private int cazeId;
    public GrantCivilMarriage(Case caze) {
        this.cazeId = caze.getId();
    }

    @Override
    public void doAction() {
        Case caze = Courts.getCourts().getCaseManager().getCase(cazeId);
        if (caze.getPlantiff() == null || caze.getDefendent() == null) {
            return;
        }
        SocialPlayer p1 = SocialCore.plugin.save.getSocialPlayer(caze.getDefendent().getUuid().toString());
        SocialPlayer p2 = SocialCore.plugin.save.getSocialPlayer(caze.getPlantiff().getUuid().toString());
        Marriage oldMarriage = SocialCore.plugin.save.getMarriage(p1,p2);
        if (oldMarriage != null) {
            oldMarriage = SocialCore.plugin.save.getMarriage(p2,p1);
            SocialCore.plugin.save.removeMarriage(oldMarriage.getName());
            p1.setMarried(false);
            p2.setMarried(false);
            p1.setMarriedTo(null);
            p2.setEngagedTo(null);
        }
        SocialCore sc = SocialCore.plugin;
        Engagement e = sc.save.getEngagement(p1,p2);
        if (e != null) {
            sc.save.removeEngagement(e.getName());
        }
        p1.setEngaged(false);
        p1.setEngagedTo("");
        p1.setMarried(true);
        p1.setMarriedTo(p2.getPlayerName());
        p2.setEngaged(false);
        p2.setEngagedTo("");
        p2.setMarried(true);
        p2.setMarriedTo(p1.getPlayerName());
        sc.save.saveSocialPlayer(p1);
        sc.save.saveSocialPlayer(p2);

        Marriage m = new Marriage(p1,p2);
        String judgeName = caze.getCaseHistory().getResolverName();
        if (judgeName == null) {
            judgeName = "Judge";
        }
        m.setPriest(judgeName);
        String dateBuilder = getMonth()+" "+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+", "+Calendar.getInstance().get(Calendar.YEAR);
        m.setDate(dateBuilder);


        sc.save.saveMarriage(m);
    }

    @Override
    public String prettyDescription() {
        return ChatColor.GREEN + "Civil Marriage will be granted";
    }
    public GrantCivilMarriage(Map<String, Object> map) {
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
