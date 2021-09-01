package com.nicholasdoherty.socialcore.genders;

import java.util.ArrayList;
import java.util.HashMap;

import com.nicholasdoherty.socialcore.SocialCore;
import java.util.List;

public class Genders {
	
	private final HashMap<String,Gender> awaitingConfirmation;
	private final HashMap<String, Integer> genderCountCache;
	private final ArrayList<Gender> genders;
	private final SocialCore sc;

	public Genders(SocialCore plugin) {
		sc = plugin;
		awaitingConfirmation = new HashMap<>();
		genderCountCache = new HashMap<>();
		genders = new ArrayList<>();
		setupGenders();
		loadGenderCache();
	}

	public void setupGenders(){
		List<String> configGenders = sc.getGendersConfig().getStringList("genders");

		for(String configGender: configGenders) {
			final Gender gender = new Gender(configGender);
			genders.add(gender);
		}
	}

	public void loadGenderCache() {
		sc.getLogger().info("[SC Handler] Counting cache for genders...");
		for (Gender gender : getGenders()) {
			if (!gender.getName().equalsIgnoreCase("UNSPECIFIED")) {
				genderCountCache.put(gender.getName(), sc.save.getCountGender(gender.getName().toUpperCase()));
			} else {
				int count = sc.save.getCountGender("null") + sc.save.getCountGender("UNSPECIFIED");
				genderCountCache.put(gender.getName(), count);
			}
		}
	}

	public HashMap<String,Gender> getAwaitingConfirmation() {
		return awaitingConfirmation;
	}
	public ArrayList<Gender> getGenders() { return genders; }
	public HashMap<String, Integer> getGenderCache() { return genderCountCache; }
	public ArrayList<String> getGenderNames() {
		ArrayList<String> genderNames = new ArrayList<>();
		for(Gender gender : genders) {
			genderNames.add(gender.getName());
		}
		return genderNames;
	}

	public Gender getGender(String genderName){
		Gender returnGender = null;
		for(Gender gender : genders){
			if(gender.getName().equalsIgnoreCase(genderName)){
				returnGender = gender;
			}
		}
		return returnGender;
	}
}