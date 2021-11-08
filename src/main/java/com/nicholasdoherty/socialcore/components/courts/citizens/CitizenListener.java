package com.nicholasdoherty.socialcore.components.courts.citizens;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.SqlSaveManager;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.Secretary;
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
    private JudgeManager judgeManager;

    public CitizenListener(CitizenManager citizenManager, JudgeManager judgeManager) {
        this.citizenManager = citizenManager;
        this.judgeManager = judgeManager;
    }

    @EventHandler
    public void login(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        Citizen citizen = citizenManager.getCitizen(p.getUniqueId());
        if (citizen == null) {
            citizen = citizenManager.toCitizen(p.getName(), p.getUniqueId());
        }

        // Handle any name changes
        if (citizen != null && citizen.getName() != null && !p.getName().equals(citizen.getName())) {
            citizen.setName(p.getName());
            citizenManager.updateName(citizen);
        }

        // Check if judge or sec, update last online time
        if (citizen != null && judgeManager.isJudge(citizen.getUuid())) {
            Judge j = judgeManager.getJudge(p.getUniqueId());
            judgeManager.updateJudgeOnlineTime(citizen.getId());
        }
        if (citizen != null && judgeManager.isSecretary(citizen.getUuid())) {
            Secretary s = judgeManager.getSecretary(p.getUniqueId());
            judgeManager.updateSecretaryOnlineTime(citizen.getId());
        }
    }
}
