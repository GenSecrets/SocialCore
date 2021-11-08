package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.stall;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.stall.Stall;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;

public class CourtStallRemoveCmd {
    private final Courts courts;
    private final CommandSender commandSender;

    public CourtStallRemoveCmd(Courts courts, CommandSender commandSender) {
        this.courts = courts;
        this.commandSender = commandSender;
    }

    public boolean runCommand(){
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player.");
            return true;
        }
        Player p = (Player) commandSender;
        Block b = p.getTargetBlock(new HashSet<Material>(Arrays.asList(new Material[]{Material.AIR})),50);
        if (b == null || b.getType() != Material.CHEST) {
            p.sendMessage(ChatColor.RED + "Look at a chest.");
            return true;
        }
        Stall stall = courts.getStallManager().getStall(b.getLocation());
        if (stall == null) {
            p.sendMessage(ChatColor.RED + "No stall here");
            return true;
        }
        courts.getStallManager().removeStall(stall);
        p.sendMessage(ChatColor.GREEN + "Removed " + stall.getStallType() + " stall");
        return true;
    }
}
