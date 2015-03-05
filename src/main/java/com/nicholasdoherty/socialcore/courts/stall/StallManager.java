package com.nicholasdoherty.socialcore.courts.stall;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.citizens.stall.CitizensStall;
import com.nicholasdoherty.socialcore.courts.citizens.stall.JudgeStall;
import com.nicholasdoherty.socialcore.courts.citizens.stall.MasterListStall;
import com.nicholasdoherty.socialcore.courts.citizens.stall.SecretaryStall;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by john on 1/6/15.
 */
public class StallManager implements ConfigurationSerializable{
    private Courts courts;
    private Set<Stall> stalls;
    private boolean cacheLoaded = false;
    private Map<Location, Stall> stallCache = new HashMap<>();
    public StallManager(Set<Stall> stalls) {
        this.courts = Courts.getCourts();
        this.stalls = stalls;
        new StallListener(courts,this);
    }
    public Stall getStall(Location loc) {
        if (!cacheLoaded) {
            loadCache();
        }
        if (!stallCache.containsKey(loc)) {
            return null;
        }
        return stallCache.get(loc);
    }
    public boolean loadCache() {
        for (Stall stall : stalls) {
            if (stall == null)
                continue;

            Location sLoc = stall.getLocation();
            if (sLoc != null) {
                stallCache.put(sLoc,stall);
            }else {
                return false;
            }
        }
        return true;
    }
    public void removeStall(Stall stall) {
        if (stalls.contains(stall)) {
            stalls.remove(stall);
            if (stallCache.containsKey(stall.getLocation())) {
                stallCache.remove(stall.getLocation());
            }
        }
    }
    public void createStall(Location loc, StallType stallType) {
        Stall stall = null;
        if (stallType == StallType.CITIZEN) {
            stall = new CitizensStall(new VLocation(loc));
        }
        if (stallType == StallType.SECRETARY) {
            stall = new SecretaryStall(new VLocation(loc));
        }
        if (stallType == StallType.JUDGE) {
            stall = new JudgeStall(new VLocation(loc));
        }
        if (stallType == StallType.MASTERLIST) {
            stall = new MasterListStall(new VLocation(loc));
        }
        stalls.add(stall);
        stallCache.put(loc,stall);
    }
    public Courts getCourts() {
        return courts;
    }

    public Set<Stall> getStalls() {
        return stalls;
    }
    public StallManager(Map<String, Object> map) {
        this.courts = Courts.getCourts();
        this.stalls = new HashSet<>((Set<Stall>)map.get("stalls"));
        new StallListener(courts,this);
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("stalls",stalls);
        return map;
    }
}
