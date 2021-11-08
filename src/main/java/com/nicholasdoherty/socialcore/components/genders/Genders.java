package com.nicholasdoherty.socialcore.components.genders;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
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
		Bukkit.getScheduler().runTaskAsynchronously(sc, () -> {
			HashMap<String, Integer> genderCounts = sc.save.getGenderCounts(getGenderNames());
			for (Gender gender : getGenders()) {
				genderCountCache.put(gender.getName(), genderCounts.get(gender.getName().toUpperCase()));
			}
		});
	}

	public void adjustGenderCache(SocialPlayer sp, boolean increase) {
		sc.getLogger().info("[SC Handler] Updating cache for genders...");
		int genderCount = genderCountCache.get(sp.getGender().getName());
		if(increase)
			genderCount++;
		if(!increase)
			genderCount--;

		genderCountCache.remove(sp.getGender().getName());
		genderCountCache.put(sp.getGender().getName(), genderCount);
	}

	public void adjustGenderCache(Gender gender, boolean increase) {
		sc.getLogger().info("[SC Handler] Updating cache for genders...");
		int genderCount = genderCountCache.get(gender.getName());
		if(increase)
			genderCount++;
		if(!increase)
			genderCount--;

		genderCountCache.remove(gender.getName());
		genderCountCache.put(gender.getName(), genderCount);
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