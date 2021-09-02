package com.nicholasdoherty.socialcore.components.courts.commands;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseLocation;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeCourtGUI;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.components.courts.judges.gui.judgecasesview.JudgeStallGUI;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.VaultUtil;
import com.voxmc.voxlib.VLocation;
import com.voxmc.voxlib.util.TextUtil;
import com.voxmc.voxlib.util.UUIDUtil;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by john on 2/15/15.
 */
public class JudgeCommand implements CommandExecutor {
    private final Courts courts;
    private final JudgeManager judgeManager;
    
    public JudgeCommand(final Courts courts, final JudgeManager judgeManager) {
        this.courts = courts;
        this.judgeManager = judgeManager;
        courts.getPlugin().getCommand("judge").setExecutor(this);
    }
    
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
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
        if(args.length == 1 && args[0].equalsIgnoreCase("chair")) {
            final VLocation loc = courts.getCourtsConfig().getDefaultCourtRoom().getJudgeChairLoc();
            if(loc == null || loc.getLocation() == null) {
                p.sendMessage(ChatColor.RED + "No judge chair defined for this courtroom.");
                return true;
            }
            p.teleport(loc.getLocation());
            p.sendMessage(ChatColor.GREEN + "Teleported you to your judge chair");
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("menu")) {
            if(courtSession == null) {
                // p.sendMessage(ChatColor.RED + "There is no active court session."); // Should this be here?
                JudgeStallGUI.createAndOpen(p, judge);
            } else {
                // Using JudgeStall.java as my guide here
                final JudgeCourtGUI judgeCourtGUI = new JudgeCourtGUI(courtSession);
                judgeCourtGUI.setPlayer(p);
                judgeCourtGUI.open();
            }
        }
        
