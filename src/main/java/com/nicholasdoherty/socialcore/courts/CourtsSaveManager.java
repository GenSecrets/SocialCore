package com.nicholasdoherty.socialcore.courts;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSessionManager;
import com.nicholasdoherty.socialcore.courts.notifications.BasicQueuedNotification;
import com.voxmc.voxlib.util.CompressedConfigAccessor;
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
import java.util.List;
import java.util.Random;

/**
 * Created by john on 1/6/15.
 */
@SuppressWarnings("unused")
public class CourtsSaveManager {
    private final Courts courts;
    private final CompressedConfigAccessor configAccessor;
    private final CompressedConfigAccessor casesConfigAccessor;
    private FileConfiguration fileConfiguration;
    private FileConfiguration casesFileConfiguration;
    
    public CourtsSaveManager(final Courts courts) throws Exception {
        this.courts = courts;
        final SocialCore plugin = courts.getPlugin();
        
        casesConfigAccessor = new CompressedConfigAccessor(plugin, "courts"+File.separator+"courts-data.zip", "cases");
        casesConfigAccessor.saveDefaultConfig();
        configAccessor = new CompressedConfigAccessor(plugin, "courts"+File.separator+"courts-data.zip", "base");
        configAccessor.saveDefaultConfig();
        reloadConfigs();
        try {
            casesFileConfiguration = casesConfigAccessor.getConfig();
        } catch(final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        
        try {
            fileConfiguration = configAccessor.getConfig();
        } catch(final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        final long saveInterval = courts.getCourtsConfig().getAutoSaveInterval();
        new AutoSave().runTaskTimer(plugin, saveInterval, saveInterval);
    }
    
    private static synchronized void saveCorrupted() {
        final File from = new File(Courts.getCourts().getPlugin().getDataFolder()+File.separator+"courts", "courts-data.zip");
        final Random ran = new Random();
        final File to = new File(Courts.getCourts().getPlugin().getDataFolder()+File.separator+"courts", "courts-data.zip.bad" + ran.nextInt(100000));
        System.out.println("Saved bad file to " + to.getName());
        try {
            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch(final IOException e1) {
            e1.printStackTrace();
        }
    }
    
    public static synchronized void revertFile() {
        System.out.println("[Courts] Error parsing saved data, reverting to backup....");
        saveCorrupted();
        final File from = new File(Courts.getCourts().getPlugin().getDataFolder()+File.separator+"courts", "courts-data.zip.bak");
        final File to = new File(Courts.getCourts().getPlugin().getDataFolder()+File.separator+"courts", "courts-data.zip");
        try {
            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch(final IOException e1) {
            e1.printStackTrace();
        }
        Courts.getCourts().setForceNotSave(true);
        final Plugin plugin = Courts.getCourts().getPlugin();
        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
        Bukkit.getServer().getPluginManager().enablePlugin(plugin);
    }
    
    public void reload() {
        try {
            casesConfigAccessor.reloadConfig();
        } catch(final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        try {
            casesFileConfiguration = casesConfigAccessor.getConfig();
        } catch(final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        
        try {
            configAccessor.reloadConfig();
        } catch(final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        try {
            fileConfiguration = configAccessor.getConfig();
        } catch(final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void reloadConfigs() throws Exception {
        try {
            configAccessor.reloadConfig();
            casesConfigAccessor.reloadConfig();
        } catch(final Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }
    
    public synchronized void saveFile() {
        final long time = new Date().getTime();
        
        try {
            configAccessor.saveConfig();
        } catch(final InvalidConfigurationException e) {
            e.printStackTrace();
            return;
        }
        try {
            casesConfigAccessor.saveConfig();
        } catch(final InvalidConfigurationException e) {
            e.printStackTrace();
            return;
        }
        if(SocialCore.plugin.isEnabled()) {
            try {
                reloadConfigs();
            } catch(final Exception e) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        revertFile();
                    }
                }.runTaskLater(SocialCore.plugin, 1);
                e.printStackTrace();
            }
        }
        final long time2 = new Date().getTime();
        final long diff = time2 - time;
        final File old = new File(courts.getPlugin().getDataFolder()+File.separator+"courts", "courts-data.zip");
        if(old.exists()) {
            final File newFile = new File(courts.getPlugin().getDataFolder()+File.separator+"courts", "courts-data.zip.bak");
            try {
                Files.copy(old.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch(final IOException e) {
                e.printStackTrace();
            }
        }
        courts.getPlugin().getLogger().info("Took " + diff + "ms to serialize " + courts.getCaseManager().amount() + " cases (async)");
    }
    
    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }
    
    public CourtSessionManager courtRoomManager() {
        if(!fileConfiguration.contains("courtroom-manager")) {
            return new CourtSessionManager(courts);
        }
        return (CourtSessionManager) fileConfiguration.get("courtroom-manager");
    }
    
    public void saveCourtroomManager(final CourtSessionManager courtSessionManager) {
        fileConfiguration.set("courtroom-manager", courtSessionManager);
    }
    
    public List<BasicQueuedNotification> queuedNotificationList() {
        if(!fileConfiguration.contains("queued-notifications")) {
            return new ArrayList<>();
        }
        //noinspection unchecked
        return new ArrayList<>((List<BasicQueuedNotification>) fileConfiguration.get("queued-notifications"));
    }
    
    public void saveQueuedNotifications() {
        fileConfiguration.set("queued-notifications", courts.getNotificationManager().getQueuedNotifications());
    }
    
    public void saveAll() {
        final long time = new Date().getTime();
        saveCourtroomManager(courts.getCourtSessionManager());
        saveQueuedNotifications();
        final long time2 = new Date().getTime();
        final long diff = time2 - time;
        courts.getPlugin().getLogger().info("Took " + diff + "ms to prepare " + courts.getCaseManager().amount() + " cases (sync)");
    }
    
    class AutoSave extends BukkitRunnable {
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
