package com.nicholasdoherty.socialcore.utils;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.election.CourtElectionRunCmd;
import com.nicholasdoherty.socialcore.components.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.components.courts.elections.Election;
import com.nicholasdoherty.socialcore.components.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CourtUtil {
    public static Candidate getCandidate(Election election, CommandSender commandSender, String name, boolean isElectionActive){
        if(!isElectionActive || election == null) {
            commandSender.sendMessage(ChatColor.RED + "There is not an active election at this time.");
            return null;
        }
        Candidate can = null;
        for(final Candidate candidate : election.getCandidateSet()) {
            if(candidate.getName().equalsIgnoreCase(name)) {
                can = candidate;
                break;
            }
        }
        if(can == null) {
            commandSender.sendMessage(ChatColor.RED + "Could not find candidate with name " + name);
            return null;
        }
        return can;
    }

    public static void tryNominate(final Election election, final Player p, Courts courts) {
        final UUID uuid = p.getUniqueId();
        final double cost = courts.getCourtsConfig().getNominateSelfCost();
        if(!CourtElectionRunCmd.toConfirmers.contains(uuid)) {
            p.sendMessage(courts.getCourtsLangManager().getConfirmNominateSelfMessage().replace("{cost}", cost + ""));
            CourtElectionRunCmd.toConfirmers.add(uuid);
            new BukkitRunnable() {
                @Override
                public void run() {
                    CourtElectionRunCmd.toConfirmers.remove(uuid);
                }
            }.runTaskLaterAsynchronously(courts.getPlugin(), 1200);
            return;
        }
        final boolean charged;
        try {
            charged = VaultUtil.charge(p, cost);
        } catch(final Exception e) {
            e.printStackTrace();
            p.sendMessage(ChatColor.RED + "Payments are currently down.");
            return;
        }
        if(!charged) {
            p.sendMessage(ChatColor.RED + "We could not charge you the " + cost + " voxels.");
            return;
        }
        Courts.getCourts().getPlugin().getLogger().info(p.getName() + " has been charged " + cost + " voxels to nominate their self to become a judge.");
        final Citizen citizen = Courts.getCourts().getCitizenManager().toCitizen(p);
        try {
            final Candidate candidate = Courts.getCourts().getSqlSaveManager().createCandidate(citizen);
            election.addCandidate(candidate);
            Courts.getCourts().getNotificationManager().notification(NotificationType.JUDGE_NOMINATED_SELF, new Object[] {p}, p);
            final Set<UUID> notSend = new HashSet<>();
            notSend.add(p.getUniqueId());
            courts.getNotificationManager().notification(NotificationType.JUDGE_NOMINATED_ALL, new Object[] {p}, notSend);
        } catch(final Exception e) {
            try {
                VaultUtil.give(p, cost);
            } catch(final Exception e1) {
                e1.printStackTrace();
            }
            p.sendMessage(ChatColor.RED + "Error adding you to the election, you may already be running.");
            p.sendMessage(ChatColor.RED + "You have been refunded.");
            Courts.getCourts().getPlugin().getLogger().info(p.getName() + " has been refunded " + cost + " voxels because of an error. (Are they already running?)");
            e.printStackTrace();
        }
    }

    public static Location locFromString(final String in, final World defaultWorld) {
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
}
