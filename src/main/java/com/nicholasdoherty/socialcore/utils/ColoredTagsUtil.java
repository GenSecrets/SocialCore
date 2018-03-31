package com.nicholasdoherty.socialcore.utils;

import com.gmail.filoghost.coloredtags.ColoredTags;
import com.gmail.filoghost.coloredtags.PermissionProvider;
import com.gmail.filoghost.coloredtags.ScoreboardHandler;
import com.gmail.filoghost.coloredtags.TeamData;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by john on 7/2/15.
 */
public class ColoredTagsUtil {
    private static Map<UUID, PrefixSuffixCombo> lastCombo = new HashMap<>();
    
    public static void removeTitle(Player p) {
        if(!lastCombo.containsKey(p.getUniqueId())) {
            return;
        }
        TeamData teamData = ColoredTags.playersMap.get(p.getName().toLowerCase());
        if(teamData == null) {
            return;
        }
        PrefixSuffixCombo prefixSuffixCombo = lastCombo.get(p.getUniqueId());
        String newPrefix = teamData.getPrefix().replace(prefixSuffixCombo.getPrefix(), "");
        String newSuffix = teamData.getSuffix().replace(prefixSuffixCombo.getSuffix(), "");
        teamData = createTeamData(newPrefix, newSuffix);
        if(teamData == null) {
            return;
        }
        if(newPrefix.equals("") && newSuffix.equals("") && ColoredTags.playersMap.containsKey(p.getName().toLowerCase())) {
            ColoredTags.playersMap.remove(p.getName().toLowerCase());
        } else {
            ColoredTags.playersMap.put(p.getName().toLowerCase(), teamData);
        }
        if(p.isOnline()) {
            ColoredTags.updateNametag(p);
        }
        lastCombo.remove(p.getUniqueId());
    }
    
    public static void setTitle(Player p, String prefix, String suffix) {
        removeTitle(p);
        TeamData teamData = ColoredTags.playersMap.get(p.getName().toLowerCase());
        if(teamData == null) {
            String group = PermissionProvider.getGRoup(p);
            if(group != null) {
                teamData = ColoredTags.groupsMap.get(group.toLowerCase());
            }
            if(teamData == null) {
                teamData = createTeamData("", "");
            }
        }
        if(teamData == null) {
            return;
        }
        String newPrefix = teamData.getPrefix() + prefix;
        String newPostFix = suffix + teamData.getSuffix();
        TeamData newTeamData = createTeamData(newPrefix, newPostFix);
        if(newTeamData != null) {
            ColoredTags.playersMap.put(p.getName().toLowerCase(), newTeamData);
            ScoreboardHandler.setPrefixSuffix(p, newTeamData);
            PrefixSuffixCombo prefixSuffixCombo = new PrefixSuffixCombo(prefix, suffix);
            lastCombo.put(p.getUniqueId(), prefixSuffixCombo);
        }
    }
    
    private static TeamData createTeamData(String prefix, String suffix) {
        try {
            Constructor constructor = TeamData.class.getDeclaredConstructors()[0];
            constructor.setAccessible(true);
            return (TeamData) constructor.newInstance(prefix, suffix);
        } catch(InvocationTargetException e) {
            e.printStackTrace();
        } catch(InstantiationException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void reset() {
        lastCombo.clear();
    }
    
    static class PrefixSuffixCombo {
        private String prefix;
        private String suffix;
        
        public PrefixSuffixCombo(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }
        
        public String getPrefix() {
            return prefix;
        }
        
        public String getSuffix() {
            return suffix;
        }
    }
}
