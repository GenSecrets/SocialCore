package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.Secretary;
import com.voxmc.voxlib.util.UUIDUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CourtDemoteCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final String[] args;

    public CourtDemoteCmd(Courts courts, CommandSender commandSender, String[] args) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.args = args;
    }

    public boolean runCommand(){
        if(args.length < 1) {
            commandSender.sendMessage(ChatColor.RED + "You must pass a name to be demoted.");
        }

        String name = args[0];
        UUID uuid = UUIDUtil.getUUID(name);
        if (uuid == null) {
            commandSender.sendMessage(ChatColor.RED +"Invalid name");
            return true;
        }
        Secretary secretary = courts.getJudgeManager().getSecretary(uuid);
        if (secretary != null) {
            secretary.getJudge().removeSecretary(secretary);
            commandSender.sendMessage(ChatColor.GOLD + name + " was demoted from being a secretary for judge " + secretary.getJudge().getName());
        }
        Judge judge = courts.getJudgeManager().getJudge(uuid);
        if (judge != null) {
            courts.getJudgeManager().demoteJudge(judge);
            commandSender.sendMessage(ChatColor.GOLD + name + " was demoted from being a judge");
        }
        if (secretary == null && judge == null) {
            commandSender.sendMessage(ChatColor.RED + name + " holds no positions in the court system.");
        }
        return true;
    }
}
