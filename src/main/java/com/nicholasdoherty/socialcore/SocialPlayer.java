package com.nicholasdoherty.socialcore;

import com.nicholasdoherty.socialcore.components.genders.Gender;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class SocialPlayer {
	// UUID = MC BUKKIT UUID
	private String uuid;
	private String playerName = "";
	private String petName = "";
	private String marriedTo = "";
	private String engagedTo = "";
	private boolean isMarried;
	private boolean isEngaged;
	private Gender gender;
	
	public SocialPlayer(String uuid) {
		this.uuid = uuid;
		setPlayerName(uuid);
		isEngaged = false;
		isMarried = false;
		this.gender = new Gender("UNSPECIFIED");

	}
	
	// GETTERS
	public String getUUID() {
		return uuid;
	}
	public String getPlayerName() {
		return playerName;
	}
	public String getPetName() {
		return petName;
	}
	public String getMarriedTo() {
		return marriedTo;
	}
	public String getEngagedTo() {
		return engagedTo;
	}
	public boolean isMarried() {
		return isMarried;
	}
	public boolean isEngaged() {
		return isEngaged;
	}
	public Gender getGender() {
		return gender;
	}



	public void setUUID(String uuid) {
		this.uuid = uuid;
	}
	public void setPlayerName(String uuid) {
		OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
		if (p.getName() != null){
			this.playerName = p.getName();
		}
	}
	public void setMarried(boolean s) {
		isMarried = s;
		if (!isMarried) {
			petName = null;
		}
	}
	public void setMarriedTo(String marriedTo) {
		this.marriedTo = marriedTo;
	}
	public void setEngaged(boolean s) {
		isEngaged = s;
	}
	public void setEngagedTo(String engagedTo) {
		this.engagedTo = engagedTo;
	}
	public void setPetName(String petName) {
		this.petName = petName;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
}
