package com.nicholasdoherty.socialcore;

import com.nicholasdoherty.socialcore.SocialCore.Gender;
import com.nicholasdoherty.socialcore.marriages.Divorce;
import com.nicholasdoherty.socialcore.marriages.Engagement;
import com.nicholasdoherty.socialcore.marriages.Marriage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
public class SaveHandler {
    
    String directory;
    SocialCore sc;
    
    public SaveHandler(final String directory, final SocialCore sc) {
        this.directory = directory;
        this.sc = sc;
    }
    
    public Gender getGenderForPlayer(final String playerName) {
        return Gender.UNSPECIFIED;
    }
    
    public List<SocialPlayer> allSocialPlayers() {
        final List<String> names = sc.store.getSocialPlayers();
        final List<SocialPlayer> socialPlayers = new ArrayList<>();
        for(final String name : names) {
            final SocialPlayer socialPlayer = getSocialPlayer(name);
            if(socialPlayer != null) {
                socialPlayers.add(socialPlayer);
            }
        }
        return socialPlayers;
    }
    
    public void purgeInvalids() {
        final Map<String, SocialPlayer> socialPlayerMap = new HashMap<>();
        final List<SocialPlayer> socialPlayers = allSocialPlayers();
        for(final SocialPlayer socialPlayer : socialPlayers) {
            socialPlayerMap.put(socialPlayer.getPlayerName(), socialPlayer);
        }
        for(final SocialPlayer socialPlayer : socialPlayers) {
            if(!socialPlayer.isMarried() && socialPlayer.getPetName() != null) {
                socialPlayer.setPetName(null);
            }
            if(!socialPlayer.isMarried() && socialPlayer.getMarriedTo() != null) {
                socialPlayer.setMarriedTo(null);
            }
            if(!socialPlayer.isEngaged() && socialPlayer.getEngagedTo() != null) {
                socialPlayer.setEngagedTo(null);
            }
            if(socialPlayer.isMarried() && socialPlayer.getMarriedTo() == null) {
                socialPlayer.setMarried(false);
            }
            if(socialPlayer.isEngaged() && socialPlayer.getEngagedTo() == null) {
                socialPlayer.setEngaged(false);
            }
            saveSocialPlayer(socialPlayer);
        }
        final List<String> marriageNames = getAllMarriageNames();
        final Collection<Marriage> marriages = new ArrayList<>();
        for(final String marriageName : marriageNames) {
            final Marriage marriage = getMarriage(marriageName);
            if(marriage != null) {
                marriages.add(marriage);
            }
        }
        final Collection<Marriage> invalidMarriages = new ArrayList<>();
        for(final Marriage marriage : marriages) {
            if(marriage.getHusband() == null || marriage.getWife() == null) {
                invalidMarriages.add(marriage);
                continue;
            }
            final SocialPlayer husband = socialPlayerMap.get(marriage.getHusband().getPlayerName());
            final SocialPlayer wife = socialPlayerMap.get(marriage.getWife().getPlayerName());
            if(husband == null || wife == null) {
                invalidMarriages.add(marriage);
                continue;
            }
            if(!husband.isMarried()) {
                husband.setMarried(true);
            }
            if(!wife.isMarried()) {
                wife.setMarried(true);
            }
            if(husband.getMarriedTo() == null || !husband.getMarriedTo().equals(wife.getPlayerName())) {
                husband.setMarriedTo(wife.getPlayerName());
            }
            if(wife.getMarriedTo() == null || !wife.getMarriedTo().equals(husband.getPlayerName())) {
                wife.setMarriedTo(husband.getPlayerName());
            }
            saveMarriage(marriage);
        }
        for(final Marriage marriage : invalidMarriages) {
            removeMarriage(marriage);
        }
        
        final Collection<Engagement> engagements = new ArrayList<>();
        final List<String> engagementNames = getAllEngagements();
        for(final String engageName : engagementNames) {
            final Engagement engagement = getEngagement(engageName);
            if(engagement != null) {
                engagements.add(engagement);
            }
        }
        final Collection<Engagement> invalidEngagements = new ArrayList<>();
        for(final Engagement engagement : engagements) {
            if(engagement.getFHusband() == null || engagement.getFWife() == null) {
                invalidEngagements.add(engagement);
                continue;
            }
            final SocialPlayer husband = socialPlayerMap.get(engagement.getFHusband().getPlayerName());
            final SocialPlayer wife = socialPlayerMap.get(engagement.getFWife().getPlayerName());
            if(husband == null || wife == null) {
                invalidEngagements.add(engagement);
                continue;
            }
            if(!husband.isEngaged()) {
                husband.setEngaged(true);
            }
            if(!wife.isEngaged()) {
                wife.setEngaged(true);
            }
            if(husband.getEngagedTo() == null || !husband.getEngagedTo().equals(wife.getPlayerName())) {
                husband.setEngagedTo(wife.getPlayerName());
            }
            if(wife.getEngagedTo() == null || !wife.getEngagedTo().equals(husband.getPlayerName())) {
                wife.setEngagedTo(husband.getPlayerName());
            }
            saveEngagement(engagement);
        }
        for(final Engagement engagement : invalidEngagements) {
            removeEngagement(engagement);
        }
    }
    
    public SocialPlayer getSocialPlayer(String playerName) {
        if(playerName == null) {
            return null;
        }
        final Player p = Bukkit.getPlayer(playerName);
        if(p != null) {
            playerName = p.getName();
        }
        if(sc.socialPlayersCache.containsKey(playerName)) {
            return sc.socialPlayersCache.get(playerName);
        }
        
        final SocialPlayer sp = sc.store.getSocialPlayer(playerName);
        sc.socialPlayersCache.put(playerName, sp);
        return sp;
    }
    
