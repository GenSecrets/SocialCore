package com.nicholasdoherty.socialcore.courts.notifications;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CourtDate;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import com.voxmc.voxlib.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by john on 1/21/15.
 */
public class Notification {
    private NotificationType notificationType;
    private boolean enabled;
    private String message;
    private long timeoutTicks,upcomingTicks;
    private NotificationMessageType messageType;

    public Notification(ConfigurationSection section) {
        this.notificationType = NotificationType.byName(section.getName());
        this.enabled = section.getBoolean("enabled",true);
        if (section.contains("base-message")) {
            this.message = ChatColor.translateAlternateColorCodes('&', section.getString("base-message"));
        }else {
            this.message = "No base message defined";
        }
         timeoutTicks = -1;
        if (section.contains("timeout")) {
            timeoutTicks = VoxTimeUnit.getTicks(section.getString("timeout"));
        }
         upcomingTicks = -1;
        if (section.contains("upcoming-time")) {
            upcomingTicks = VoxTimeUnit.getTicks(section.getString("upcoming-time"));
        }
        messageType = NotificationMessageType.NORMAL;
        if (section.contains("message-type")) {
            messageType = NotificationMessageType.valueOf(section.getString("message-type").trim().toUpperCase());
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
    public String doReplacements(String cusMessage, Object[] relevantObjects, Map<String, String> map) {
        if (map != null) {
            for (String key : map.keySet()) {
                cusMessage = cusMessage.replace(key,map.get(key));
            }
        }
        if (relevantObjects != null) {
            for (Object o : relevantObjects) {
                if (o instanceof CourtSession) {
                    CourtSession courtSession = (CourtSession) o;
                    if (courtSession.getJudge() != null) {
                        cusMessage = cusMessage.replace("{judge-name}",courtSession.getJudge().getName());
                    }
                }
                if (o instanceof Judge) {
                    Judge judge = (Judge) o;
                    cusMessage = cusMessage.replace("{judge-name}",judge.getName());
                }else if (o instanceof Case) {
                    Case caze = (Case) o;
                    cusMessage = cusMessage.replace("{case-name}",caze.caseName());
                    if (cusMessage.contains("{case-datetime}"))  {
                        String dateTime = "Not scheduled";
                        if (caze.getCourtDate() != null) {
                            dateTime = TextUtil.formatDate(caze.getCourtDate().getTime());
                        }
                        cusMessage = cusMessage.replace("{case-datetime}",dateTime);
                    }
                    if (cusMessage.contains("{case-category}")) {
                        String caseCategory = "none";
                        if (caze.getCaseCategory() != null) {
                            caseCategory = caze.getCaseCategory().getName();
                        }
                        cusMessage = cusMessage.replace("{case-category}",caseCategory);
                    }
                    if (cusMessage.contains("{case-number}")) {
                        cusMessage = cusMessage.replace("{case-number}",caze.getId()+"");
                    }
                    if (cusMessage.contains("{case-plaintiff}")) {
                        String casePlaintiff = "none";
                        if (caze.getPlantiff() != null) {
                            casePlaintiff = caze.getPlantiff().getName();
                        }
                        cusMessage = cusMessage.replace("{case-plaintiff}",casePlaintiff);
                    }
                }else if (o instanceof Player) {
                    Player p = (Player) o;
                    cusMessage = cusMessage.replace("{player-name}",p.getName());
                }
                if (o instanceof ApprovedCitizen) {
                    ApprovedCitizen approvedCitizen = (ApprovedCitizen) o;
                    cusMessage = cusMessage.replace("{approvals}",approvedCitizen.getApprovals().size()+"");
                    cusMessage = cusMessage.replace("{disapprovals}",approvedCitizen.getDisapprovals().size()+"");
                    cusMessage = cusMessage.replace("{new-approvals}",approvedCitizen.getNewApprovals()+"");
                    cusMessage = cusMessage.replace("{new-disapprovals}",approvedCitizen.getNewDisapprovals()+"");
                    cusMessage = cusMessage.replace("{approval-rating}", TextUtil.formatDouble(approvedCitizen.approvalPercentage(), 2));
                }
            }

        }
        return cusMessage;
    }
    public String getBasicMessage(Object[] relevantObjects, Map<String, String> map) {
        String cusMessage = this.message;
        return doReplacements(cusMessage,relevantObjects,map);
    }
    public String getBasicMessage(Object[] relevantObjects) {
        return getBasicMessage(relevantObjects,null);
    }
    public String personalizeRec(Player rec, String message) {
        if (rec == null)
            return message;
        return message.replace("{name}",rec.getName());
    }
    public String personalizeRec(Citizen rec, String message) {
        if (rec == null)
            return message;
        return message.replace("{name}",rec.getName());
    }
    public void sendAll(Object[] rele, Set<UUID> notSend) {
        String basic = getBasicMessage(rele, null);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (notSend != null && !notSend.contains(p.getUniqueId())) {
                sendBasedOnType(Courts.getCourts().getCitizenManager().toCitizen(p),personalizeRec(p, basic));
            }
        }
    }
    public void sendCitizens(Object[] rele, Iterable<Citizen> citizens) {
        String basic = getBasicMessage(rele, null);
        for (Citizen citizen : citizens) {
            sendBasedOnType(citizen,personalizeRec(citizen, basic));
        }
    }
    public boolean sendBasedOnType(Citizen citizen, String message) {
        if (messageType == NotificationMessageType.MAIL || messageType == NotificationMessageType.BOTH) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mail send " + citizen.getName() + " " + message);
            return true;
        }
        Player p = citizen.getPlayer();
        if (p == null || !p.isOnline()) {
            return false;
        }
        p.sendMessage(message);
        return true;
    }
    public boolean sendBasedOnType(Player p, String message) {
        Citizen citizen = Courts.getCourts().getCitizenManager().toCitizen(p);
        return sendBasedOnType(citizen,message);
    }

