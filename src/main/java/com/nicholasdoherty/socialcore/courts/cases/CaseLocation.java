package com.nicholasdoherty.socialcore.courts.cases;

import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 2/15/15.
 */
public class CaseLocation implements ConfigurationSerializable{
    private String name;
    private VLocation vLocation;

    public CaseLocation(String name, VLocation vLocation) {
        this.name = name;
        this.vLocation = vLocation;
    }
    public CaseLocation(Map<String,Object> map) {
        this.name = (String) map.get("name");
        this.vLocation = (VLocation) map.get("location");
    }
    public String getName() {
        return name;
    }

    public VLocation getvLocation() {
        return vLocation;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("location",vLocation);
        return map;
    }
}
