package com.nicholasdoherty.socialcore.components.courts.citizens;

import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by john on 5/22/15.
 */
public class CitizenListener implements Listener {
    private CitizenManager citizenManager;

    public CitizenListener(CitizenManager citizenManager) {
        this.citizenManager = citizenManager;
    }

    @EventHandler
    public void login(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        Citizen citizen = citizenManager.getCitizen(p.getUniqueId());
        if (citizen != null && p != null && citizen.getName() != null && p.getName() !=null && !p.getName().equals(citizen.getName())) {
            citizen.setName(p.getName());
            citizenManager.updateName(citizen);
        }
    }
}
