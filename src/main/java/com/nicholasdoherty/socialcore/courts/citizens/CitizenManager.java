package com.nicholasdoherty.socialcore.courts.citizens;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.SqlSaveManager;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

/**
 * Created by john on 3/4/15.
 */
public class CitizenManager {
    private Courts courts;
    private SqlSaveManager sqlSaveManager;
    public CitizenManager(Courts courts) {
        this.courts = courts;
        this.sqlSaveManager = courts.getSqlSaveManager();
        courts.getPlugin().getServer().getPluginManager().registerEvents(new CitizenListener(this),courts.getPlugin());
    }

    public Citizen getCitizen(UUID uuid) {
        Citizen citizen = sqlSaveManager.getCitizen(uuid);
        return citizen;
    }
    public Citizen getCitizen(String name) {
        Citizen citizen = sqlSaveManager.getCitizen(name);
        return citizen;
    }
    public Citizen getCitizen(int id) {
        Citizen citizen = sqlSaveManager.getCitizen(id);
        return citizen;
    }
    public Citizen toCitizen(OfflinePlayer p) {
        String name = p.getName();
        UUID uuid = p.getUniqueId();
        return toCitizen(name,uuid);
    }
    public Citizen toCitizen(String name, UUID uuid) {
        Citizen citizen = getCitizen(uuid);
        if (citizen == null) {
            citizen = sqlSaveManager.createCitizen(uuid,name);
        }
        return citizen;
    }
    public void updateName(Citizen citizen) {
        sqlSaveManager.updateCitizen(citizen);
    }
}
