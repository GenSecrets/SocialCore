package com.nicholasdoherty.socialcore.courts;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseManager;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSessionManager;
import com.nicholasdoherty.socialcore.courts.elections.ElectionManager;
import com.nicholasdoherty.socialcore.courts.fines.FineManager;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.courts.stall.Stall;
import com.nicholasdoherty.socialcore.courts.stall.StallManager;
import com.nicholasdoherty.socialcore.utils.CompressedConfigAccessor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by john on 1/6/15.
 */
public class CourtsSaveManager {
    private Courts courts;
    private CompressedConfigAccessor configAccessor;
    private FileConfiguration fileConfiguration;
    private CompressedConfigAccessor casesConfigAccessor;
    private FileConfiguration casesFileConfiguration;

    public CourtsSaveManager(Courts courts) throws Exception {
        this.courts = courts;
        SocialCore plugin = courts.getPlugin();

        casesConfigAccessor = new CompressedConfigAccessor(plugin,"courts-data.zip","cases");
        casesConfigAccessor.saveDefaultConfig();
        configAccessor = new CompressedConfigAccessor(plugin, "courts-data.zip","base");
        configAccessor.saveDefaultConfig();
        reloadConfigs();
        try {
            casesFileConfiguration = casesConfigAccessor.getConfig();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        courts.setCaseManager(caseManager());


        try {
            fileConfiguration = configAccessor.getConfig();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        long saveInterval = courts.getCourtsConfig().getAutoSaveInterval();
        new AutoSave().runTaskTimer(plugin, saveInterval, saveInterval);
    }
    public void reload() {
        try {
            casesConfigAccessor.reloadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        try {
            casesFileConfiguration = casesConfigAccessor.getConfig();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            configAccessor.reloadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        try {
            fileConfiguration = configAccessor.getConfig();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    private static synchronized void saveCorrupted() {
        File from = new File(Courts.getCourts().getPlugin().getDataFolder(), "courts-data.zip");
        Random ran = new Random();
        File to = new File(Courts.getCourts().getPlugin().getDataFolder(), "courts-data.zip.bad" + ran.nextInt(100000));
        System.out.println("Saved bad file to " + to.getName());
        try {
            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public static synchronized void revertFile() {
        System.out.println("[Courts] Error parsing saved data, reverting to backup....");
        saveCorrupted();
        File from = new File(Courts.getCourts().getPlugin().getDataFolder(), "courts-data.zip.bak");
        File to = new File(Courts.getCourts().getPlugin().getDataFolder(), "courts-data.zip");
        try {
            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Courts.getCourts().setForceNotSave(true);
        Plugin plugin = Courts.getCourts().getPlugin();
        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
        Bukkit.getServer().getPluginManager().enablePlugin(plugin);
    }
    public synchronized void reloadConfigs() throws Exception {
        try {
            configAccessor.reloadConfig();
            casesConfigAccessor.reloadConfig();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }
    public synchronized void saveFile() {
        long time = new Date().getTime();

        try {
            configAccessor.saveConfig();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return;
        }
        try {
            casesConfigAccessor.saveConfig();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return;
        }
        if (SocialCore.plugin.isEnabled()) {
            try {
                reloadConfigs();
            } catch (Exception e) {
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        revertFile();
                    }
                }.runTaskLater(SocialCore.plugin,1);
                e.printStackTrace();
            }

        }
        long time2 = new Date().getTime();
        long diff = time2-time;
        File old = new File(courts.getPlugin().getDataFolder(), "courts-data.zip");
        if (old.exists()) {
            File newFile = new File(courts.getPlugin().getDataFolder(), "courts-data.zip.bak");
            try {
                Files.copy(old.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        courts.getPlugin().getLogger().info("Took " + diff + "ms to serialize " + courts.getCaseManager().amount() + " cases (async)");
    }


    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public ElectionManager electionManager() {
        if (!fileConfiguration.contains("election-manager")) {
            return new ElectionManager(null,null);
        }
        return (ElectionManager) fileConfiguration.get("election-manager");
    }
    public void saveElectionManager(ElectionManager electionManager) {
        fileConfiguration.set("election-manager",electionManager);
    }

    public JudgeManager judgeManager() {
        if (!fileConfiguration.contains("judge-manager")) {
            return new JudgeManager(new HashSet<Judge>());
        }
        return (JudgeManager) fileConfiguration.get("judge-manager");
    }
    public void saveJudgeManager(JudgeManager judgeManager) {
        fileConfiguration.set("judge-manager",judgeManager);
    }
    public StallManager stallManager() {
        if (!fileConfiguration.contains("stall-manager")) {
            return new StallManager(new HashSet<Stall>());
        }
        return (StallManager) fileConfiguration.get("stall-manager");
    }
    public void saveStallManager(StallManager stallManager) {
        fileConfiguration.set("stall-manager",stallManager);
    }

    public CaseManager caseManager() throws Exception {
        if (!casesFileConfiguration.contains("case-manager")) {
            return new CaseManager(new ArrayList<Case>());
        }
        Object o = casesFileConfiguration.get("case-manager");
        if (o == null) {
          throw new Exception();
        }
        return (CaseManager) o;
    }
    public void saveCasemanager(CaseManager caseManager) {
        casesFileConfiguration.set("case-manager",caseManager);
    }

    public CourtSessionManager courtRoomManager() {
        if (!fileConfiguration.contains("courtroom-manager")) {
            return new CourtSessionManager(courts);
        }
        return (CourtSessionManager) fileConfiguration.get("courtroom-manager");
    }

    public void saveCourtroomManager(CourtSessionManager courtSessionManager) {
        fileConfiguration.set("courtroom-manager", courtSessionManager);
    }
    //public List<BasicQueuedNotification> queuedNotificationList() {
    //    if (!fileConfiguration.contains("queued-notifications")) {
    //        return new ArrayList<>();
    //    }
    //    return new ArrayList<>((List<BasicQueuedNotification>)fileConfiguration.get("queued-notifications"));
    //}
    //public void saveQueuedNotifications() {
    //    fileConfiguration.set("queued-notifications",courts.getNotificationManager().getQueuedNotifications());
    //}
    public FineManager fineManager() {
        if (fileConfiguration.contains("fine-manager")) {
            return (FineManager) fileConfiguration.get("fine-manager");
        }
        return new FineManager();
    }
    public void saveFineManager(FineManager fineManager) {
        fileConfiguration.set("fine-manager",fineManager);
    }
    public void saveAll() {
        long time = new Date().getTime();
        saveElectionManager(courts.getElectionManager());
        saveJudgeManager(courts.getJudgeManager());
        saveStallManager(courts.getStallManager());
        saveCasemanager(courts.getCaseManager());
        saveCourtroomManager(courts.getCourtSessionManager());
        //saveQueuedNotifications();
        saveFineManager(courts.getFineManager());
        long time2 = new Date().getTime();
        long diff = time2-time;
        courts.getPlugin().getLogger().info("Took " + diff + "ms to prepare " + courts.getCaseManager().amount() + " cases (sync)");
    }
    class AutoSave extends BukkitRunnable{
        @Override
        public void run() {
            courts.getPlugin().getLogger().info("Autosaving courts");
            saveAll();
            new AutoSaveAsyncPart().runTaskAsynchronously(courts.getPlugin());
        }
    }
    class AutoSaveAsyncPart extends BukkitRunnable {

        @Override
        public void run() {
            saveFile();
        }
    }
}
