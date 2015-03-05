package com.nicholasdoherty.socialcore.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/14/15.
 */
public class AffirmPlaintiffGuilty implements PostCourtAction,ConfigurationSerializable {
    public AffirmPlaintiffGuilty() {
    }

    @Override
    public void doAction() {

    }

    @Override
    public String prettyDescription() {
        return ChatColor.GREEN + "Plaintiff declared guilty";
    }
    public AffirmPlaintiffGuilty(Map<String, Object> map) {
    }
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>();
    }
}
