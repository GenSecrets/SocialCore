package com.nicholasdoherty.socialcore.marriages;

import java.util.ArrayList;
import java.util.HashMap;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.marriages.types.Divorce;
import com.nicholasdoherty.socialcore.marriages.types.Engagement;
import com.nicholasdoherty.socialcore.marriages.types.Marriage;


public class Marriages {
	
	public enum Status {
		Single, ProposeFrom, ProposeTo, Engaged, Married
	}
	
	private final SocialCore sc;
	
	public HashMap<String, Marriage>marriagesCache;
	public HashMap<String, Engagement>engagementsCache;
	public HashMap<String, Divorce>divorcesCache;
	public HashMap<SocialPlayer,SocialPlayer>proposals;
	private final HashMap<String,Long> kisses;
	private final ArrayList<String>pendingDivorces;
	
	public Marriages(SocialCore plugin) {
		sc = plugin;
		marriagesCache = new HashMap<>();
		engagementsCache = new HashMap<>();
		proposals = new HashMap<>(); //form -> proposedFrom : proposedTo
		divorcesCache = new HashMap<>();
		kisses = new HashMap<>();
		pendingDivorces = new ArrayList<>();
	}
	
	public Status getStatus(SocialPlayer sp) {
		if (sp.isMarried())
			return Status.Married;
		if (sp.isEngaged())
			return Status.Engaged;
		if (proposals.containsKey(sp))
			return Status.ProposeFrom;
		if (proposals.containsValue(sp))
			return Status.ProposeTo;
		
		return Status.Single;
	}
	
	public boolean canPlayerKiss(String playerName) {
		
		if (!kisses.containsKey(playerName))
			return true;
		
		long currentTime = System.currentTimeMillis();
		long saveTime = kisses.get(playerName);
		

		if (saveTime+(sc.marriageConfig.kissingCooldown*1000) < currentTime) {
			kisses.remove(playerName);
			return true;
		}
		else {
			return false;
		}
	}
	public void kissPlayer(String playerName) {
		kisses.put(playerName, System.currentTimeMillis());
	}
	
	public ArrayList<String>getPendingDivorces(){
		return pendingDivorces;
	}

}