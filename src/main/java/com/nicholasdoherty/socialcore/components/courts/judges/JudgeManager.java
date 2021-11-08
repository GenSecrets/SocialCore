package com.nicholasdoherty.socialcore.components.courts.judges;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.JudgeListener;
import com.nicholasdoherty.socialcore.components.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.components.courts.notifications.BasicQueuedNotification;
import com.nicholasdoherty.socialcore.components.courts.notifications.NotificationManager;
import com.nicholasdoherty.socialcore.components.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.components.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.utils.VaultUtil;
import com.nicholasdoherty.socialcore.utils.time.VoxTimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.Days;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by john on 1/6/15.
 */
public class JudgeManager {
    private Set<Judge> judges;
    private final Map<UUID, Judge> judgesByUUID = new HashMap<>();
    private final Courts courts;
    
    public JudgeManager(final Courts courts) {
        judges = courts.getSqlSaveManager().getJudges();
        this.courts = courts;
        setupUUID();
        new JudgeListener(this);
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanup();
            }
        }.runTaskLater(courts.getPlugin(), 1);
    }
    
    public void cleanup() {
        for(final Judge judge : judges) {
            judge.getSecretaries().stream().filter(sec -> sec.getUuid().equals(judge.getUuid()))
                    .collect(Collectors.toSet()).forEach(judge::removeSecretary);
            if(judge.getSecretaries().size() > courts.getCourtsConfig().getSecretariesPerJudge()) {
                final int toRemove = judge.getSecretaries().size() - courts.getCourtsConfig().getSecretariesPerJudge();
                for(int i = 0; i < toRemove; i++) {
                    judge.getSecretaries().stream().findAny().ifPresent(judge::removeSecretary);
                }
            }
        }
    }

    public void refreshJudges() {
        judges = courts.getSqlSaveManager().getJudges();
    }
    
    public void setupUUID() {
        for(final Judge judge : judges) {
            judgesByUUID.put(judge.getUuid(), judge);
        }
    }
    
    public Judge getJudge(final int id) {
        for(final Judge judge : judges) {
            if(judge.getJudgeId() == id) {
                return judge;
            }
        }
        return null;
    }
    
    public boolean isAtMax() {
        final int maxJudges = Courts.getCourts().getCourtsConfig().getMaxJudges();
        final int amtJudges = judges.size();
        return amtJudges >= maxJudges;
    }
    
    public Judge promoteJudge(final ApprovedCitizen approvedCitizen) {
        refreshJudges();
        Judge judge = null;
        for(final Judge judge1 : getJudges()) {
            if(judge1!= null && judge1.getId() == approvedCitizen.getId()) {
                judge = judge1;
                break;
            }
        }
        if(judge == null) {
            judge = courts.getSqlSaveManager().createJudge(approvedCitizen);
        }
        judges.add(judge);
        judgesByUUID.put(judge.getUuid(), judge);
        setPerms(judge.getUuid());
        return judge;
    }
    
    public void demoteJudge(final Judge judge) {
        courts.getSqlSaveManager().removeJudge(judge);
        judges.remove(judge);
        judgesByUUID.remove(judge.getUuid());
        Courts.getCourts().getCaseManager().onJudgeDemoted(judge);
        setPerms(judge.getUuid());
        Courts.getCourts().getElectionManager().checkShouldScheduleFile();
    }

    public void updateJudgeOnlineTime(final int id) {
        courts.getSqlSaveManager().updateJudgeOnlineTime(id);
    }

    public void updateSecretaryOnlineTime(final int id) {
        courts.getSqlSaveManager().updateSecretaryOnlineTime(id);
    }

    
    public Judge getJudge(final UUID uuid) {
        return judgesByUUID.get(uuid);
    }
    
    public boolean isJudge(final UUID uuid) {
        return getJudge(uuid) != null;
    }
    
    public boolean isSecretary(final UUID uuid) {
        for(final Judge judge : judges) {
            if(judge.isSecretary(uuid)) {
                return true;
            }
        }
        return false;
    }
    
    public Secretary getSecretary(final UUID uuid) {
        for(final Judge judge : judges) {
            final Secretary secretary = judge.getSecretary(uuid);
            if(secretary != null) {
                return secretary;
            }
        }
        return null;
    }
    
    public Judge judgeByName(final String name) {
        for(final Judge judge : judges) {
            if(judge.getName().equalsIgnoreCase(name)) {
                return judge;
            }
        }
        return null;
    }
    
    public void setPerms(final UUID uuid) {
        final Player p = Bukkit.getPlayer(uuid);
        if(p != null && p.isOnline()) {
            setPerms(p);
        }
    }
    
    public void setPerms(@SuppressWarnings("TypeMayBeWeakened") final Player p) {
        final Courts courts = Courts.getCourts();
        final UUID uuid = p.getUniqueId();
        PermissionAttachment permissionAttachment = null;
        final Collection<String> newPerms = new HashSet<>();
        if(isJudge(uuid)) {
            for(final String perm : courts.getCourtsConfig().getJudgePermissions()) {
                if(!newPerms.contains(perm)) {
                    newPerms.add(perm);
                }
            }
        }
        if(isSecretary(uuid)) {
            for(final String perm : courts.getCourtsConfig().getSecretaryPermissions()) {
                if(!newPerms.contains(perm)) {
                    newPerms.add(perm);
                }
            }
        }
        if(p.hasMetadata("cpa")) {
            permissionAttachment = (PermissionAttachment) p.getMetadata("cpa").get(0).value();
        }
        if(newPerms.isEmpty() && permissionAttachment != null) {
            try {
                p.removeAttachment(permissionAttachment);
            } catch(final Exception ignored) {
            }
            return;
        }
        if(permissionAttachment == null && !newPerms.isEmpty()) {
            permissionAttachment = p.addAttachment(Courts.getCourts().getPlugin());
            p.setMetadata("cpa", new FixedMetadataValue(Courts.getCourts().getPlugin(), permissionAttachment));
            for(final String perm : newPerms) {
                VaultUtil.addPermission(permissionAttachment, perm);
            }
            return;
        }
        if(permissionAttachment != null && !newPerms.isEmpty()) {
            try {
                p.removeAttachment(permissionAttachment);
            } catch(final Exception ignored) {
            }
            p.removeMetadata("cpa", courts.getPlugin());
            
            permissionAttachment = p.addAttachment(Courts.getCourts().getPlugin());
            p.setMetadata("cpa", new FixedMetadataValue(Courts.getCourts().getPlugin(), permissionAttachment));
            
            for(final String perm : newPerms) {
                VaultUtil.addPermission(permissionAttachment, perm);
            }
        }
    }
    
    public void update(final Judge judge) {
        if((new Date().getTime() - judge.getJudgeLastOnlineDate()) > Days.days(Courts.getCourts().getCourtsConfig().getJudgeInactiveDaysAllowed()).toStandardDuration().getMillis()) {
            demoteJudge(judge);
            final Courts courts = Courts.getCourts();
            final NotificationManager notificationManager = courts.getNotificationManager();
            
            notificationManager.notification(NotificationType.JUDGE_INACTIVE_ALL, new Object[] {judge});
            
            final Player judgeP = Bukkit.getPlayer(judge.getUuid());
            final String judgeMessage = Courts.getCourts().getNotificationManager().getNotificationString(NotificationType.JUDGE_INACTIVE_JUDGE, null, new Object[] {judge}, judgeP);
            final long timeoutTicks = Courts.getCourts().getNotificationManager().notificationTimeout(NotificationType.JUDGE_INACTIVE_JUDGE);
            final BasicQueuedNotification queuedNotification = new BasicQueuedNotification(judge, judgeMessage, new Date().getTime() + VoxTimeUnit.TICK.toMillis(timeoutTicks), NotificationType.JUDGE_INACTIVE_JUDGE);
            Courts.getCourts().getNotificationManager().addQueuedNotification(queuedNotification);
            return;
        }
        if((new Date().getTime() - judge.getJoinDateTime()) > Days.days(Courts.getCourts().getCourtsConfig().getJudgeTermLimitDays()).toStandardDuration().getMillis()) {
            /*Bukkit.getPlayer("GenSecrets").sendMessage("Term limit days: " + Days.days(Courts.getCourts().getCourtsConfig().getJudgeTermLimitDays()));
            Bukkit.getPlayer("GenSecrets").sendMessage("Term limit days: " + Courts.getCourts().getCourtsConfig().getJudgeTermLimitDays());
            Bukkit.getPlayer("GenSecrets").sendMessage("Term limit millis: " + Days.days(Courts.getCourts().getCourtsConfig().getJudgeTermLimitDays()).toStandardDuration().getMillis());
            Bukkit.getPlayer("GenSecrets").sendMessage("Current time: " + (new Date().getTime()));
            Bukkit.getPlayer("GenSecrets").sendMessage("Judge join date: " + (judge.getJoinDateTime()));
            Bukkit.getPlayer("GenSecrets").sendMessage("Current time minus join date: " + (new Date().getTime() - judge.getJoinDateTime()));*/
            demoteJudge(judge);
            final Courts courts = Courts.getCourts();
            final NotificationManager notificationManager = courts.getNotificationManager();

            notificationManager.notification(NotificationType.JUDGE_TERM_REACHED_ALL, new Object[] {judge});

            final Player judgeP = Bukkit.getPlayer(judge.getUuid());
            final String judgeMessage = Courts.getCourts().getNotificationManager().getNotificationString(NotificationType.JUDGE_TERM_REACHED, null, new Object[] {judge}, judgeP);
            final long timeoutTicks = Courts.getCourts().getNotificationManager().notificationTimeout(NotificationType.JUDGE_TERM_REACHED);
            final BasicQueuedNotification queuedNotification = new BasicQueuedNotification(judge, judgeMessage, new Date().getTime() + VoxTimeUnit.TICK.toMillis(timeoutTicks), NotificationType.JUDGE_TERM_REACHED);
            Courts.getCourts().getNotificationManager().addQueuedNotification(queuedNotification);
            return;
        }
        if(judge.approvalPercentage() < Courts.getCourts().getCourtsConfig().getJudgeApprovalRateDemoted()) {
            demoteJudge(judge);
            Courts.getCourts().getNotificationManager().notification(NotificationType.JUDGE_REMOVED_RATING_ALL, new Object[] {judge});
            
            final Player judgeP = Bukkit.getPlayer(judge.getUuid());
            final String judgeMessage = Courts.getCourts().getNotificationManager().getNotificationString(NotificationType.JUDGE_REMOVED_RATING_JUDGE, null, new Object[] {judge}, judgeP);
            final long timeoutTicks = Courts.getCourts().getNotificationManager().notificationTimeout(NotificationType.JUDGE_REMOVED_RATING_JUDGE);
            final BasicQueuedNotification queuedNotification = new BasicQueuedNotification(judge, judgeMessage, new Date().getTime() + VoxTimeUnit.TICK.toMillis(timeoutTicks), NotificationType.JUDGE_REMOVED_RATING_JUDGE);
            Courts.getCourts().getNotificationManager().addQueuedNotification(queuedNotification);
        }
    }
    
    public Set<Judge> getJudges() {
        //refreshJudges();
        return judges;
    }
}
