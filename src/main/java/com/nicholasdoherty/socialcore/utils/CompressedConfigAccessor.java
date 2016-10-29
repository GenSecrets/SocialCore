package com.nicholasdoherty.socialcore.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class CompressedConfigAccessor {

    private final String fileName;
    private final JavaPlugin plugin;

    private File configFile;
    private CompressedFileConfiguration fileConfiguration;

    private String id;

    public CompressedConfigAccessor(JavaPlugin plugin, String fileName, String id) {
        this.id = id;
        if (plugin == null)
            throw new IllegalArgumentException("plugin cannot be null");
        if (!plugin.isInitialized())
            throw new IllegalArgumentException("plugin must be initiaized");
        this.plugin = plugin;
        this.fileName = fileName;
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null)
            throw new IllegalStateException();
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }

    public void reloadConfig() throws IOException, InvalidConfigurationException {
        fileConfiguration = CompressedFileConfiguration.loadConfiguration(configFile,id);
        // Look for defaults in the jar
        if (!configFile.exists()) {
            InputStream defConfigStream = plugin.getResource(fileName);
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                fileConfiguration.setDefaults(defConfig);
            }
        }
    }

    public FileConfiguration getConfig() throws IOException, InvalidConfigurationException {
        if (fileConfiguration == null) {
            this.reloadConfig();
        }
        return fileConfiguration;
    }

    public void saveConfig() throws InvalidConfigurationException {
        if (fileConfiguration == null || configFile == null) {
            return;
        } else {
            try {
                getConfig().save(configFile);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
            }
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            this.plugin.saveResource(fileName, false);
        }
    }

}