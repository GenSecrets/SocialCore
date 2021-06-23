package com.nicholasdoherty.socialcore;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SCConfigHandler {
    private static SocialCore sc;
    public SCConfigHandler(SocialCore plugin){
        sc = plugin;
        checkConfig();
    }

    public void setupCourtsConfig(){
        File courtsFolder = new File(sc.getDataFolder().getPath() + File.separator + "courts");
        if (!courtsFolder.exists()){
            courtsFolder.mkdir();
        }

        File courtsConfigFile = new File(courtsFolder.getPath(), "config.yml");
        File courtsLangConfigFile = new File(courtsFolder.getPath(), "lang.yml");

        if (!courtsConfigFile.exists()) {
            courtsConfigFile.getParentFile().mkdirs();
            sc.saveResource(courtsFolder.getName()+File.separator+"config.yml", false);
        }
        if (!courtsLangConfigFile.exists()) {
            courtsLangConfigFile.getParentFile().mkdirs();
            sc.saveResource(courtsFolder.getName()+File.separator+"lang.yml", false);
        }

        sc.courtsConfig= new YamlConfiguration();
        sc.courtsLangConfig= new YamlConfiguration();

        try {
            sc.courtsConfig.load(courtsConfigFile);
            sc.courtsLangConfig.load(courtsLangConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void setupMarriagesConfig(){
        File marriagesFolder = new File(sc.getDataFolder().getPath() + File.separator + "marriages");
        if (!marriagesFolder.exists()){
            marriagesFolder.mkdir();
        }

        File marriagesConfigFile = new File(marriagesFolder.getPath(), "config.yml");
        if (!marriagesConfigFile.exists()) {
            marriagesConfigFile.getParentFile().mkdirs();
            sc.saveResource(marriagesFolder.getName()+File.separator+"config.yml", false);
        }

        sc.marriagesConfig= new YamlConfiguration();

        try {
            sc.marriagesConfig.load(marriagesConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void setupGendersConfig(){
        File gendersFolder = new File(sc.getDataFolder().getPath() + File.separator + "genders");
        if (!gendersFolder.exists()){
            gendersFolder.mkdir();
        }

        File gendersConfigFile = new File(gendersFolder.getPath(), "config.yml");
        if (!gendersConfigFile.exists()) {
            gendersConfigFile.getParentFile().mkdirs();
            sc.saveResource(gendersFolder.getName()+File.separator+"config.yml", false);
        }

        sc.gendersConfig= new YamlConfiguration();

        try {
            sc.gendersConfig.load(gendersConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void setupWelcomerConfig(){
        File welcomerConfigFile = new File(sc.getDataFolder(), "welcomer.yml");
        if (!welcomerConfigFile.exists()) {
            welcomerConfigFile.getParentFile().mkdirs();
            sc.saveResource("welcomer.yml", false);
        }

        sc.welcomerConfig= new YamlConfiguration();

        try {
            sc.welcomerConfig.load(welcomerConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void checkConfig() {
        if(!sc.getDataFolder().isDirectory()) {
            sc.getLogger().info("Data folder was not found, making one...");
            sc.getDataFolder().mkdirs();
            sc.log.info("Finished creating data folder!");
        }
        if(!new File(sc.getDataFolder(), "config.yml").isFile()) {
            sc.getLogger().info("Main config.yml file not found, making one...");
            if(writeDefaultFileFromJar(new File(sc.getDataFolder(), "config.yml"), "config.yml", true)) {
                sc.log.info("Finished creating config.yml!");
            }
        }
    }

    public boolean writeDefaultFileFromJar(final File writeName, final String jarPath, final boolean backupOld) {
        try {
            final File fileBackup = new File(sc.getDataFolder(), "backup-" + writeName);
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
            sc.log.warning("[SocialCore] Failed to write default config. Stack trace follows:");
            ex.printStackTrace();
            return false;
        }
    }
}
