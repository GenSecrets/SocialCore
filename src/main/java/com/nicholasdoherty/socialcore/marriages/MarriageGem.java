package com.nicholasdoherty.socialcore.marriages;

public class MarriageGem {
	
	private int blockID;
	private String name;
	
	public MarriageGem(int blockID, String name) {
		this.blockID = blockID;
		this.name = name;
	}
	
	public int getBlockID() {
		return blockID;
	}
	public String getName() {
		return name;
	}
}
