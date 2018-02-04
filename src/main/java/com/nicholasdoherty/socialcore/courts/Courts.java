package com.nicholasdoherty.socialcore.courts;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.cases.*;
import com.nicholasdoherty.socialcore.courts.citizens.CitizenManager;
import com.nicholasdoherty.socialcore.courts.commands.*;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSessionManager;
import com.nicholasdoherty.socialcore.courts.courtroom.actions.*;
import com.nicholasdoherty.socialcore.courts.courtroom.voting.*;
import com.nicholasdoherty.socialcore.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.courts.elections.Election;
import com.nicholasdoherty.socialcore.courts.elections.ElectionManager;
import com.nicholasdoherty.socialcore.courts.fines.FineManager;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.courts.notifications.BasicQueuedNotification;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationManager;
import com.nicholasdoherty.socialcore.courts.notifications.VoteSummaryQueued;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.courts.policies.PolicyManager;
import com.nicholasdoherty.socialcore.courts.prefix.PrefixManager;
import com.nicholasdoherty.socialcore.courts.stall.StallManager;
import com.nicholasdoherty.socialcore.utils.SerializableUUID;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.Date;

/**
 * Created by john on 1/3/15.
 */
public class Courts {
    private static Courts courts;
    private SocialCore plugin;
    private CourtsConfig courtsConfig;
    private CourtsSaveManager courtsSaveManager;
    private ElectionManager electionManager;
    private JudgeManager judgeManager;
    private StallManager stallManager;
    private CaseManager caseManager;
    private CourtSessionManager courtSessionManager;
    private DefaultDayGetter defaultDayGetter;
    private NotificationManager notificationManager;
    private CourtsLangManager courtsLangManager;
    private FineManager fineManager;
    private DivorceManager divorceManager;
    private SqlSaveManager sqlSaveManager;
    private CitizenManager citizenManager;
    private PolicyManager policyManager;
    private boolean forceNotSave = false;
    
    public Courts(SocialCore plugin) {
        courts = this;
        this.plugin = plugin;
        registerSerializers();
        
        //DO NOT MODIFY
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        courtsConfig = CourtsConfig.fromConfig(plugin.getConfig().getConfigurationSection("courts"));
        courtsLangManager = new CourtsLangManager(plugin.getConfig().getConfigurationSection("courts.lang"));
        sqlSaveManager = new SqlSaveManager();
        sqlSaveManager.upgrade();
        sqlSaveManager.clean();
        try {
            sqlSaveManager.purgeVotes();
        } catch(Exception e) {
            e.printStackTrace();
        }
        long time1 = new Date().getTime();
        citizenManager = new CitizenManager(courts);
        try {
            courtsSaveManager = new CourtsSaveManager(this);
        } catch(Exception e) {
            e.printStackTrace();
        }
        electionManager = new ElectionManager(sqlSaveManager.election());
        judgeManager = new JudgeManager(this);
        caseManager = new CaseManager(sqlSaveManager.getCases());
        stallManager = new StallManager(sqlSaveManager.getStalls());
        courtSessionManager = courtsSaveManager.courtRoomManager();
        defaultDayGetter = new DefaultDayGetter(caseManager);
        notificationManager = new NotificationManager(this);
        fineManager = new FineManager(sqlSaveManager.getFines());
        fineManager.startTimer();
        policyManager = new PolicyManager(this);
        long time2 = new Date().getTime();
        long diff = time2 - time1;
        plugin.getLogger().info("Took " + diff + "ms to deserialize " + caseManager.amount() + " cases");
        new CourtCommand(this);
        new ElectionCommand(this, electionManager);
        new JudgesCommand(this, judgeManager);
        new TestCommand(this);
        new JudgeCommand(this, judgeManager);
        new SecretaryCommand(this, judgeManager);
        new IfElectionCommand(this, electionManager);
        for(Player p : Bukkit.getOnlinePlayers()) {
            judgeManager.setPerms(p);
            judgeManager.setPrefix(p);
        }
        new PrefixManager(this);
        divorceManager = new DivorceManager(plugin, this);
    }
    
    public static Courts getCourts() {
        return courts;
    }
    
    public DivorceManager getDivorceManager() {
        return divorceManager;
    }
    
    public FineManager getFineManager() {
        return fineManager;
    }
    
