package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.secretary;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.SecretaryAddRequest;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SecretaryAcceptCmd {
    private final Courts courts;
    private final JudgeManager judgeManager;
    private final CommandSender commandSender;

    public SecretaryAcceptCmd(Courts courts, JudgeManager judgeManager, CommandSender commandSender) {
        this.courts = courts;
        this.judgeManager = judgeManager;
        this.commandSender = commandSender;
    }

    public boolean runCommand() {
        Player p = (Player) commandSender;
        final List<SecretaryAddRequest> secretaryAddRequests = courts.secretaryAddRequestMap.get(p.getUniqueId());
        SecretaryAddRequest secreq = null;
        if(secretaryAddRequests != null) {
             secreq = secretaryAddRequests.get(0);
        }
        if(secreq == null) {
            p.sendMessage(ChatColor.RED + "You have not been invited to be a secretary!");
            return true;
        }
        final SecretaryAddRequest secretaryAddRequest = secreq;

        if(!judgeManager.isJudge(secretaryAddRequest.getJudge().getUuid())) {
            p.sendMessage(ChatColor.RED + secretaryAddRequest.getJudge().getName() + " is no longer a judge.");
            courts.removeSecretaryRequest(secretaryAddRequest);
            return true;
        }
        final int maxSec = courts.getCourtsConfig().getSecretariesPerJudge();
        if(secretaryAddRequest.getJudge().getSecretaries().size() > maxSec) {
            p.sendMessage(ChatColor.RED + "Judge " + secretaryAddRequest.getJudge().getName() + " has reached the maximum amount of secretaries");
            courts.removeSecretaryRequest(secretaryAddRequest);
            return true;
        }

        if(secretaryAddRequest.getJudge() != null && secretaryAddRequest.getJudge().getSecretaries() != null && secretaryAddRequest.getJudge().getSecretaries().stream().anyMatch(sec -> sec != null && sec.getUuid() != null && sec.getUuid().equals(secretaryAddRequest.getSecretary().getUuid()))) {
            p.sendMessage(ChatColor.RED + "This player is already a secretary.");
            return true;
        }
        final Secretary secretary = Courts.getCourts().getSqlSaveManager().createSecretary(secretaryAddRequest.getJudge(), secretaryAddRequest.getSecretary());
        if(courts.getElectionManager().getCurrentElection() != null && courts.getElectionManager().getCurrentElection().isInElection(secretary.getUuid())) {
            p.sendMessage(ChatColor.RED + "You may not accept because you are running for judge.");
            courts.removeSecretaryRequest(secretaryAddRequest);
            return true;
        }
        judgeManager.getJudge(secretaryAddRequest.getJudge().getUuid()).addSecretary(secretary);

        String acceptMessage = courts.getCourtsLangManager().getSecretaryRequestedAcceptMessage();
        if(acceptMessage != null) {
            acceptMessage = acceptMessage.replace("{judge-name}", secretaryAddRequest.getJudge().getName());
            p.sendMessage(acceptMessage);
        }

        String requesterAcceptMessage = courts.getCourtsLangManager().getSecretaryRequesterAcceptedMessage();
        if(requesterAcceptMessage != null) {
            requesterAcceptMessage = requesterAcceptMessage.replace("{requested-name}", secretaryAddRequest.getSecretary().getName());
            final Player judgeP = secretaryAddRequest.getJudge().getPlayer();
            if(judgeP != null) {
                judgeP.sendMessage(requesterAcceptMessage);
            }
        }
        courts.removeSecretaryRequest(secretaryAddRequest);
        return true;
    }
}
