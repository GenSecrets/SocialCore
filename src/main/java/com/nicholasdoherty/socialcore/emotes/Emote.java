package com.nicholasdoherty.socialcore.emotes;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/11/13
 * Time: 23:00
 * To change this template use File | Settings | File Templates.
 */
public class Emote {
	String name,commmand,permission,emoteString,emoteStringTargetted,usage,emoteStringMale,permissionToBypassTargetBlacklist,emoteStringFemale,emoteStringMaleTargetted,emoteStringFemaleTargeted;
	boolean canBetargeted;
	boolean canBeUntargeted;
	List<String> targetBlacklist;
	public Emote(String name, String commmand, String permission, String emoteString, String emoteStringTargetted, String usage, String emoteStringMale, String emoteStringFemale, String emoteStringMaleTargetted, String emoteStringFemaleTargeted, List<String> targetBlacklist, String permissionToBypassTargetBlacklist) {
		this.name = name;
		this.commmand = commmand;
		this.permission = permission;
		this.emoteString = emoteString;
		this.emoteStringTargetted = emoteStringTargetted;
		this.usage = usage;
		this.emoteStringMale = emoteStringMale;
		this.emoteStringFemale = emoteStringFemale;
		this.emoteStringMaleTargetted = emoteStringMaleTargetted;
		this.emoteStringFemaleTargeted = emoteStringFemaleTargeted;
		if (emoteStringTargetted != null || (emoteStringMaleTargetted != null && emoteStringFemaleTargeted !=null))
			canBetargeted = true;
		if (emoteString != null || (emoteStringFemale != null && emoteStringMale != null))
			canBeUntargeted =true;
		this.targetBlacklist = targetBlacklist;
		this.permissionToBypassTargetBlacklist = permissionToBypassTargetBlacklist;
	}

	public Emote(String name, String commmand, String permission, String emoteString, String usage) {
		this.name = name;
		this.commmand = commmand;
		this.permission = permission;
		this.emoteString = emoteString;
		this.usage = usage;
		canBetargeted = false;
		canBeUntargeted = true;
	}

	public boolean isCanBeUntargeted() {
		return canBeUntargeted;
	}

	public boolean isCanBetargeted() {
		return canBetargeted;
	}

	public String getEmoteStringMale() {
		return emoteStringMale;
	}

	public String getEmoteStringFemale() {
		return emoteStringFemale;
	}

	public String getEmoteStringMaleTargetted() {
		return emoteStringMaleTargetted;
	}

	public String getEmoteStringFemaleTargeted() {
		return emoteStringFemaleTargeted;
	}

	public String getName() {
		return name;
	}

	public String getCommmand() {
		return commmand;
	}

	public String getPermission() {
		return permission;
	}

	public String getEmoteString() {
		return emoteString;
	}

	public String getEmoteStringTargetted() {
		return emoteStringTargetted;
	}


	public String getEmoteMessage(Player p){
		SocialPlayer socialPlayer = SocialCore.plugin.save.getSocialPlayer(p.getName());
		SocialCore.Gender gender = socialPlayer.getGender();
		String message = emoteString;
		if (gender == SocialCore.Gender.MALE && emoteStringMale != null)
			message = emoteStringMale;
		else if (gender == SocialCore.Gender.FEMALE && emoteStringFemale != null)
			message = emoteStringFemale;
	    message = ChatColor.translateAlternateColorCodes('&', message);
		message = message.replace("<player>", p.getName());
		for (Replacement replacement : SocialCore.plugin.emotes.getReplacements()) {
			message = message.replace(replacement.getKey(), replacement.getReplacement(gender));
		}
		return message;
	}
	public String getEmoteMessageTargeted(Player p, Player targeted) {
		SocialPlayer sender = SocialCore.plugin.save.getSocialPlayer(p.getName());
		SocialPlayer reciv = SocialCore.plugin.save.getSocialPlayer(targeted.getName());
		SocialCore.Gender gender = SocialCore.Gender.UNSPECIFIED;
		String message = emoteStringTargetted;
		if (!sender.getGender().equals(SocialCore.Gender.UNSPECIFIED))
			gender = sender.getGender();
		else if (!reciv.getGender().equals(SocialCore.Gender.UNSPECIFIED))
			gender = reciv.getGender();
		if (gender == SocialCore.Gender.MALE && emoteStringMaleTargetted != null)
			message = emoteStringMaleTargetted;
		else if (gender == SocialCore.Gender.FEMALE && emoteStringFemaleTargeted != null)
			message = emoteStringFemaleTargeted;
		message = ChatColor.translateAlternateColorCodes('&', message);
		message = message.replace("<player>", p.getName());
		message = message.replace("<target>", targeted.getName());
		for (Replacement replacement : SocialCore.plugin.emotes.getReplacements()) {
			message = message.replace(replacement.getKey(), replacement.getReplacement(gender));
		}
		return message;
	}

	public String getUsage() {
		return usage;
	}
}
