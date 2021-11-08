package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.election;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.elections.ElectionManager;
import com.nicholasdoherty.socialcore.components.courts.elections.gui.ElectionGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CourtElectionVoteCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final ElectionManager electionManager;

    public CourtElectionVoteCmd(Courts courts, CommandSender commandSender, ElectionManager electionManager) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.electionManager = electionManager;
    }

    public boolean runCommand(){
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to open the Election GUI.");
            return true;
        }
        final Player p = (Player) commandSender;
        if(electionManager.isElectionActive() && electionManager.getCurrentElection().getCandidateSet().size() < 0) {
            commandSender.sendMessage(courts.getCourtsLangManager().getElectionNoSlots());
            return true;
        } else if(electionManager.isElectionActive()) {
            if(courts.getJudgeManager().isAtMax()) {
                commandSender.sendMessage(ChatColor.RED + "The judge roster is currently full.");
                return true;
            }
            ElectionGUI.createAndOpen(p, electionManager.getCurrentElection());
            return true;
        } else {
            commandSender.sendMessage(ChatColor.RED + "There is no election in progress.");
            return true;
        }
    }
}
