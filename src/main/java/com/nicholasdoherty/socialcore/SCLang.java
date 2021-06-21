package com.nicholasdoherty.socialcore;

import com.nicholasdoherty.socialcore.marriages.MarriageGem;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SCLang {
    
    public List<MarriageGem> marriageGems = new ArrayList<>();
    public int priestDistance;
    public int coupleDistance;
    public int coupleXPDistance;
    public double coupleXPPercent;
    public int kissingCooldown;
    public int kissHealAmount;
    public long piggybackCooldown;
    public double maxConsumeDistanceSquared;
    public double maxShareInventDistanceSquared;
    public String petNameLoginMessage;
    public String petNameLogoutMessage;
    public String petNameChangeSpouseMessage;
    public long divorceProposeCooldownMillis = 1000 * 60 * 60 * 24 * 2;
    public String defaultWorld;
    SocialCore sc;
    
    public SCLang(final SocialCore sc) {
        this.sc = sc;
    }
    
    public void loadConfig() {
        //MARRIAGE SETTINGS
        marriageGems = new ArrayList<>();
        final int s = sc.getMarriagesConfig().getConfigurationSection("marriage.marriage-rings").getKeys(false).size();
        for(int i = 0; i < s; i++) {
            final String p = sc.getMarriagesConfig().getString("marriage.marriage-rings." + (i + 1));
            final String[] data = p.split(" ");
            final String[] d2 = data[0].split(":");
            final Material bID = Material.getMaterial(d2[0]);
            final String n = registerColors(data[1]);
            final MarriageGem gem = new MarriageGem(bID, n);
            marriageGems.add(gem);
        }
        priestDistance = sc.getMarriagesConfig().getInt("marriage.priest-marriage-distance");
        coupleDistance = sc.getMarriagesConfig().getInt("marriage.marriage-couple-distance");
        if(sc.getMarriagesConfig().contains("marriage.divorce-remarry-cooldown")) {
            divorceProposeCooldownMillis = 50 * VoxTimeUnit.getTicks(sc.getMarriagesConfig().getString("marriage.divorce-remarry-cooldown"));
        }

        // PERKS SETTINGS
        // KISSING
        kissingCooldown = sc.getMarriagesConfig().getInt("perks.kissing.kiss-cooldown");
        kissHealAmount = sc.getMarriagesConfig().getInt("perks.kissing.kiss-heal-amount");
        // SHARING
        piggybackCooldown = VoxTimeUnit.getTicks(sc.getMarriagesConfig().getString("perks.piggyback.piggyback-cooldown"));
        maxConsumeDistanceSquared = Math.pow(sc.getMarriagesConfig().getDouble("perks.sharing.food-share-distance", 5), 2);
        maxShareInventDistanceSquared = Math.pow(sc.getMarriagesConfig().getDouble("perks.sharing.inventory-share-distance", 5), 2);
        coupleXPDistance = sc.getMarriagesConfig().getInt("perks.sharing.xp.xp-bonus-distance");
        coupleXPPercent = sc.getMarriagesConfig().getDouble("perks.sharing.xp.xp-bonus-percent");
        // PETNAMES
        petNameLoginMessage = ChatColor.translateAlternateColorCodes('&', sc.getMarriagesConfig().getString("perks.petname.login-message"));
        petNameLogoutMessage = ChatColor.translateAlternateColorCodes('&', sc.getMarriagesConfig().getString("perks.petname.logout-message"));
        petNameChangeSpouseMessage = ChatColor.translateAlternateColorCodes('&', sc.getMarriagesConfig().getString("perks.petname.change-name-message"));
        defaultWorld = sc.getMarriagesConfig().getString("default-world", "world");
    }
    
    public String registerColors(final String msg) {
        String newMsg = msg.replaceAll("&0", ChatColor.BLACK + "");
        newMsg = newMsg.replaceAll("&0", ChatColor.BLACK + "");
        newMsg = newMsg.replaceAll("&1", ChatColor.DARK_BLUE + "");
        newMsg = newMsg.replaceAll("&2", ChatColor.DARK_GREEN + "");
        newMsg = newMsg.replaceAll("&3", ChatColor.DARK_AQUA + "");
        newMsg = newMsg.replaceAll("&4", ChatColor.DARK_RED + "");
        newMsg = newMsg.replaceAll("&5", ChatColor.DARK_PURPLE + "");
        newMsg = newMsg.replaceAll("&6", ChatColor.GOLD + "");
        newMsg = newMsg.replaceAll("&7", ChatColor.GRAY + "");
        newMsg = newMsg.replaceAll("&8", ChatColor.DARK_GRAY + "");
        newMsg = newMsg.replaceAll("&9", ChatColor.BLUE + "");
        newMsg = newMsg.replaceAll("&a", ChatColor.GREEN + "");
        newMsg = newMsg.replaceAll("&b", ChatColor.AQUA + "");
        newMsg = newMsg.replaceAll("&c", ChatColor.RED + "");
        newMsg = newMsg.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
        newMsg = newMsg.replaceAll("&e", ChatColor.YELLOW + "");
        newMsg = newMsg.replaceAll("&f", ChatColor.WHITE + "");
        
        newMsg = newMsg.replaceAll("&l", ChatColor.BOLD + "");
        newMsg = newMsg.replaceAll("&n", ChatColor.UNDERLINE + "");
        newMsg = newMsg.replaceAll("&o", ChatColor.ITALIC + "");
        newMsg = newMsg.replaceAll("&k", ChatColor.STRIKETHROUGH + "");
        newMsg = newMsg.replaceAll("&m", ChatColor.RESET + "");
        newMsg = newMsg.replaceAll("&newline", "\n");
        return newMsg;
    }
}