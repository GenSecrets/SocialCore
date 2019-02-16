package com.nicholasdoherty.socialcore;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inputlib.InputLib;
import com.voxmc.voxlib.gui.inventorygui.InventoryGUIManager;
import com.nicholasdoherty.socialcore.emotes.EmoteCommand;
import com.nicholasdoherty.socialcore.emotes.EmoteListener;
import com.nicholasdoherty.socialcore.emotes.Emotes;
import com.nicholasdoherty.socialcore.emotes.ForceEmoteCommand;
import com.nicholasdoherty.socialcore.genders.GenderCommandHandler;
import com.nicholasdoherty.socialcore.genders.Genders;
import com.nicholasdoherty.socialcore.marriages.*;
import com.nicholasdoherty.socialcore.misc.GlobalMute;
import com.nicholasdoherty.socialcore.races.Race;
import com.nicholasdoherty.socialcore.races.Races;
import com.nicholasdoherty.socialcore.store.SQLStore;
import com.nicholasdoherty.socialcore.time.Clock;
import com.nicholasdoherty.socialcore.time.condition.TimeConditionManager;
import com.nicholasdoherty.socialcore.titles.TitleManager;
import com.voxmc.voxlib.util.VaultUtil;
import com.voxmc.voxlib.util.VaultUtil.NotSetupException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
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
    public Races races;
    //players
    public Map<String, SocialPlayer> socialPlayersCache;
    
    //genders
    public Genders genders;
    
    //marriages
    public Marriages marriages;
    public List<String> whitelistPiggybackWorlds;
    // globalmute
    public GlobalMute globalMute;
    //emotes
    public Emotes emotes;
    public SQLStore store;
    private InventoryGUIManager inventoryGUIManager;
    private InputLib inputLib;
    private Courts courts;
    private TimeConditionManager timeConditionManager;
    private TitleManager titleManager;
    
    public InventoryGUIManager getInventoryGUIManager() {
        return inventoryGUIManager;
    }
    
    public InputLib getInputLib() {
        return inputLib;
    }
    
    public Courts getCourts() {
        return courts;
    }
    
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
        //helpers
        checkConfig();
        getLogger().info("Config checked!");
        final String directory = getDataFolder().toString();
        getLogger().info("Creating handlers...");
        races = new Races(this);
        getLogger().info("[SC Handler] Created races handler");
        save = new SaveHandler(directory, this);
        getLogger().info("[SC Handler] Created save handler");
        marriages = new Marriages(this);
        getLogger().info("[SC Handler] Created marriages handler");
        store = new SQLStore();
        getLogger().info("[SC Handler] Created MySQL handler");
        courts = new Courts(this);
        getLogger().info("[SC Handler] Created courts handler");
        globalMute = new GlobalMute(this);
        getLogger().info("[SC Handler] Created global mute handler");
        whitelistPiggybackWorlds = getConfig().getStringList("piggyback-world-whitelist"); // Putting this here so I don't have to read from config a lot
        getLogger().info("[SC Handler] Created piggyback whitelist config");
        getLogger().info("Finished creating handlers!");
        //commands
        final CommandExecutor scCommandHandler = new SCCommandHandler(this);
        final CommandExecutor marriageCommandHandler = new MarriageCommandHandler(this);
        final CommandExecutor genderCommandHandler = new GenderCommandHandler(this);
        getCommand("socialcore").setExecutor(scCommandHandler);
        getCommand("male").setExecutor(genderCommandHandler);
        getCommand("female").setExecutor(genderCommandHandler);
        getCommand("gender").setExecutor(genderCommandHandler);
        getCommand("marriage").setExecutor(marriageCommandHandler);
        getCommand("marriages").setExecutor(marriageCommandHandler);
        getCommand("engagements").setExecutor(marriageCommandHandler);
        getCommand("propose").setExecutor(marriageCommandHandler);
        getCommand("marry").setExecutor(marriageCommandHandler);
        getCommand("divorce").setExecutor(marriageCommandHandler);
        getCommand("divorces").setExecutor(marriageCommandHandler);
        getCommand("adivorce").setExecutor(marriageCommandHandler);
        getCommand("amarry").setExecutor(marriageCommandHandler);
        getCommand("share").setExecutor(marriageCommandHandler);
        getCommand("unengage").setExecutor(marriageCommandHandler);
        getCommand("aunengage").setExecutor(marriageCommandHandler);
        getCommand("status").setExecutor(new StatusCommand());
        getCommand("purgeinvalids").setExecutor(new PurgeInvalidCommand());
        getCommand("petname").setExecutor(new PetnameCommand());
        //
        //testing
        getCommand("getpermissions").setExecutor(new ViewPermissionsCommand());
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
//
        //emotes
        emotes = new Emotes(this);
        new EmoteListener(this);
        new EmoteCommand(this);
        new FixerCommand(this);
        new ForceEmoteCommand(this);
        getLogger().info("Finished setting up emotes!");
        races.reloadRaces();
        getLogger().info("Reloaded races!");
        for(final Player p : Bukkit.getOnlinePlayers()) {
            final SocialPlayer socialPlayer = save.getSocialPlayer(p.getName());
            if(socialPlayer.getRace() != null) {
                final Race race = socialPlayer.getRace();
                PermissionAttachment permissionAttachment = null;
                if(p.hasMetadata("pa")) {
                    permissionAttachment = (PermissionAttachment) p.getMetadata("pa").get(0).value();
                }
                if(permissionAttachment == null) {
                    permissionAttachment = p.addAttachment(this);
                }
//
//
                for(final String key : permissionAttachment.getPermissions().keySet()) {
                    permissionAttachment.unsetPermission(key);
                }
                if(p.hasPermission("sc.race.issupernatural")) {
                    socialPlayer.setRace(races.getDefaultRace().getName());
                    races.getDefaultRace().applyRace(p, permissionAttachment);
                    return;
                }
                race.applyRace(p, permissionAttachment);
            }
        }
        getLogger().info("Updated online player races!");
        titleManager = new TitleManager(this);
        getLogger().info("Updated titles!");
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
        titleManager.onDisable();
        titleManager.saveCacheFile();
    }
    
    @SuppressWarnings("unused")
    public void onReload() {
        
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
                    final InputStreamReader isr = new InputStreamReader(in, "UTF8");
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
                    final OutputStreamWriter osw = new OutputStreamWriter(out, "UTF8");
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
