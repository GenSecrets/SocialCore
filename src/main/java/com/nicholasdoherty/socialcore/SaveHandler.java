package com.nicholasdoherty.socialcore;

import com.nicholasdoherty.socialcore.marriages.Divorce;
import com.nicholasdoherty.socialcore.marriages.Engagement;
import com.nicholasdoherty.socialcore.marriages.Marriage;
import org.bukkit.OfflinePlayer;
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
            if(marriage.getSpouse1() == null || marriage.getSpouse2() == null) {
                invalidMarriages.add(marriage);
                continue;
            }
            final SocialPlayer spouse1 = socialPlayerMap.get(marriage.getSpouse1().getPlayerName());
            final SocialPlayer spouse2 = socialPlayerMap.get(marriage.getSpouse2().getPlayerName());
            if(spouse1 == null || spouse2 == null) {
                invalidMarriages.add(marriage);
                continue;
            }
            if(!spouse1.isMarried()) {
                spouse1.setMarried(true);
            }
            if(!spouse2.isMarried()) {
                spouse2.setMarried(true);
            }
            if(spouse1.getMarriedTo() == null || !spouse1.getMarriedTo().equals(spouse2.getPlayerName())) {
                spouse1.setMarriedTo(spouse2.getPlayerName());
            }
            if(spouse2.getMarriedTo() == null || !spouse2.getMarriedTo().equals(spouse1.getPlayerName())) {
                spouse2.setMarriedTo(spouse1.getPlayerName());
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
            if(engagement.getFutureSpouse1() == null || engagement.getFutureSpouse2() == null) {
                invalidEngagements.add(engagement);
                continue;
            }
            final SocialPlayer futureSpouse1 = socialPlayerMap.get(engagement.getFutureSpouse1().getPlayerName());
            final SocialPlayer futureSpouse2 = socialPlayerMap.get(engagement.getFutureSpouse2().getPlayerName());
            if(futureSpouse1 == null || futureSpouse2 == null) {
                invalidEngagements.add(engagement);
                continue;
            }
            if(!futureSpouse1.isEngaged()) {
                futureSpouse1.setEngaged(true);
            }
            if(!futureSpouse2.isEngaged()) {
                futureSpouse2.setEngaged(true);
            }
            if(futureSpouse1.getEngagedTo() == null || !futureSpouse1.getEngagedTo().equals(futureSpouse2.getPlayerName())) {
                futureSpouse1.setEngagedTo(futureSpouse2.getPlayerName());
            }
            if(futureSpouse2.getEngagedTo() == null || !futureSpouse2.getEngagedTo().equals(futureSpouse1.getPlayerName())) {
                futureSpouse2.setEngagedTo(futureSpouse1.getPlayerName());
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
        final OfflinePlayer p = sc.getServer().getOfflinePlayer(playerName);
        if(!p.hasPlayedBefore()) {
            return null;
        }

        playerName = p.getName();
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
        return getMarriage(marriage.getSpouse1(), marriage.getSpouse2());
    }
    
    public Marriage getMarriage(final SocialPlayer husband, final SocialPlayer wife) {
        Marriage marriage;
        if(husband == null || wife == null){
            return null;
        }
        final String marriageName = husband.getPlayerName() + Marriage.NAME_DELIMITER + wife.getPlayerName();
        if(sc.marriages.marriagesCache.containsKey(marriageName)) {
            return sc.marriages.marriagesCache.get(marriageName);
        }

        marriage = sc.store.getMarriage(husband, wife);
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

    public int getCountGender(String genderName){
        return sc.store.getGenderStats(genderName);
    }
    
    public List<String> getAllMarriageNames() {
        return sc.store.getAllMarriageNames();
    }
    
    public void removeMarriage(final Marriage marriage) {
        sc.store.deleteMarriage(marriage);
    }

    public void removeMarriage(final String marriage) {
        sc.store.deleteMarriage(marriage);
    }
    
    public Engagement getEngagement(final String engagementName) {
        final Engagement engagement = new Engagement(engagementName, sc);
        return getEngagement(engagement.getFutureSpouse1(), engagement.getFutureSpouse2());
    }
    
    public Engagement getEngagement(final SocialPlayer fHusband, final SocialPlayer fWife) {
        Engagement engagement = null;
        if (fHusband != null && fWife != null){
            final String engagementName = fHusband.getPlayerName() + Engagement.NAME_DELIMITER + fWife.getPlayerName();
            if(sc.marriages.engagementsCache.containsKey(engagementName)) {
                return sc.marriages.engagementsCache.get(engagementName);
            }

            engagement = sc.store.getEngagement(fHusband, fWife);
            sc.marriages.engagementsCache.put(engagementName, engagement);
        }
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

    public void removeEngagement(final String engagement) {
        sc.store.deleteEngagement(engagement);
    }
    
    public List<String> getAllEngagements() {
        return sc.store.getAllEngagementNames();
    }
    
    public Divorce getDivorce(final String divorceName) {
        final Divorce divorce = new Divorce(divorceName, sc);
        return getDivorce(divorce.getExSpouse1(), divorce.getExSpouse2());
    }
    
    public Divorce getDivorce(final SocialPlayer exSpouse1, final SocialPlayer exSpouse2) {
        Divorce divorce = null;
        if(exSpouse1 != null && exSpouse2 != null && (exSpouse1.getPlayerName() != null || exSpouse2.getPlayerName() != null)){
            final String divorceName = exSpouse1.getPlayerName() + Engagement.NAME_DELIMITER + exSpouse1.getPlayerName();
            if(sc.marriages.divorcesCache.containsKey(divorceName)) {
                return sc.marriages.divorcesCache.get(divorceName);
            }

            divorce = sc.store.getDivorce(exSpouse1, exSpouse2);
            sc.marriages.divorcesCache.put(divorceName, divorce);
        }
        
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

    public void removeDivorce(final String divorce) {
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
