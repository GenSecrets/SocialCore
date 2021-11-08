package com.nicholasdoherty.socialcore.components.courts.commands.judgeSubCommands;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeCourtGUI;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.components.courts.judges.gui.judgecasesview.JudgeStallGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JudgeMenuCmd {
    private final Courts courts;
    private final JudgeManager judgeManager;
    private final CommandSender commandSender;

    public JudgeMenuCmd(Courts courts, JudgeManager judgeManager, CommandSender commandSender) {
        this.courts = courts;
        this.judgeManager = judgeManager;
        this.commandSender = commandSender;
    }

    public boolean runCommand(){
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player.");
            return true;
        }
        final Player p = (Player) commandSender;
        final Judge judge = judgeManager.getJudge(p.getUniqueId());

        if(judge == null) {
            p.sendMessage(ChatColor.RED + "You are not a judge.");
            return true;
        }
        CourtSession courtSession = null;
        for(final CourtSession courtSession1 : courts.getCourtSessionManager().getInSession()) {
            if(courtSession1.getJudge() != null && courtSession1.getJudge().equals(judge)) {
                courtSession = courtSession1;
                break;
            }
        }

        if(courtSession == null) {
            JudgeStallGUI.createAndOpen(p, judge);
        } else {
            final JudgeCourtGUI judgeCourtGUI = new JudgeCourtGUI(courtSession);
            judgeCourtGUI.setPlayer(p);
            judgeCourtGUI.open();
        }
        return true;
    }
}
