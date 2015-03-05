package com.nicholasdoherty.socialcore.races;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.libraries.ConfigAccessor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/14/13
 * Time: 1:03
 * To change this template use File | Settings | File Templates.
 */
public class Races {
	Map<String, Race> races = new HashMap<String, Race>();
	SocialCore plugin;
	ConfigAccessor racesConfig;
	int cooldown = 10;
	String defaultRace = null;
	Map<String, Map<String, String>> commandStrings = new HashMap<String, Map<String, String>>();
	List<Race> racesList = new ArrayList<Race>();
	public Races(SocialCore plugin) {
		this.plugin = plugin;
		loadRaces();
		new RaceCommand(plugin);
	}
	public void loadRaces() {
		if (racesConfig == null) {
			racesConfig = new ConfigAccessor(plugin, "races.yml");
			racesConfig.saveDefaultConfig();
		}
		FileConfiguration fileConfiguration = racesConfig.getConfig();
		races = new HashMap<String, Race>();
		racesList = new ArrayList<Race>();
		cooldown = racesConfig.getConfig().getInt("cooldown");
		if (fileConfiguration.contains("default-race"))
			defaultRace = fileConfiguration.getString("default-race").toLowerCase();
		for (String path : fileConfiguration.getConfigurationSection("races").getKeys(false)) {
			ConfigurationSection section = fileConfiguration.getConfigurationSection("races").getConfigurationSection(path);
			String name = path.toLowerCase();
			String info = section.getString("short-info");
			if (info != null)
				info = ChatColor.translateAlternateColorCodes('&', info);
			String longInfo = section.getString("long-info");
			if (longInfo != null)
				longInfo = ChatColor.translateAlternateColorCodes('&', longInfo);
			String permissionRequired = "";
			if (section.contains("permission-required"))
				permissionRequired = section.getString("permission-required");
			Map<String,Boolean> permissionsGiven = new HashMap<String, Boolean>();
			String loreName = name;
			if (section.contains("lore-name"))
				loreName = section.getString("lore-name");
			if (section.contains("permissions-given")) {
				for (String permission : section.getStringList("permissions-given")) {
					permissionsGiven.put(permission, true);
				}
			}
			if (section.contains("permissions-given-no-override")) {
				for (String permission : section.getStringList("permissions-given-no-override")) {
					permissionsGiven.put(permission, false);
				}
			}
			//System.out.println(name + " " + permissionsGiven);
			String joinInfo = null;
			if (section.contains("join-info")) {
				joinInfo = section.getString("join-info");
				joinInfo = ChatColor.translateAlternateColorCodes('&', joinInfo);
			}
			Race race = new Race(name,loreName,info,longInfo,permissionRequired,permissionsGiven, joinInfo);
			races.put(name, race);
			racesList.add(race);
		}
		commandStrings = new HashMap<String, Map<String, String>>();
		for (String path : fileConfiguration.getConfigurationSection("lang").getConfigurationSection("commands").getConfigurationSection("race").getKeys(false)) {
			ConfigurationSection section = fileConfiguration.getConfigurationSection("lang.commands.race").getConfigurationSection(path);
			Map<String, String> strings = new HashMap<String, String>();
			for (String key : section.getKeys(false)) {
				strings.put(key, section.getString(key));
			}
			commandStrings.put(path, strings);
		}
	}
	public void reloadRaces() {
		racesConfig.reloadConfig();
		loadRaces();
	}
	public Race getRace(String name) {
		if (name == null)
			return races.get(defaultRace);
		name = name.toLowerCase();
		if (name.equalsIgnoreCase("default") && defaultRace != null) {
			if (races.containsKey(defaultRace))
				return races.get(defaultRace);
		}
		if (races.containsKey(name))
			return races.get(name);
		for (Race race : races.values()) {
			if (race.getName().equalsIgnoreCase(name)) {
				return race;
			}
		}
		return races.get(defaultRace);
	}
	public Race getRaceByName(String name) {
		name = name.toLowerCase();
		if (races.containsKey(name))
			return races.get(name);
		return races.get(defaultRace);
	}
	public Race getDefaultRace() {
		return races.get(defaultRace);
	}
	public List<Race> getRaces() {
		return racesList;
	}
}
