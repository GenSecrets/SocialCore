package com.nicholasdoherty.socialcore;

import com.nicholasdoherty.socialcore.courts.CourtTeleportationHandler;
import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inputlib.InputLib;
import com.nicholasdoherty.socialcore.genders.GenderCommandHandler;
import com.nicholasdoherty.socialcore.genders.Genders;
import com.nicholasdoherty.socialcore.marriages.*;
import com.nicholasdoherty.socialcore.store.SQLStore;
import com.nicholasdoherty.socialcore.time.Clock;
import com.nicholasdoherty.socialcore.time.condition.TimeConditionManager;
import com.nicholasdoherty.socialcore.utils.ColorUtil;
import com.nicholasdoherty.socialcore.utils.VaultUtil;
import com.nicholasdoherty.socialcore.welcomer.WelcomeCommandHandler;
import com.voxmc.voxlib.gui.InventoryGUIManager;
import com.earth2me.essentials.api.Economy;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import co.aikar.commands.PaperCommandManager;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class SocialCore extends JavaPlugin {

    // PLUGIN BASE
    public static SocialCore plugin;
    private static PaperCommandManager manager;

    // Logger
    public final Logger log = Logger.getLogger("Minecraft");
    // Utility
    public SQLStore store;
    public SaveHandler save;
    private InputLib inputLib;
    private InventoryGUIManager inventoryGUIManager;
    private TimeConditionManager timeConditionManager;
    private boolean isClockEnabled;
    private boolean isVaultEnabled;
    // players
    public Map<String, SocialPlayer> socialPlayersCache;
    // Components
    public Genders genders;
    private Courts courts;
    public Marriages marriages;
    public List<String> whitelistPiggybackWorlds;
    public String welcomerLastJoined;

    // Plugin settings
    public String prefix;
    public String defaultAlias;
    public ChatColor errorColor;
    public ChatColor successColor;
    public ChatColor messageColor;
    public ChatColor commandColor;

    // Configs
    public SCConfigHandler configs;
    public boolean isCourtsEnabled = true;
    public boolean isGendersEnabled = true;
    public boolean isMarriagesEnabled = true;
    public boolean isWelcomerEnabled = true;
    public FileConfiguration courtsConfig;
    public FileConfiguration courtsLangConfig;
    public FileConfiguration marriagesConfig;
    public FileConfiguration gendersConfig;
    public FileConfiguration welcomerConfig;
    public MarriageConfig marriageConfig;

    // GETTERS
    public InventoryGUIManager getInventoryGUIManager() {
        return inventoryGUIManager;
    }
    public InputLib getInputLib() {
        return inputLib;
    }
    public Courts getCourts() {
        return courts;
    }
    public FileConfiguration getCourtsConfig() { return this.courtsConfig; }
    public FileConfiguration getCourtsLangConfig() { return this.courtsLangConfig; }
    public FileConfiguration getMarriagesConfig() { return this.marriagesConfig; }
    public FileConfiguration getGendersConfig() { return this.gendersConfig; }
    public FileConfiguration getWelcomerConfig() { return this.welcomerConfig; }
    public static PaperCommandManager getCommandManager() { return manager; }
    public static SocialCore getPlugin() { return plugin; }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEnable() {
        getLogger().info("[START] Initializing SocialCore...");

        plugin = this;
        manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("help");
        try {
            Clock.start(plugin);
            isClockEnabled = true;
        } catch (Exception e) {
            isClockEnabled = false;
            log.severe("Unable to initiate plugin clock, disabling SocialCore...");
        }
        timeConditionManager = new TimeConditionManager();
        inventoryGUIManager = new InventoryGUIManager(this);
        inputLib = new InputLib(this);
        socialPlayersCache = new HashMap<>();
        final String directory = getDataFolder().toString();


        // Utility Handlers & Configs
        getLogger().info("Checking for plugin's default configs...");
        setupPluginSettings();
        configs = new SCConfigHandler(this);
        getLogger().info("Creating handlers for components and commands...");
        save = new SaveHandler(directory, this);
        store = new SQLStore();
        getLogger().info("[SC Handler] Created Save and MySQL handlers");
        if(isClockEnabled)
            getLogger().info("[SC Handler] Clock started!");

        final CommandExecutor scCommandHandler = new SCCommandHandler(this);
        getCommand("socialcore").setExecutor(scCommandHandler);
        getCommand("profile").setExecutor(scCommandHandler);

        // SETUP COMPONENTS
        setupCourt();
        setupMarriages();
        setupGenders();
        setupWelcomer();
        getLogger().info("Finished creating handlers!");
        getLogger().info("Finished setting up all components and commands!");

        // SETUP EVENTS
        getServer().getPluginManager().registerEvents(new SCListener(this), this);
        getServer().getPluginManager().registerEvents(new CourtTeleportationHandler(this), this);
        getLogger().info("Finished registering all listeners!");
        getLogger().info("[DONE] SocialCore has finished starting!");
    }
    
    public TimeConditionManager getTimeConditionManager() {
        return timeConditionManager;
    }
    
    @Override
    public void onDisable() {
        if(isClockEnabled){
            Clock.save();
        }
        if(isCourtsEnabled){
            courts.onDisable();
        }
        SCListener.riding.keySet().stream().filter(p -> p != null).forEach(p -> {
            final Player player = Bukkit.getPlayer(p);
            try {
                player.leaveVehicle();
            } catch(final Exception ignored) {
            }
        });
    }

    private void setupPluginSettings(){
        defaultAlias = getConfig().getString("default-alias");
        prefix = ChatColor.translateAlternateColorCodes('&',getConfig().getString("plugin-prefix")) + ChatColor.RESET;
        errorColor = ColorUtil.convertToChatColor(getConfig().getString("colors.error"));
        successColor = ColorUtil.convertToChatColor(getConfig().getString("colors.success"));
        messageColor = ColorUtil.convertToChatColor(getConfig().getString("colors.messages"));
        commandColor = ColorUtil.convertToChatColor(getConfig().getString("colors.commands"));
    }

    private void setupCourt(){
        if(getConfig().getBoolean("components.enable-courts")){
            configs.setupCourtsConfig();
            courts = new Courts(this);
            getLogger().info("[SC Handler] Created courts handler");
            isCourtsEnabled = true;
        } else {
            isCourtsEnabled = false;
        }
    }

    private void setupMarriages(){
        if(getConfig().getBoolean("components.enable-marriages")){
            configs.setupMarriagesConfig();
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
            //getCommand("petname").setExecutor(new PetnameCommand());
            getCommand("purgeinvalids").setExecutor(new PurgeInvalidCommand());
            getLogger().info("[SC Handler] Created marriages handler");
            marriageConfig = new MarriageConfig(this);
            marriageConfig.loadConfig();
            isMarriagesEnabled = true;
        } else {
            isMarriagesEnabled = false;
        }
    }

    private void setupGenders(){
        if(getConfig().getBoolean("components.enable-genders")){
            configs.setupGendersConfig();
            genders = new Genders(this);
            manager.registerCommand(new GenderCommandHandler(this, genders));
            manager.getCommandCompletions().registerAsyncCompletion("genderNames", n -> {
                return genders.getGenderNames();
            });
            getLogger().info("[SC Handler] Created genders handler");
            isGendersEnabled = true;
        } else {
            isGendersEnabled = false;
        }
    }

    private void setupWelcomer(){
        if(getConfig().getBoolean("components.enable-welcomer")){
            configs.setupWelcomerConfig();
            manager.registerCommand(new WelcomeCommandHandler(this));
            getLogger().info("[SC Handler] Created welcomer handler");
            isWelcomerEnabled = true;
        } else {
            isWelcomerEnabled = false;
        }
    }

    public static Logger getStaticLogger(){ return Logger.getLogger("Minecraft"); }
}
