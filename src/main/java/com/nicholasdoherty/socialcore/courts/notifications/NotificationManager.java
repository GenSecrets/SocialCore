package com.nicholasdoherty.socialcore.courts.notifications;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by john on 1/21/15.
 */
public class NotificationManager {
    private Courts courts;
    private Map<NotificationType, Notification> notificationTypeNotificationMap;
    private Map<MessageEventType, Set<NotificationType>> eventNotifs;
    private List<QueuedNotification> queuedNotifications = new ArrayList<>();
    private Map<UUID, List<QueuedNotification>> notificationsByUUID = new HashMap<>();
    public NotificationManager(Courts courts) {
        this.courts = courts;
        initNotifs();
        new NotificationsListener(courts,this);
        new BukkitRunnable(){
            @Override
            public void run() {
                doQueuedNotifications();
            }
        }.runTaskTimer(courts.getPlugin(),40,40);
        for (Player p : Bukkit.getOnlinePlayers()) {
            online.add(p.getUniqueId());
        }
    }
    public void doQueuedNotifications(UUID uuid) {
        if (!notificationsByUUID.containsKey(uuid))
            return;
        for (QueuedNotification queuedNotification : new ArrayList<>(notificationsByUUID.get(uuid))) {
            doQueuedNotification(queuedNotification);
        }
    }
    public void doQueuedNotification(QueuedNotification queuedNotification) {
        if (queuedNotification.hasTimedOut()) {
            removeQueuedNotification(queuedNotification);
            return;
        }
        boolean did = queuedNotification.trySend();
        if (did) {
            removeQueuedNotification(queuedNotification);
        }
    }

    public Map<NotificationType, Notification> getNotificationTypeNotificationMap() {
        return notificationTypeNotificationMap;
    }

    public void removeQueuedNotification(QueuedNotification queuedNotification) {
        if (queuedNotifications.contains(queuedNotification)) {
            UUID uuid = queuedNotification.getRecUUID();
            notificationsByUUID.get(uuid).remove(queuedNotification);
            if (notificationsByUUID.get(uuid).isEmpty()) {
                notificationsByUUID.remove(uuid);
            }
            queuedNotifications.remove(queuedNotification);
        }
    }
    public void doQueuedNotifications() {
        List<QueuedNotification> todo = new ArrayList<>();
        for (QueuedNotification queuedNotification : queuedNotifications) {
            if (online.contains(queuedNotification.getRecUUID())) {
                todo.add(queuedNotification);
            }
        }
        for (QueuedNotification queuedNotification : todo) {
            queuedNotification.trySend();
        }
    }
    public void addQueuedNotification(QueuedNotification queuedNotification) {
        if (queuedNotification.trySend()) {
            return;
        }
        UUID uuid = queuedNotification.getRecUUID();
        queuedNotifications.add(0,queuedNotification);
        if (!notificationsByUUID.containsKey(uuid)) {
            notificationsByUUID.put(uuid,new ArrayList<QueuedNotification>());
        }
        notificationsByUUID.get(uuid).add(queuedNotification);
    }
    public List<QueuedNotification> getQueuedNotifications() {
        return queuedNotifications;
    }

