package com.nicholasdoherty.socialcore.marriages.types;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;

public class Divorce {
	
	public static final String NAME_DELIMITER = "+&";
	
	private SocialPlayer exSpouse1;
	private SocialPlayer exSpouse2;
	private String filedBy;
	private String date;
	
	
	public Divorce(SocialPlayer exSpouse1, SocialPlayer exSpouse2) {
		this.exSpouse1 = exSpouse1;
		this.exSpouse2 = exSpouse2;
	}
	public Divorce(String name, SocialCore sc) {
		try {
			String[] data = name.split("\\"+NAME_DELIMITER);
			if (data.length > 1) {
				if (sc.save.getSocialPlayer(data[0]) != null && sc.save.getSocialPlayer(data[1]) != null){
					this.exSpouse1 = sc.save.getSocialPlayer(data[0]);
					this.exSpouse2 = sc.save.getSocialPlayer(data[1]);
				}
			}
			else {
				throw new Exception();
			}
		} catch (Exception e){
			sc.log.severe("Failed to create divorce from name: "+name);
		}
	}
	
	
	public String getName() {
		return exSpouse1.getUUID()+NAME_DELIMITER+exSpouse2.getUUID();
	}
	
	
	public SocialPlayer getExSpouse1() {
		return exSpouse1;
	}
	public void setExSpouse1(SocialPlayer exSpouse1) {
		this.exSpouse1 = exSpouse1;
	}
	public SocialPlayer getExSpouse2() {
		return exSpouse2;
	}
	public void setExSpouse2(SocialPlayer exSpouse2) {
		this.exSpouse2 = exSpouse2;
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
