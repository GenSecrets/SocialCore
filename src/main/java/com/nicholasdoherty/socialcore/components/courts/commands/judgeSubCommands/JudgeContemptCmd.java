package com.nicholasdoherty.socialcore.components.courts.commands.judgeSubCommands;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.commands.JudgeCommandHandler;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JudgeContemptCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final JudgeManager judgeManager;
    private final String[] args;

    public JudgeContemptCmd(Courts courts, CommandSender commandSender, JudgeManager judgeManager, String[] args) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.judgeManager = judgeManager;
        this.args = args;
    }

    public boolean runCommand() {
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

        final String name = args[0];
        final Player toMute = Bukkit.getPlayer(name);
        if(toMute == null) {
            p.sendMessage(ChatColor.RED + "Could not find player: " + name);
            return true;
        }
        if(courtSession.getCourtRoom().isInRoom(toMute.getLocation())) {
            toMute.teleport(toMute.getWorld().getSpawnLocation());
        }
        if(courtSession.getContempt().contains(toMute.getUniqueId())) {
            courtSession.getContempt().remove(toMute.getUniqueId());
            p.sendMessage(ChatColor.GREEN + toMute.getName() + " will now be allowed in the court room.");
            return true;
        } else {
            courtSession.getContempt().add(toMute.getUniqueId());
            p.sendMessage(ChatColor.GREEN + toMute.getName() + " will now " + ChatColor.RED + "NOT" + ChatColor.GREEN + " be allowed in the court room.");
            return true;
        }
    }
}