    public void onLogin(Player p) {
        doQueuedNotifications(p.getUniqueId());
       if (!eventNotifs.containsKey(MessageEventType.ON_LOGIN)) {
           return;
       }
        for (NotificationType notificationType : eventNotifs.get(MessageEventType.ON_LOGIN)) {
            if (notificationTypeNotificationMap.containsKey(notificationType)) {
                Notification notification = notificationTypeNotificationMap.get(notificationType);
                if (notification.isEnabled()) {
                    notification.doActionsPlayer(p);
                }
            }
        }
    }
    public void notification(NotificationType notificationTypes, Object[] rele, Player p) {
        if (notificationTypeNotificationMap.containsKey(notificationTypes)) {
            Notification notification = notificationTypeNotificationMap.get(notificationTypes);
            if (notification.isEnabled()) {
                notificationTypeNotificationMap.get(notificationTypes).send(rele,p);
            }
        }
    }
    public void notification(NotificationType notificationTypes, Object[] rele, Set<UUID> doNotSend) {
        if (notificationTypeNotificationMap.containsKey(notificationTypes)) {
            Notification notification = notificationTypeNotificationMap.get(notificationTypes);
            if (notification.isEnabled()) {
                notificationTypeNotificationMap.get(notificationTypes).sendAll(rele,doNotSend);
            }
        }
    }
    public void notification(NotificationType notificationTypes, Object[] rele) {
        notification(notificationTypes,rele,new HashSet<UUID>());
    }
    public String getNotificationString(NotificationType notificationType, Map<String, String> map, Object[] rele, Player p) {
        Notification notification = notificationTypeNotificationMap.get(notificationType);
        if (notification == null) {
            return null;
        }
        return notification.personalizeRec(p,notification.getBasicMessage(rele,map));
    }
    public long notificationTimeout(NotificationType notificationType) {
        return notificationTypeNotificationMap.get(notificationType).getTimeoutTicks();
    }
    public void summary(ApprovedCitizen approvedCitizen) {
        if (notificationsByUUID.containsKey(approvedCitizen.getUuid())) {
            for (QueuedNotification queuedNotification : notificationsByUUID.get(approvedCitizen.getUuid())) {
                if (queuedNotification instanceof VoteSummaryQueued) {
                    return;
                }
            }
        }
        VoteSummaryQueued summaryQueued = new VoteSummaryQueued(approvedCitizen,notificationTimeout(NotificationType.VOTE_SUMMARY));
        addQueuedNotification(summaryQueued);
    }
    private Set<UUID> online = new HashSet<>();
    public void login(UUID uuid) {
        if (!online.contains(uuid)) {
            online.add(uuid);
        }
    }
    public void logout(UUID uuid) {
        if (online.contains(uuid)) {
            online.remove(uuid);
        }
    }
    private void initNotifs() {
        notificationTypeNotificationMap = new HashMap<>();
        eventNotifs = new HashMap<>();
        ConfigurationSection configurationSection = courts.getPlugin().getCourtsConfig();
        configurationSection = configurationSection.getConfigurationSection("notifier");
        ConfigurationSection messagesSection = configurationSection.getConfigurationSection("messages");

        for (QueuedNotification queuedNotification : Courts.getCourts().getCourtsSaveManager().queuedNotificationList()) {
            if (queuedNotification != null) {
                addQueuedNotification(queuedNotification);
            }
        }

        for (String key : messagesSection.getKeys(false)) {
            NotificationType notificationType = NotificationType.byName(key);
            ConfigurationSection nSection = messagesSection.getConfigurationSection(key);
            if (notificationType != null) {
                Notification notification = Notification.fromConfig(nSection);
                notificationTypeNotificationMap.put(notificationType,notification);
            }else {
                courts.getPlugin().getLogger().severe("Invalid notification type: " + key);
            }
        }
        ConfigurationSection eventsSection = configurationSection.getConfigurationSection("events");
        if (eventsSection != null) {
            for (String key : eventsSection.getKeys(false)) {
                MessageEventType messageEventType = MessageEventType.byName(key);
                if (messageEventType != null){
                    Set<NotificationType> notificationTypes = new HashSet<>();
                    for (String nTS : eventsSection.getStringList(key)) {
                        NotificationType notificationType = NotificationType.byName(nTS);
                        if (notificationType != null) {
                            notificationTypes.add(notificationType);
                        }else {
                            courts.getPlugin().getLogger().severe("Invalid notification type: " + nTS);
                        }
                    }
                    eventNotifs.put(messageEventType,notificationTypes);
                }else {
                    courts.getPlugin().getLogger().severe("Invalid event type: " + key);
                }
            }
        }
    }
}
