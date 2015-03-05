package com.nicholasdoherty.socialcore.courts.citizens.stall;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeCourtGUI;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeStallGUI;
import com.nicholasdoherty.socialcore.courts.stall.Stall;
import com.nicholasdoherty.socialcore.courts.stall.StallType;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Created by john on 1/9/15.
 */
public class JudgeStall extends Stall implements ConfigurationSerializable {
    public JudgeStall(VLocation vLocation) {
        super(StallType.JUDGE, vLocation);
    }

    @Override
    public void onClick(Player p) {
        Courts courts = Courts.getCourts();
        Judge judge = courts.getJudgeManager().getJudge(p.getUniqueId());
        if (judge == null) {
                p.sendMessage(ChatColor.RED + "You are not a judge.");
                return;
        }
        CourtSession courtSession = Courts.getCourts().getCourtSessionManager().getActiveCourtSession(judge,p.getLocation());
        if (courtSession != null) {
            JudgeCourtGUI judgeCourtGUI = new JudgeCourtGUI(courtSession);
            judgeCourtGUI.setPlayer(p);
            judgeCourtGUI.open();
        }else {
            JudgeStallGUI.createAndOpen(p, judge);
        }
    }

    public JudgeStall(Map<String, Object> map) {
        super(map);
    }
}
