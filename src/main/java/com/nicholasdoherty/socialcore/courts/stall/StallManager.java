package com.nicholasdoherty.socialcore.courts.stall;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.utils.ConfigAccessor;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by john on 1/6/15.
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class StallManager {
    private final Courts courts;
    private final Set<Stall> stalls;
    private final boolean cacheLoaded = false;
    private final Map<Location, Stall> stallCache = new HashMap<>();
    private final ConfigAccessor stallStore;
    
    public StallManager(final Set<Stall> stalls) {
        courts = Courts.getCourts();
        this.stalls = stalls;
        
        stallStore = new ConfigAccessor(Courts.getCourts().getPlugin(), "stalls.yml");
        stallStore.saveDefaultConfig();
        loadMoreStalls();
        new StallListener(courts, this);
    }
    
    private void loadMoreStalls() {
        int id = stalls.size();
        for(final String key : stallStore.getConfig().getKeys(false)) {
            final ConfigurationSection section = stallStore.getConfig().getConfigurationSection(key);
            final VLocation loc = VLocation.fromString(section.getString("location"));
            final StallType stallType = StallType.valueOf(section.getString("type"));
            final Stall stall = Stall.createStall(/*Integer.parseInt(key.replace("stall-", "")
                    .replaceAll("_(.*)", ""))*/id, stallType, loc);
            id++;
            stalls.add(stall);
        }
    }
    
    public Stall getStall(final Location loc) {
        if(!cacheLoaded) {
            loadCache();
        }
        if(!stallCache.containsKey(loc)) {
            return null;
        }
        return stallCache.get(loc);
    }
    
    public boolean loadCache() {
        for(final Stall stall : stalls) {
            if(stall == null) {
                continue;
            }
            
            final Location sLoc = stall.getLocation();
            if(sLoc != null) {
                stallCache.put(sLoc, stall);
            } else {
                return false;
            }
        }
        return true;
    }
    
    public void removeStall(final Stall stall) {
        if(stalls.contains(stall)) {
            stalls.remove(stall);
            if(stallCache.containsKey(stall.getLocation())) {
                stallCache.remove(stall.getLocation());
            }
        }
        stallStore.getConfig().set("stall-" + stall.getId(), null);
        stallStore.saveConfig();
        Courts.getCourts().getSqlSaveManager().removeStall(stall);
    }
    
    public void addStall(final Stall stall) {
        stalls.add(stall);
        stallCache.put(stall.getLocation(), stall);
    }
    
    public Stall createStall(final Location loc, final StallType stallType) {
        final Stall stall = Courts.getCourts().getSqlSaveManager().addStall(stallType, new VLocation(loc));
        addStall(stall);
        final ConfigurationSection section = stallStore.getConfig().createSection("stall-" + stall.getId() + '_' + UUID.randomUUID());
        section.set("location", new VLocation(loc).toString());
        section.set("type", stallType.toString());
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
