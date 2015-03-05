package com.nicholasdoherty.socialcore;

import com.nicholasdoherty.socialcore.races.Race;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionAttachment;

public class SocialPlayer {

	private String playerName;
	private SocialCore.Gender gender;
	private String race;
	private long lastRaceChange = 0;
	private String petName;

	public Race getRace() {
		return SocialCore.plugin.races.getRace(race);
	}
	public String getRaceString() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public long getLastRaceChange() {
		return lastRaceChange;
	}

	public void setLastRaceChange(long lastRaceChange) {
		this.lastRaceChange = lastRaceChange;
	}

	//engagement
	private boolean isEngaged;
	private String engagedTo = "";
	//marriage
	private boolean isMarried;
	private String marriedTo = "";
	
	public SocialPlayer(String playerName) {
		this.playerName = playerName;
		gender = SocialCore.Gender.UNSPECIFIED;
		isMarried = false;
	}
	
	//setters and getters
	public SocialCore.Gender getGender() {
		return gender;
	}
	public void setGender(SocialCore.Gender gender) {
		this.gender = gender;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public boolean isMarried() {
		return isMarried;
	}
	public void setMarried(boolean s) {
		isMarried = s;
		if (!isMarried) {
			petName = null;
		}
	}
	public String getMarriedTo() {
		return marriedTo;
	}
	public void setMarriedTo(String marriedTo) {
		this.marriedTo = marriedTo;
	}
	public boolean isEngaged() {
		return isEngaged;
	}
	public void setEngaged(boolean s) {
		isEngaged = s;
	}
	public String getEngagedTo() {
		return engagedTo;
	}
	public void setEngagedTo(String engagedTo) {
		this.engagedTo = engagedTo;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public void applyRace() {
		if (race != null && !race.equals("")) {
			Race race1 = SocialCore.plugin.races.getRace(race);
			if (race1 != null) {
				race1.applyRace(Bukkit.getPlayer(playerName));
			}
		}
	}
	public void applyRace(PermissionAttachment pa) {
		if (race != null && !race.equals("")) {
			Race race1 = SocialCore.plugin.races.getRace(race);
			if (race1 != null) {
				race1.applyRace(Bukkit.getPlayer(playerName),pa);
			}
		}
	}
}
