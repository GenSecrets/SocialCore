package com.nicholasdoherty.socialcore.courts.courtroom;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.courts.cases.Resolve;
import com.nicholasdoherty.socialcore.courts.courtroom.actions.*;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.courts.courtroom.voting.GulityInnocentVote;
import com.nicholasdoherty.socialcore.courts.courtroom.voting.RegionRestricted;
import com.nicholasdoherty.socialcore.courts.courtroom.voting.Vote;
import com.nicholasdoherty.socialcore.courts.courtroom.voting.YayNayVote;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationManager;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.time.condition.TickCondition;
import com.nicholasdoherty.socialcore.time.condition.TimeCondition;
import com.nicholasdoherty.socialcore.utils.SerializableUUID;
import com.nicholasdoherty.socialcore.utils.VoxEffects;
import com.nicholasdoherty.socialcore.utils.VoxStringUtils;
import jdk.nashorn.internal.runtime.options.Option;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by john on 1/13/15.
 */
public class CourtSession implements ConfigurationSerializable, PostCourtActionHolder{
    private int caseId;
    private Judge judge;
    private List<PostCourtAction> postCourtActions = new ArrayList<>();
    private boolean inRecess;
    private CourtRoom courtRoom;
    private com.nicholasdoherty.socialcore.courts.courtroom.voting.Vote vote;
    private long judgeOfflineTime;
    private TimeCondition judgeOfflineCondition;
    private Set<UUID> muted,unmuted,contempt;
    private boolean isSilenced = false;

    public CourtSession(Case caze, Judge judge, CourtRoom courtRoom) {
        this.caseId = caze.getId();
        this.judge = judge;
        this.courtRoom = courtRoom;
        judgeOfflineTime = 0;
        muted = new HashSet<>();
        unmuted = new HashSet<>();
        contempt = new HashSet<>();
    }
    public long judgeOfflineTimeLeft() {
        if (judgeOfflineCondition == null) {
            return Courts.getCourts().getCourtsConfig().getMaxJudgeOfflineTicks() - judgeOfflineTime;
        }else {
            long elapsed = judgeOfflineCondition.tickLength() - judgeOfflineCondition.ticksLeft();
            return Courts.getCourts().getCourtsConfig().getMaxJudgeOfflineTicks() - judgeOfflineTime - elapsed;
        }
    }
    public void silence() {
        courtRoom.silence();
        isSilenced = true;
        new BukkitRunnable(){
            @Override
            public void run() {
                isSilenced = false;
            }
        }.runTaskLater(Courts.getCourts().getPlugin(),Courts.getCourts().getCourtsConfig().getSilenceMuteLength());
    }

    public boolean isSilenced() {
        return isSilenced;
    }

