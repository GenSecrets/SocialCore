package com.nicholasdoherty.socialcore;

import com.massivecraft.vampire.entity.UPlayer;
import com.nicholasdoherty.werewolf.core.storage.WStore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by john on 2/7/14.
 */
public class SupernaturalUtils {
    public static boolean isVampire(String name) {
        UPlayer uPlayer = UPlayer.get(name);
        if(uPlayer == null) {
            return false;
        }
        if(uPlayer.isInfected()) {
            return true;
        }
        if(uPlayer.getInfection() > 0) {
            return true;
        }
        if(uPlayer.isVampire()) {
            return true;
        }
        return false;
    }
    
    public static boolean isWerewolf(String name) {
        Player p = Bukkit.getPlayer(name);
        if(p != null) {
            name = Bukkit.getPlayer(name).getName();
        }
        return WStore.playerIsInfected(name);
    }
    
    public static boolean isSupernatural(String name) {
        return (isVampire(name) || isWerewolf(name));
    }
    
    public static boolean isHuman(String name) {
        SocialPlayer socialPlayer = SocialCore.plugin.save.getSocialPlayer(name);
        if(socialPlayer == null) {
            SocialCore.plugin.getLogger().warning("Could not get social player for: " + name);
            return true;
        }
        if(socialPlayer.getRaceString() == null) {
            //SocialCore.plugin.getLogger().warning("Could not get race for: " + name);
            return true;
        }
        if(SocialCore.plugin.races.getDefaultRace() == null) {
            SocialCore.plugin.getLogger().severe("Default race not set!");
            return true;
        }
        if(SocialCore.plugin.races.getDefaultRace().getName() == null) {
            SocialCore.plugin.getLogger().severe("Default race does not have a name.");
        }
        if(socialPlayer.getRaceString().equalsIgnoreCase(SocialCore.plugin.races.getDefaultRace().getName())) {
            return true;
        }
        if(socialPlayer.getRaceString().equalsIgnoreCase("human")) {
            return true;
        }
        return false;
    }
}
