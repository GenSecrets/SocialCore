package com.nicholasdoherty.socialcore.courts.commands;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.elections.ElectionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Created by john on 4/18/16.
 */
public class IfElectionCommand implements CommandExecutor {
    ElectionManager electionManager;
    private Courts courts;
    
    public IfElectionCommand(Courts courts, ElectionManager electionManager) {
        this.courts = courts;
        this.electionManager = electionManager;
        courts.getPlugin().getCommand("ifelection").setExecutor(this);
        /*if (Bukkit.getServer().getPluginManager().isPluginEnabled("LogFilter")) {
            LogFilter plugin = (LogFilter) Bukkit.getServer().getPluginManager().getPlugin("LogFilter");
            try {
                Set<Pattern> patterns = (Set<Pattern>) LogFilter.class.getDeclaredField("patterns").get(plugin);
                patterns.add(Pattern.compile("/ifelection ifelection"));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }*/
    }
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length == 1 && strings[0].equalsIgnoreCase("throw")) {
            if(!electionManager.requirementsForScheduleElectionMet()) {
                throw new CommandException("Throwing exception because requirements not met... (harmless)");
            }
            return true;
        }
        if(strings.length == 2 && strings[0].equalsIgnoreCase("shouldwait")) {
            if(electionManager.isScheduled()) {
                long time = Long.parseLong(strings[1]);
                if(time - electionManager.judgeNeededTime() <= courts.getCourtsConfig().getMinElectionWaitMillis()) {
                    throw new CommandException("Should delay be an interval");
                }
            }
            return true;
        }
        if(strings.length < 1) {
            commandSender.sendMessage("No command");
            return true;
        }
        String[] args = Arrays.copyOfRange(strings, 1, strings.length);
        String commandString = strings[0] + " " + String.join(" ", args);
        if(electionManager.requirementsForScheduleElectionMet()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandString);
        }
        return true;
    }
}
