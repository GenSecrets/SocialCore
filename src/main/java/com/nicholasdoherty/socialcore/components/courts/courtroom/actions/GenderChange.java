package com.nicholasdoherty.socialcore.components.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.components.genders.Gender;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 3/29/15.
 */
public class GenderChange implements PostCourtAction, ConfigurationSerializable {
    private int cazeId;
    private Gender gender;

    public GenderChange(Case caze, Gender gender) {
        this.cazeId = caze.getId();
        this.gender = gender;
    }
    public GenderChange(Map<String, Object> map) {
        cazeId = (int) map.get("case-id");
        gender = new Gender((String) map.get("gender"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("case-id",cazeId);
        map.put("gender",gender.toString());
        return map;
    }

    @Override
    public void doAction() {
        Case caze = Courts.getCourts().getCaseManager().getCase(cazeId);
        if (caze == null)
            return;
        if (caze.getPlantiff() != null) {
            SocialPlayer socialPlayer = SocialCore.plugin.save.getSocialPlayer(caze.getPlantiff().getUuid().toString());
            if (socialPlayer != null) {
                socialPlayer.setGender(gender);
                SocialCore.plugin.save.saveSocialPlayer(socialPlayer);
            }
        }
    }

    @Override
    public String prettyDescription() {
        return ChatColor.GREEN + "Changes plaintiff's gender to " + gender.getName();
    }
}
