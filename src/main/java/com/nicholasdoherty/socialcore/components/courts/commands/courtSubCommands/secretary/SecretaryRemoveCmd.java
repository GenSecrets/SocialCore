package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.secretary;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.commands.CourtCommandHandler;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.components.courts.notifications.BasicQueuedNotification;
import com.nicholasdoherty.socialcore.components.courts.notifications.NotificationType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class SecretaryRemoveCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final JudgeManager judgeManager;
    private final String[] args;

    public SecretaryRemoveCmd(Courts courts, CommandSender commandSender, JudgeManager judgeManager, String[] args) {
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
            CourtCommandHandler.sendCourtHelp(commandSender, false, true);
            return true;
        }
        final String name = args[0];
        Secretary secretary = null;
        for(final Secretary secretaryI : judge.getSecretaries()) {
            if(secretaryI.getName().equalsIgnoreCase(name)) {
                secretary = secretaryI;
            }
        }
        if(secretary == null) {
            p.sendMessage(courts.getCourtsLangManager().getNotSecretaryMessage().replace("{player-name}", name));
            return true;
        }
        judge.removeSecretary(secretary);
        p.sendMessage(courts.getCourtsLangManager().getSecretaryRemovedMessage().replace("{player-name}", secretary.getName()));

        final String notificationMessage = courts.getNotificationManager().getNotificationString(NotificationType.SECRETARY_REMOVED, new HashMap<>(), new Object[] {judge}, null);
        final BasicQueuedNotification queuedNotification = new BasicQueuedNotification(secretary, notificationMessage, courts.getNotificationManager().notificationTimeout(NotificationType.SECRETARY_REMOVED), NotificationType.SECRETARY_REMOVED);
        courts.getNotificationManager().addQueuedNotification(queuedNotification);
        return true;
    }
}
