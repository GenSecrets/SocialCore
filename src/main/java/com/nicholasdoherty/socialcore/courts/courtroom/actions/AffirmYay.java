package com.nicholasdoherty.socialcore.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/14/15.
 */
public class AffirmYay implements PostCourtAction,ConfigurationSerializable {
    public AffirmYay() {
    }

    @Override
    public void doAction() {

    }

    @Override
    public String prettyDescription() {
        return ChatColor.GREEN + "Court decides yay";
    }
    public AffirmYay(Map<String, Object> map) {
    }
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>();
    }
}
