package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.secretary;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.commands.CourtCommandHandler;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.SecretaryAddRequest;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SecretaryAddCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final JudgeManager judgeManager;
    private final String[] args;

    public SecretaryAddCmd(Courts courts, CommandSender commandSender, JudgeManager judgeManager, String[] args) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.judgeManager = judgeManager;
        this.args = args;
    }

    public boolean runCommand() {
        Player p = (Player) commandSender;
        final Judge judge = judgeManager.getJudge(p.getUniqueId());

        if(judge == null) {
            p.sendMessage(ChatColor.RED + "You are not a judge.");
            return true;
        }
        if(args.length < 1) {
            CourtCommandHandler.sendCourtHelp(p, false, true);
            return true;
        }

        final int maxSec = courts.getCourtsConfig().getSecretariesPerJudge();
        if(judge.getSecretaries().size() > maxSec) {
            p.sendMessage(ChatColor.RED + "You have reached the maximum number of secretaries: " + maxSec);
            p.sendMessage(ChatColor.RED + "To make room for another, use /secretary remove <name>");
            return true;
        }
        final String name = args[0];
        final Player requestedPlayer = Bukkit.getPlayer(name);
        if(requestedPlayer == null || !requestedPlayer.isOnline()) {
            p.sendMessage(ChatColor.RED + "Your prospective secretary must be online to be added.");
            return true;
        }
        final Citizen secretaryCitizen = Courts.getCourts().getCitizenManager().toCitizen(requestedPlayer);
        if(courts.secretaryAddRequestMap.containsKey(secretaryCitizen.getUuid())) {
            for(final SecretaryAddRequest secretaryAddRequest : courts.secretaryAddRequestMap.get(secretaryCitizen.getUuid())) {
                if(secretaryAddRequest.getJudge().equals(judge)) {
                    p.sendMessage(ChatColor.RED + "You have already requested this player become your secretary.");
                    return true;
                }
            }
        }
        if(courts.getElectionManager().getCurrentElection() != null && courts.getElectionManager().getCurrentElection().isInElection(secretaryCitizen.getUuid())) {
            p.sendMessage(ChatColor.RED + "You cannot add somebody who is running for judge as a secretary.");
            return true;
        }
        final SecretaryAddRequest secretaryAddRequest = new SecretaryAddRequest(judge, secretaryCitizen);
        if(!courts.secretaryAddRequestMap.containsKey(secretaryAddRequest.getSecretary().getUuid())) {
            courts.secretaryAddRequestMap.put(secretaryAddRequest.getSecretary().getUuid(), new ArrayList<>());
        }
        courts.secretaryAddRequestMap.get(secretaryCitizen.getUuid()).add(secretaryAddRequest);
        String requestingMessage = Courts.getCourts().getCourtsLangManager().getSecretaryRequestingMessage();
        requestingMessage = requestingMessage.replace("{requested-name}", secretaryCitizen.getName()).replace("{judge-name}", judge.getName());
        p.sendMessage(requestingMessage);

        String requstedConfirmMessage = courts.getCourtsLangManager().getSecretaryRequestedConfirmMessage();
        requstedConfirmMessage = requstedConfirmMessage.replace("{judge-name}", judge.getName());
        requestedPlayer.sendMessage(requstedConfirmMessage);
        return true;
    }
}
