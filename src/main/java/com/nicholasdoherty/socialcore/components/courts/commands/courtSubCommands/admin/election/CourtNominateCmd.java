package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.election;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.elections.Election;
import com.nicholasdoherty.socialcore.components.courts.elections.ElectionManager;
import com.nicholasdoherty.socialcore.utils.CourtUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class CourtNominateCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final ElectionManager electionManager;
    private final String[] args;

    public CourtNominateCmd(Courts courts, CommandSender commandSender, ElectionManager electionManager, String[] args) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.electionManager = electionManager;
        this.args = args;
    }

    public boolean runCommand(){
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to run this command.");
            return true;
        }

        final Player p = courts.getCitizenManager().getCitizen(args[0]).getPlayer();
        if(p == null) {
            commandSender.sendMessage(ChatColor.RED + "Could not find the player, either they aren't online or you mistyped their name.");
            return true;
        }
        if(courts.getJudgeManager().isSecretary(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "Player may not run as judge while being a secretary.");
            return true;
        }
        if(!electionManager.isElectionActive()) {
            commandSender.sendMessage(ChatColor.RED + "There is not an active election at this time.");
        } else {
            final Election election = electionManager.getCurrentElection();
            final UUID uuid = p.getUniqueId();
            if(election.isInElection(uuid)) {
                p.sendMessage(ChatColor.RED + "You are already running in this election.");
            } else {
                CourtUtil.tryNominate(election, p, courts);
            }
        }
        return true;
    }
}
