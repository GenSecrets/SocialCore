package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin;

import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import com.voxmc.voxlib.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class CourtToCitizenCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final String[] args;

    public CourtToCitizenCmd(Courts courts, CommandSender commandSender, String[] args) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.args = args;
    }

    public boolean runCommand() {
        String name = args[0];
        OfflinePlayer op = Bukkit.getOfflinePlayer(UUIDUtil.getUUID(name));

        if (!op.hasPlayedBefore()) {
            commandSender.sendMessage(ChatColor.RED + "Invalid name");
            return true;
        }

        Citizen citizen = courts.getCitizenManager().toCitizen(op);
        if (citizen == null){
            commandSender.sendMessage(ChatColor.RED + "Unable to save Citizen.");
            return true;
        }
        commandSender.sendMessage(ChatColor.GREEN + "Citizen saved.");
        return true;
    }
}
