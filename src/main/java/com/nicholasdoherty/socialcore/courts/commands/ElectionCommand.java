package com.nicholasdoherty.socialcore.courts.commands;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.courts.elections.Election;
import com.nicholasdoherty.socialcore.courts.elections.ElectionManager;
import com.nicholasdoherty.socialcore.courts.elections.gui.ElectionGUI;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.VaultUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by john on 1/6/15.
 */
public class ElectionCommand implements CommandExecutor{
    Courts courts;
    ElectionManager electionManager;

    public ElectionCommand(Courts courts, ElectionManager electionManager) {
        this.courts = courts;
        this.electionManager = electionManager;
        courts.getPlugin().getCommand("election").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "You must be a player to open the Election GUI.");
                return true;
            }
            Player p = (Player) commandSender;
            if (electionManager.isElectionActive() && electionManager.getCurrentElection().getCandidateSet().size() < 0) {
                commandSender.sendMessage(courts.getCourtsLangManager().getElectionNoSlots());
                return true;
            }else if (electionManager.isElectionActive()){
                if (courts.getJudgeManager().isAtMax()) {
                    commandSender.sendMessage(ChatColor.RED + "The judge roster is currently full.");
                    return true;
                }
                ElectionGUI.createAndOpen(p,electionManager.getCurrentElection());
                return true;
            }else {
                commandSender.sendMessage(ChatColor.RED + "There is no election in progress.");
                return true;
            }
        }
        if (commandSender.isOp() && args.length > 1 && args[0].equalsIgnoreCase("removewithvotes")) {
            String playerName = args[1];
            Citizen citizen = courts.getCitizenManager().getCitizen(playerName);
            if (citizen == null) {
                commandSender.sendMessage("could not find " + playerName);
                return true;
            }
            Election election =electionManager.getCurrentElection();
            if (election != null) {
                Optional<Candidate> cand = election.getCandidateSet().stream().filter(candidate -> candidate.getUuid().equals(citizen.getUuid())).findAny();
                cand.ifPresent(ApprovedCitizen::resetVotes);
                cand.ifPresent(election::removeCandiate);
            }
            Judge judge = courts.getJudgeManager().getJudge(citizen.getUuid());
            if (judge != null) {
                courts.getJudgeManager().demoteJudge(judge);
            }
            courts.getSqlSaveManager().removeVotes(citizen);
            commandSender.sendMessage("removed all votes and elected ranks from " + citizen.getName());
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("run")) {
            if (!commandSender.hasPermission("courts.run")) {
                commandSender.sendMessage(ChatColor.RED + "You don't have the required permissions");
                return true;
            }
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "You must be a player to run in an election.");
                return true;
            }
            Player p = (Player) commandSender;
            if (courts.getJudgeManager().isSecretary(p.getUniqueId())) {
                p.sendMessage(ChatColor.RED + "You may not run as judge as a secretary.");
                return true;
            }
            if (!electionManager.isElectionActive()) {
                commandSender.sendMessage(ChatColor.RED + "There is not an active election at this time.");
                return true;
            }else {
                Election election = electionManager.getCurrentElection();
                UUID uuid = p.getUniqueId();
                if (election.isInElection(uuid)) {
                    p.sendMessage(ChatColor.RED + "You are already running in this election.");
                }else {
                    tryNominate(election, p);
                }
                return true;
            }
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            if (!commandSender.hasPermission("courts.admin")) {
                commandSender.sendMessage(ChatColor.RED + "You don't have the required permissions");
                return true;
            }
            Election election = electionManager.getCurrentElection();
            if (!electionManager.isElectionActive() || election == null) {
                commandSender.sendMessage(ChatColor.RED + "There is not an active election at this time.");
                return true;
            }
            String name = args[1];
            Candidate toRemove = null;
            for (Candidate candidate : election.getCandidateSet()) {
                if (candidate.getName().equalsIgnoreCase(name)) {
                    toRemove = candidate;
                    break;
                }
            }
            if (toRemove == null) {
                commandSender.sendMessage(ChatColor.RED + "Could not find candiate with name " + name);
                return true;
            }
            election.removeCandiate(toRemove);
            commandSender.sendMessage(ChatColor.GREEN + "Removed" + toRemove.getName() + " from the election");
            return true;
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("addvotes")) {
            if (!commandSender.hasPermission("courts.admin")) {
                commandSender.sendMessage(ChatColor.RED + "You don't have the required permissions");
                return true;
            }
            Election election = electionManager.getCurrentElection();
            if (!electionManager.isElectionActive() || election == null) {
                commandSender.sendMessage(ChatColor.RED + "There is not an active election at this time.");
                return true;
            }
            String name = args[1];
            Candidate can = null;
            for (Candidate candidate : election.getCandidateSet()) {
                if (candidate.getName().equalsIgnoreCase(name)) {
                    can = candidate;
                    break;
                }
            }
            if (can == null) {
                commandSender.sendMessage(ChatColor.RED + "Could not find candiate with name " + name);
                return true;
            }
            boolean approve = true;
            if (args[2].contains("d") || args[1].contains("D")) {
                approve = false;
            }
            int amountVotes = Integer.parseInt(args[3]);
            for (int i = 0; i < amountVotes; i++) {
                if (approve) {
                    can.approve(UUID.randomUUID());
                }else {
                    can.disapprove(UUID.randomUUID());
                }
            }
            Courts.getCourts().getElectionManager().checkWin(election,can);
            String approveString = "approve";
            if (!approve) {
                approveString = "disapprove";
            }
            commandSender.sendMessage(ChatColor.GREEN + "Gave " + amountVotes + " " + approveString + " votes to " + can.getName());
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("resetvotes")) {
            if (!commandSender.hasPermission("courts.admin")) {
                commandSender.sendMessage(ChatColor.RED + "You don't have the required permissions");
                return true;
            }
            Election election = electionManager.getCurrentElection();
            if (!electionManager.isElectionActive() || election == null) {
                commandSender.sendMessage(ChatColor.RED + "There is not an active election at this time.");
                return true;
            }
            String name = args[1];
            Candidate can = null;
            for (Candidate candidate : election.getCandidateSet()) {
                if (candidate.getName().equalsIgnoreCase(name)) {
                    can = candidate;
                    break;
                }
            }
            if (can == null) {
                commandSender.sendMessage(ChatColor.RED + "Could not find candiate with name " + name);
                return true;
            }
            can.resetVotes();
            Courts.getCourts().getElectionManager().checkWin(election,can);
            commandSender.sendMessage(ChatColor.GREEN + "Reset " + can.getName() +"'s votes");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
            if (!commandSender.hasPermission("courts.admin")) {
                commandSender.sendMessage(ChatColor.RED + "You don't have the required permissions");
                return true;
            }
            if (!electionManager.requirementsToStartElectionMet() && !Arrays.asList(args).stream().anyMatch(m -> m.equalsIgnoreCase("force"))) {
                commandSender.sendMessage("Requirements not met... use force to force start");
            }
            electionManager.startElection();
            commandSender.sendMessage(ChatColor.GREEN + "A new election has begun.");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("end")) {
            if (!commandSender.hasPermission("courts.admin")) {
                commandSender.sendMessage(ChatColor.RED + "You don't have the required permissions");
                return true;
            }
            if (!electionManager.isElectionActive()) {
                commandSender.sendMessage(ChatColor.RED + "There is not an active election at this time.");
                return true;
            }
            electionManager.endCurrentElection();
            commandSender.sendMessage(ChatColor.GREEN + "Election ended.");
            return true;
        }
        sendHelp(commandSender);
        return true;
    }

    private Set<UUID> toConfirmers = new HashSet<>();
    public void tryNominate(Election election,Player p) {
        final UUID uuid = p.getUniqueId();
        double cost = courts.getCourtsConfig().getNominateSelfCost();
        if (!toConfirmers.contains(uuid)) {
            p.sendMessage(courts.getCourtsLangManager().getConfirmNominateSelfMessage().replace("{cost}", cost+""));
            toConfirmers.add(uuid);
            new BukkitRunnable(){
                @Override
                public void run() {
                    if (toConfirmers.contains(uuid)) {
                        toConfirmers.remove(uuid);
                    }
                }
            }.runTaskLater(courts.getPlugin(),1200);
            return;
        }
        boolean charged;
        try {
            charged = VaultUtil.charge(p,cost);
        } catch (VaultUtil.NotSetupException e) {
            e.printStackTrace();
            p.sendMessage(ChatColor.RED + "Payments are currently down.");
            return;
        }
        if (!charged) {
            p.sendMessage(ChatColor.RED + "We could not charge you the " + cost + " voxels.");
            return;
        }
        Courts.getCourts().getPlugin().getLogger().info(p.getName() + " has been charged "+cost+" voxels to nominate their self to become a judge.");
        Citizen citizen = Courts.getCourts().getCitizenManager().toCitizen(p);
        try {
            VaultUtil.give(p,cost);
        }catch (Exception e) {
            p.sendMessage(ChatColor.RED + "Error adding you to the election, you may already be running.");
            p.sendMessage(ChatColor.RED + "You have been refunded.");
            Courts.getCourts().getPlugin().getLogger().info(p.getName() + " has been refunded "+cost+" voxels because of an error. (Are they already running?)");
            e.printStackTrace();
            return;
        }
        Candidate candidate = Courts.getCourts().getSqlSaveManager().createCandidate(citizen);
        election.addCandidate(candidate);
        Courts.getCourts().getNotificationManager().notification(NotificationType.JUDGE_NOMINATED_SELF,new Object[]{p},p);
        //p.sendMessage(ChatColor.GREEN + "You are now running in this election.");
        Set<UUID> notSend = new HashSet<>();
        notSend.add(p.getUniqueId());
        courts.getNotificationManager().notification(NotificationType.JUDGE_NOMINATED_ALL, new Object[]{p}, notSend);
    }
    private void sendHelp(CommandSender commandSender) {
        List<String> helpCommands = new ArrayList<>();
        if (commandSender.hasPermission("courts.run")) {
            helpCommands.add(formHelpLine("/election run", "Nominate yourself to run in the current election"));
        }
        if (commandSender.hasPermission("courts.admin")) {
            //helpCommands.add(formHelpLine("/election start", "Start a new election"));
            //helpCommands.add(formHelpLine("/election end", "Ends current election"));
            helpCommands.add(formHelpLine("/election remove [name]","Removes a candidate from the current election"));
            helpCommands.add(formHelpLine("/election addvotes <name> <approve/disapprove> <amount>","Removes a candidate from the current election"));
            helpCommands.add(formHelpLine("/election resetvotes <name>","Removes a candidate from the current election"));
        }

        for (String help : helpCommands) {
            commandSender.sendMessage(help);
        }

    }
    private String formHelpLine(String command, String description) {
        return ChatColor.YELLOW + command + ChatColor.BLUE + " - " + description;
    }

}
