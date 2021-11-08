package com.nicholasdoherty.socialcore.components.courts.commands.judgeSubCommands;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.voxmc.voxlib.VLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JudgeChairCmd {
    private final Courts courts;
    private final CommandSender commandSender;

    public JudgeChairCmd(Courts courts, CommandSender commandSender) {
        this.courts = courts;
        this.commandSender = commandSender;
    }

    public boolean runCommand(){
        final Player p = (Player) commandSender;
        final VLocation vLoc = courts.getCourtsConfig().getDefaultCourtRoom().getJudgeChairLoc();
        if(vLoc == null || vLoc.getLocation() == null) {
            p.sendMessage(ChatColor.RED + "No judge chair defined for this courtroom.");
            return true;
        }
        Location loc = vLoc.getLocation();
        loc.setYaw(-90L);
        loc.setPitch(45L);
        p.teleport(loc);
        p.sendMessage(ChatColor.GREEN + "Teleported you to your judge chair");
        return true;
    }
}
