package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CourtTeleportCmd {
    private final Courts courts;
    private final CommandSender commandSender;

    public CourtTeleportCmd(Courts courts, CommandSender commandSender) {
        this.courts = courts;
        this.commandSender = commandSender;
    }

    public boolean runCommand() {
        Player p = (Player) commandSender;
        Citizen citizen = Courts.getCourts().getCitizenManager().toCitizen(p);
        CourtSession courtSession = courts.getCourtSessionManager().getActiveCourtSession(citizen);
        if (courtSession == null) {
            p.sendMessage(ChatColor.RED + "You are not a part of any ongoing court sessions.");
            return true;
        }
        Location loc = courtSession.getCourtRoom().getTpLocation().getLocation();
        if (loc == null) {
            p.sendMessage(ChatColor.RED + "No teleport location, Notify admins...");
            return true;
        }
        p.teleport(loc);
        p.sendMessage(ChatColor.GREEN + "You have been teleported to the court room of judge " + courtSession.getJudge().getName());
        return true;
    }
}
