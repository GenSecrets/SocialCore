package com.nicholasdoherty.socialcore.components.courts.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.commands.judgeSubCommands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("judge")
@CommandPermission("socialcore.courts.judge")
@Description("Manages all judge related commands for court.")
public class JudgeCommandHandler extends BaseCommand {
    private Courts courts;
    private SocialCore sc;

    public JudgeCommandHandler(Courts courts, SocialCore sc) {
        this.courts = courts;
        this.sc = sc;
    }

    @Default
    public boolean onDefaultCommand(final CommandSender commandSender){
        return sendJudgeHelp(commandSender);
    }

    @Subcommand("contempt")
    @CommandPermission("socialcore.courts.judge.contempt")
    public boolean onJudgeContempt(final CommandSender commandSender, final String[] args) {
        JudgeContemptCmd cmd = new JudgeContemptCmd(courts, commandSender, courts.getJudgeManager(), args);
        return cmd.runCommand();
    }

    @Subcommand("fine")
    @CommandPermission("socialcore.courts.judge.fine")
    public boolean onJudgeFine(final CommandSender commandSender, final String[] args) {
        JudgeFineCmd cmd = new JudgeFineCmd(courts, commandSender, courts.getJudgeManager(), args);
        return cmd.runCommand();
    }

    @Subcommand("mark")
    @CommandPermission("socialcore.courts.judge.mark")
    public boolean onJudgeMark(final CommandSender commandSender, final String[] args) {
        JudgeMarkCmd cmd = new JudgeMarkCmd(courts, commandSender, courts.getJudgeManager(),args);
        return cmd.runCommand();
    }

    @Subcommand("menu")
    @CommandPermission("socialcore.courts.judge.menu")
    public boolean onJudgeMenu(final CommandSender commandSender) {
        JudgeMenuCmd cmd = new JudgeMenuCmd(courts, courts.getJudgeManager(), commandSender);
        return cmd.runCommand();
    }

    @Subcommand("chair")
    @CommandPermission("socialcore.courts.judge.chair")
    public boolean onJudgeChair(final CommandSender commandSender) {
        JudgeChairCmd cmd = new JudgeChairCmd(courts, commandSender);
        return cmd.runCommand();
    }

    @Subcommand("teleport")
    @CommandPermission("socialcore.courts.judge.teleport")
    public boolean onJudgeTeleport(final CommandSender commandSender, final String[] args) {
        JudgeTeleportCmd cmd = new JudgeTeleportCmd(courts, commandSender, courts.getJudgeManager(), args);
        return cmd.runCommand();
    }

    @Subcommand("mute")
    @CommandPermission("socialcore.courts.judge.mute")
    public boolean onJudgeMute(final CommandSender commandSender, final String[] args) {
        JudgeMuteCmd cmd = new JudgeMuteCmd(courts, commandSender, courts.getJudgeManager(), args);
        return cmd.runCommand();
    }

    @Subcommand("unmute")
    @CommandPermission("socialcore.courts.judge.unmute")
    public boolean onJudgeUnmute(final CommandSender commandSender, final String[] args) {
        JudgeUnmuteCmd cmd = new JudgeUnmuteCmd(courts, commandSender, courts.getJudgeManager(), args);
        return cmd.runCommand();
    }

    public static boolean sendJudgeHelp(final CommandSender commandSender) {
        List<String> helpCommands = new ArrayList<>();
        helpCommands.add(formHelpLine("/judge chair", "Teleports you to your judge chair"));
        helpCommands.add(formHelpLine("/judge mark", "Mark a location for the ongoing court session"));
        helpCommands.add(formHelpLine("/judge mark [world],x,y,z", "Marks defined location for the ongoing court session"));
        helpCommands.add(formHelpLine("/judge tp [world],x,y,z", "Teleports you, the plaintiff, and the defendant to defined location"));
        helpCommands.add(formHelpLine("/judge tp court", "Teleports you, the plaintiff, and the defendant back to the court room"));
        helpCommands.add(formHelpLine("/judge mute <player>", "Mutes a player in the court room"));
        helpCommands.add(formHelpLine("/judge unmute <player>", "Unmutes a player in the court room"));
        helpCommands.add(formHelpLine("/judge contempt <player>", "Prevents player from entering courtroom"));
        helpCommands.add(formHelpLine("/judge fine <player> <amount>", "Fines a player specified amount of voxels"));

        for (String help : helpCommands) {
            commandSender.sendMessage(help);
        }
        return true;
    }

    private static String formHelpLine(final String command, final String description) {
        return ChatColor.YELLOW + command + ChatColor.BLUE + " - " + description;
    }
}
