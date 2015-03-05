package com.nicholasdoherty.socialcore.marriages;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;

public class Marriage {
	
	public static final String NAME_DELIMITER = "+&";
	
	private SocialPlayer husband;
	private SocialPlayer wife;
	private String priest;
	private String date;
	
	public Marriage(SocialPlayer husband, SocialPlayer wife) {
		this.husband = husband;
		this.wife = wife;
	}
	public Marriage(String marriageName, SocialCore sc) {
		String data[] = marriageName.split("\\"+NAME_DELIMITER);
		if (data.length > 1) {
			this.husband = sc.save.getSocialPlayer(data[0]);
			this.wife = sc.save.getSocialPlayer(data[1]);
		}
		else {
			sc.log.severe("Failed to create marraige from name: "+marriageName);
		}
	}
	
	//congat marriage
	public String getName(){
		return husband.getPlayerName()+NAME_DELIMITER+wife.getPlayerName();
	}
	
	//setters and getters
	public SocialPlayer getHusband() {
		return husband;
	}
	public void setHusband(SocialPlayer husband) {
		this.husband = husband;
	}
	public SocialPlayer getWife() {
		return wife;
	}
	public void setWife(SocialPlayer wife) {
		this.wife = wife;
	}
	public String getPriest() {
		return priest;
	}
	public void setPriest(String priest) {
		this.priest = priest;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
}
