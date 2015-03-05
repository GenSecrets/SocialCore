package com.nicholasdoherty.socialcore.courts;

import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Created by john on 1/18/15.
 */
public class EconomyManager {
    Plugin plugin;
    Economy econ;

    public EconomyManager(Plugin plugin) {
        this.plugin = plugin;
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    public void chargeCitizen(Citizen citizen, int amount) {
        
    }
}
