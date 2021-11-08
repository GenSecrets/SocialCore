package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.stall;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.stall.StallType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;

public class CourtStallCreateCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final String[] args;

    public CourtStallCreateCmd(Courts courts, CommandSender commandSender, String[] args) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.args = args;
    }

    public boolean runCommand() {
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

        if(args.length < 1){
            p.sendMessage(ChatColor.RED + "You must specify what type of stall to create: Judge, Secretary, Citizen, or Master");
        }

        switch (args[0].toLowerCase()) {
            case "citizen":
                courts.getStallManager().createStall(b.getLocation(), StallType.CITIZEN);
                p.sendMessage(ChatColor.GREEN + "Created citizen stall");
                break;
            case "secretary":
                courts.getStallManager().createStall(b.getLocation(), StallType.SECRETARY);
                p.sendMessage(ChatColor.GREEN + "Created secretary stall");
                break;
            case "master":
                courts.getStallManager().createStall(b.getLocation(), StallType.MASTERLIST);
                p.sendMessage(ChatColor.GREEN + "Created master cases stall");
                break;
            case "judge":
                courts.getStallManager().createStall(b.getLocation(), StallType.JUDGE);
                p.sendMessage(ChatColor.GREEN + "Created judge stall");
                break;
            default:
                p.sendMessage(ChatColor.RED + "You must specify what type of stall to create: Judge, Secretary, Citizen, or Master");
        }
        return true;
    }
}
