package com.nicholasdoherty.socialcore.marriages.types;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;

public class Marriage {
	
	public static final String NAME_DELIMITER = "+&";
	
	private SocialPlayer spouse1;
	private SocialPlayer spouse2;
	private String priest;
	private String date;
	
	public Marriage(SocialPlayer spouse1, SocialPlayer spouse2) {
		this.spouse1 = spouse1;
		this.spouse2 = spouse2;
	}
	public Marriage(String marriageName, SocialCore sc) {
		String data[] = marriageName.split("\\"+NAME_DELIMITER);
		if (data.length > 1) {
			spouse1 = sc.save.getSocialPlayer(data[0]);
			spouse2 = sc.save.getSocialPlayer(data[1]);
		}
		else {
			sc.log.severe("Failed to create marriage from name: "+marriageName);
		}
	}
	
	//concatenated marriage
	public String getName(){
		return spouse1.getUUID() + NAME_DELIMITER + spouse2.getUUID();
	}
	
	//setters and getters
	public SocialPlayer getSpouse1() {
		return spouse1;
	}
	public void setSpouse1(SocialPlayer spouse1) {
		this.spouse1 = spouse1;
	}
	public SocialPlayer getSpouse2() {
		return spouse2;
	}
	public void setSpouse2(SocialPlayer spouse2) {
		this.spouse2 = spouse2;
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
