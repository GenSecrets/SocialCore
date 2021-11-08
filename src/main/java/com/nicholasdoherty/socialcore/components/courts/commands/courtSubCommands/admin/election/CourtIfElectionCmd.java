package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.election;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.elections.ElectionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class CourtIfElectionCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final ElectionManager electionManager;
    private final String[] args;

    public CourtIfElectionCmd(Courts courts, CommandSender commandSender, ElectionManager electionManager, String[] args) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.electionManager = electionManager;
        this.args = args;
    }

    public boolean runCommand(){
        if(args.length == 1 && args[0].equalsIgnoreCase("throw")) {
            if(!electionManager.requirementsForScheduleElectionMet()) {
                throw new CommandException("Throwing exception because requirements not met... (harmless)");
            }
            return true;
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("shouldwait")) {
            if(electionManager.isScheduled()) {
                long time = Long.parseLong(args[1]);
                if(time - electionManager.judgeNeededTime() <= courts.getCourtsConfig().getMinElectionWaitMillis()) {
                    throw new CommandException("Should delay be an interval");
                }
            }
            return true;
        }
        if(args.length < 1) {
            commandSender.sendMessage("No command");
            return true;
        }
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        String commandString = newArgs[0] + " " + String.join(" ", newArgs);
        if(electionManager.requirementsForScheduleElectionMet()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandString);
        }
        return true;
    }
}
