package com.nicholasdoherty.socialcore.courts.judges;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.JudgeListener;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.courts.notifications.BasicQueuedNotification;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationManager;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import com.voxmc.voxlib.util.PlayerUtil;
import com.voxmc.voxlib.util.VaultUtil;
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
    private final Set<Judge> judges;
    private final Map<UUID, Judge> judgesByUUID = new HashMap<>();
    private final Courts courts;
    private final Map<UUID, String> setPrefixes = new HashMap<>();
    
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
        Judge judge = null;
        for(final Judge judge1 : getJudges()) {
            if(judge1.getId() == approvedCitizen.getId()) {
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
        setPrefix(judge.getUuid());
        return judge;
    }
    
    public void demoteJudge(final Judge judge) {
        courts.getSqlSaveManager().removeJudge(judge);
        judges.remove(judge);
        judgesByUUID.remove(judge.getUuid());
        Courts.getCourts().getCaseManager().onJudgeDemoted(judge);
        setPerms(judge.getUuid());
        setPrefix(judge.getUuid());
        Courts.getCourts().getElectionManager().checkShouldScheduleFile();
    }
    
    public void removeSetPrefix(final UUID uuid) {
        setPrefixes.remove(uuid);
    }
    
    public void setPrefix(final UUID uuid) {
        final Player p = Bukkit.getPlayer(uuid);
        if(p != null && p.isOnline()) {
            setPrefix(p);
        }
    }
    
    public void revertPrefix(final Player p) {
        if(setPrefixes.containsKey(p.getUniqueId())) {
            VaultUtil.setPrefix(p, setPrefixes.get(p.getUniqueId()));
            setPrefixes.remove(p.getUniqueId());
        }
    }
    
    public void setPrefix(final Player p) {
        final UUID uuid = p.getUniqueId();
        final boolean isJudge = isJudge(uuid);
        final String judgePrefix = Courts.getCourts().getCourtsLangManager().getJudgePrefix();
        if(!isJudge && !setPrefixes.containsKey(uuid)) {
            final String oldPrefix = VaultUtil.getPrefix(p);
            if(oldPrefix != null && oldPrefix.equals(judgePrefix)) {
                VaultUtil.setPrefix(p, "");
            }
            return;
        }
        if(!isJudge && setPrefixes.containsKey(uuid)) {
            VaultUtil.setPrefix(p, setPrefixes.get(uuid));
            removeSetPrefix(uuid);
            return;
        }
        if(isJudge) {
            String oldPrefix = VaultUtil.getPrefix(p);
            if(oldPrefix == null) {
                oldPrefix = "";
            }
            if(judgePrefix.equals(oldPrefix)) {
                return;
            }
            VaultUtil.setPrefix(p, judgePrefix);
            setPrefixes.put(uuid, oldPrefix);
        }
    }
    
    public Judge getJudge(final UUID uuid) {
        //Player player = Bukkit.getPlayer(uuid);
        //if (player != null && !judgesByUUID.containsKey(uuid) && player.hasPermission("courts.admin")) {
        //    return Judge.adminJudge(player);
        //}
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
        //Player player = Bukkit.getPlayer(uuid);
        //if (player != null && player.hasPermission("courts.admin")) {
        //    Judge judge = getJudge(uuid);
        //    Secretary secretary = new Secretary(player.getName(),player.getUniqueId(),judge);
        //    judge.addSecretary(secretary);
        //    return secretary;
        //}
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
    
    private void addPermission(final PermissionAttachment permissionAttachment, String perm) {
        boolean value = true;
        if(perm.contains("-")) {
            value = false;
            perm = perm.replace("-", "");
        }
        permissionAttachment.setPermission(perm, value);
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
                addPermission(permissionAttachment, perm);
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
                addPermission(permissionAttachment, perm);
            }
        }
    }
    
    public void update(final Judge judge) {
        if(PlayerUtil.timeSinceLastOnline(judge.getUuid()) > Days.days(Courts.getCourts().getCourtsConfig().getJudgeInactiveDaysAllowed()).toStandardDuration().getMillis()) {
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
        return judges;
    }
}
