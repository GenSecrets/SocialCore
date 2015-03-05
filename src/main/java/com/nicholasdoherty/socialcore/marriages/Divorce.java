package com.nicholasdoherty.socialcore.marriages;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;

public class Divorce {
	
	public static final String NAME_DELIMITER = "+&";
	
	private SocialPlayer exHusband;
	private SocialPlayer exWife;
	private String filedBy;
	private String date;
	
	
	public Divorce(SocialPlayer exHusband, SocialPlayer exWife) {
		this.exHusband = exHusband;
		this.exWife = exWife;
	}
	public Divorce(String name, SocialCore sc) {
		String data[] = name.split("\\"+NAME_DELIMITER);
		if (data.length > 1) {
			this.exHusband = sc.save.getSocialPlayer(data[0]);
			this.exWife = sc.save.getSocialPlayer(data[1]);
		}
		else {
			sc.log.severe("Failed to create engagement from name: "+name);
		}
	}
	
	
	public String getName() {
		return exHusband.getPlayerName()+NAME_DELIMITER+exWife.getPlayerName();
	}
	
	
	public SocialPlayer getExhusband() {
		return exHusband;
	}
	public void setExhusband(SocialPlayer exHusband) {
		this.exHusband = exHusband;
	}
	public SocialPlayer getExwife() {
		return exWife;
	}
	public void setExWife(SocialPlayer exWife) {
		this.exWife = exWife;
	}
	public String getDate(){
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getFiledBy(){
		return filedBy;
	}
	public void setFiledBy(String filedBy) {
		this.filedBy = filedBy;
	}

}
