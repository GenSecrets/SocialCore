package com.nicholasdoherty.socialcore.courts.courtroom;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.courts.cases.CourtDate;
import com.nicholasdoherty.socialcore.courts.courtroom.voting.VotingManager;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.time.condition.RealTimeCondition;
import com.nicholasdoherty.socialcore.time.condition.TimeCondition;
import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by john on 1/16/15.
 */
public class CourtSessionManager implements ConfigurationSerializable{
    private Courts courts;
    private VotingManager votingManager;
    private Set<CourtSession> inSession;
    private Set<String> removedGlobal = new HashSet<>();
    public CourtSessionManager(Courts courts) {
        this.courts = courts;
        votingManager = new VotingManager();
        inSession = new HashSet<>();
    }
    public void addAllBackToGlobal() {
        Chat chat = (Chat) Bukkit.getServer().getPluginManager().getPlugin("TownyChat");
        Channel chan = chat.getChannelsHandler().getChannel("global");
        if (chan == null) {
            return;
        }
        for (String name : new HashSet<>(removedGlobal)) {
            chan.join(name);
            if (removedGlobal.contains(name))
                removedGlobal.remove(name);
        }
    }
    public void removeFromGlobal(String name) {
        Chat chat = (Chat) Bukkit.getServer().getPluginManager().getPlugin("TownyChat");
        Channel chan = chat.getChannelsHandler().getChannel("global");
        if (chan == null) {
            return;
        }
        chan.leave(name);
        removedGlobal.add(name);
    }
    public void addToGlobal(String name) {
        Chat chat = (Chat) Bukkit.getServer().getPluginManager().getPlugin("TownyChat");
        Channel chan = chat.getChannelsHandler().getChannel("global");
        if (chan == null) {
            return;
        }
        chan.join(name);
        if (removedGlobal.contains(name))
            removedGlobal.remove(name);
    }
    public void setup() {
        new CourtRoomListener(this);
        new BukkitRunnable(){
            @Override
            public void run() {
                registerFutureSessions();
            }
        }.runTaskLater(SocialCore.plugin,1);
    }
    public void registerFutureSessions() {
        for (Case caze : courts.getCaseManager().getCases()) {
            registerSession(caze);
        }
    }
    public void unRegisterSession(Case caze) {
        TimeCondition toRemove = null;
        for (TimeCondition timeCondition : SocialCore.plugin.getTimeConditionManager().getNotDone()) {
            if (timeCondition.getRun() instanceof StartSessionRunnable) {
                StartSessionRunnable startSessionRunnable = (StartSessionRunnable) timeCondition.getRun();
                if (startSessionRunnable.getCaze().equals(caze)) {
                    toRemove = timeCondition;
                    break;
                }
            }
        }
        if (toRemove != null) {
            SocialCore.plugin.getTimeConditionManager().remove(toRemove);
        }
    }
    public void registerSession(Case caze) {
        unRegisterSession(caze);
        if (caze.getCaseStatus() == CaseStatus.COURT_DATE_SET && caze.getCourtDate() == null) {
            caze.backToProcessed();
        }
        if (caze.getCaseStatus() == CaseStatus.COURT_DATE_SET && caze.getCourtDate() != null) {
            CourtDate courtDate = caze.getCourtDate();
            long time = courtDate.getTime();
            if (time < new Date().getTime()+3000) {
                caze.backToProcessed();
            }
            RealTimeCondition realTimeCondition = new RealTimeCondition(new StartSessionRunnable(caze),false,time);
            SocialCore.plugin.getTimeConditionManager().register(realTimeCondition);
        }
    }
    public CourtSession getActiveCourtSession(Judge judge, Location location) {
        for (CourtSession courtSession : inSession) {
            if (courtSession.getJudge() != null) {
                if (courtSession.getJudge().equals(judge)) {
                    if (courtSession.getCourtRoom().isInRoom(location)) {
                        return courtSession;
                    }
                }
            }
        }
        return null;
    }
    public CourtSession getActiveCourtSession(Citizen citizen) {
        for (CourtSession courtSession : inSession) {
            if (courtSession.isParticipant(citizen))
                return courtSession;
        }
        return null;
    }
    public VotingManager getVotingManager() {
        return votingManager;
    }

    public Set<CourtSession> getInSession() {
        return inSession;
    }
    public void addInSession(CourtSession courtSession) {
        if (courtSession == null || courtSession.getJudge() == null) {
            return;
        }
        inSession.add(courtSession);
    }
    public void removeInSession(CourtSession courtSession) {
        if (inSession.contains(courtSession)) {
            inSession.remove(courtSession);
        }
        for (CourtSession courtSessionI : new HashSet<>(inSession)) {
            if (courtSessionI.equals(courtSession)) {
                inSession.remove(courtSession);
                break;
            }
            if (courtSessionI.getCaze().getId() == courtSession.getCaze().getId()) {
                inSession.remove(courtSessionI);
            }
        }
    }
    public void endAll() {
        for (CourtSession courtSession : new HashSet<>(inSession)) {
            courtSession.end();
        }
        inSession.clear();
    }

    public CourtSessionManager(Map<String, Object> map) {
        courts = Courts.getCourts();
        this.votingManager = (VotingManager) map.get("voting-manager");
        Set<CourtSession> ses = (Set<CourtSession>) map.get("in-session");
        this.inSession = new HashSet<>();
        for (CourtSession courtSession : ses) {
            if (courtSession != null && courtSession.getJudge() != null) {
                this.inSession.add(courtSession);
            }
        }
        setup();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("voting-manager",votingManager);
        map.put("in-session",inSession);
        return map;
    }
}
