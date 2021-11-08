package com.nicholasdoherty.socialcore.components.courts.courtroom;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.components.courts.cases.Resolve;
import com.nicholasdoherty.socialcore.components.courts.courtroom.actions.*;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.components.courts.courtroom.voting.GulityInnocentVote;
import com.nicholasdoherty.socialcore.components.courts.courtroom.voting.RegionRestricted;
import com.nicholasdoherty.socialcore.components.courts.courtroom.voting.Vote;
import com.nicholasdoherty.socialcore.components.courts.courtroom.voting.YayNayVote;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.notifications.NotificationManager;
import com.nicholasdoherty.socialcore.components.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.time.condition.TickCondition;
import com.nicholasdoherty.socialcore.utils.time.condition.TimeCondition;
import com.voxmc.voxlib.util.SerializableUUID;
import com.voxmc.voxlib.util.VoxEffects;
import com.voxmc.voxlib.util.VoxStringUtils;
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
@SuppressWarnings({"unused", "ConstantConditions", "unchecked"})
public class CourtSession implements ConfigurationSerializable, PostCourtActionHolder {
    private final int caseId;
    private final Judge judge;
    private final Set<UUID> muted;
    private final Set<UUID> unmuted;
    private final Set<UUID> contempt;
    private List<PostCourtAction> postCourtActions = new ArrayList<>();
    private boolean inRecess;
    private CourtRoom courtRoom;
    private Vote vote;
    private long judgeOfflineTime;
    private TimeCondition judgeOfflineCondition;
    private boolean isSilenced;
    
    public CourtSession(final Case caze, final Judge judge, final CourtRoom courtRoom) {
        caseId = caze.getId();
        this.judge = judge;
        this.courtRoom = courtRoom;
        judgeOfflineTime = 0;
        muted = new HashSet<>();
        unmuted = new HashSet<>();
        contempt = new HashSet<>();
    }
    
    public CourtSession(final Map<String, Object> map) {
        final Courts courts = Courts.getCourts();
        caseId = (int) map.get("case-id");
        judge = (Judge) map.get("judge");
        final List<PostCourtAction> postCourtActions = (List<PostCourtAction>) map.get("post-court-actions");
        this.postCourtActions = new ArrayList<>(postCourtActions);
        inRecess = (boolean) map.get("in-recess");
        final String courtRoomId = (String) map.get("court-room-id");
        courtRoom = courts.getCourtsConfig().getCourtRoom(courtRoomId);
        if(courtRoom == null) {
            courtRoom = courts.getCourtsConfig().getDefaultCourtRoom();
        }
        if(map.containsKey("vote")) {
            vote = (Vote) map.get("vote");
        }
        judgeOfflineTime = Long.valueOf(map.get("judge-offline-time") + "");
        if(judgeOfflineTime > 0) {
            judgeOfflineTime -= 3600;
            if(judgeOfflineTime < 0) {
                judgeOfflineTime = 0;
            }
        }
        if(judge == null || !judge.isOnline()) {
            startJudgeOfflineTime();
        }
        if(map.containsKey("muted")) {
            muted = SerializableUUID.fromSerializableSet((Set<SerializableUUID>) map.get("muted"));
        } else {
            muted = new HashSet<>();
        }
        if(map.containsKey("unmuted")) {
            unmuted = SerializableUUID.fromSerializableSet((Set<SerializableUUID>) map.get("unmuted"));
        } else {
            unmuted = new HashSet<>();
        }
        if(map.containsKey("contempt")) {
            contempt = SerializableUUID.fromSerializableSet((Set<SerializableUUID>) map.get("contempt"));
        } else {
            contempt = new HashSet<>();
        }
    }
    
    public long judgeOfflineTimeLeft() {
        if(judgeOfflineCondition == null) {
            return Courts.getCourts().getCourtsConfig().getMaxJudgeOfflineTicks() - judgeOfflineTime;
        } else {
            final long elapsed = judgeOfflineCondition.tickLength() - judgeOfflineCondition.ticksLeft();
            return Courts.getCourts().getCourtsConfig().getMaxJudgeOfflineTicks() - judgeOfflineTime - elapsed;
        }
    }
    