    public void startSession() {
        Case caze = Courts.getCourts().getCaseManager().getCase(caseId);
        Courts.getCourts().getCourtSessionManager().addInSession(this);
        NotificationManager notificationManager = Courts.getCourts().getNotificationManager();
        Object[] rele = new Object[]{caze,judge,this};
        for (Citizen part : participants()) {
            if (part.isOnline()) {
                Player p = part.getPlayer();
                notificationManager.notification(NotificationType.ONGOING_CASE_PARTICIPANT,rele,p);
            }
        }
        notificationManager.notification(NotificationType.ONGOING_CASE_ALL, rele);
        VoxEffects voxEffects = Courts.getCourts().getCourtsConfig().getStartSessionEffects();
        if (voxEffects != null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("{judge-name}",judge.getName());
            replacements.put("{case-name}",caze.caseName());
            voxEffects.play(courtRoom.getJudgeChairLoc().getLocation(),courtRoom.playersInRoom(),replacements);
        }

        if (!judge.isOnline()) {
            courtRoom.sendMessage(ChatColor.RED + "Judge " + judge.getName() + " is currently offline, will wait " + (judgeOfflineTimeLeft() / (20 * 60)) + " minutes before postponing case.");
            startJudgeOfflineTime();
            return;
        }
        courtRoom.sendMessage(ChatColor.GREEN + "Case-" + caze.getId() + " will be presided by the honorable Judge " + judge.getName());
        if (caze.getPlantiff() != null) {
            courtRoom.sendMessage(ChatColor.YELLOW + "Plaintiff: " + caze.getPlantiff().getName());
        }
        if (caze.getDefendent() != null) {
            courtRoom.sendMessage(ChatColor.YELLOW + "Defendant: " + caze.getDefendent().getName());
        }
    }
    public Set<Citizen> participants() {
        Case caze = Courts.getCourts().getCaseManager().getCase(caseId);
        Set<Citizen> part = new HashSet<>();
        Set<UUID> uuidsAdded = new HashSet<>();
        if (judge != null) {
            part.add(judge);
            uuidsAdded.add(judge.getUuid());
        }
        if (caze.getPlantiff() != null) {
            if (!uuidsAdded.contains(caze.getPlantiff().getUuid())) {
                part.add(caze.getPlantiff());
                uuidsAdded.add(caze.getPlantiff().getUuid());
            }
        }
        if (caze.getDefendent() != null) {
            if (!uuidsAdded.contains(caze.getDefendent().getUuid())) {
                part.add(caze.getDefendent());
            }
        }
        return part;
    }
    public void stopJudgeOfflineTime() {
        if (judgeOfflineCondition == null)
            return;
        long elapsed = judgeOfflineCondition.tickLength() - judgeOfflineCondition.ticksLeft();
        SocialCore.plugin.getTimeConditionManager().remove(judgeOfflineCondition);
        judgeOfflineCondition = null;
        judgeOfflineTime += elapsed;
    }
    public void startJudgeOfflineTime() {
        final long timeRemain = Courts.getCourts().getCourtsConfig().getMaxJudgeOfflineTicks() - judgeOfflineTime;
        if (timeRemain <= 0) {
            timeoutCase();
        }else {
            judgeOfflineCondition = new TickCondition(timeRemain,false,timeRemain);
            judgeOfflineCondition.setRun(new Runnable() {
                @Override
                public void run() {
                    timeoutCase();
                }
            });
            SocialCore.plugin.getTimeConditionManager().register(judgeOfflineCondition);
        }
    }
    public boolean isJudgeOffline() {
        return judgeOfflineCondition != null;
    }
    public void timeoutCase() {
        if (Courts.getCourts() == null || Courts.getCourts().getCaseManager() == null) {
            System.out.println("[Courts] Case manager not loaded... Error 01");
            return;
        }
        Case caze = Courts.getCourts().getCaseManager().getCase(caseId);
        if (judge != null) {
            addPostCourtAction(new PostponeIndef(caze,judge.getName()));
        }else {
            addPostCourtAction(new PostponeIndef(caze,"none"));
        }
        end();
    }

    public Case getCaze() {

        Case caze = Courts.getCourts().getCaseManager().getCase(caseId);
        return caze;
    }

    public Judge getJudge() {
        return judge;
    }

    public boolean isBeingRescheduled() {
        if (postCourtActions.size() == 1 && postCourtActions.get(0) instanceof RescheduleCase)
            return true;
        return false;
    }
    public void clearPostCourtActions() {
        postCourtActions.clear();
    }
    public void addPostCourtAction(PostCourtAction postCourtAction) {
        if (postCourtAction instanceof OnlyAction) {
            clearPostCourtActions();
        }
        if (postCourtAction != null && postCourtAction instanceof GrantBuildingPermit) {
            GrantBuildingPermit permit1 = (GrantBuildingPermit) postCourtAction;
            for (PostCourtAction postCourtAction1 : postCourtActions) {
                if (postCourtAction1 != null && postCourtAction1 instanceof GrantBuildingPermit) {
                    GrantBuildingPermit permit2 = (GrantBuildingPermit) postCourtAction1;
                    if (permit1.prettyDescription() != null && permit2.prettyDescription() != null && permit1.prettyDescription().equals(permit2.prettyDescription())) {
                        return;
                    }
                }
            }
        }
        if (postCourtAction != null && postCourtAction instanceof GrantChestPermit) {
            GrantChestPermit permit1 = (GrantChestPermit) postCourtAction;
            for (PostCourtAction postCourtAction1 : postCourtActions) {
                if (postCourtAction1 != null && postCourtAction1 instanceof GrantChestPermit) {
                    GrantChestPermit permit2 = (GrantChestPermit) postCourtAction1;
                    if (permit1.prettyDescription() != null && permit2.prettyDescription() != null && permit1.prettyDescription().equals(permit2.prettyDescription())) {
                        return;
                    }
                }
            }
        }
        if (postCourtAction instanceof JailDefendent) {
            removeAllPostCourtAction(JailPlantiff.class);
        }
        if (postCourtAction instanceof JailPlantiff) {
            removeAllPostCourtAction(JailDefendent.class);
        }
        if (postCourtAction instanceof FineDefendent) {
            removeAllPostCourtAction(FinePlantiff.class);
        }
        if (postCourtAction instanceof FinePlantiff) {
            removeAllPostCourtAction(FineDefendent.class);
        }
        if (postCourtAction instanceof AffirmYay) {
            removeAllPostCourtAction(AffirmNay.class);
        }
        if (postCourtAction instanceof AffirmNay) {
            removeAllPostCourtAction(AffirmYay.class);
        }
        if (postCourtAction instanceof AffirmDefendantGuilty) {
            removeAllPostCourtAction(AffirmPlaintiffGuilty.class);
        }
        if (postCourtAction instanceof AffirmPlaintiffGuilty) {
            removeAllPostCourtAction(AffirmDefendantGuilty.class);
        }
        postCourtActions.add(postCourtAction);
    }

