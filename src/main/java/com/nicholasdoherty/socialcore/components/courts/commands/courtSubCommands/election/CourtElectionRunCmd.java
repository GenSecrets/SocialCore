package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.election;

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

public class CourtElectionRunCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final ElectionManager electionManager;
    public final static Collection<UUID> toConfirmers = new HashSet<>();

    public CourtElectionRunCmd(Courts courts, CommandSender commandSender, ElectionManager electionManager) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.electionManager = electionManager;
    }

    public boolean runCommand() {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to run in an election.");
            return true;
        }
        final Player p = (Player) commandSender;
        if(courts.getJudgeManager().isSecretary(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "You may not run as judge as a secretary.");
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
