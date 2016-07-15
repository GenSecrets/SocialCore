package com.nicholasdoherty.socialcore;

import com.nicholasdoherty.socialcore.marriages.Divorce;
import com.nicholasdoherty.socialcore.marriages.Engagement;
import com.nicholasdoherty.socialcore.marriages.Marriage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveHandler {
	
	String directory;
	SocialCore sc;
	
	public SaveHandler(String directory, SocialCore sc) {
		this.directory = directory;
		this.sc = sc;

	}
	
	public SocialCore.Gender getGenderForPlayer(String playerName) {
		
		return SocialCore.Gender.UNSPECIFIED;
	}
	public List<SocialPlayer> allSocialPlayers() {
		List<String> names = sc.store.getSocialPlayers();
		List<SocialPlayer> socialPlayers = new ArrayList<>();
		for (String name : names) {
			SocialPlayer socialPlayer = getSocialPlayer(name);
			if (socialPlayer != null) {
				socialPlayers.add(socialPlayer);
			}
		}
		return socialPlayers;
	}
	public void purgeInvalids() {
		Map<String, SocialPlayer> socialPlayerMap = new HashMap<>();
		List<SocialPlayer> socialPlayers = allSocialPlayers();
		for (SocialPlayer socialPlayer : socialPlayers) {
			socialPlayerMap.put(socialPlayer.getPlayerName(),socialPlayer);
		}
		for (SocialPlayer socialPlayer : socialPlayers) {
			if (!socialPlayer.isMarried() && socialPlayer.getPetName() != null) {
				socialPlayer.setPetName(null);
			}
			if (!socialPlayer.isMarried() && socialPlayer.getMarriedTo() != null) {
				socialPlayer.setMarriedTo(null);
			}
			if (!socialPlayer.isEngaged() && socialPlayer.getEngagedTo() != null) {
				socialPlayer.setEngagedTo(null);
			}
			if (socialPlayer.isMarried() && socialPlayer.getMarriedTo() == null) {
				socialPlayer.setMarried(false);
			}
			if (socialPlayer.isEngaged() && socialPlayer.getEngagedTo() == null) {
				socialPlayer.setEngaged(false);
			}
			saveSocialPlayer(socialPlayer);
		}
		List<String> marriageNames = getAllMarriageNames();
		List<Marriage> marriages = new ArrayList<>();
		for (String marriageName  : marriageNames) {
			Marriage marriage = getMarriage(marriageName);
			if (marriage != null) {
				marriages.add(marriage);
			}
		}
		List<Marriage> invalidMarriages = new ArrayList<>();
		for (Marriage marriage : marriages) {
			if (marriage.getHusband() == null || marriage.getWife() == null) {
				invalidMarriages.add(marriage);
				continue;
			}
			SocialPlayer husband = socialPlayerMap.get(marriage.getHusband().getPlayerName());
			SocialPlayer wife = socialPlayerMap.get(marriage.getWife().getPlayerName());
			if (husband == null || wife == null) {
				invalidMarriages.add(marriage);
				continue;
			}
			if (!husband.isMarried()) {
				husband.setMarried(true);
			}
			if (!wife.isMarried()) {
				wife.setMarried(true);
			}
			if (husband.getMarriedTo() == null || !husband.getMarriedTo().equals(wife.getPlayerName())) {
				husband.setMarriedTo(wife.getPlayerName());
			}
			if (wife.getMarriedTo() == null || !wife.getMarriedTo().equals(husband.getPlayerName())) {
				wife.setMarriedTo(husband.getPlayerName());
			}
			saveMarriage(marriage);
		}
		for (Marriage marriage : invalidMarriages) {
			removeMarriage(marriage);
		}

		List<Engagement> engagements = new ArrayList<>();
		List<String> engagementNames = getAllEngagements();
		for (String engageName : engagementNames) {
			Engagement engagement = getEngagement(engageName);
			if (engagement != null) {
				engagements.add(engagement);
			}
		}
		List<Engagement> invalidEngagements = new ArrayList<>();
		for (Engagement engagement : engagements) {
			if (engagement.getFHusband() == null || engagement.getFWife() == null) {
				invalidEngagements.add(engagement);
				continue;
			}
			SocialPlayer husband = socialPlayerMap.get(engagement.getFHusband().getPlayerName());
			SocialPlayer wife = socialPlayerMap.get(engagement.getFWife().getPlayerName());
			if (husband == null || wife == null) {
				invalidEngagements.add(engagement);
				continue;
			}
			if (!husband.isEngaged()) {
				husband.setEngaged(true);
			}
			if (!wife.isEngaged()) {
				wife.setEngaged(true);
			}
			if (husband.getEngagedTo() == null || !husband.getEngagedTo().equals(wife.getPlayerName())) {
				husband.setEngagedTo(wife.getPlayerName());
			}
			if (wife.getEngagedTo() == null || !wife.getEngagedTo().equals(husband.getPlayerName())) {
				wife.setEngagedTo(husband.getPlayerName());
			}
			saveEngagement(engagement);
		}
		for (Engagement engagement : invalidEngagements) {
			removeEngagement(engagement);
		}

	}
	public SocialPlayer getSocialPlayer(String playerName) {
        Player p = Bukkit.getPlayer(playerName);
        if (p != null )
		    playerName = p.getName();
		if (sc.socialPlayersCache.containsKey(playerName))
			return sc.socialPlayersCache.get(playerName);
		
		SocialPlayer sp = sc.store.getSocialPlayer(playerName);
		sc.socialPlayersCache.put(playerName, sp);
		return sp;
		
	}
	@SuppressWarnings("unchecked")
	public void saveSocialPlayer(SocialPlayer socialPlayer) {
		if (socialPlayer != null) {
			sc.store.syncSocialPlayer(socialPlayer);
		}
		else
			sc.log.severe("Cannot save a null player!");
	}
	
	public Marriage getMarriage(String marriageName) {
		
		Marriage marriage = new Marriage(marriageName, sc);
		return getMarriage(marriage.getHusband(), marriage.getWife());
	}
	
	public Marriage getMarriage(SocialPlayer husband, SocialPlayer wife) {
		
		String marriageName = husband.getPlayerName()+Marriage.NAME_DELIMITER+wife.getPlayerName();
		if (sc.marriages.marriagesCache.containsKey(marriageName)) {
			return sc.marriages.marriagesCache.get(marriageName);
		}
		
		Marriage marriage = sc.store.getMarriage(husband,wife);
		sc.marriages.marriagesCache.put(marriageName, marriage);
		
		return marriage;
	}
	
	@SuppressWarnings("unchecked")
	public void saveMarriage(Marriage marriage) {
		
		if (marriage!=null) {
			sc.store.saveMarriage(marriage);
			
			sc.marriages.marriagesCache.put(marriage.getName(), marriage);
		}
		else
			sc.log.severe("Cannot save a null player!");
		
	}
	
	public List<String> getAllMarriageNames() {
		return sc.store.getAllMarriageNames();
	}
	public void removeMarriage(Marriage marriage) {
		sc.store.deleteMarriage(marriage);
	}
	
	public Engagement getEngagement(String engagementName) {
		Engagement engagement = new Engagement(engagementName,sc);
		return getEngagement(engagement.getFHusband(), engagement.getFWife());
	}
	
	public Engagement getEngagement (SocialPlayer fHusband, SocialPlayer fWife) {
		String engagementName = fHusband.getPlayerName()+Engagement.NAME_DELIMITER+fWife.getPlayerName();
		if (sc.marriages.engagementsCache.containsKey(engagementName))
			return sc.marriages.engagementsCache.get(engagementName);
		
		Engagement engagement = sc.store.getEngagement(fHusband, fWife);

		
		sc.marriages.engagementsCache.put(engagementName, engagement);
		
		return engagement;
	}
	
	@SuppressWarnings("unchecked")
	public void saveEngagement(Engagement engagement) {
		if (engagement != null) {
			sc.store.saveEngagement(engagement);
			sc.marriages.engagementsCache.put(engagement.getName(), engagement);
		}
		else
			sc.log.severe("Cannot save a null engagement!");
	}
	
	public void removeEngagement(Engagement engagement) {
		sc.store.deleteEngagement(engagement);
	}
	
	public List<String>getAllEngagements() {
		return sc.store.getAllEngagementNames();
	}
	
	public Divorce getDivorce(String divorceName) {
		Divorce divorce = new Divorce(divorceName,sc);
		return getDivorce(divorce.getExhusband(),divorce.getExwife());
	}
	public Divorce getDivorce(SocialPlayer exHusband, SocialPlayer exWife) {
		String divorceName = exHusband.getPlayerName()+Engagement.NAME_DELIMITER+exHusband.getPlayerName();
		if (sc.marriages.divorcesCache.containsKey(divorceName))
			return sc.marriages.divorcesCache.get(divorceName);
		
		Divorce divorce = sc.store.getDivorce(exHusband, exWife);
		
		sc.marriages.divorcesCache.put(divorceName, divorce);
		
		return divorce;
	}
	@SuppressWarnings("unchecked")
	public void saveDivorce(Divorce divorce) {
		if (divorce != null) {
			sc.store.saveDivorce(divorce);
			
			sc.marriages.divorcesCache.put(divorce.getName(), divorce);
		}
		else
			sc.log.severe("Cannot save a null divorce!");
	}
	public void removeDivorce(Divorce divorce) {
		sc.store.deleteDivorce(divorce);
	}
	public List<String>getAllDivorces() {
		return sc.store.getAllDivorceNames();
	}

	private void saveJson(JSONObject jsonObject, String fileName, String type) {
		try {

			FileWriter file = new FileWriter((directory+"/"+type+"/" + fileName + ".json"));
			file.write(jsonObject.toJSONString());
			file.flush();
			file.close();
		}
		catch (FileNotFoundException e) {
			sc.log.severe("No file for player: "+fileName);
		}
		catch (IOException e) {
			sc.log.severe("An error occured while saving player: "+fileName);
		}
		catch (NullPointerException e) {

		}
	}
	
	private JSONObject getJson(String fileName, String type) {
		
		JSONParser parser = new JSONParser();
		
		try {
			
			Object obj = parser.parse((new FileReader((directory+"/"+type+"/" + fileName + ".json"))));
			JSONObject jsonObject = (JSONObject)obj;
			return jsonObject;
			
		}
		catch (FileNotFoundException e) {
			
		}
		catch (IOException e) {
			sc.log.severe("An error occured while loading player: "+fileName);
		}
		catch (ParseException e) {
			sc.log.severe("An error occured while parsing player: "+fileName);
		}
		
		return null;
	}
}
