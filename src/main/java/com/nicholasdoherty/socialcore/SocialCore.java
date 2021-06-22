package com.nicholasdoherty.socialcore;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inputlib.InputLib;
import com.nicholasdoherty.socialcore.genders.GenderCommandHandler;
import com.nicholasdoherty.socialcore.genders.Genders;
import com.nicholasdoherty.socialcore.marriages.*;
import com.nicholasdoherty.socialcore.store.SQLStore;
import com.nicholasdoherty.socialcore.time.Clock;
import com.nicholasdoherty.socialcore.time.condition.TimeConditionManager;
import com.voxmc.voxlib.gui.InventoryGUIManager;
import com.voxmc.voxlib.util.VaultUtil;
import com.voxmc.voxlib.util.VaultUtil.NotSetupException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class SocialCore extends JavaPlugin {
    public static SocialCore plugin;
    //logger
    public final Logger log = Logger.getLogger("Minecraft");
    
    //lang
    public SCLang lang;
    
    //save
    public SaveHandler save;
    //players
    public Map<String, SocialPlayer> socialPlayersCache;
    
    //genders
    public Genders genders;
    
    //marriages
    public Marriages marriages;
    public List<String> whitelistPiggybackWorlds;
    public SQLStore store;
    private InventoryGUIManager inventoryGUIManager;
    private InputLib inputLib;
    private Courts courts;
    private TimeConditionManager timeConditionManager;
    
    public InventoryGUIManager getInventoryGUIManager() {
        return inventoryGUIManager;
    }
    
    public InputLib getInputLib() {
        return inputLib;
    }
    
    public Courts getCourts() {
        return courts;
    }
    public String prefix;
    public boolean isCourtsEnabled;
    public boolean isGendersEnabled;
    public boolean isMarriagesEnabled;
    private FileConfiguration courtsConfig;
    private FileConfiguration courtsLangConfig;
    private FileConfiguration marriagesConfig;
    private FileConfiguration gendersConfig;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("Starting SocialCore...");
        try {
            VaultUtil.setup(getServer());
        } catch(final NotSetupException e) {
            getLogger().severe("Vault not detected, exceptions ahoy");
        }
        Clock.start(plugin);
        getLogger().info("SC Clock started!");
        timeConditionManager = new TimeConditionManager();
        inventoryGUIManager = new InventoryGUIManager(this);
        inputLib = new InputLib(this);
        socialPlayersCache = new HashMap<>();
        prefix = getConfig().getString("plugin-prefix");
        //helpers
        checkConfig();
        getLogger().info("Config checked!");
        final String directory = getDataFolder().toString();
        getLogger().info("Creating handlers...");
        save = new SaveHandler(directory, this);
        getLogger().info("[SC Handler] Created save handler");
        store = new SQLStore();
        getLogger().info("[SC Handler] Created MySQL handler");
        //
        // COURTS SETUP
        //
        if(getConfig().getBoolean("enable-courts")){
            setupCourtsConfig();
            courts = new Courts(this);
            getLogger().info("[SC Handler] Created courts handler");
            isCourtsEnabled = true;
        } else {
            isCourtsEnabled = false;
        }
        //
        // MARRIAGES SETUP
        //
        if(getConfig().getBoolean("enable-marriages")){
            setupMarriagesConfig();
            marriages = new Marriages(this);
            whitelistPiggybackWorlds = getConfig().getStringList("piggyback-world-whitelist");
            getLogger().info("[SC Handler] Created piggyback whitelist config");
            final CommandExecutor marriageCommandHandler = new MarriageCommandHandler(this);
            getCommand("marriage").setExecutor(marriageCommandHandler);
            getCommand("marriages").setExecutor(marriageCommandHandler);
            getCommand("engagements").setExecutor(marriageCommandHandler);
            getCommand("propose").setExecutor(marriageCommandHandler);
            getCommand("marry").setExecutor(marriageCommandHandler);
            getCommand("unengage").setExecutor(marriageCommandHandler);
            getCommand("divorce").setExecutor(marriageCommandHandler);
            getCommand("divorces").setExecutor(marriageCommandHandler);
            getCommand("adivorce").setExecutor(marriageCommandHandler);
            getCommand("amarry").setExecutor(marriageCommandHandler);
            getCommand("aunengage").setExecutor(marriageCommandHandler);
            getCommand("status").setExecutor(new StatusCommand());
            getCommand("share").setExecutor(marriageCommandHandler);
            getCommand("petname").setExecutor(new PetnameCommand());
            getCommand("purgeinvalids").setExecutor(new PurgeInvalidCommand());
            getLogger().info("[SC Handler] Created marriages handler");
            isMarriagesEnabled = true;
        } else {
            isMarriagesEnabled = false;
        }
        //
        // GENDERS SETUP
        //
        if(getConfig().getBoolean("enable-genders")){
            setupGendersConfig();
            final CommandExecutor genderCommandHandler = new GenderCommandHandler(this);
            getCommand("male").setExecutor(genderCommandHandler);
            getCommand("female").setExecutor(genderCommandHandler);
            getCommand("gender").setExecutor(genderCommandHandler);
            getLogger().info("[SC Handler] Created genders handler");
            isGendersEnabled = true;
        } else {
            isGendersEnabled = false;
        }
        getLogger().info("Finished creating handlers!");
        //commands
        final CommandExecutor scCommandHandler = new SCCommandHandler(this);
        getCommand("socialcore").setExecutor(scCommandHandler);
        getLogger().info("Finished setting up commands!");
        //langs
        lang = new SCLang(this);
        lang.loadConfig();
        getLogger().info("Finished setting up lang!");
        
        //events
        getServer().getPluginManager().registerEvents(new SCListener(this), this);
        getLogger().info("Registered listeners!");
        
        //players
//
        //genders
        genders = new Genders();
        getLogger().info("Finished setting up genders!");
        
        //marriages
        getLogger().info("SocialCore has finished starting!");
    }
    
    public TimeConditionManager getTimeConditionManager() {
        return timeConditionManager;
    }
    
    @Override
    public void onDisable() {
        Clock.save();
        courts.onDisable();
        SCListener.riding.keySet().stream().filter(p -> p != null).forEach(p -> {
            final Player player = Bukkit.getPlayer(p);
            try {
                player.leaveVehicle();
            } catch(final Exception ignored) {
            }
        });
    }
    
    @SuppressWarnings("unused")
    public void onReload() {
        
    }

    public FileConfiguration getCourtsConfig() { return this.courtsConfig; }
    public FileConfiguration getCourtsLangConfig() { return this.courtsLangConfig; }
    public FileConfiguration getMarriagesConfig() { return this.marriagesConfig; }
    public FileConfiguration getGendersConfig() { return this.gendersConfig; }

    private void setupCourtsConfig(){
        File courtsFolder = new File(getDataFolder().getPath() + File.separator + "courts");
        if (!courtsFolder.exists()){
            courtsFolder.mkdir();
        }

        File courtsConfigFile = new File(courtsFolder.getPath(), "config.yml");
        File courtsLangConfigFile = new File(courtsFolder.getPath(), "lang.yml");

        if (!courtsConfigFile.exists()) {
            courtsConfigFile.getParentFile().mkdirs();
            saveResource(courtsFolder.getName()+File.separator+"config.yml", false);
        }
        if (!courtsLangConfigFile.exists()) {
            courtsLangConfigFile.getParentFile().mkdirs();
            saveResource(courtsFolder.getName()+File.separator+"lang.yml", false);
        }

        courtsConfig= new YamlConfiguration();
        courtsLangConfig= new YamlConfiguration();

        try {
            courtsConfig.load(courtsConfigFile);
            courtsLangConfig.load(courtsLangConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void setupMarriagesConfig(){
        File marriagesFolder = new File(getDataFolder().getPath() + File.separator + "marriages");
        if (!marriagesFolder.exists()){
            marriagesFolder.mkdir();
        }

        File marriagesConfigFile = new File(marriagesFolder.getPath(), "config.yml");
        if (!marriagesConfigFile.exists()) {
            marriagesConfigFile.getParentFile().mkdirs();
            saveResource(marriagesFolder.getName()+File.separator+"config.yml", false);
        }

        marriagesConfig= new YamlConfiguration();

        try {
            marriagesConfig.load(marriagesConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void setupGendersConfig(){
        File gendersFolder = new File(getDataFolder().getPath() + File.separator + "genders");
        if (!gendersFolder.exists()){
            gendersFolder.mkdir();
        }

        File gendersConfigFile = new File(gendersFolder.getPath(), "config.yml");
        if (!gendersConfigFile.exists()) {
            gendersConfigFile.getParentFile().mkdirs();
            saveResource(gendersFolder.getName()+File.separator+"config.yml", false);
        }

        gendersConfig= new YamlConfiguration();

        try {
            gendersConfig.load(gendersConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void checkConfig() {
        if(!getDataFolder().isDirectory()) {
            getDataFolder().mkdirs();
        }
        if(!new File(getDataFolder(), "config.yml").isFile()) {
            writeConfig();
        }
    }
    
    private void writeConfig() {
        if(writeDefaultFileFromJar(new File(getDataFolder(), "config.yml"), "config.yml", true)) {
            log.info("[SocialCore] Saved default config.");
        }
    }
    
    private boolean writeDefaultFileFromJar(final File writeName, final String jarPath, final boolean backupOld) {
        try {
            final File fileBackup = new File(getDataFolder(), "backup-" + writeName);
            final File jarloc = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getCanonicalFile();
            if(jarloc.isFile()) {
                final JarFile jar = new JarFile(jarloc);
                final JarEntry entry = jar.getJarEntry(jarPath);
                if(entry != null && !entry.isDirectory()) {
                    final InputStream in = jar.getInputStream(entry);
                    final InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
                    if(writeName.isFile()) {
                        if(backupOld) {
                            if(fileBackup.isFile()) {
                                fileBackup.delete();
                            }
                            writeName.renameTo(fileBackup);
                        } else {
                            writeName.delete();
                        }
                    }
                    final FileOutputStream out = new FileOutputStream(writeName);
                    final OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                    final char[] tempbytes = new char[512];
                    int readbytes = isr.read(tempbytes, 0, 512);
                    while(readbytes > -1) {
                        osw.write(tempbytes, 0, readbytes);
                        readbytes = isr.read(tempbytes, 0, 512);
                    }
                    osw.close();
                    isr.close();
                    
                    return true;
                }
                jar.close();
            }
            return false;
        } catch(final Exception ex) {
            log.warning("[SocialCore] Failed to write default config. Stack trace follows:");
            ex.printStackTrace();
            return false;
        }
    }
    
    public enum Gender {
        MALE, FEMALE, UNSPECIFIED
    }
}
