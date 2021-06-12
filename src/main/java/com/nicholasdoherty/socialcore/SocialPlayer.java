package com.nicholasdoherty.socialcore;

public class SocialPlayer {

	private String playerName;
	private String petName;

	//engagement
	private boolean isEngaged;
	private String engagedTo = "";
	//marriage
	private boolean isMarried;
	private String marriedTo = "";
	
	public SocialPlayer(String playerName) {
		this.playerName = playerName;
		isMarried = false;

	}
	
	//setters and getters
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

}