    public CourtsLangManager getCourtsLangManager() {
        return courtsLangManager;
    }
    
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        CourtsConfig courtsConfig = CourtsConfig.fromConfig(plugin.getConfig().getConfigurationSection("courts"));
        if(courtsConfig == null) {
            plugin.getLogger().severe("Could not load config. Error");
            return;
        }
        this.courtsConfig = courtsConfig;
        courtsLangManager = new CourtsLangManager(plugin.getConfig().getConfigurationSection("courts.lang"));
    }
    
    public void setForceNotSave(boolean forceNotSave) {
        this.forceNotSave = forceNotSave;
    }
    
    public SqlSaveManager getSqlSaveManager() {
        return sqlSaveManager;
    }
    
    public void onDisable() {
        try {
            for(Player p : Bukkit.getOnlinePlayers()) {
                judgeManager.revertPrefix(p);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(!forceNotSave) {
            try {
                courtsSaveManager.saveAll();
                courtsSaveManager.saveFile();
                courtSessionManager.addAllBackToGlobal();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void registerSerializers() {
        ConfigurationSerialization.registerClass(Case.class);
        ConfigurationSerialization.registerClass(CaseHistory.class);
        ConfigurationSerialization.registerClass(CaseHistory.HistoryEntry.class);
        ConfigurationSerialization.registerClass(Candidate.class);
        ConfigurationSerialization.registerClass(Election.class);
        ConfigurationSerialization.registerClass(Secretary.class);
        ConfigurationSerialization.registerClass(Judge.class);
        ConfigurationSerialization.registerClass(ApprovedCitizen.class);
        ConfigurationSerialization.registerClass(Citizen.class);
        ConfigurationSerialization.registerClass(VLocation.class);
        ConfigurationSerialization.registerClass(SerializableUUID.class);
        ConfigurationSerialization.registerClass(CaseLocation.class);
        ConfigurationSerialization.registerClass(CaseMeta.class);
        
        ConfigurationSerialization.registerClass(RegionRestricted.class);
        ConfigurationSerialization.registerClass(Vote.class);
        ConfigurationSerialization.registerClass(CourtSession.class);
        ConfigurationSerialization.registerClass(CourtSessionManager.class);
        ConfigurationSerialization.registerClass(VotingManager.class);
        ConfigurationSerialization.registerClass(Resolve.class);
        
        ConfigurationSerialization.registerClass(AffirmDefendantGuilty.class);
        ConfigurationSerialization.registerClass(AffirmNay.class);
        ConfigurationSerialization.registerClass(AffirmPlaintiffGuilty.class);
        ConfigurationSerialization.registerClass(AffirmYay.class);
        ConfigurationSerialization.registerClass(FineDefendent.class);
        ConfigurationSerialization.registerClass(FinePlantiff.class);
        ConfigurationSerialization.registerClass(GrantBuildingPermit.class);
        ConfigurationSerialization.registerClass(GrantChestPermit.class);
        ConfigurationSerialization.registerClass(GrantDivorce.class);
        ConfigurationSerialization.registerClass(JailDefendent.class);
        ConfigurationSerialization.registerClass(JailPlantiff.class);
        ConfigurationSerialization.registerClass(PostponeIndef.class);
        ConfigurationSerialization.registerClass(RescheduleCase.class);
        ConfigurationSerialization.registerClass(ThrowoutCase.class);
        ConfigurationSerialization.registerClass(GrantSameSexMarriage.class);
        
        ConfigurationSerialization.registerClass(YayNayVote.class);
        ConfigurationSerialization.registerClass(GulityInnocentVote.class);
        
        ConfigurationSerialization.registerClass(BasicQueuedNotification.class);
        ConfigurationSerialization.registerClass(VoteSummaryQueued.class);
    }
    
    public PolicyManager getPolicyManager() {
        return policyManager;
    }
    
    public CitizenManager getCitizenManager() {
        return citizenManager;
    }
    
    public DefaultDayGetter getDefaultDayGetter() {
        return defaultDayGetter;
    }
    
    public StallManager getStallManager() {
        return stallManager;
    }
    
    public CaseManager getCaseManager() {
        return caseManager;
    }
    
    public void setCaseManager(CaseManager caseManager) {
        this.caseManager = caseManager;
    }
    
    public SocialCore getPlugin() {
        return plugin;
    }
    
    public CourtsConfig getCourtsConfig() {
        return courtsConfig;
    }
    
    public CourtSessionManager getCourtSessionManager() {
        return courtSessionManager;
    }
    
    public CourtsSaveManager getCourtsSaveManager() {
        return courtsSaveManager;
    }
    
    public ElectionManager getElectionManager() {
        return electionManager;
    }
    
    public JudgeManager getJudgeManager() {
        return judgeManager;
    }
}
