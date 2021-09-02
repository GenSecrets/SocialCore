package com.nicholasdoherty.socialcore.components.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.components.courts.courtroom.PostCourtAction;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/14/15.
 */
public class AffirmNay implements PostCourtAction, ConfigurationSerializable {
    public AffirmNay() {
    }

    @Override
    public void doAction() {

    }

    @Override
    public String prettyDescription() {
        return ChatColor.GREEN + "Court decides nay";
    }
    public AffirmNay(Map<String, Object> map) {
    }
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>();
    }
}
