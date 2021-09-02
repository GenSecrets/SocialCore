package com.nicholasdoherty.socialcore.components.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.components.courts.courtroom.PostCourtAction;
import com.voxmc.voxlib.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/14/15.
 */
public class JailDefendent implements PostCourtAction,ConfigurationSerializable {
    long endTime;

    public JailDefendent(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public void doAction() {

    }

    @Override
    public String prettyDescription() {
        return ChatColor.DARK_AQUA + "The defendant will be jailed until " + TextUtil.formatDate(endTime);
    }

    public JailDefendent(Map<String, Object> map) {
        endTime = Long.valueOf(""+map.get("endtime"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("endtime",endTime);
        return map;
    }
}
