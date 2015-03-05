package com.nicholasdoherty.socialcore.emotes;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.libraries.ConfigAccessor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

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
	ConfigAccessor configAccessor;
	int radius = 0;
	String baseEmoteCommandString;
	int cooldown = 0;
	boolean targetMustBeInRange = true;
	List<Replacement> replacements = new ArrayList<Replacement>();
	public Emotes(SocialCore plugin) {
		this.plugin = plugin;
		configAccessor = new ConfigAccessor(plugin, "emotes.yml");
		configAccessor.saveDefaultConfig();
		loadEmotes();
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

	public ConfigAccessor getConfigAccessor() {
		return configAccessor;
	}

	public String getBaseEmoteCommandString() {
		return baseEmoteCommandString;
	}

	public void loadEmotes() {
		FileConfiguration fileConfiguration = configAccessor.getConfig();
		ConfigurationSection emotesRoot = fileConfiguration.getConfigurationSection("emotes");
		radius = fileConfiguration.getInt("radius");
		ConfigurationSection lang = fileConfiguration.getConfigurationSection("lang");
		baseEmoteCommandString = lang.getString("emote-command-base-string");
		emoteMap = new HashMap<String, Emote>();
		replacements = new ArrayList<Replacement>();
		cooldown = fileConfiguration.getInt("cooldown");
		if (fileConfiguration.contains("target-must-be-in-range"))
			targetMustBeInRange = fileConfiguration.getBoolean("target-must-be-in-range");
		for (String path : fileConfiguration.getConfigurationSection("gender-replacements").getKeys(false)) {
			ConfigurationSection genderSection = fileConfiguration.getConfigurationSection("gender-replacements").getConfigurationSection(path);
			String key = path;
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
			Replacement replacement = new Replacement(key, male, female, defaultReplacement);
			replacements.add(replacement);
		}
		for (String path : emotesRoot.getKeys(false)) {
			ConfigurationSection emoteSection = emotesRoot.getConfigurationSection(path);
			String name = path;
			String command = emoteSection.getString("command").toLowerCase();
			String permission = null;
			if (emoteSection.contains("permission")) {
				permission = emoteSection.getString("permission");
			}
			String emoteString = null;
			if (emoteSection.contains("emote-text"))
				emoteString = emoteSection.getString("emote-text");
			String emoteStringTargetted = null;
			if (emoteSection.contains("emote-text-targeted"))
				emoteStringTargetted = emoteSection.getString("emote-text-targeted");
			String emoteStringMale = null;
			if (emoteSection.contains("emote-text-male")) {
				emoteStringMale = emoteSection.getString("emote-text-male");
			}
			String emoteStringFemale = null;
			if (emoteSection.contains("emote-text-female")) {
				emoteStringFemale = emoteSection.getString("emote-text-female");
			}
			String emoteStringFemaleTargeted = null;
			if (emoteSection.contains("emote-text-female-targeted")) {
				emoteStringFemaleTargeted = emoteSection.getString("emote-text-female-targeted");
			}
			String emoteStringMaleTargeted = null;
			if (emoteSection.contains("emote-text-male-targeted")) {
				emoteStringMaleTargeted = emoteSection.getString("emote-text-male-targeted");
			}
			String usage = emoteSection.getString("usage");
			usage = ChatColor.translateAlternateColorCodes('&', usage);
			List<String> targetBlacklist = new ArrayList<String>();
			if (emoteSection.contains("target-blacklist")) {
				targetBlacklist = emoteSection.getStringList("target-blacklist");
			}
			String permissionToBypassTargetBlacklist = null;
			if (emoteSection.contains("permission-to-bypass-target-blacklist"))
				permissionToBypassTargetBlacklist = emoteSection.getString("permission-to-bypass-target-blacklist");
			Emote emote = new Emote(name, command, permission, emoteString, emoteStringTargetted, usage, emoteStringMale, emoteStringFemale, emoteStringMaleTargeted, emoteStringFemaleTargeted, targetBlacklist, permissionToBypassTargetBlacklist);
			emoteMap.put(command,emote);
		}
	}
	public void reloadEmotes() {
		configAccessor.reloadConfig();
		loadEmotes();
	}
	public Emote getEmote(String command) {
		command = command.toLowerCase();
		if (emoteMap.containsKey(command))
			return emoteMap.get(command);
		return null;
	}
}