        if(args.length == 0 || courtSession == null) {
            if(args.length >= 1 && (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("mark")
                    || args[0].equalsIgnoreCase("mute") || args[0].equalsIgnoreCase("unmute")
                    || args[0].equalsIgnoreCase("contempt"))) {
                p.sendMessage(courts.getCourtsLangManager().getCourtNotInSession());
            }
            sendHelp(p, judge, courtSession);
            return true;
        }
        if(args[0].equalsIgnoreCase("tp")) {
            if(args.length == 2 && args[1].equalsIgnoreCase("court")) {
                courtSession.getCourtRoom().teleportTo(courtSession);
                p.sendMessage(ChatColor.GREEN + "Participants teleported.");
                return true;
            }
            if(args.length == 2) {
                final Location loc = locFromString(args[1], Bukkit.getWorld(SocialCore.plugin.getCourts().getCourtsConfig().getDefaultWorld()));
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
        }
        if(args[0].equalsIgnoreCase("mark")) {
            final Location loc;
            if(args.length >= 2) {
                loc = locFromString(args[0], p.getWorld());
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
        if(args.length == 2 && args[0].equalsIgnoreCase("mute")) {
            final String name = args[1];
            final Player toMute = Bukkit.getPlayer(name);
            if(toMute == null) {
                p.sendMessage(ChatColor.RED + "Could not find player: " + name);
                return true;
            }
            if(courtSession.getMuted().contains(toMute.getUniqueId())) {
                p.sendMessage(ChatColor.RED + name + " is already muted.");
                return true;
            }
            courtSession.getMuted().add(toMute.getUniqueId());
            p.sendMessage(ChatColor.GREEN + "Muted " + toMute.getName());
            return true;
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("unmute")) {
            final String name = args[1];
            final Player toMute = Bukkit.getPlayer(name);
            if(toMute == null) {
                p.sendMessage(ChatColor.RED + "Could not find player: " + name);
                return true;
            }
            if(!courtSession.getMuted().contains(toMute.getUniqueId())) {
                p.sendMessage(ChatColor.RED + name + " is not muted.");
                return true;
            }
            courtSession.getMuted().remove(toMute.getUniqueId());
            p.sendMessage(ChatColor.GREEN + "Unmuted " + toMute.getName());
            return true;
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("contempt")) {
            final String name = args[1];
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
        if(args.length == 3 && args[0].equalsIgnoreCase("fine")) {
            final String name = args[1];
            final UUID uuid = UUIDUtil.getUUID(name);
            if(uuid == null) {
                p.sendMessage(ChatColor.RED + "Could not find player: " + name);
                return true;
            }
            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if(offlinePlayer == null) {
                p.sendMessage(ChatColor.RED + "Could not find player: " + name);
                return true;
            }
            double amount = 0;
            try {
                amount = Math.round(Double.parseDouble(args[2]));
            } catch(final Exception e) {
                p.sendMessage(ChatColor.RED + "Failed to parse amount: " + args[2]);
            }
            final double maxFine = Courts.getCourts().getCourtsConfig().getMaxFine();
            if(amount > maxFine) {
                p.sendMessage(ChatColor.RED + "Could not fine because the maximum fine allowed is " + maxFine);
                return true;
            }
            boolean didIt = false;
            try {
                didIt = VaultUtil.charge(offlinePlayer, amount);
            } catch(final Exception e) {
                e.printStackTrace();
            }
            if(didIt) {
                p.sendMessage(ChatColor.GREEN + "Fined " + args[1] + ' ' + amount + " voxels.");
            } else {
                p.sendMessage(ChatColor.RED + "Could not fine " + args[1] + ' ' + amount + " voxels.");
            }
            Courts.getCourts().getPlugin().getLogger().info("Judge " + judge.getName() + " fined " + args[1] + ' ' + amount + " voxels.");
            return true;
        }
        sendHelp(p, judge, courtSession);
        return true;
    }
    
    public Location locFromString(final String in, final World defaultWorld) {
        final String[] split = in.split(",");
        if(split.length < 3 || split.length > 4) {
            return null;
        }
        final World world;
        if(split.length == 4) {
            world = Bukkit.getWorld(split[0]);
        } else {
            world = defaultWorld;
        }
        if(world == null) {
            return null;
        }
        final double x;
        final double y;
        final double z;
        try {
            int offset = 0;
            if(split.length > 3) {
                offset = 1;
            }
            x = Double.parseDouble(split[offset]);
            y = Double.parseDouble(split[offset + 1]);
            z = Double.parseDouble(split[offset + 2]);
        } catch(final Exception e) {
            return null;
        }
        return new Location(world, x, y, z);
    }
    
    private String formHelpLine(final String command, final String description) {
        return ChatColor.YELLOW + command + ChatColor.BLUE + " - " + description;
    }
    
    private void sendHelp(final Player p, final Judge judge, final CourtSession courtSession) {
        p.sendMessage(ChatColor.GRAY + "Your current approval rating is " + TextUtil.formatDouble(judge.approvalPercentage(), 2) + '%');
        if(courtSession == null) {
            p.sendMessage(ChatColor.RED + "There are no /judge commands you may currently use.");
            return;
        }
        p.sendMessage(formHelpLine("/judge chair", "Teleports you to your judge chair"));
        p.sendMessage(formHelpLine("/judge mark", "Mark a location for the ongoing court session"));
        p.sendMessage(formHelpLine("/judge mark [world],x,y,z", "Marks defined location for the ongoing court session"));
        p.sendMessage(formHelpLine("/judge tp [world],x,y,z", "Teleports you, the plaintiff, and the defendant to defined location"));
        p.sendMessage(formHelpLine("/judge tp court", "Teleports you, the plaintiff, and the defendant back to the court room"));
        p.sendMessage(formHelpLine("/judge mute <player>", "Mutes a player in the court room"));
        p.sendMessage(formHelpLine("/judge unmute <player>", "Unmutes a player in the court room"));
        p.sendMessage(formHelpLine("/judge contempt <player>", "Prevents player from entering courtroom"));
        p.sendMessage(formHelpLine("/judge fine <player> <amount>", "Fines a player specified amount of voxels"));
    }
}
