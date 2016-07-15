package com.nicholasdoherty.socialcore.courts.stall;

import com.garbagemule.MobArena.util.config.ConfigSection;
import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.utils.ConfigAccessor;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by john on 1/6/15.
 */
public class StallManager{
    private Courts courts;
    private Set<Stall> stalls;
    private boolean cacheLoaded = false;
    private Map<Location, Stall> stallCache = new HashMap<>();
    private ConfigAccessor stallStore;
    public StallManager(Set<Stall> stalls) {
        this.courts = Courts.getCourts();
        this.stalls = stalls;

        stallStore = new ConfigAccessor(Courts.getCourts().getPlugin(),"stalls.yml");
        stallStore.saveDefaultConfig();
        loadMoreStalls();
        new StallListener(courts,this);
    }
    private void loadMoreStalls() {
        for (String key : stallStore.getConfig().getKeys(false)) {
            ConfigurationSection section = stallStore.getConfig().getConfigurationSection(key);
            VLocation loc = VLocation.fromString(section.getString("location"));
            StallType stallType = StallType.valueOf(section.getString("type"));
            Stall stall = Stall.createStall(Integer.parseInt(key.replace("stall-","")),stallType,loc);
            stalls.add(stall);
        }
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
        stallStore.getConfig().set("stall-"+stall.getId(),null);
        stallStore.saveConfig();
        Courts.getCourts().getSqlSaveManager().removeStall(stall);
    }
    public void addStall(Stall stall) {
        stalls.add(stall);
        stallCache.put(stall.getLocation(),stall);
    }
    public Stall createStall(Location loc, StallType stallType) {
        Stall stall = Courts.getCourts().getSqlSaveManager().addStall(stallType,new VLocation(loc));
        addStall(stall);
        ConfigurationSection section = stallStore.getConfig().createSection("stall-" + stall.getId());
        section.set("location",new VLocation(loc).toString());
        section.set("type",stallType.toString());
        stallStore.saveConfig();
        return stall;
    }
    public Courts getCourts() {
        return courts;
    }

    public Set<Stall> getStalls() {
        return stalls;
    }

}
