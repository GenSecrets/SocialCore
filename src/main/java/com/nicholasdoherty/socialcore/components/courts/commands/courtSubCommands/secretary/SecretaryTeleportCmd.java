package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.secretary;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.voxmc.voxlib.VLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SecretaryTeleportCmd {
    private final Courts courts;
    private final CommandSender commandSender;

    public SecretaryTeleportCmd(Courts courts, CommandSender commandSender) {
        this.courts = courts;
        this.commandSender = commandSender;
    }

    public boolean runCommand(){
        final Player p = (Player) commandSender;
        final VLocation vLoc = courts.getCourtsConfig().getDefaultCourtRoom().getSecDeskLoc();
        if(vLoc == null || vLoc.getLocation() == null) {
            p.sendMessage(ChatColor.RED + "No secretary desk location defined for this courtroom.");
            return true;
        }
        Location loc = vLoc.getLocation();
        loc.setYaw(90L);
        loc.setPitch(0L);
        p.teleport(loc);
        p.sendMessage(ChatColor.GREEN + "Teleported you to your secretary desk.");
        return true;
    }
}
