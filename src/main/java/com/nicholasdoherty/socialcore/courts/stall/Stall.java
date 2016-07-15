package com.nicholasdoherty.socialcore.courts.stall;

import com.nicholasdoherty.socialcore.courts.citizens.stall.CitizensStall;
import com.nicholasdoherty.socialcore.courts.citizens.stall.JudgeStall;
import com.nicholasdoherty.socialcore.courts.citizens.stall.MasterListStall;
import com.nicholasdoherty.socialcore.courts.citizens.stall.SecretaryStall;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/6/15.
 */
public abstract class Stall{
    private int id;
    private StallType stallType;
    private VLocation vLocation;

    public Stall(int id, StallType stallType, VLocation vLocation) {
        this.stallType = stallType;
        this.vLocation = vLocation;
    }

    public StallType getStallType() {
        return stallType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getLocation() {
        return vLocation.getLocation();
    }
    public boolean isStall(Location loc) {
        if (loc == null)
            return false;
        Location stallLoc = vLocation.getLocation();
        if (stallLoc == null)
            return false;
        return stallLoc.equals(loc);
    }
    public abstract void onClick(Player p);

    public static Stall createStall(int id, StallType stallType, VLocation vLocation) {
        switch (stallType) {
            case CITIZEN:
                return new CitizensStall(id,vLocation);
            case JUDGE:
                return new JudgeStall(id,vLocation);
            case SECRETARY:
                return new SecretaryStall(id,vLocation);
            case MASTERLIST:
                return new MasterListStall(id,vLocation);
        }
        return null;
    }
}
