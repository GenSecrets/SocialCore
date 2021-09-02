package com.nicholasdoherty.socialcore.components.courts.commands;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.components.courts.elections.Election;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.components.courts.mastercases.MasterCaseGUI;
import com.nicholasdoherty.socialcore.components.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.components.courts.stall.Stall;
import com.nicholasdoherty.socialcore.components.courts.stall.StallType;
import com.voxmc.voxlib.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by john on 1/6/15.
 */
public class CourtCommand implements CommandExecutor{
    private Courts courts;

    public CourtCommand(Courts courts) {
        this.courts = courts;
        courts.getPlugin().getCommand("court").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sendHelp(commandSender);
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("tp") && commandSender instanceof Player) {
            Player p = (Player) commandSender;
            Citizen citizen = Courts.getCourts().getCitizenManager().toCitizen(p);
            CourtSession courtSession = courts.getCourtSessionManager().getActiveCourtSession(citizen);
            if (courtSession == null) {
                p.sendMessage(ChatColor.RED + "You are not a part of any ongoing court sessions.");
                return true;
            }
            Location loc = courtSession.getCourtRoom().getTpLocation().getLocation();
            if (loc == null) {
                p.sendMessage(ChatColor.RED + "No teleport location, Notify admins...");
                return true;
            }
            p.teleport(loc);
            p.sendMessage(ChatColor.GREEN + "You have been teleported to the court room of judge " + courtSession.getJudge().getName());
            return true;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("master") && args[1].equalsIgnoreCase("cases")) {
            if (!commandSender.hasPermission("courts.mastercases")) {
                commandSender.sendMessage(ChatColor.RED +"You don't have permission to view the master case list.");
                return true;
            }
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "You must be a player.");
                return true;
            }
            Player p = (Player) commandSender;
            MasterCaseGUI.createAndOpen(p);
            return true;
        }
        if (commandSender.hasPermission("courts.admin")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("silence")) {
                courts.getCourtsConfig().getDefaultCourtRoom().silence();
                commandSender.sendMessage(ChatColor.GREEN + "Sent silence");
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("endsessions")) {
                courts.getCourtSessionManager().endAll();
                commandSender.sendMessage("ended");
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(ChatColor.RED + "You must be a player.");
                    return true;
                }
                Player p = (Player) commandSender;
                Block b = p.getTargetBlock(new HashSet<Material>(Arrays.asList(new Material[]{Material.AIR})),50);
                if (b == null || b.getType() != Material.CHEST) {
                    p.sendMessage(ChatColor.RED + "Look at a chest.");
                    return true;
                }
                Stall satll = courts.getStallManager().getStall(b.getLocation());
                if (satll == null) {
                    p.sendMessage(ChatColor.RED + "No stall here");
                    return true;
                }
                courts.getStallManager().removeStall(satll);
                p.sendMessage(ChatColor.GREEN + "Removed " + satll.getStallType() + " stall");
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("citizenstall")) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(ChatColor.RED + "You must be a player.");
                    return true;
                }
                Player p = (Player) commandSender;
                Block b = p.getTargetBlock(new HashSet<Material>(Arrays.asList(new Material[]{Material.AIR})),50);
                if (b == null || b.getType() != Material.CHEST) {
                    p.sendMessage(ChatColor.RED + "Look at a chest.");
                    return true;
                }
                courts.getStallManager().createStall(b.getLocation(), StallType.CITIZEN);
                p.sendMessage(ChatColor.GREEN + "Created citizen stall");
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("secretarystall")) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(ChatColor.RED + "You must be a player.");
                    return true;
                }
                Player p = (Player) commandSender;
                Block b = p.getTargetBlock(new HashSet<Material>(Arrays.asList(new Material[]{Material.AIR})),50);
                if (b == null || b.getType() != Material.CHEST) {
                    p.sendMessage(ChatColor.RED + "Look at a chest.");
                    return true;
                }
                courts.getStallManager().createStall(b.getLocation(), StallType.SECRETARY);
                p.sendMessage(ChatColor.GREEN + "Created secretary stall");
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("masterliststall")) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(ChatColor.RED + "You must be a player.");
                    return true;
                }
                Player p = (Player) commandSender;
                Block b = p.getTargetBlock(new HashSet<Material>(Arrays.asList(new Material[]{Material.AIR})),50);
                if (b == null || b.getType() != Material.CHEST) {
                    p.sendMessage(ChatColor.RED + "Look at a chest.");
                    return true;
                }
                courts.getStallManager().createStall(b.getLocation(), StallType.MASTERLIST);
                p.sendMessage(ChatColor.GREEN + "Created masterlist stall");
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("judgestall")) {
                if (!(commandSender instanceof Player)) {
                    commandSender.sendMessage(ChatColor.RED + "You must be a player.");
                    return true;
                }
                Player p = (Player) commandSender;
                Block b = p.getTargetBlock(new HashSet<Material>(Arrays.asList(new Material[]{Material.AIR})),50);
                if (b == null || b.getType() != Material.CHEST) {
                    p.sendMessage(ChatColor.RED + "Look at a chest.");
                    return true;
                }
                courts.getStallManager().createStall(b.getLocation(), StallType.JUDGE);
                p.sendMessage(ChatColor.GREEN + "Created judge stall");
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                courts.reloadConfig();
                commandSender.sendMessage(ChatColor.GREEN + "Reloaded config.yml for courts");
                return true;
            }
            if (args.length > 1 && args[0].equalsIgnoreCase("promote")) {
                String name = args[1];
                Player p = Bukkit.getPlayer(name);
                if (p == null) {
                    commandSender.sendMessage(ChatColor.RED +"Invalid name");
                    return true;
                }
                UUID uuid = p.getUniqueId();
                name = UUIDUtil.prettyName(name,uuid);
                if (args.length == 3 && args[2].equalsIgnoreCase("judge")) {
                    Citizen citizen = Courts.getCourts().getCitizenManager().toCitizen(name,uuid);
                    ApprovedCitizen approvedCitizen = Courts.getCourts().getSqlSaveManager().getApprovedCitizen(citizen);
                    int votes = courts.getCourtsConfig().getJudgeRequiredVotes();
                    for (int i = 0; i < votes; i++) {
                        approvedCitizen.vote(UUID.randomUUID(),true);
                    }
                    courts.getJudgeManager().promoteJudge(approvedCitizen);
                    if (courts.getElectionManager().getCurrentElection() != null) {
                        Election election = courts.getElectionManager().getCurrentElection();
                        Candidate toRemove = null;
                        for (Candidate candidate : election.getCandidateSet()) {
                            if (candidate.getUuid().equals(approvedCitizen.getUuid())) {
                                toRemove = candidate;
                                break;
                            }
                        }
                        if (toRemove != null) {
                            election.removeCandiate(toRemove);
                        }
                    }
                    commandSender.sendMessage(ChatColor.GREEN + name + " has been made a judge.");
                    return true;
                }
                if (args.length == 4 && args[2].equalsIgnoreCase("secretary")) {
                    String judgeName = args[3];
                    Judge judge = courts.getJudgeManager().judgeByName(judgeName);
                    if (judge == null) {
                        commandSender.sendMessage(ChatColor.RED + judgeName + " is not a judge.");
                        return true;
                    }
                    judge.addSecretary(Courts.getCourts().getSqlSaveManager().createSecretary(judge,Courts.getCourts().getCitizenManager().toCitizen(name,uuid)));
                    commandSender.sendMessage(ChatColor.GREEN + name + " has been made a secretary for judge " + judge.getName());
                    return true;
                }
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("demote")) {
                String name = args[1];
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

        sendHelp(commandSender);
        return true;
    }
    private void sendHelp(CommandSender commandSender) {
        List<String> helpCommands = new ArrayList<>();

        if (commandSender.hasPermission("courts.mastercases")) {
            helpCommands.add(formHelpLine("/court master cases", "Brings up a list of every case"));
        }
        if (commandSender.hasPermission("courts.admin")) {
            helpCommands.add(formHelpLine("/court silence", "Silences default court room"));
            helpCommands.add(formHelpLine("/court endsessions", "Ends all active court sessions"));
            helpCommands.add(formHelpLine("/court reload", "Reloads the config options in config.yml."));
            helpCommands.add(formHelpLine("/court promote <name> judge","Promotes named player to judge status."));
            helpCommands.add(formHelpLine("/court promote <name> secretary <judge-name>", "Promotes named player to secretary under named judge."));
            helpCommands.add(formHelpLine("/court demote <name>","Demotes player from any judge/secretary roles they may hold."));
            helpCommands.add(formHelpLine("/court remove", "Removes the stall you are looking at from courts."));
            helpCommands.add(formHelpLine("/court citizenstall", "Transforms the chest you are looking at into a citizen stall."));
            helpCommands.add(formHelpLine("/court secretarystall", "Transforms the chest you are looking at into a secretary stall."));
            helpCommands.add(formHelpLine("/court judgestall", "Transforms the chest you are looking at into a judge stall."));
            helpCommands.add(formHelpLine("/court masterliststall", "Transforms the chest you are looking at into a judge stall."));
        }
        for (String help : helpCommands) {
            commandSender.sendMessage(help);
        }
    }
    private String formHelpLine(String command, String description) {
        return ChatColor.YELLOW + command + ChatColor.BLUE + " - " + description;
    }
}
