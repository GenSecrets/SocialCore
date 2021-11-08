package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.components.courts.judges.gui.approvalgui.JudgesApprovalGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CourtOfficialsCmd {
    private final CommandSender commandSender;
    private final JudgeManager judgeManager;
    private final String[] args;

    public CourtOfficialsCmd(CommandSender commandSender, JudgeManager judgeManager, String[] args) {
        this.commandSender = commandSender;
        this.judgeManager = judgeManager;
        this.args = args;
    }

    public boolean runCommand(){
            if (args.length == 0) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(ChatColor.RED + "You must be a player.");
                    return true;
                }
                Player p = (Player) commandSender;
                JudgesApprovalGUI.createAndOpen(p,judgeManager);
            }
        return true;
    }
}
