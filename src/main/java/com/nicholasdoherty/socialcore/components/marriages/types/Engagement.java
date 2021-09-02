package com.nicholasdoherty.socialcore.components.marriages.types;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;

public class Engagement {
	
	public static final String NAME_DELIMITER = "+&";
	
	private SocialPlayer futureSpouse1;
	private SocialPlayer futureSpouse2;
	private String date;
	private long time;
	
	public Engagement(SocialPlayer futureSpouse1, SocialPlayer futureSpouse2) {
		this.futureSpouse1 = futureSpouse1;
		this.futureSpouse2 = futureSpouse2;
	}
	
	public Engagement(String engagementName, SocialCore sc) {
		String data[] = engagementName.split("\\"+NAME_DELIMITER);
		if (data.length > 1) {
			futureSpouse1 = sc.save.getSocialPlayer(data[0]);
			futureSpouse2 = sc.save.getSocialPlayer(data[1]);
		}
		else {
			sc.log.severe("Failed to create engagement from name: "+engagementName);
		}
	}
	
	//congat marriage
	public String getName(){
		return futureSpouse1.getUUID()+NAME_DELIMITER+futureSpouse2.getUUID();
	}
	
	//setters and getters
	public SocialPlayer getFutureSpouse1() {
		return futureSpouse1;
	}
	public void setFutureSpouse1(SocialPlayer futureSpouse1) {
		this.futureSpouse1 = futureSpouse1;
	}
	public SocialPlayer getFutureSpouse2() {
		return futureSpouse2;
	}
	public void setFutureSpouse2(SocialPlayer fWife) {
		this.futureSpouse2 = fWife;
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