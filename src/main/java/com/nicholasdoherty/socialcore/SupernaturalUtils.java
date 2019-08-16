package com.nicholasdoherty.socialcore;

/**
 * Created by john on 2/7/14.
 */
public final class SupernaturalUtils {
    private SupernaturalUtils() {
    }
    
    public static boolean isVampire(final String name) {
        /*
        final UPlayer uPlayer = UPlayer.get(name);
        return uPlayer != null && (uPlayer.isInfected() || uPlayer.getInfection() > 0 || uPlayer.isVampire());
         */
        return false;
    }
    
    public static boolean isWerewolf(String name) {
        /*
        if(name == null) {
            return false;
        }
        final Player p = Bukkit.getPlayer(name);
        if(p != null) {
            name = Bukkit.getPlayer(name).getName();
        }
        return WStore.playerIsInfected(name);
         */
        return false;
    }
    
    public static boolean isSupernatural(final String name) {
        return false; // isVampire(name) || isWerewolf(name);
    }
    
    public static boolean isHuman(final String name) {
        if(true) {
            return true;
        }
        if(name == null) {
            return true;
        }
        final SocialPlayer socialPlayer = SocialCore.plugin.save.getSocialPlayer(name);
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
        return socialPlayer.getRaceString().equalsIgnoreCase(SocialCore.plugin.races.getDefaultRace().getName()) || socialPlayer.getRaceString().equalsIgnoreCase("human");
    }
}