    @Override
    public Case getCase() {

        Case caze = Courts.getCourts().getCaseManager().getCase(caseId);
        return caze;
    }

    public void removeAllPostCourtAction(Class clazz) {
        List<PostCourtAction> toRemove = new ArrayList<>();
        for (PostCourtAction postCourtAction : postCourtActions) {
            if (postCourtAction.getClass().equals(clazz)) {
                toRemove.add(postCourtAction);
            }
        }
        for (PostCourtAction remove : toRemove) {
            postCourtActions.remove(remove);
        }
    }
    public void removePostCourtAction(PostCourtAction postCourtAction) {
        postCourtActions.remove(postCourtAction);
    }

    @Override
    public List<PostCourtAction> getPostCourtActions() {
        return postCourtActions;
    }

    private boolean doPostCourtActions() {
        boolean changeStatus = true;
        for (PostCourtAction postCourtAction : postCourtActions) {
            if (postCourtAction instanceof DontChangeStatus)
                changeStatus = false;
            postCourtAction.doAction();
        }

        if (postCourtActions.size() > 0 && judge != null) {
            Player judgePlayer = judge.getPlayer();
            if (judgePlayer != null && judgePlayer.isOnline()) {
                String actionsString = VoxStringUtils.formatToString(VoxStringUtils.toStringList(postCourtActions, new VoxStringUtils.ToStringConverter() {
                    @Override
                    public String convertToString(Object o) {
                        PostCourtAction postCourtAction = (PostCourtAction) o;
                        return postCourtAction.prettyDescription();
                    }
                }));
                judgePlayer.sendMessage(ChatColor.GREEN + "The following actions have been performed: ");
                judgePlayer.sendMessage(ChatColor.GREEN + actionsString);
            }

        }
        return  changeStatus;
    }

    private void callVote(com.nicholasdoherty.socialcore.courts.courtroom.voting.Vote vote) {
        if (vote != null) {
            vote.setOpen(false);
            Courts.getCourts().getCourtSessionManager().getVotingManager().removeVote(vote);
        }
        courtRoom.sendMessage(vote.helpMessage());
        Courts.getCourts().getCourtSessionManager().getVotingManager().addVote(vote);
    }
    public void callYayNay() {
        vote = new YayNayVote(new RegionRestricted(courtRoom));
        callVote(vote);
        //todo notif
    }
    public void callInnocentGuilty() {
        vote = new GulityInnocentVote(new RegionRestricted(courtRoom));
        callVote(vote);
    }
    public void clearVotes() {
        if (vote != null) {
            vote.clear();
        }
    }
    public void closeVote() {
        vote.setOpen(false);
    }

    public Vote getVote() {
        return vote;
    }

    public void setInRecess(boolean inRecess) {
        this.inRecess = inRecess;
    }

    public boolean isInRecess() {
        return inRecess;
    }