    public NotificationMessageType getMessageType() {
        return messageType;
    }

    public long getTimeoutTicks() {
        return timeoutTicks;
    }

    public void send(Map<String, String> map, Player p) {
        sendBasedOnType(p, personalizeRec(p, getBasicMessage(null, map)));
    }
    public void send(Object[] rele, Player p) {
        sendBasedOnType(p, personalizeRec(p, getBasicMessage(rele)));
    }
    public void doActionsPlayer(Player p) {
        Judge judge = Courts.getCourts().getJudgeManager().getJudge(p.getUniqueId());
        if (judge != null) {
            if (notificationType == NotificationType.JUDGE_UPCOMING_CASE) {
                List<Case> upcoming = judge.upcomingCases();
                for (Case caze : upcoming) {
                    CourtDate courtDate = caze.getCourtDate();
                    if (upcomingTicks != -1) {
                        if (courtDate.ticksUntil() > upcomingTicks) {
                            continue;
                        }
                    }
                    Object[] relevant = {judge,caze};
                    String basic = getBasicMessage(relevant);
                    String personalize = personalizeRec(p,basic);
                    sendBasedOnType(p,personalize);
                }
            }
        }
        if (judge == null && notificationType == NotificationType.PARTICIPANT_UPCOMING_CASE) {
            List<Case> upcoming = Courts.getCourts().getCaseManager().involvedCases(p.getUniqueId(),false);
            for (Case caze : upcoming) {
                CourtDate courtDate = caze.getCourtDate();
                if (upcomingTicks != -1) {
                    if (courtDate.ticksUntil() > upcomingTicks) {
                        continue;
                    }
                }
                Object[] relevant = {caze.getCourtDate().getJudge(),caze};
                String basic = getBasicMessage(relevant);
                String personalize = personalizeRec(p,basic);
                sendBasedOnType(p, personalize);
            }
        }
        if (notificationType == NotificationType.ONGOING_CASE_ALL || notificationType == NotificationType.ONGOING_CASE_PARTICIPANT) {
            Set<CourtSession> ongoing = Courts.getCourts().getCourtSessionManager().getInSession();
            for (CourtSession courtSession : ongoing) {
                Case caze = courtSession.getCaze();
                Object[] relevant = {caze};
                if (notificationType == NotificationType.ONGOING_CASE_PARTICIPANT && courtSession.isParticipant(Courts.getCourts().getCitizenManager().toCitizen(p))) {
                    sendBasedOnType(p, personalizeRec(p, getBasicMessage(relevant)));
                }else if (notificationType == NotificationType.ONGOING_CASE_ALL) {
                    sendBasedOnType(p, personalizeRec(p, getBasicMessage(relevant)));
                }
            }
        }
    }
    public static Notification fromConfig(ConfigurationSection section) {
        NotificationType notificationType = NotificationType.byName(section.getName());
        if (notificationType == NotificationType.VOTE_NOTIFICATION) {
            return new VoteNotification(section);
        }
        if (notificationType == NotificationType.VOTE_SUMMARY) {
            return new VoteSummary(section);
        }
        return new Notification(section);
    }
}