    public void silence() {
        courtRoom.silence();
        isSilenced = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                isSilenced = false;
            }
        }.runTaskLater(Courts.getCourts().getPlugin(), Courts.getCourts().getCourtsConfig().getSilenceMuteLength());
    }
    
    public boolean isSilenced() {
        return isSilenced;
    }
    
    public void startSession() {
        final Case caze = Courts.getCourts().getCaseManager().getCase(caseId);
        Courts.getCourts().getCourtSessionManager().addInSession(this);
        final NotificationManager notificationManager = Courts.getCourts().getNotificationManager();
        final Object[] rele = {caze, judge, this};
        for(final Citizen part : participants()) {
            if(part.isOnline()) {
                final Player p = part.getPlayer();
                notificationManager.notification(NotificationType.ONGOING_CASE_PARTICIPANT, rele, p);
            }
        }
        notificationManager.notification(NotificationType.ONGOING_CASE_ALL, rele);
        final VoxEffects voxEffects = Courts.getCourts().getCourtsConfig().getStartSessionEffects();
        if(voxEffects != null) {
            final Map<String, String> replacements = new HashMap<>();
            replacements.put("{judge-name}", judge.getName());
            replacements.put("{case-name}", caze.caseName());
            voxEffects.play(courtRoom.getJudgeChairLoc().getLocation(), courtRoom.playersInRoom(), replacements);
        }
        
        if(!judge.isOnline()) {
            courtRoom.sendMessage(ChatColor.RED + "Judge " + judge.getName() + " is currently offline, will wait " + judgeOfflineTimeLeft() / (20 * 60) + " minutes before postponing case.");
            startJudgeOfflineTime();
            return;
        }
        courtRoom.sendMessage(ChatColor.GREEN + "" + caze.getPlantiff() + "'s case(" + caze.getId() + ") will be presided by the honorable Judge " + judge.getName());
        if(caze.getPlantiff() != null) {
            courtRoom.sendMessage(ChatColor.YELLOW + "Plaintiff: " + caze.getPlantiff().getName());
        }
        if(caze.getDefendent() != null) {
            courtRoom.sendMessage(ChatColor.YELLOW + "Defendant: " + caze.getDefendent().getName());
        }
    }
    
    public Collection<Citizen> participants() {
        final Case caze = Courts.getCourts().getCaseManager().getCase(caseId);
        final Collection<Citizen> part = new HashSet<>();
        final Collection<UUID> uuidsAdded = new HashSet<>();
        if(judge != null) {
            part.add(judge);
            uuidsAdded.add(judge.getUuid());
        }
        if(caze.getPlantiff() != null) {
            if(!uuidsAdded.contains(caze.getPlantiff().getUuid())) {
                part.add(caze.getPlantiff());
                uuidsAdded.add(caze.getPlantiff().getUuid());
            }
        }
        if(caze.getDefendent() != null) {
            if(!uuidsAdded.contains(caze.getDefendent().getUuid())) {
                part.add(caze.getDefendent());
            }
        }
        return part;
    }
    
    public void stopJudgeOfflineTime() {
        if(judgeOfflineCondition == null) {
            return;
        }
        final long elapsed = judgeOfflineCondition.tickLength() - judgeOfflineCondition.ticksLeft();
        SocialCore.plugin.getTimeConditionManager().remove(judgeOfflineCondition);
        judgeOfflineCondition = null;
        judgeOfflineTime += elapsed;
    }
    
    public void startJudgeOfflineTime() {
        final long timeRemain = Courts.getCourts().getCourtsConfig().getMaxJudgeOfflineTicks() - judgeOfflineTime;
        if(timeRemain <= 0) {
            timeoutCase();
        } else {
            judgeOfflineCondition = new TickCondition(timeRemain, false, timeRemain);
            judgeOfflineCondition.setRun(this::timeoutCase);
            SocialCore.plugin.getTimeConditionManager().register(judgeOfflineCondition);
        }
    }
    
    public boolean isJudgeOffline() {
        return judgeOfflineCondition != null;
    }
    
    public void timeoutCase() {
        if(Courts.getCourts() == null || Courts.getCourts().getCaseManager() == null) {
            System.out.println("[Courts] Case manager not loaded... Error 01");
            return;
        }
        final Case caze = Courts.getCourts().getCaseManager().getCase(caseId);
        if(judge != null) {
            addPostCourtAction(new PostponeIndef(caze, judge.getName()));
        } else {
            addPostCourtAction(new PostponeIndef(caze, "none"));
        }
        end();
    }
    
    public Case getCaze() {
        return Courts.getCourts().getCaseManager().getCase(caseId);
    }
    
    public Judge getJudge() {
        return judge;
    }
    
    public boolean isBeingRescheduled() {
        return postCourtActions.size() == 1 && postCourtActions.get(0) instanceof RescheduleCase;
    }
    
    public void clearPostCourtActions() {
        postCourtActions.clear();
    }
    
    public void addPostCourtAction(final PostCourtAction postCourtAction) {
        if(postCourtAction instanceof OnlyAction) {
            clearPostCourtActions();
        }
        if(postCourtAction instanceof GrantBuildingPermit) {
            for(final PostCourtAction postCourtAction1 : postCourtActions) {
                if(postCourtAction1 instanceof GrantBuildingPermit) {
                    if(postCourtAction.prettyDescription() != null && postCourtAction1.prettyDescription() != null && postCourtAction.prettyDescription().equals(postCourtAction1.prettyDescription())) {
                        return;
                    }
                }
            }
        }
        if(postCourtAction instanceof JailDefendent) {
            removeAllPostCourtAction(JailPlantiff.class);
        }
        if(postCourtAction instanceof JailPlantiff) {
            removeAllPostCourtAction(JailDefendent.class);
        }
        if(postCourtAction instanceof FineDefendent) {
            removeAllPostCourtAction(FinePlantiff.class);
        }
        if(postCourtAction instanceof FinePlantiff) {
            removeAllPostCourtAction(FineDefendent.class);
        }
        if(postCourtAction instanceof AffirmYay) {
            removeAllPostCourtAction(AffirmNay.class);
        }
        if(postCourtAction instanceof AffirmNay) {
            removeAllPostCourtAction(AffirmYay.class);
        }
        if(postCourtAction instanceof AffirmDefendantGuilty) {
            removeAllPostCourtAction(AffirmPlaintiffGuilty.class);
        }
        if(postCourtAction instanceof AffirmPlaintiffGuilty) {
            removeAllPostCourtAction(AffirmDefendantGuilty.class);
        }
        postCourtActions.add(postCourtAction);
    }
    
    @Override
    public Case getCase() {
        
        return Courts.getCourts().getCaseManager().getCase(caseId);
    }
    
    public void removeAllPostCourtAction(final Class clazz) {
        final Collection<PostCourtAction> toRemove = new ArrayList<>();
        for(final PostCourtAction postCourtAction : postCourtActions) {
            if(postCourtAction.getClass().equals(clazz)) {
                toRemove.add(postCourtAction);
            }
        }
        for(final PostCourtAction remove : toRemove) {
            postCourtActions.remove(remove);
        }
    }
    
    public void removePostCourtAction(final PostCourtAction postCourtAction) {
        postCourtActions.remove(postCourtAction);
    }
    
    @Override
    public List<PostCourtAction> getPostCourtActions() {
        return postCourtActions;
    }
    
    private boolean doPostCourtActions() {
        boolean changeStatus = true;
        for(final PostCourtAction postCourtAction : postCourtActions) {
            if(postCourtAction instanceof DontChangeStatus) {
                changeStatus = false;
            }
            postCourtAction.doAction();
        }
        
        if(!postCourtActions.isEmpty() && judge != null) {
            final Player judgePlayer = judge.getPlayer();
            if(judgePlayer != null && judgePlayer.isOnline()) {
                final String actionsString = VoxStringUtils.formatToString(VoxStringUtils.toStringList(
                        postCourtActions, PostCourtAction::prettyDescription));
                judgePlayer.sendMessage(ChatColor.GREEN + "The following actions have been performed: ");
                judgePlayer.sendMessage(ChatColor.GREEN + actionsString);
            }
        }
        return changeStatus;
    }
    
    private void callVote(final Vote vote) {
        if(vote != null) {
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
        if(vote != null) {
            vote.clear();
        }
    }
    
    public void closeVote() {
        vote.setOpen(false);
    }
    
    public Vote getVote() {
        return vote;
    }
    
    public boolean isInRecess() {
        return inRecess;
    }
    
    public void setInRecess(final boolean inRecess) {
        this.inRecess = inRecess;
    }
    
    public CourtRoom getCourtRoom() {
        return courtRoom;
    }
    
    public boolean isParticipant(final Citizen citizen) {
        if(citizen.equals(judge)) {
            return true;
        }
        final Case caze = Courts.getCourts().getCaseManager().getCase(caseId);
        
        return caze.getPlantiff() != null && citizen.equals(caze.getPlantiff()) || caze.getDefendent() != null && citizen.equals(caze.getDefendent());
    }
    
    public void end() {
        final Case caze = Courts.getCourts().getCaseManager().getCase(caseId);
        String judgeName = "none";
        if(judge != null) {
            judgeName = judge.getName();
        }
        final CaseStatus origStatus = caze.getCaseStatus();
        
        Courts.getCourts().getCourtSessionManager().removeInSession(this);
        Courts.getCourts().getCourtSessionManager().endAll();
        final Optional<TimeCondition> timeout = Courts.getCourts().getPlugin().getTimeConditionManager().getNotDone().stream()
                .filter(Objects::nonNull).filter(c -> c == judgeOfflineCondition).findAny();
        timeout.ifPresent(c -> Courts.getCourts().getPlugin().getTimeConditionManager().remove(c));
        final boolean changeStatus = doPostCourtActions();
        sendPostCourtActionMessages();
        if(changeStatus) {
            caze.setCaseStatus(CaseStatus.RESOLVED, judgeName);
            caze.setResolve(Resolve.fromPost(postCourtActions));
            caze.updateSave();
            if(judge != null) {
                final Player judgeP = judge.getPlayer();
                if(judgeP != null) {
                    final List<ItemStack> judgementAward = Courts.getCourts().getCourtsConfig().getJudgementReward();
                    if(judgementAward == null || judgementAward.isEmpty()) {
                        Courts.getCourts().getPlugin().getLogger().warning("No judgement award defined in config");
                    } else {
                        for(final ItemStack itemStack : judgementAward) {
                            judgeP.getInventory().addItem(itemStack);
                        }
                    }
                }
            }
            Courts.getCourts().getNotificationManager().notification(NotificationType.COURT_SESSION_END, new Object[] {caze, judge, this});
            final VoxEffects end = Courts.getCourts().getCourtsConfig().getEndSessionEffects();
            if(end != null) {
                end.play(courtRoom.getCenter().getLocation());
            }
            final List<Player> inRoom = courtRoom.playersInRoom();
            final VoxEffects voxEffects = Courts.getCourts().getCourtsConfig().getEndSessionEffects();
            if(voxEffects != null) {
                final Map<String, String> replacements = new HashMap<>();
                replacements.put("{judge-name}", judgeName);
                replacements.put("{case-name}", caze.caseName());
                voxEffects.play(courtRoom.getJudgeChairLoc().getLocation(), courtRoom.playersInRoom(), replacements);
            }
            final List<ItemStack> sesionReward = Courts.getCourts().getCourtsConfig().getSessionReward();
            if(origStatus != CaseStatus.RESOLVED) {
                if(sesionReward != null && !sesionReward.isEmpty()) {
                    for(final Player p : inRoom) {
                        for(final ItemStack itemStack : sesionReward) {
                            p.getInventory().addItem(itemStack);
                        }
                    }
                }
            }
        } else {
            caze.backToProcessed();
        }
    }
    
    public void sendPostCourtActionMessages() {
        courtRoom.sendMessage(ChatColor.GREEN + "The judge has ended the session, ruling to perform the following actions:");
        for(final PostCourtAction postCourtAction : postCourtActions) {
            courtRoom.sendMessage(postCourtAction.prettyDescription());
        }
    }
    
    public void teleportParticipants(final Location loc) {
        for(final Citizen citizen : participants()) {
            final Player p = citizen.getPlayer();
            if(p != null) {
                p.teleport(loc);
            }
        }
    }
    
    public synchronized Collection<UUID> getContempt() {
        return contempt;
    }
    
    public synchronized Set<UUID> getUnmuted() {
        return unmuted;
    }
    
    public synchronized Collection<UUID> getMuted() {
        return muted;
    }
    
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put("case-id", caseId);
        map.put("judge", judge);
        map.put("post-court-actions", postCourtActions);
        map.put("in-recess", inRecess);
        map.put("court-room-id", courtRoom.getName());
        map.put("vote", vote);
        map.put("judge-offline-time", judgeOfflineTime);
        map.put("muted", SerializableUUID.toSerializableSet(muted));
        map.put("unmuted", SerializableUUID.toSerializableSet(unmuted));
        map.put("contempt", SerializableUUID.toSerializableSet(contempt));
        return map;
    }
}
