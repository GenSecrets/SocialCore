package com.nicholasdoherty.socialcore.components.courts.commands.judgeSubCommands;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseLocation;
import com.nicholasdoherty.socialcore.components.courts.commands.JudgeCommandHandler;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.utils.CourtUtil;
import com.voxmc.voxlib.VLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JudgeMarkCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final JudgeManager judgeManager;
    private final String[] args;

    public JudgeMarkCmd(Courts courts, CommandSender commandSender, JudgeManager judgeManager, String[] args) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.judgeManager = judgeManager;
        this.args = args;
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
        if(args.length == 0 || courtSession == null) {
            if(args.length >= 1) {
                p.sendMessage(courts.getCourtsLangManager().getCourtNotInSession());
            }
            JudgeCommandHandler.sendJudgeHelp(commandSender);
            return true;
        }

        final Location loc;
        if(args.length >= 1) {
            loc = CourtUtil.locFromString(args[0], p.getWorld());
        } else {
            loc = p.getLocation();
        }
        if(loc == null) {
            p.sendMessage(ChatColor.RED + "Invalid location");
            return true;
        }
        final VLocation vLocation = new VLocation(loc);
        courtSession.getCaze().getCaseMeta().setCaseLocation(new CaseLocation("Marked location", vLocation));
        p.sendMessage(ChatColor.GREEN + "Marked location " + vLocation);
        return true;
    }
}
