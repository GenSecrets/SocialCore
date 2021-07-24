package com.nicholasdoherty.socialcore.courts.citizens.stall;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeCourtGUI;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeStallGUI;
import com.nicholasdoherty.socialcore.courts.stall.Stall;
import com.nicholasdoherty.socialcore.courts.stall.StallType;
import com.voxmc.voxlib.VLocation;
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
