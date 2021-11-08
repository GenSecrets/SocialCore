package com.nicholasdoherty.socialcore;

import com.nicholasdoherty.socialcore.components.marriages.types.Divorce;
import com.nicholasdoherty.socialcore.components.marriages.types.Engagement;
import com.nicholasdoherty.socialcore.components.marriages.types.Marriage;
import org.bukkit.Bukkit;
import org.json.simple.JSONAware;
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

@SuppressWarnings("unused")
public class SaveHandler {
    
    String directory;
    SocialCore sc;
    
    public SaveHandler(final String directory, final SocialCore sc) {
        this.directory = directory;
        this.sc = sc;
    }
    
    public List<SocialPlayer> allSocialPlayers() {
        final List<String> uuids = sc.store.getSocialPlayers();
        final List<SocialPlayer> socialPlayers = new ArrayList<>();
        for(final String uuid : uuids) {
            final SocialPlayer socialPlayer = getSocialPlayer(uuid);
            if(socialPlayer != null) {
                socialPlayers.add(socialPlayer);
            }
        }
        return socialPlayers;
    }
    
    public SocialPlayer getSocialPlayer(String uuid) {
        if(uuid == null) {
            return null;
        }

        if(sc.socialPlayersCache.containsKey(uuid)) {
            return sc.socialPlayersCache.get(uuid);
        }

        final SocialPlayer sp = sc.store.getSocialPlayer(uuid);
        sc.socialPlayersCache.put(uuid, sp);
        return sp;
    }
    
    @SuppressWarnings("unchecked")
    public void saveSocialPlayer(final SocialPlayer socialPlayer) {
        Bukkit.getScheduler().runTaskAsynchronously(sc, () -> {
            if(socialPlayer !=null){
                sc.store.syncSocialPlayer(socialPlayer);
            } else{
                sc.log.severe("Cannot save a null player!");
            }
        });
    }
    
    public Marriage getMarriage(final String marriageName) {
        final Marriage marriage = new Marriage(marriageName, sc);
        return getMarriage(marriage.getSpouse1(), marriage.getSpouse2());
    }
    
    public Marriage getMarriage(final SocialPlayer spouse1, final SocialPlayer spouse2) {
        Marriage marriage = null;
        if(spouse1 != null && spouse2 != null){
            final String marriageName = spouse1.getUUID() + Marriage.NAME_DELIMITER + spouse2.getUUID();
            if(sc.marriages.marriagesCache.containsKey(marriageName)) {
                return sc.marriages.marriagesCache.get(marriageName);
            }

            marriage = sc.store.getMarriage(spouse1, spouse2);
            sc.marriages.marriagesCache.put(marriageName, marriage);
        }
        return marriage;
    }
    
    @SuppressWarnings("unchecked")
    public void saveMarriage(final Marriage marriage) {
        Bukkit.getScheduler().runTaskAsynchronously(sc, () -> {
            if (marriage != null) {
                sc.store.saveMarriage(marriage);

                sc.marriages.marriagesCache.put(marriage.getName(), marriage);
            } else {
                sc.log.severe("Cannot save a null marriage!");
            }
        });
    }

    public int getCountGender(String genderName){
        return sc.store.getGenderStats(genderName);
    }

    public HashMap<String, Integer> getGenderCounts(List<String> names){
        return sc.store.getGenderTotals(names);
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
    
    public Engagement getEngagement(final SocialPlayer fSpouse1, final SocialPlayer fSpouse2) {
        Engagement engagement = null;
        if (fSpouse1 != null && fSpouse2 != null){
            final String engagementName = fSpouse1.getPlayerName() + Engagement.NAME_DELIMITER + fSpouse2.getPlayerName();
            if(sc.marriages.engagementsCache.containsKey(engagementName)) {
                return sc.marriages.engagementsCache.get(engagementName);
            }

            engagement = sc.store.getEngagement(fSpouse1, fSpouse2);
            sc.marriages.engagementsCache.put(engagementName, engagement);
        }
        return engagement;
    }
    
    @SuppressWarnings("unchecked")
    public void saveEngagement(final Engagement engagement) {
        Bukkit.getScheduler().runTaskAsynchronously(sc, () -> {
            if (engagement != null) {
                sc.store.saveEngagement(engagement);
                sc.marriages.engagementsCache.put(engagement.getName(), engagement);
            } else {
                sc.log.severe("Cannot save a null engagement!");
            }
        });
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
        if(exSpouse1 != null && exSpouse2 != null){
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
        Bukkit.getScheduler().runTaskAsynchronously(sc, () -> {
            if (divorce != null) {
                sc.store.saveDivorce(divorce);
                sc.marriages.divorcesCache.put(divorce.getName(), divorce);
            } else {
                sc.log.severe("Cannot save a null divorce!");
            }
        });
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
        Bukkit.getScheduler().runTaskAsynchronously(sc, () -> {
            try {
                final FileWriter file = new FileWriter(directory + '/' + type + '/' + fileName + ".json");
                file.write(jsonObject.toJSONString());
                file.flush();
                file.close();
            } catch (final FileNotFoundException e) {
                sc.log.severe("No file for player: " + fileName);
            } catch (final IOException e) {
                sc.log.severe("An error occurred while saving player: " + fileName);
            } catch (final NullPointerException ignored) {

            }
        });
    }
    
    private JSONObject getJson(final String fileName, final String type) {
        final JSONParser parser = new JSONParser();
        
        try {
            final Object obj = parser.parse(new FileReader(directory + '/' + type + '/' + fileName + ".json"));
            return (JSONObject) obj;
        } catch(final FileNotFoundException ignored) {
            
        } catch(final IOException e) {
            sc.log.severe("An error occurred while loading player: " + fileName);
        } catch(final ParseException e) {
            sc.log.severe("An error occurred while parsing player: " + fileName);
        }
        
        return null;
    }
}
