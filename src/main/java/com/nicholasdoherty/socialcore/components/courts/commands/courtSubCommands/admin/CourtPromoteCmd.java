package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.components.courts.elections.Election;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import com.voxmc.voxlib.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class CourtPromoteCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final String[] args;

    public CourtPromoteCmd(Courts courts, CommandSender commandSender, String[] args) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.args = args;
    }

    public boolean runCommand(){
        String name = args[0];
        OfflinePlayer op = Bukkit.getOfflinePlayer(UUIDUtil.getUUID(name));
        Player p = op.getPlayer();
        if (!op.hasPlayedBefore() && p == null) {
            commandSender.sendMessage(ChatColor.RED +"Invalid name");
            return true;
        }
        UUID uuid = op.getUniqueId();
        name = UUIDUtil.prettyName(name,uuid);
        if (args.length == 2 && args[1].equalsIgnoreCase("judge")) {
            final String lamName = name;
            Bukkit.getScheduler().runTaskAsynchronously(courts.getPlugin(), () -> {
                Citizen citizen = Courts.getCourts().getCitizenManager().getCitizen(uuid);
                if(citizen != null) {
                    ApprovedCitizen approvedCitizen = Courts.getCourts().getSqlSaveManager().getApprovedCitizen(citizen);
                    if (approvedCitizen == null) {
                        Courts.getCourts().getSqlSaveManager().createInitialVote(citizen, citizen.getUuid(), true);
                        approvedCitizen = Courts.getCourts().getSqlSaveManager().getApprovedCitizen(citizen);
                    }
                    int votes = courts.getCourtsConfig().getJudgeRequiredVotes();
                    for (int i = 0; i < votes / 2; i++) {
                        approvedCitizen.vote(UUID.randomUUID(), true);
                    }
                    if(approvedCitizen != null){
                        Judge j = courts.getJudgeManager().promoteJudge(approvedCitizen);
                        if (j != null) {
                            if (Courts.getCourts().getElectionManager().getCurrentElection() != null) {
                                Election election = Courts.getCourts().getElectionManager().getCurrentElection();
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
                            commandSender.sendMessage(ChatColor.GREEN + j.getName() + " has been made a judge.");
                        } else {
                            commandSender.sendMessage(ChatColor.RED + lamName + " was not able to be made a judge.");
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "A Citizen " + lamName + " was unable to be found.");
                    }
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Citizen " + lamName + " was unable to be found.");
                }
            });
            return true;
        }
        if (args.length == 3 && args[1].equalsIgnoreCase("secretary")) {
            String judgeName = args[2];
            Judge judge = courts.getJudgeManager().judgeByName(judgeName);
            if (judge == null) {
                commandSender.sendMessage(ChatColor.RED + judgeName + " is not a judge.");
                return true;
            }
            judge.addSecretary(Courts.getCourts().getSqlSaveManager().createSecretary(judge,Courts.getCourts().getCitizenManager().toCitizen(name,uuid)));
            commandSender.sendMessage(ChatColor.GREEN + name + " has been made a secretary for judge " + judge.getName());
            return true;
        }
        return true;
    }
}
