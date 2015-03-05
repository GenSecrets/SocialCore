package com.nicholasdoherty.socialcore.courts.judges;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.JudgeListener;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.courts.notifications.BasicQueuedNotification;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationManager;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import com.nicholasdoherty.socialcore.utils.PlayerUtil;
import com.nicholasdoherty.socialcore.utils.VaultUtil;
import com.voxmc.socialcore.libs.org.joda.time.Days;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

/**
 * Created by john on 1/6/15.
 */
public class JudgeManager implements ConfigurationSerializable{
    private Set<Judge> judges;
    private Map<UUID, Judge> judgesByUUID = new HashMap<>();

    public JudgeManager(Set<Judge> judges) {
        this.judges = judges;
        setupUUID();
        new JudgeListener(this);
    }
    public void setupUUID() {
        for (Judge judge : judges) {
            judgesByUUID.put(judge.getUuid(),judge);
        }
    }

    public Judge promoteJudge(ApprovedCitizen approvedCitizen) {
        Judge judge = new Judge(approvedCitizen);
        judges.add(judge);
        judgesByUUID.put(judge.getUuid(),judge);
        setPerms(judge.getUuid());
        setPrefix(judge.getUuid());
        return judge;
    }
    public void demoteJudge(Judge judge) {
        judges.remove(judge);
        judgesByUUID.remove(judge.getUuid());
        Courts.getCourts().getCaseManager().onJudgeDemoted(judge);
        setPerms(judge.getUuid());
        setPrefix(judge.getUuid());
    }
    private Map<UUID, String> setPrefixes = new HashMap<>();
    public void removeSetPrefix(UUID uuid) {
        if (setPrefixes.containsKey(uuid)) {
            setPrefixes.remove(uuid);
        }
    }
    public void setPrefix(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()) {
            setPrefix(p);
        }
    }
    public void revertPrefix(Player p) {
        if (setPrefixes.containsKey(p.getUniqueId())) {
            VaultUtil.setPrefix(p,setPrefixes.get(p.getUniqueId()));
            setPrefixes.remove(p.getUniqueId());
        }
    }
    public void setPrefix(Player p) {
        UUID uuid = p.getUniqueId();
        boolean isJudge = isJudge(uuid);
        String judgePrefix = Courts.getCourts().getCourtsLangManager().getJudgePrefix();
        if (!isJudge && !setPrefixes.containsKey(uuid)) {
            String oldPrefix = VaultUtil.getPrefix(p);
            if (oldPrefix != null && oldPrefix.equals(judgePrefix)) {
                VaultUtil.setPrefix(p, "");
            }
            return;
        }
        if (!isJudge && setPrefixes.containsKey(uuid)) {
            VaultUtil.setPrefix(p,setPrefixes.get(uuid));
            removeSetPrefix(uuid);
            return;
        }
        if (isJudge) {
            String oldPrefix = VaultUtil.getPrefix(p);
            if (oldPrefix == null) {
                oldPrefix = "";
            }
            if (judgePrefix.equals(oldPrefix)) {
                return;
            }
            VaultUtil.setPrefix(p,judgePrefix);
            setPrefixes.put(uuid,oldPrefix);
        }
    }
    public Judge getJudge(UUID uuid) {
        //Player player = Bukkit.getPlayer(uuid);
        //if (player != null && !judgesByUUID.containsKey(uuid) && player.hasPermission("courts.admin")) {
        //    return Judge.adminJudge(player);
        //}
        return judgesByUUID.get(uuid);
    }
    public boolean isJudge(UUID uuid) {
        return getJudge(uuid) != null;
    }
    public boolean isSecretary(UUID uuid) {
        for (Judge judge : judges) {
            if (judge.isSecretary(uuid)) {
                return true;
            }
        }
        return false;
    }
    public Secretary getSecretary(UUID uuid) {
        for (Judge judge : judges) {
            Secretary secretary = judge.getSecretary(uuid);
            if (secretary != null) {
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
    public Judge judgeByName(String name) {
        for (Judge judge : judges) {
            if (judge.getName().equalsIgnoreCase(name)) {
                return judge;
            }
        }
        return null;
    }
    private void addPermission(PermissionAttachment permissionAttachment, String perm) {
        boolean value = true;
        if (perm.contains("-")) {
            value = false;
            perm = perm.replace("-","");
        }
        permissionAttachment.setPermission(perm,value);
    }
    public void setPerms(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()) {
            setPerms(p);
        }
    }
    public void setPerms(Player p) {
        Courts courts = Courts.getCourts();
        UUID uuid = p.getUniqueId();
        PermissionAttachment permissionAttachment = null;
        Set<String> newPerms = new HashSet<>();
        if (isJudge(uuid)) {
            for (String perm : courts.getCourtsConfig().getJudgePermissions()) {
                if (!newPerms.contains(perm)) {
                    newPerms.add(perm);
                }
            }
        }
        if (isSecretary(uuid)) {
            for (String perm : courts.getCourtsConfig().getSecretaryPermissions()) {
                if (!newPerms.contains(perm)) {
                    newPerms.add(perm);
                }
            }
        }
        if (p.hasMetadata("cpa")) {
            permissionAttachment = (PermissionAttachment) p.getMetadata("cpa").get(0).value();
        }
        if (newPerms.size() == 0 && permissionAttachment != null) {
            try {
                p.removeAttachment(permissionAttachment);
            }catch (Exception e) {

            }
            return;
        }
        if (permissionAttachment == null && newPerms.size() > 0) {
            permissionAttachment = p.addAttachment(Courts.getCourts().getPlugin());
            p.setMetadata("cpa", new FixedMetadataValue(Courts.getCourts().getPlugin(),permissionAttachment));
            for (String perm : newPerms) {
                addPermission(permissionAttachment,perm);
            }
            return;
        }
        if (permissionAttachment != null && newPerms.size() > 0) {
            try {
                p.removeAttachment(permissionAttachment);
            }catch (Exception e) {

            }
            p.removeMetadata("cpa",courts.getPlugin());

            permissionAttachment = p.addAttachment(Courts.getCourts().getPlugin());
            p.setMetadata("cpa", new FixedMetadataValue(Courts.getCourts().getPlugin(),permissionAttachment));

            for (String perm : newPerms) {
                addPermission(permissionAttachment,perm);
            }
        }
    }
    public void update(Judge judge) {
        if (PlayerUtil.timeSinceLastOnline(judge.getUuid()) > Days.days(Courts.getCourts().getCourtsConfig().getJudgeInactiveDaysAllowed()).toStandardDuration().getMillis()) {
            demoteJudge(judge);
            Courts courts = Courts.getCourts();
            NotificationManager notificationManager = courts.getNotificationManager();

            notificationManager.notification(NotificationType.JUDGE_INACTIVE_ALL, new Object[]{judge});

            Player judgeP = Bukkit.getPlayer(judge.getUuid());
            String judgeMessage = Courts.getCourts().getNotificationManager().getNotificationString(NotificationType.JUDGE_INACTIVE_JUDGE,null,new Object[]{judge},judgeP);
            long timeoutTicks = Courts.getCourts().getNotificationManager().notificationTimeout(NotificationType.JUDGE_INACTIVE_JUDGE);
            BasicQueuedNotification queuedNotification = new BasicQueuedNotification(judge,judgeMessage,new Date().getTime() + VoxTimeUnit.TICK.toMillis(timeoutTicks),NotificationType.JUDGE_INACTIVE_JUDGE);
            Courts.getCourts().getNotificationManager().addQueuedNotification(queuedNotification);
            return;
        }
        if (judge.approvalPercentage() < Courts.getCourts().getCourtsConfig().getJudgeApprovalRateDemoted()) {
            demoteJudge(judge);
            Courts.getCourts().getNotificationManager().notification(NotificationType.JUDGE_REMOVED_RATING_ALL, new Object[]{judge});


            Player judgeP = Bukkit.getPlayer(judge.getUuid());
            String judgeMessage = Courts.getCourts().getNotificationManager().getNotificationString(NotificationType.JUDGE_REMOVED_RATING_JUDGE,null,new Object[]{judge},judgeP);
            long timeoutTicks = Courts.getCourts().getNotificationManager().notificationTimeout(NotificationType.JUDGE_REMOVED_RATING_JUDGE);
            BasicQueuedNotification queuedNotification = new BasicQueuedNotification(judge,judgeMessage,new Date().getTime()+ VoxTimeUnit.TICK.toMillis(timeoutTicks),NotificationType.JUDGE_REMOVED_RATING_JUDGE);
            Courts.getCourts().getNotificationManager().addQueuedNotification(queuedNotification);
        }
    }


    public Set<Judge> getJudges() {
        return judges;
    }
    public JudgeManager(Map<String, Object> map) {
        this.judges = new HashSet<>((Set<Judge>) map.get("judges"));
        setupUUID();
        new JudgeListener(this);
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("judges",judges);
        return map;
    }
}
