package com.nicholasdoherty.socialcore.marriages;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;

public class Engagement {
	
	public static final String NAME_DELIMITER = "+&";
	
	private SocialPlayer fHusband;
	private SocialPlayer fWife;
	private String date;
	private long time;
	
	public Engagement(SocialPlayer fHusband, SocialPlayer fWife) {
		this.fHusband = fHusband;
		this.fWife = fWife;
	}
	
	public Engagement(String engagementName, SocialCore sc) {
		String data[] = engagementName.split("\\"+NAME_DELIMITER);
		if (data.length > 1) {
			this.fHusband = sc.save.getSocialPlayer(data[0]);
			this.fWife = sc.save.getSocialPlayer(data[1]);
		}
		else {
			sc.log.severe("Failed to create engagement from name: "+engagementName);
		}
	}
	
	//congat marriage
	public String getName(){
		return fHusband.getPlayerName()+NAME_DELIMITER+fWife.getPlayerName();
	}
	
	//setters and getters
	public SocialPlayer getFHusband() {
		return fHusband;
	}
	public void setFHusband(SocialPlayer fHusband) {
		this.fHusband = fHusband;
	}
	public SocialPlayer getFWife() {
		return fWife;
	}
	public void setFWife(SocialPlayer fWife) {
		this.fWife = fWife;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String s) {
		this.date = s;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long t) {
		time = t;
	}

}