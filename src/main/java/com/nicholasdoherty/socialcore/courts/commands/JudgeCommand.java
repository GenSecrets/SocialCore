package com.nicholasdoherty.socialcore.courts.commands;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.CaseLocation;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.utils.TextUtil;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by john on 2/15/15.
 */
public class JudgeCommand implements CommandExecutor{
    private Courts courts;
    private JudgeManager judgeManager;

    public JudgeCommand(Courts courts, JudgeManager judgeManager) {
        this.courts = courts;
        this.judgeManager = judgeManager;
        courts.getPlugin().getCommand("judge").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player.");
            return true;
        }
        Player p = (Player) commandSender;
        Judge judge = judgeManager.getJudge(p.getUniqueId());

        if (judge == null) {
            p.sendMessage(ChatColor.RED + "You are not a judge.");
            return true;
        }
        CourtSession courtSession = null;
        for (CourtSession courtSession1 : courts.getCourtSessionManager().getInSession()) {
            if (courtSession1.getJudge().equals(judge)) {
                courtSession = courtSession1;
                break;
            }
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("chair")) {
            VLocation loc = courts.getCourtsConfig().getDefaultCourtRoom().getJudgeChairLoc();
            if (loc == null || loc.getLocation() == null) {
                p.sendMessage(ChatColor.RED + "No judge chair defined for this courtroom.");
                return true;
            }
            p.teleport(loc.getLocation());
            p.sendMessage(ChatColor.GREEN + "Teleported you to your judge chair");
            return true;
        }
        if (args.length == 0 || courtSession == null) {
            sendHelp(p,judge,courtSession);
            return true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("tp")) {
            if (args.length == 2 && args[1].equalsIgnoreCase("court")) {
                courtSession.getCourtRoom().teleportTo(courtSession);
                p.sendMessage(ChatColor.GREEN + "Participants teleported.");
                return true;
            }
            if (args.length == 2) {
                Location loc = locFromString(args[1],p.getWorld());
                if (loc == null) {
                    p.sendMessage(ChatColor.RED + "Invalid location");
                    return true;
                }
                courtSession.teleportParticipants(loc);
                p.sendMessage(ChatColor.GREEN + "Participants teleported.");
                return true;
            }
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("mark")) {
            Location loc;
            if (args.length >= 2) {
                loc = locFromString(args[0],p.getWorld());
            }else {
                loc = p.getLocation();
            }
            if (loc == null) {
                p.sendMessage(ChatColor.RED + "Invalid location");
                return true;
            }
            VLocation vLocation = new VLocation(loc);
            courtSession.getCaze().getCaseMeta().setCaseLocation(new CaseLocation("Marked location",vLocation));
            p.sendMessage(ChatColor.GREEN + "Marked location " + vLocation.toString());
            return true;
        }
        sendHelp(p,judge,courtSession);
        return true;
    }
    public Location locFromString(String in, World defaultWorld) {
        String[] split = in.split(",");
        if (split.length < 3 || split.length > 4) {
            return null;
        }
        World world;
        if (split.length == 4) {
            world = Bukkit.getWorld(split[0]);
        }else {
            world = defaultWorld;
        }
        if (world == null) {
            return null;
        }
        double x,y,z;
        try {
            int offset = 0;
            if (split.length > 3) {
                offset = 1;
            }
            x = Double.parseDouble(split[offset]);
            y = Double.parseDouble(split[offset+1]);
            z = Double.parseDouble(split[offset+2]);
        }catch (Exception e) {
            return null;
        }
        Location loc = new Location(world,x,y,z);
        return loc;
    }
    private String formHelpLine(String command, String description) {
        return ChatColor.YELLOW + command + ChatColor.BLUE + " - " + description;
    }
    private void sendHelp(Player p, Judge judge, CourtSession courtSession) {
        p.sendMessage(ChatColor.GRAY + "Your current approval rating is " + TextUtil.formatDouble(judge.approvalPercentage(), 2) + "%");
        if (courtSession == null) {
            p.sendMessage(ChatColor.RED + "There are no /judge commands you may currently use.");
            return;
        }
        p.sendMessage(formHelpLine("/judge chair","Teleports you to your judge chair"));
        p.sendMessage(formHelpLine("/judge mark","Mark a location for the ongoing court session"));
        p.sendMessage(formHelpLine("/judge mark [world],x,y,z","Marks defined location for the ongoing court session"));
        p.sendMessage(formHelpLine("/judge tp [world],x,y,z","Teleports you, the plaintiff, and the defendant to defined location"));
        p.sendMessage(formHelpLine("/judge tp court","Teleports you, the plaintiff, and the defendant back to the court room"));
    }
}
