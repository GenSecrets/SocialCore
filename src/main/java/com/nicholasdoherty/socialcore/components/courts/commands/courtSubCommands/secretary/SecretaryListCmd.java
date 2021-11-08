package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.secretary;

import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.utils.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SecretaryListCmd {
    private final CommandSender commandSender;
    private final JudgeManager judgeManager;

    public SecretaryListCmd(CommandSender commandSender, JudgeManager judgeManager) {
        this.commandSender = commandSender;
        this.judgeManager = judgeManager;
    }

    public boolean runCommand() {
        Player p = (Player) commandSender;
        final Judge judge = judgeManager.getJudge(p.getUniqueId());

        if(judge == null) {
            p.sendMessage(ChatColor.RED + "You are not a judge.");
            return true;
        }

        if(judge.getSecretaries().isEmpty()) {
            p.sendMessage(ChatColor.GREEN + "You have no secretaries.");
        } else {
            p.sendMessage(ChatColor.GREEN + "Your secretaries are: " + TextUtil.fancyList(TextUtil.citizenNames(judge.getSecretaries())));
        }
        return true;
    }
}
