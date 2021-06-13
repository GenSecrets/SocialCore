package com.nicholasdoherty.socialcore.courts;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.cases.*;
import com.nicholasdoherty.socialcore.courts.cases.CaseHistory.HistoryEntry;
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
import com.voxmc.voxlib.util.SerializableUUID;
import com.voxmc.voxlib.VLocation;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.Date;

/**
 * Created by john on 1/3/15.
 */
@SuppressWarnings("unused")
public class Courts {
    private static Courts courts;
    private final SocialCore plugin;
    private CourtsConfig courtsConfig;
    private CourtsSaveManager courtsSaveManager;
    private final ElectionManager electionManager;
    private final JudgeManager judgeManager;
    private final StallManager stallManager;
    private CaseManager caseManager;
    private final CourtSessionManager courtSessionManager;
    private final DefaultDayGetter defaultDayGetter;
    private final NotificationManager notificationManager;
    private CourtsLangManager courtsLangManager;
    private final FineManager fineManager;
    private final DivorceManager divorceManager;
    private final SqlSaveManager sqlSaveManager;
    private final CitizenManager citizenManager;
    private final PolicyManager policyManager;
    private boolean forceNotSave;
    
    public Courts(final SocialCore plugin) {
        plugin.getLogger().info("[COURTS] Starting courts...");
        courts = this;
        this.plugin = plugin;
        registerSerializers();
        plugin.getLogger().info("[COURTS] Registered serializers!");
        
        //DO NOT MODIFY
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        plugin.getLogger().info("[COURTS] Reloaded config!");
        courtsConfig = CourtsConfig.fromConfig(plugin.getConfig().getConfigurationSection("courts"));
        courtsLangManager = new CourtsLangManager(plugin.getConfig().getConfigurationSection("courts.lang"));
        plugin.getLogger().info("[COURTS] Loaded config and lang!");
        sqlSaveManager = new SqlSaveManager();
        sqlSaveManager.upgrade();
        plugin.getLogger().info("[COURTS] Upgraded DB!");
        sqlSaveManager.clean();
        plugin.getLogger().info("[COURTS] Cleaned DB!");
        try {
            sqlSaveManager.purgeVotes();
        } catch(final Exception e) {
            e.printStackTrace();
        }
        plugin.getLogger().info("[COURTS] Purged outdated votes!");
        final long time1 = new Date().getTime();
        citizenManager = new CitizenManager(courts);
        plugin.getLogger().info("[COURTS] Set up citizens manager!");
        try {
            courtsSaveManager = new CourtsSaveManager(this);
        } catch(final Exception e) {
            e.printStackTrace();
        }
        plugin.getLogger().info("[COURTS] Set up save manager!");
        electionManager = new ElectionManager(sqlSaveManager.election());
        plugin.getLogger().info("[COURTS] Set up election manager!");
        judgeManager = new JudgeManager(this);
        plugin.getLogger().info("[COURTS] Set up judge manager!");
        caseManager = new CaseManager(sqlSaveManager.getCases());
        plugin.getLogger().info("[COURTS] Set up case manager!");
        stallManager = new StallManager(sqlSaveManager.getStalls());
        plugin.getLogger().info("[COURTS] Set up stall manager!");
        courtSessionManager = courtsSaveManager.courtRoomManager();
        plugin.getLogger().info("[COURTS] Set up court session manager!");
        defaultDayGetter = new DefaultDayGetter(caseManager);
        plugin.getLogger().info("[COURTS] Set up default day getter!");
        notificationManager = new NotificationManager(this);
        plugin.getLogger().info("[COURTS] Set up notification manager!");
        fineManager = new FineManager(sqlSaveManager.getFines());
        fineManager.startTimer();
        plugin.getLogger().info("[COURTS] Set up fine manager!");
        policyManager = new PolicyManager(this);
        plugin.getLogger().info("[COURTS] Set up policy manager!");
        final long time2 = new Date().getTime();
        final long diff = time2 - time1;
        plugin.getLogger().info("[COURTS] Took " + diff + "ms to deserialize " + caseManager.amount() + " cases");
        new CourtCommand(this);
        new ElectionCommand(this, electionManager);
        new JudgesCommand(this, judgeManager);
        new TestCommand(this);
        new JudgeCommand(this, judgeManager);
        new SecretaryCommand(this, judgeManager);
        new IfElectionCommand(this, electionManager);
        plugin.getLogger().info("[COURTS] Registered commands!");
        for(final Player p : Bukkit.getOnlinePlayers()) {
            judgeManager.setPerms(p);
            //judgeManager.setPrefix(p);
        }
        plugin.getLogger().info("[COURTS] Updated online judge perms/prefixes!");
        new PrefixManager(this);
        plugin.getLogger().info("[COURTS] Set up prefix manager!");
        divorceManager = new DivorceManager(plugin, this);
        plugin.getLogger().info("[COURTS] Set up divorce manager!");
        plugin.getLogger().info("[COURTS] Finished loading courts!");
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
        final CourtsConfig courtsConfig = CourtsConfig.fromConfig(plugin.getConfig().getConfigurationSection("courts"));
        //noinspection ConstantConditions
        if(courtsConfig == null) {
            plugin.getLogger().severe("Could not load config. Error");
            return;
        }
        this.courtsConfig = courtsConfig;
        courtsLangManager = new CourtsLangManager(plugin.getConfig().getConfigurationSection("courts.lang"));
    }
    
    public void setForceNotSave(final boolean forceNotSave) {
        this.forceNotSave = forceNotSave;
    }
    
    public SqlSaveManager getSqlSaveManager() {
        return sqlSaveManager;
    }
    
    public void onDisable() {
        //try {
        //    for(final Player p : Bukkit.getOnlinePlayers()) {
        //        judgeManager.revertPrefix(p);
        //    }
        //} catch(final Exception e) {
        //    e.printStackTrace();
        //}
        if(!forceNotSave) {
            try {
                courtsSaveManager.saveAll();
                courtsSaveManager.saveFile();
                courtSessionManager.addAllBackToGlobal();
            } catch(final Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void registerSerializers() {
        ConfigurationSerialization.registerClass(Case.class);
        ConfigurationSerialization.registerClass(CaseHistory.class);
        ConfigurationSerialization.registerClass(HistoryEntry.class);
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
    
    public void setCaseManager(final CaseManager caseManager) {
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
