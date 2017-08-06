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
public class GrantDivorce implements PostCourtAction, ConfigurationSerializable {
    private int cazeId;
    
    public GrantDivorce(final Case caze) {
        cazeId = caze.getId();
    }
    
    public GrantDivorce(final Map<String, Object> map) {
        if(map.containsKey("case")) {
            final Case caze = (Case) map.get("case");
            cazeId = caze.getId();
        } else if(map.containsKey("case-id")) {
            cazeId = (int) map.get("case-id");
        }
    }
    
    @Override
    public void doAction() {
        final Case caze = Courts.getCourts().getCaseManager().getCase(cazeId);
        if(caze.getPlantiff() == null || caze.getDefendent() == null) {
            return;
        }
        final SocialPlayer p1 = SocialCore.plugin.save.getSocialPlayer(caze.getDefendent().getName());
        final SocialPlayer p2 = SocialCore.plugin.save.getSocialPlayer(caze.getPlantiff().getName());
        Marriage marriage = SocialCore.plugin.save.getMarriage(p1, p2);
        p1.setMarried(false);
        p2.setMarried(false);
        p1.setMarriedTo("");
        p2.setMarriedTo("");
        
        p1.setEngaged(false);
        p2.setEngaged(false);
        p1.setEngagedTo("");
        p2.setEngagedTo("");
        
        // TODO: Pet name don't seem to matter in /adivorce, so...
        //p1.setPetName(null);
        //p2.setPetName(null);
        SocialCore.plugin.save.saveSocialPlayer(p1);
        SocialCore.plugin.save.saveSocialPlayer(p2);
        if(marriage == null) {
            marriage = SocialCore.plugin.save.getMarriage(p2, p1);
        }
        /*if(marriage == null) {
            return;
        }*/
        if(marriage != null) {
            SocialCore.plugin.save.removeMarriage(marriage);
            final Divorce divorce = Courts.getCourts().getDivorceManager().getDivorce(caze);
            if(divorce != null) {
                SocialCore.plugin.save.removeDivorce(divorce);
            }
        }
    }
    
    @Override
    public String prettyDescription() {
        return ChatColor.GREEN + "Divorce will be granted";
    }
    
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put("case-id", cazeId);
        return map;
    }
}
