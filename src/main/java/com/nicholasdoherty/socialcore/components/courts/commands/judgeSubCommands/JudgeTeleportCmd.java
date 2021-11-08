package com.nicholasdoherty.socialcore.components.courts.commands.judgeSubCommands;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.commands.JudgeCommandHandler;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.CourtUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JudgeTeleportCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final JudgeManager judgeManager;
    private final String[] args;

    public JudgeTeleportCmd(Courts courts, CommandSender commandSender, JudgeManager judgeManager, String[] args) {
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

        if(args.length == 2 && args[1].equalsIgnoreCase("court")) {
            courtSession.getCourtRoom().teleportTo(courtSession);
            p.sendMessage(ChatColor.GREEN + "Participants teleported.");
            return true;
        }
        if(args.length == 2) {
            final Location loc = CourtUtil.locFromString(args[1], Bukkit.getWorld(courts.getCourtsConfig().getDefaultWorld()));
            if(loc == null) {
                p.sendMessage(ChatColor.RED + "Invalid location");
                return true;
            }
            courtSession.teleportParticipants(loc);
            if(courts.getCourtsConfig().getJudgeTeleportEffects() != null) {
                final Map<String, String> replacements = new HashMap<>();
                replacements.put("{judge-name}", courtSession.getJudge().getName());
                replacements.put("{location}", loc.getWorld().getName() + ',' + loc.getBlockX() + ',' + loc.getBlockY()
                        + ',' + loc.getBlockZ());
                courts.getCourtsConfig().getJudgeTeleportEffects().play(courtSession.getCourtRoom()
                        .getJudgeChairLoc().getLocation(), courtSession.participants().stream()
                        .map(Citizen::getPlayer).filter(Objects::nonNull).collect(Collectors.toList()), replacements);
            }
            p.sendMessage(ChatColor.GREEN + "Participants teleported.");
            return true;
        }
        return true;
    }
}
