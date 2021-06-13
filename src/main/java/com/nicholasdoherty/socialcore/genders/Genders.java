package com.nicholasdoherty.socialcore.genders;

import java.util.HashMap;

import com.nicholasdoherty.socialcore.SocialCore;

import java.util.HashMap;


public class Genders {
	
	private HashMap<String,SocialCore.Gender> awaitingConfirmation;
	
	public Genders() {
		awaitingConfirmation = new HashMap<String,SocialCore.Gender>();
	}
	
	//awaitingConfirmation
	
	
	//setters and getters
	public HashMap<String,SocialCore.Gender> getAwaitingConfirmation() {
		return awaitingConfirmation;
	}

}
