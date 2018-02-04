package com.nicholasdoherty.socialcore.courts.commands;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.SecretaryAddRequest;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.courts.notifications.BasicQueuedNotification;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by john on 2/18/15.
 */
public class SecretaryCommand implements CommandExecutor{
    private Courts courts;
    private JudgeManager judgeManager;
    private Map<UUID, List<SecretaryAddRequest>> secretaryAddRequestMap = new HashMap<>();
    public SecretaryCommand(Courts courts, JudgeManager judgeManager) {
        this.courts = courts;
        this.judgeManager = judgeManager;
        courts.getPlugin().getCommand("secretary").setExecutor(this);
    }
    public void removeRequest(SecretaryAddRequest secretaryAddRequest) {
        if (secretaryAddRequestMap.containsKey(secretaryAddRequest.getSecretary().getUuid())) {
            List<SecretaryAddRequest> requests = secretaryAddRequestMap.get(secretaryAddRequest.getSecretary().getUuid());
            requests.remove(secretaryAddRequest);
            if (requests.isEmpty()) {
                secretaryAddRequestMap.remove(secretaryAddRequest.getSecretary().getUuid());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player.");
            return true;
        }
        Player p = (Player) commandSender;

        if ( secretaryAddRequestMap != null && secretaryAddRequestMap.containsKey(p.getUniqueId()) && !secretaryAddRequestMap.get(p.getUniqueId()).isEmpty()) {
            SecretaryAddRequest secretaryAddRequest = secretaryAddRequestMap.get(p.getUniqueId()).get(0);
            if (!judgeManager.isJudge(secretaryAddRequest.getJudge().getUuid())) {
                p.sendMessage(ChatColor.RED + secretaryAddRequest.getJudge().getName() + " is no longer a judge.");
                removeRequest(secretaryAddRequest);
                return true;
            }
            int maxSec = courts.getCourtsConfig().getSecretariesPerJudge();
            if (secretaryAddRequest.getJudge().getSecretaries().size() > maxSec) {
                p.sendMessage(ChatColor.RED + "Judge " + secretaryAddRequest.getJudge().getName() + " has reached the maximum amount of secretaries");
                removeRequest(secretaryAddRequest);
                return true;
            }

            if (args.length > 0 && (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny"))) {
                if (args[0].equalsIgnoreCase("accept")) {
                    if (secretaryAddRequest.getJudge() != null && secretaryAddRequest.getJudge().getSecretaries() != null && secretaryAddRequest.getJudge().getSecretaries().stream().anyMatch(sec -> sec != null && sec.getUuid() != null && sec.getUuid().equals(secretaryAddRequest.getSecretary().getUuid()))) {
                        p.sendMessage(ChatColor.RED + "This player is already a secretary.");
                        return true;
                    }
                    Secretary secretary = Courts.getCourts().getSqlSaveManager().createSecretary(secretaryAddRequest.getJudge(),secretaryAddRequest.getSecretary());
                    if (courts.getElectionManager().getCurrentElection() != null && courts.getElectionManager().getCurrentElection().isInElection(secretary.getUuid())) {
                        p.sendMessage(ChatColor.RED + "You may not accept because you are running for judge.");
                        removeRequest(secretaryAddRequest);
                        return true;
                    }
                    judgeManager.getJudge(secretaryAddRequest.getJudge().getUuid()).addSecretary(secretary);

                    String acceptMessage = courts.getCourtsLangManager().getSecretaryRequestedAcceptMessage();
                    if (acceptMessage != null) {
                        acceptMessage = acceptMessage.replace("{judge-name}", secretaryAddRequest.getJudge().getName());
                        p.sendMessage(acceptMessage);
                    }

                    String requesterAcceptMessage = courts.getCourtsLangManager().getSecretaryRequesterAcceptedMessage();
                    if (requesterAcceptMessage != null) {
                        requesterAcceptMessage = requesterAcceptMessage.replace("{requested-name}", secretaryAddRequest.getSecretary().getName());
                        Player judgeP = secretaryAddRequest.getJudge().getPlayer();
                        if (judgeP != null) {
                            judgeP.sendMessage(requesterAcceptMessage);
                        }
                    }
                } else {
                    String denyMessage = courts.getCourtsLangManager().getSecretaryRequestedDenyMessage();
                    denyMessage = denyMessage.replace("{judge-name}", secretaryAddRequest.getJudge().getName());
                    p.sendMessage(denyMessage);

                    String requesterDeniedMessage = courts.getCourtsLangManager().getSecretaryRequesterDeniedMessage();
                    requesterDeniedMessage = requesterDeniedMessage.replace("{requested-name}", secretaryAddRequest.getSecretary().getName());
                    Player judgeP = secretaryAddRequest.getJudge().getPlayer();
                    if (judgeP != null) {
                        judgeP.sendMessage(requesterDeniedMessage);
                    }
                }
                removeRequest(secretaryAddRequest);
                return true;
            } else {
                p.sendMessage(ChatColor.RED + "Please type /secretary accept or /secretary deny to respond to Judge " + secretaryAddRequest.getJudge().getName() + "'s request");
                return true;
            }
        }

        Judge judge = judgeManager.getJudge(p.getUniqueId());

        if (judge == null) {
            p.sendMessage(ChatColor.RED + "You are not a judge.");
            return true;
        }
        if (args.length == 0) {
            sendHelp(p,judge);
            return true;
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 2) {
                sendHelp(p,judge);
                return true;
            }
            int maxSec = courts.getCourtsConfig().getSecretariesPerJudge();
            if (judge.getSecretaries().size() > maxSec) {
                p.sendMessage(ChatColor.RED + "You have reached the maximum number of secretaries: " + maxSec);
                p.sendMessage(ChatColor.RED + "To make room for another, use /secretary remove <name>");
                return true;
            }
            String name = args[1];
            Player requestedPlayer = Bukkit.getPlayer(name);
            if (requestedPlayer == null || !requestedPlayer.isOnline()) {
                p.sendMessage(ChatColor.RED + "Your prospective secretary must be online to be added.");
                return true;
            }
            Citizen secretaryCitizen = Courts.getCourts().getCitizenManager().toCitizen(requestedPlayer);
            if (secretaryAddRequestMap.containsKey(secretaryCitizen.getUuid())) {
                for (SecretaryAddRequest secretaryAddRequest : secretaryAddRequestMap.get(secretaryCitizen.getUuid())) {
                    if (secretaryAddRequest.getJudge().equals(judge)) {
                        p.sendMessage(ChatColor.RED + "You have already requested this player become your secretary.");
                        return true;
                    }
                }
            }
            if (courts.getElectionManager().getCurrentElection() != null && courts.getElectionManager().getCurrentElection().isInElection(secretaryCitizen.getUuid())) {
                p.sendMessage(ChatColor.RED + "You cannot add somebody who is running for judge as a secretary.");
                return true;
            }
            SecretaryAddRequest secretaryAddRequest = new SecretaryAddRequest(judge,secretaryCitizen);
            if (!secretaryAddRequestMap.containsKey(secretaryAddRequest.getSecretary().getUuid())) {
                secretaryAddRequestMap.put(secretaryAddRequest.getSecretary().getUuid(),new ArrayList<SecretaryAddRequest>());
            }
            secretaryAddRequestMap.get(secretaryCitizen.getUuid()).add(secretaryAddRequest);
            String requestingMessage = Courts.getCourts().getCourtsLangManager().getSecretaryRequestingMessage();
            requestingMessage = requestingMessage.replace("{requested-name}",secretaryCitizen.getName()).replace("{judge-name}",judge.getName());
            p.sendMessage(requestingMessage);

            String requstedConfirmMessage = courts.getCourtsLangManager().getSecretaryRequestedConfirmMessage();
            requstedConfirmMessage = requstedConfirmMessage.replace("{judge-name}", judge.getName());
            requestedPlayer.sendMessage(requstedConfirmMessage);
            return true;
        }
        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                sendHelp(p,judge);
                return true;
            }
            String name = args[1];
            Secretary secretary = null;
            for (Secretary secretaryI : judge.getSecretaries()) {
                if (secretaryI.getName().equalsIgnoreCase(name)) {
                    secretary = secretaryI;
                }
            }
            if (secretary == null) {
                p.sendMessage(courts.getCourtsLangManager().getNotSecretaryMessage().replace("{player-name}",name));
                return true;
            }
            judge.removeSecretary(secretary);
            p.sendMessage(courts.getCourtsLangManager().getSecretaryRemovedMessage().replace("{player-name}", secretary.getName()));

            String notificationMessage = courts.getNotificationManager().getNotificationString(NotificationType.SECRETARY_REMOVED,new HashMap<String, String>(),new Object[]{judge},null);
            BasicQueuedNotification queuedNotification = new BasicQueuedNotification(secretary,notificationMessage,courts.getNotificationManager().notificationTimeout(NotificationType.SECRETARY_REMOVED),NotificationType.SECRETARY_REMOVED);
            courts.getNotificationManager().addQueuedNotification(queuedNotification);
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            if (judge.getSecretaries().size() == 0) {
                p.sendMessage(ChatColor.GREEN + "You have no secretaries.");
            }else {
                p.sendMessage(ChatColor.GREEN + "Your secretaries are: " + TextUtil.fancyList(TextUtil.citizenNames(judge.getSecretaries())));
            }
            return true;
        }
        sendHelp(p,judge);
        return true;
    }
    public Location locFromString(String in, World defaultWorld) {
        String[] split = in.split(",");
        if (split.length < 3 || split.length > 4) {
            return null;
        }
        World world;
        if (split.length == 4) {
            world = Bukkit.getWorld(split[0]);
        }else {
            world = defaultWorld;
        }
        if (world == null) {
            return null;
        }
        double x,y,z;
        try {
            int offset = 0;
            if (split.length > 3) {
                offset = 1;
            }
            x = Double.parseDouble(split[offset]);
            y = Double.parseDouble(split[offset+1]);
            z = Double.parseDouble(split[offset+2]);
        }catch (Exception e) {
            return null;
        }
        Location loc = new Location(world,x,y,z);
        return loc;
    }
    private String formHelpLine(String command, String description) {
        return ChatColor.YELLOW + command + ChatColor.BLUE + " - " + description;
    }
    private void sendHelp(Player p, Judge judge) {
        p.sendMessage(formHelpLine("/secretary add <name>","Sends a request to a player to become your secretary"));
        p.sendMessage(formHelpLine("/secretary remove <name>","Removes a player as your secretary"));
        p.sendMessage(formHelpLine("/secretary list","Lists out your secretaries"));
    }
}