    public CourtRoom getCourtRoom() {
        return courtRoom;
    }
    public boolean isParticipant(Citizen citizen) {
        if (citizen.equals(judge))
            return true;
        Case caze = Courts.getCourts().getCaseManager().getCase(caseId);

        if (caze.getPlantiff() != null && citizen.equals(caze.getPlantiff())) {
            return true;
        }
        if (caze.getDefendent() != null && citizen.equals(caze.getDefendent())) {
            return true;
        }
        return false;
    }
    public void end() {
        Case caze = Courts.getCourts().getCaseManager().getCase(caseId);
        String judgeName = "none";
        if (judge != null) {
            judgeName = judge.getName();
        }
        CaseStatus origStatus = caze.getCaseStatus();

        Courts.getCourts().getCourtSessionManager().removeInSession(this);
        Courts.getCourts().getCourtSessionManager().endAll();
        Optional<TimeCondition> timeout = Courts.getCourts().getPlugin().getTimeConditionManager().getNotDone().stream()
                .filter(c -> c != null).filter(c -> c == judgeOfflineCondition).findAny();
        timeout.ifPresent(c -> Courts.getCourts().getPlugin().getTimeConditionManager().remove(c));
        boolean changeStatus = doPostCourtActions();
        sendPostCourtActionMessages();
        if (changeStatus) {
            caze.setCaseStatus(CaseStatus.RESOLVED,judgeName);
            caze.setResolve(Resolve.fromPost(postCourtActions));
            caze.updateSave();
            if (judge != null) {
                Player judgeP = judge.getPlayer();
                if (judgeP != null) {
                    List<ItemStack> judgementAward = Courts.getCourts().getCourtsConfig().getJudgementReward();
                    if (judgementAward == null || judgementAward.isEmpty()) {
                        Courts.getCourts().getPlugin().getLogger().warning("No judgement award defined in config");
                    }else {
                        for (ItemStack itemStack : judgementAward) {
                            judgeP.getInventory().addItem(itemStack);
                        }
                    }
                }
            }
            Courts.getCourts().getNotificationManager().notification(NotificationType.COURT_SESSION_END,new Object[]{caze,judge,this});
            VoxEffects end = Courts.getCourts().getCourtsConfig().getEndSessionEffects();
            if (end != null) { end.play(courtRoom.getCenter().getLocation());}
            List<Player> inRoom = courtRoom.playersInRoom();
            VoxEffects voxEffects = Courts.getCourts().getCourtsConfig().getEndSessionEffects();
            if (voxEffects != null) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("{judge-name}",judgeName);
                replacements.put("{case-name}",caze.caseName());
                voxEffects.play(courtRoom.getJudgeChairLoc().getLocation(),courtRoom.playersInRoom(),replacements);
            }
            List<ItemStack> sesionReward = Courts.getCourts().getCourtsConfig().getSessionReward();
            if (origStatus != CaseStatus.RESOLVED) {
                if (sesionReward != null && !sesionReward.isEmpty()) {
                    for (Player p : inRoom) {
                        for (ItemStack itemStack : sesionReward) {
                            p.getInventory().addItem(itemStack);
                        }
                    }
                }
            }
        }else {
            caze.backToProcessed();
        }
    }
    public void sendPostCourtActionMessages() {
        courtRoom.sendMessage(ChatColor.GREEN + "The judge has ended the session, ruling to perform the following actions:");
        for (PostCourtAction postCourtAction : postCourtActions) {
            courtRoom.sendMessage(postCourtAction.prettyDescription());
        }
    }
    public void teleportParticipants(Location loc) {
        for (Citizen citizen : participants()) {
            Player p = citizen.getPlayer();
            if (p != null) {
                p.teleport(loc);
            }
        }
    }

    public synchronized Set<UUID> getContempt() {
        return contempt;
    }

    public synchronized Set<UUID> getUnmuted() {
        return unmuted;
    }

    public synchronized Set<UUID> getMuted() {
        return muted;
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("case-id",caseId);
        map.put("judge",judge);
        map.put("post-court-actions",postCourtActions);
        map.put("in-recess",inRecess);
        map.put("court-room-id",courtRoom.getName());
        map.put("vote",vote);
        map.put("judge-offline-time",judgeOfflineTime);
        map.put("muted", SerializableUUID.toSerializableSet(muted));
        map.put("unmuted",SerializableUUID.toSerializableSet(unmuted));
        map.put("contempt",SerializableUUID.toSerializableSet(contempt));
        return map;
    }

    public CourtSession(Map<String, Object> map) {
        Courts courts = Courts.getCourts();
        this.caseId = (int) map.get("case-id");
        this.judge = (Judge) map.get("judge");
        List<PostCourtAction> postCourtActions = (List<PostCourtAction>) map.get("post-court-actions");
        this.postCourtActions = new ArrayList<>(postCourtActions);
        this.inRecess = (boolean) map.get("in-recess");
        String courtRoomId = (String) map.get("court-room-id");
        this.courtRoom = courts.getCourtsConfig().getCourtRoom(courtRoomId);
        if (this.courtRoom == null) {
            this.courtRoom = courts.getCourtsConfig().getDefaultCourtRoom();
        }
        if (map.containsKey("vote")) {
            this.vote = (Vote) map.get("vote");
        }
        this.judgeOfflineTime = Long.valueOf(map.get("judge-offline-time")+"");
        if (judgeOfflineTime > 0) {
            judgeOfflineTime -= 3600;
            if (judgeOfflineTime < 0) {
                judgeOfflineTime = 0;
            }
        }
        if (judge == null || !judge.isOnline()) {
            startJudgeOfflineTime();
        }
        if (map.containsKey("muted")) {
            muted = SerializableUUID.fromSerializableSet((Set<SerializableUUID>) map.get("muted"));
        }else {
            muted = new HashSet<>();
        }
        if (map.containsKey("unmuted")) {
            unmuted = SerializableUUID.fromSerializableSet((Set<SerializableUUID>) map.get("unmuted"));
        }else {
            unmuted = new HashSet<>();
        }
        if (map.containsKey("contempt")) {
             contempt = SerializableUUID.fromSerializableSet((Set<SerializableUUID>) map.get("contempt"));
        }else {
            contempt = new HashSet<>();
        }
    }
}
