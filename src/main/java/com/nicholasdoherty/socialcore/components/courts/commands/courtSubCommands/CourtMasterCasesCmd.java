package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands;

import com.nicholasdoherty.socialcore.components.courts.mastercases.MasterCaseGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CourtMasterCasesCmd {
    private final CommandSender commandSender;

    public CourtMasterCasesCmd(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    public boolean runCommand() {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player.");
            return true;
        }
        Player p = (Player) commandSender;
        MasterCaseGUI.createAndOpen(p);
        return true;
    }
}
