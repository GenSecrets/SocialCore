package com.nicholasdoherty.socialcore.marriages;

import java.util.ArrayList;
import java.util.HashMap;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.marriages.Divorce;
import com.nicholasdoherty.socialcore.marriages.Engagement;
import com.nicholasdoherty.socialcore.marriages.Marriage;


public class Marriages {
	
	public enum Status {
		Single,ProposeFrom,ProposeTo,Engaged,Married;
	}
	
	private SocialCore sc;
	
	public HashMap<String,Marriage>marriagesCache;
	public HashMap<String,Engagement>engagementsCache;
	public HashMap<String,Divorce>divorcesCache;
	public HashMap<SocialPlayer,SocialPlayer>proposals;
	private HashMap<String,Long>kissings;
	private ArrayList<String>pendingDivorces;
	
	public Marriages(SocialCore derp) {
		sc = derp;
		marriagesCache = new HashMap<String,Marriage>();
		engagementsCache = new HashMap<String,Engagement>();
		proposals = new HashMap<SocialPlayer,SocialPlayer>();//form -> proposedFrom : proposedTo
		divorcesCache = new HashMap<String,Divorce>();
		kissings = new HashMap<String,Long>();
		pendingDivorces = new ArrayList<String>();
	}
	
	public Status getStatus(SocialPlayer sp) {
		
		if (sp.isMarried())
			return Status.Married;
		if (sp.isEngaged())
			return Status.Engaged;
		if (proposals.keySet().contains(sp))
			return Status.ProposeFrom;
		if (proposals.values().contains(sp))
			return Status.ProposeTo;
		
		return Status.Single;
	}
	
	public boolean canPlayerKiss(String playerName) {
		
		if (!kissings.containsKey(playerName))
			return true;
		
		long currentTime = System.currentTimeMillis();
		long saveTime = kissings.get(playerName);
		

		if (saveTime+(sc.marriageConfig.kissingCooldown*1000) < currentTime) {
			kissings.remove(playerName);
			return true;
		}
		else {
			return false;
		}
	}
	public void kissPlayer(String playerName) {
		kissings.put(playerName, System.currentTimeMillis());
	}
	
	public ArrayList<String>getPendingDivorces(){
		return pendingDivorces;
	}

}