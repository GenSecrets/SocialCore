package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.secretary;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.SecretaryAddRequest;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SecretaryDenyCmd {
    private final Courts courts;
    private final JudgeManager judgeManager;
    private final CommandSender commandSender;

    public SecretaryDenyCmd(Courts courts, JudgeManager judgeManager, CommandSender commandSender) {
        this.courts = courts;
        this.judgeManager = judgeManager;
        this.commandSender = commandSender;
    }

    public boolean runCommand(){
        Player p = (Player) commandSender;
        final SecretaryAddRequest secretaryAddRequest = courts.secretaryAddRequestMap.get(p.getUniqueId()).get(0);
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

        String denyMessage = courts.getCourtsLangManager().getSecretaryRequestedDenyMessage();
        denyMessage = denyMessage.replace("{judge-name}", secretaryAddRequest.getJudge().getName());
        p.sendMessage(denyMessage);

        String requesterDeniedMessage = courts.getCourtsLangManager().getSecretaryRequesterDeniedMessage();
        requesterDeniedMessage = requesterDeniedMessage.replace("{requested-name}", secretaryAddRequest.getSecretary().getName());
        final Player judgeP = secretaryAddRequest.getJudge().getPlayer();
        if(judgeP != null) {
            judgeP.sendMessage(requesterDeniedMessage);
        }
        courts.removeSecretaryRequest(secretaryAddRequest);
        return true;
    }
}
