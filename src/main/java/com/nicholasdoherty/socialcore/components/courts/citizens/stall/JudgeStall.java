package com.nicholasdoherty.socialcore.components.courts.citizens.stall;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeCourtGUI;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.gui.judgecasesview.JudgeStallGUI;
import com.nicholasdoherty.socialcore.components.courts.stall.Stall;
import com.nicholasdoherty.socialcore.components.courts.stall.StallType;
import com.voxmc.voxlib.VLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/9/15.
 */
public class JudgeStall extends Stall {
    public JudgeStall(final int id, final VLocation vLocation) {
        super(id,StallType.JUDGE, vLocation);
    }

    @Override
    public void onClick(final Player p) {
        final Courts courts = Courts.getCourts();
        final Judge judge = courts.getJudgeManager().getJudge(p.getUniqueId());
        if (judge == null) {
            p.sendMessage(ChatColor.RED + "You are not a judge.");
            return;
        }
        final CourtSession courtSession = Courts.getCourts().getCourtSessionManager().getActiveCourtSession(judge,p.getLocation());
        if (courtSession != null) {
            final JudgeCourtGUI judgeCourtGUI = new JudgeCourtGUI(courtSession);
            judgeCourtGUI.setPlayer(p);
            judgeCourtGUI.open();
        }else {
            JudgeStallGUI.createAndOpen(p, judge);
        }
    }

}
