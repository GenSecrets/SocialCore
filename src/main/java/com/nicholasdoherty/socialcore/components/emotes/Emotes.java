package com.nicholasdoherty.socialcore.components.emotes;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.libraries.ConfigAccessor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/11/13
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public class Emotes {
	Map<String, Emote> emoteMap = new HashMap<String, Emote>();
	SocialCore plugin;
	List<ConfigAccessor> configs;
	int radius = 0;
	String baseEmoteCommandString;
	int cooldown = 0;
	boolean targetMustBeInRange = true;
	List<Replacement> replacements;
	public boolean isEnabled = false;

	public Emotes(SocialCore plugin) {
		this.plugin = plugin;
		configs = new ArrayList<>();
		replacements = new ArrayList<>();
		isEnabled = loadEmotes();
	}

	public int getRadius() {
		return radius;
	}

	public List<Replacement> getReplacements() {
		return replacements;
	}

	public Map<String, Emote> getEmoteMap() {
		return emoteMap;
	}
	public Collection<Emote> getEmotes() {
		return emoteMap.values();
	}
	public SocialCore getPlugin() {
		return plugin;
	}

	public String getBaseEmoteCommandString() {
		return baseEmoteCommandString;
	}

	public void setConfigs(ArrayList<ConfigAccessor> configs) {
		this.configs = configs;
	}


	public void setupEmotesConfig(){
		File emotesFolder = new File(plugin.getDataFolder().getPath() + File.separator + "emotes");
		if (!emotesFolder.exists()){
			emotesFolder.mkdir();
		}

		File[] emotesFiles = emotesFolder.listFiles();
		ArrayList<ConfigAccessor> emotesConfigs = new ArrayList<>();
		for(File f : emotesFiles){
			ConfigAccessor config = new ConfigAccessor(plugin, "emotes" + File.separator + f.getName());
			emotesConfigs.add(config);
		}

		if (emotesFiles.length == 0) {
			emotesFolder.getParentFile().mkdirs();
			plugin.saveResource(emotesFolder.getName() + File.separator + "config.yml", false);
			plugin.saveResource(emotesFolder.getName() + File.separator + "emotes.yml", false);
		}

		this.configs = emotesConfigs;
	}

	public boolean loadEmotes() {
		setupEmotesConfig();

		ConfigAccessor mainConfigAccessor = new ConfigAccessor(plugin, "emotes" + File.separator + "config.yml");
		FileConfiguration mainConfig = mainConfigAccessor.getConfig();
		ConfigurationSection lang = mainConfig.getConfigurationSection("lang");
		ConfigurationSection genderReplacementsRoot = mainConfig.getConfigurationSection("gender-replacements");
		emoteMap = new HashMap<String, Emote>();
		replacements = new ArrayList<Replacement>();
		if (genderReplacementsRoot == null) {
			plugin.getLogger().warning("Error while attempting to load emotes config.yml - no 'gender-replacements' section. Aborting loading emotes.");
			plugin.isEmotesEnabled = false;
			return false;
		}
		if (lang == null) {
			plugin.getLogger().warning("Error while attempting to load emotes config.yml - no 'lang' section. Aborting loading emotes.");
			plugin.isEmotesEnabled = false;
			return false;
		}

		for (String path : genderReplacementsRoot.getKeys(false)) {
			if (path == null) {
				plugin.getLogger().warning("Error while attempting to load emotes file. Could not find proper 'gender-replacements' section. File: " + mainConfigAccessor.getFile().getName());
				continue;
			}

			ConfigurationSection genderSection = genderReplacementsRoot.getConfigurationSection(path);
			if (genderSection == null) {
				plugin.getLogger().warning("Error while attempting to load '" + path + "' from the 'gender-replacements' section of file: " + mainConfigAccessor.getFile().getName());
				continue;
			}

			String defaultReplacement = null;
			if (genderSection.contains("default")) {
				defaultReplacement = genderSection.getString("default");
			}
			String male = null;
			if (genderSection.contains("male"))
				male = genderSection.getString("male");
			String female = null;
			if (genderSection.contains("female"))
				female = genderSection.getString("female");
			Replacement replacement = new Replacement(path, male, female, defaultReplacement);
			replacements.add(replacement);
		}

		cooldown = mainConfig.getInt("cooldown");
		radius = mainConfig.getInt("radius");
		if (lang != null && lang.contains("emote-command-base-string"))
			baseEmoteCommandString = lang.getString("emote-command-base-string");
		if (mainConfig.contains("target-must-be-in-range"))
			targetMustBeInRange = mainConfig.getBoolean("target-must-be-in-range");


		////////////////////////////////////////////////
		//
		//       HANDLE THE EMOTES
		//
		////////////////////////////////////////////////
		for(ConfigAccessor configAccessor : configs) {
			if(configAccessor.getConfig().getKeys(false).contains("gender-replacements") &&
					configAccessor.getConfig().getKeys(false).contains("lang") &&
					configAccessor.getConfig().getKeys(false).contains("radius") &&
					configAccessor.getConfig().getKeys(false).contains("cooldown") &&
					configAccessor.getConfig().getKeys(false).contains("target-must-be-in-range"))
				continue;

			FileConfiguration fileConfiguration = configAccessor.getConfig();
			ConfigurationSection emotesRoot = fileConfiguration.getConfigurationSection("emotes");

			// Check if contains "emotes" section in config
			if (emotesRoot == null) {
				plugin.getLogger().warning("Error while attempting to load emotes file. Could not find a proper 'emotes' section. File: " + configAccessor.getFile().getName());
				continue;
			}

			for (String emoteName : emotesRoot.getKeys(false)) {
				ConfigurationSection emoteSection = emotesRoot.getConfigurationSection(emoteName);

				if (emoteSection == null) {
					plugin.getLogger().warning("Error while attempting to load the emote '" + emoteName + "' from the file: " + configAccessor.getFile().getName());
					continue;
				}

				// COMMAND, PERMS, USAGE
				String command = "";
				if (emoteSection.contains("command"))
					command = emoteSection.getString("command").toLowerCase();
				String permission = "";
				if (emoteSection.contains("permission"))
					permission = emoteSection.getString("permission");
				String usage = "";
				if (emoteSection.contains("usage"))
					usage = emoteSection.getString("usage");
				usage = ChatColor.translateAlternateColorCodes('&', usage);

				// EMOTE TEXTS
				String emoteString = null;
				if (emoteSection.contains("emote-text"))
					emoteString = emoteSection.getString("emote-text");
				String emoteStringMale = null;
				if (emoteSection.contains("emote-text-male"))
					emoteStringMale = emoteSection.getString("emote-text-male");
				String emoteStringFemale = null;
				if (emoteSection.contains("emote-text-female"))
					emoteStringFemale = emoteSection.getString("emote-text-female");
				String emoteStringTargeted = null;
				if (emoteSection.contains("emote-text-targeted"))
					emoteStringTargeted = emoteSection.getString("emote-text-targeted");
				String emoteStringMaleTargeted = null;
				if (emoteSection.contains("emote-text-male-targeted"))
					emoteStringMaleTargeted = emoteSection.getString("emote-text-male-targeted");
				String emoteStringFemaleTargeted = null;
				if (emoteSection.contains("emote-text-female-targeted"))
					emoteStringFemaleTargeted = emoteSection.getString("emote-text-female-targeted");

				// BLACKLIST SETTINGS
				List<String> targetBlacklist = new ArrayList<>();
				if (emoteSection.contains("target-blacklist")) {
					targetBlacklist = emoteSection.getStringList("target-blacklist");
				}
				String permissionToBypassTargetBlacklist = null;
				if (emoteSection.contains("permission-to-bypass-target-blacklist"))
					permissionToBypassTargetBlacklist = emoteSection.getString("permission-to-bypass-target-blacklist");

				// CREATE THE EMOTE
				Emote emote = new Emote(emoteName, command, permission, emoteString, emoteStringTargeted, usage, emoteStringMale, emoteStringFemale, emoteStringMaleTargeted, emoteStringFemaleTargeted, targetBlacklist, permissionToBypassTargetBlacklist);
				emoteMap.put(command, emote);
				String commandParsed = command.replace("/", "");

				// REGISTER THE EMOTE
				SocialCore.manager.getCommandReplacements().addReplacement("emote", commandParsed);
				SocialCore.manager.registerCommand(emote);
			}
		}
		return true;
	}
	public void reloadEmotes() {
		ConfigAccessor main = new ConfigAccessor(plugin, "emotes" + File.separator + "config.yml");
		main.reloadConfig();
		for(ConfigAccessor config : configs){
			config.reloadConfig();
		}
		loadEmotes();
	}
	public Emote getEmote(String command) {
		command = command.toLowerCase();
		if (emoteMap.containsKey(command))
			return emoteMap.get(command);
		return null;
	}
}
