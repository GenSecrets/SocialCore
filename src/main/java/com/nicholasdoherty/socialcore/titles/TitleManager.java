package com.nicholasdoherty.socialcore.titles;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.libraries.ConfigAccessor;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import com.nicholasdoherty.socialcore.utils.ColoredTagsUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

/**
 * Created by john on 7/2/15.
 */
public class TitleManager {
    private SocialCore plugin;
    private Map<String, Title> titles;
    private long cooldown = 600;
    
    private ConfigAccessor titleCache;
    private Map<UUID, String> playerTitles;
    private Set<UUID> onCooldown = new HashSet<>();
    
    public TitleManager(SocialCore plugin) {
        this.plugin = plugin;
        loadTitles();
        loadCache();
        initTitles();
        new TitleCommand(this);
        new TitleListener(this);
    }
    
    public Map<String, Title> getTitles() {
        return titles;
    }
    
    public SocialCore getPlugin() {
        return plugin;
    }
    
    private void loadCache() {
        titleCache = new ConfigAccessor(plugin, "title-cache.dat");
        titleCache.saveDefaultConfig();
        playerTitles = new HashMap<>();
        FileConfiguration config = titleCache.getConfig();
        for(String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            String titleName = config.getString(key);
            playerTitles.put(uuid, titleName);
        }
    }
    
    public boolean hasTitle(Player p) {
        return playerTitles.containsKey(p.getUniqueId());
    }
    
    public void turnOffTitle(Player p) {
        playerTitles.remove(p.getUniqueId());
        saveIntoCache(p.getUniqueId(), null);
        ColoredTagsUtil.removeTitle(p);
    }
    
    private void saveIntoCache(UUID uuid, String title) {
        titleCache.getConfig().set(uuid.toString(), title);
    }
    
    public void saveCacheFile() {
        titleCache.saveConfig();
    }
    
    public boolean isOncooldown(final UUID uuid) {
        if(onCooldown.contains(uuid)) {
            return true;
        }
        onCooldown.add(uuid);
        new BukkitRunnable() {
            @Override
            public void run() {
                onCooldown.remove(uuid);
            }
        }.runTaskLater(plugin, cooldown);
        return false;
    }
    
    public void setTitle(Player p, Title title) {
        playerTitles.put(p.getUniqueId(), title.getName());
        applyTitle(p, title);
        saveIntoCache(p.getUniqueId(), title.getName());
        saveCacheFile();
    }
    
    public void addTitle(Player p) {
        if(!playerTitles.containsKey(p.getUniqueId())) {
            return;
        }
        String titleName = playerTitles.get(p.getUniqueId());
        if(titleName == null) {
            return;
        }
        if(!p.hasPermission("sc.title." + titleName.toLowerCase())) {
            saveIntoCache(p.getUniqueId(), null);
            return;
        }
        Title title = titles.get(titleName);
        if(title == null) {
            saveIntoCache(p.getUniqueId(), null);
            return;
        }
        applyTitle(p, title);
    }
    
    private void applyTitle(Player p, Title title) {
        ColoredTagsUtil.setTitle(p, title.getPrefix(), title.getSuffix());
    }
    
    public void initTitles() {
        for(Player p : plugin.getServer().getOnlinePlayers()) {
            addTitle(p);
        }
    }
    
    public void onDisable() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            ColoredTagsUtil.removeTitle(p);
        }
    }
    
    public void clearCache() {
        File file = new File(plugin.getDataFolder(), "title-cache.dat");
        if(file.exists()) {
            file.delete();
        }
    }
    
    public void loadTitles() {
        titles = new HashMap<>();
        ConfigAccessor configAccessor = new ConfigAccessor(plugin, "titles-config.yml");
        configAccessor.saveDefaultConfig();
        configAccessor.reloadConfig();
        FileConfiguration config = configAccessor.getConfig();
        if(config.contains("title-cooldown")) {
            cooldown = VoxTimeUnit.getTicks(config.getString("title-cooldown"));
        }
        if(config.contains("titles")) {
            ConfigurationSection titlesSection = config.getConfigurationSection("titles");
            for(String titleName : titlesSection.getKeys(false)) {
                ConfigurationSection titleSection = titlesSection.getConfigurationSection(titleName);
                String prefix = "";
                String suffix = "";
                if(titleSection.contains("prefix")) {
                    prefix = ChatColor.translateAlternateColorCodes('&', titleSection.getString("prefix"));
                }
                if(titleSection.contains("suffix")) {
                    suffix = ChatColor.translateAlternateColorCodes('&', titleSection.getString("suffix"));
                }
                Title title = new Title(titleName, prefix, suffix);
                titles.put(titleName.toLowerCase(), title);
            }
        }
    }
}