    @SuppressWarnings("unchecked")
    public void saveSocialPlayer(final SocialPlayer socialPlayer) {
        if(socialPlayer != null) {
            sc.store.syncSocialPlayer(socialPlayer);
        } else {
            sc.log.severe("Cannot save a null player!");
        }
    }
    
    public Marriage getMarriage(final String marriageName) {
        
        final Marriage marriage = new Marriage(marriageName, sc);
        return getMarriage(marriage.getHusband(), marriage.getWife());
    }
    
    public Marriage getMarriage(final SocialPlayer husband, final SocialPlayer wife) {
        
        final String marriageName = husband.getPlayerName() + Marriage.NAME_DELIMITER + wife.getPlayerName();
        if(sc.marriages.marriagesCache.containsKey(marriageName)) {
            return sc.marriages.marriagesCache.get(marriageName);
        }
        
        final Marriage marriage = sc.store.getMarriage(husband, wife);
        sc.marriages.marriagesCache.put(marriageName, marriage);
        
        return marriage;
    }
    
    @SuppressWarnings("unchecked")
    public void saveMarriage(final Marriage marriage) {
        
        if(marriage != null) {
            sc.store.saveMarriage(marriage);
            
            sc.marriages.marriagesCache.put(marriage.getName(), marriage);
        } else {
            sc.log.severe("Cannot save a null player!");
        }
    }
    
    public List<String> getAllMarriageNames() {
        return sc.store.getAllMarriageNames();
    }
    
    public void removeMarriage(final Marriage marriage) {
        sc.store.deleteMarriage(marriage);
    }
    
    public Engagement getEngagement(final String engagementName) {
        final Engagement engagement = new Engagement(engagementName, sc);
        return getEngagement(engagement.getFHusband(), engagement.getFWife());
    }
    
    public Engagement getEngagement(final SocialPlayer fHusband, final SocialPlayer fWife) {
        final String engagementName = fHusband.getPlayerName() + Engagement.NAME_DELIMITER + fWife.getPlayerName();
        if(sc.marriages.engagementsCache.containsKey(engagementName)) {
            return sc.marriages.engagementsCache.get(engagementName);
        }
        
        final Engagement engagement = sc.store.getEngagement(fHusband, fWife);
        
        sc.marriages.engagementsCache.put(engagementName, engagement);
        
        return engagement;
    }
    
    @SuppressWarnings("unchecked")
    public void saveEngagement(final Engagement engagement) {
        if(engagement != null) {
            sc.store.saveEngagement(engagement);
            sc.marriages.engagementsCache.put(engagement.getName(), engagement);
        } else {
            sc.log.severe("Cannot save a null engagement!");
        }
    }
    
    public void removeEngagement(final Engagement engagement) {
        sc.store.deleteEngagement(engagement);
    }
    
    public List<String> getAllEngagements() {
        return sc.store.getAllEngagementNames();
    }
    
    public Divorce getDivorce(final String divorceName) {
        final Divorce divorce = new Divorce(divorceName, sc);
        return getDivorce(divorce.getExhusband(), divorce.getExwife());
    }
    
    public Divorce getDivorce(final SocialPlayer exHusband, final SocialPlayer exWife) {
        final String divorceName = exHusband.getPlayerName() + Engagement.NAME_DELIMITER + exHusband.getPlayerName();
        if(sc.marriages.divorcesCache.containsKey(divorceName)) {
            return sc.marriages.divorcesCache.get(divorceName);
        }
        
        final Divorce divorce = sc.store.getDivorce(exHusband, exWife);
        
        sc.marriages.divorcesCache.put(divorceName, divorce);
        
        return divorce;
    }
    
    @SuppressWarnings("unchecked")
    public void saveDivorce(final Divorce divorce) {
        if(divorce != null) {
            sc.store.saveDivorce(divorce);
            
            sc.marriages.divorcesCache.put(divorce.getName(), divorce);
        } else {
            sc.log.severe("Cannot save a null divorce!");
        }
    }
    
    public void removeDivorce(final Divorce divorce) {
        sc.store.deleteDivorce(divorce);
    }
    
    public List<String> getAllDivorces() {
        return sc.store.getAllDivorceNames();
    }
    
    private void saveJson(final JSONAware jsonObject, final String fileName, final String type) {
        try {
            
            final FileWriter file = new FileWriter(directory + '/' + type + '/' + fileName + ".json");
            file.write(jsonObject.toJSONString());
            file.flush();
            file.close();
        } catch(final FileNotFoundException e) {
            sc.log.severe("No file for player: " + fileName);
        } catch(final IOException e) {
            sc.log.severe("An error occured while saving player: " + fileName);
        } catch(final NullPointerException ignored) {
            
        }
    }
    
    private JSONObject getJson(final String fileName, final String type) {
        
        final JSONParser parser = new JSONParser();
        
        try {
            
            final Object obj = parser.parse(new FileReader(directory + '/' + type + '/' + fileName + ".json"));
            return (JSONObject) obj;
        } catch(final FileNotFoundException ignored) {
            
        } catch(final IOException e) {
            sc.log.severe("An error occured while loading player: " + fileName);
        } catch(final ParseException e) {
            sc.log.severe("An error occured while parsing player: " + fileName);
        }
        
        return null;
    }
}
