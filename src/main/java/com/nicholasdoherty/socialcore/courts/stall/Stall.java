package com.nicholasdoherty.socialcore.courts.stall;

import com.nicholasdoherty.socialcore.courts.citizens.stall.CitizensStall;
import com.nicholasdoherty.socialcore.courts.citizens.stall.JudgeStall;
import com.nicholasdoherty.socialcore.courts.citizens.stall.MasterListStall;
import com.nicholasdoherty.socialcore.courts.citizens.stall.SecretaryStall;
import com.voxmc.voxlib.VLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/6/15.
 */
@SuppressWarnings("unused")
public abstract class Stall {
    private int id;
    private final StallType stallType;
    private final VLocation vLocation;
    
    public Stall(final int id, final StallType stallType, final VLocation vLocation) {
        this.stallType = stallType;
        this.vLocation = vLocation;
    }
    
    public static Stall createStall(final int id, final StallType stallType, final VLocation vLocation) {
        switch(stallType) {
            case CITIZEN:
                return new CitizensStall(id, vLocation);
            case JUDGE:
                return new JudgeStall(id, vLocation);
            case SECRETARY:
                return new SecretaryStall(id, vLocation);
            case MASTERLIST:
                return new MasterListStall(id, vLocation);
        }
        return null;
    }
    
    public StallType getStallType() {
        return stallType;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public Location getLocation() {
        return vLocation.getLocation();
    }
    
    public boolean isStall(final Location loc) {
        if(loc == null) {
            return false;
        }
        final Location stallLoc = vLocation.getLocation();
        if(stallLoc == null) {
            return false;
        }
        return stallLoc.equals(loc);
    }
    
    public abstract void onClick(Player p);
}
