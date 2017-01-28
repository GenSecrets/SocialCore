package com.nicholasdoherty.socialcore.courts.commands;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.courts.judges.gui.approvalgui.JudgesApprovalGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/6/15.
 */
public class JudgesCommand implements CommandExecutor{
    private Courts courts;
    private JudgeManager judgeManager;

    public JudgesCommand(Courts courts, JudgeManager judgeManager) {
        this.courts = courts;
        this.judgeManager = judgeManager;
        courts.getPlugin().getCommand("judges").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
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
