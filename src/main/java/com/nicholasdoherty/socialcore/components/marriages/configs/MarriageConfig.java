package com.nicholasdoherty.socialcore.components.marriages.configs;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.marriages.MarriageGem;
import com.nicholasdoherty.socialcore.utils.ColorUtil;
import com.nicholasdoherty.socialcore.utils.time.VoxTimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MarriageConfig {
    
    public List<MarriageGem> marriageGems = new ArrayList<>();
    public int priestDistance;
    public int coupleDistance;
    public int coupleXPDistance;
    public double coupleXPPercent;
    public int kissingCooldown;
    public int kissHealAmount;
    public long piggybackCooldown;
    public long expMessageCooldown;
    public long foodMessageCooldown;
    public double maxConsumeDistanceSquared;
    public double maxShareInventDistanceSquared;
    public String petNameLoginMessage;
    public String petNameLogoutMessage;
    public String petNameChangeSpouseMessage;
    public long divorceProposeCooldownMillis = 1000 * 60 * 60 * 24 * 2;
    public List<String> whitelistPiggybackWorlds;
    SocialCore sc;
    
    public MarriageConfig(final SocialCore sc) {
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
            final String n = ColorUtil.registerColors(data[1]);
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
        whitelistPiggybackWorlds = sc.getMarriagesConfig().getStringList("perks.piggyback.piggyback-world-whitelist");
        foodMessageCooldown = VoxTimeUnit.getTicks(sc.getMarriagesConfig().getString("perks.sharing.food.food-message-cooldown"));
        maxConsumeDistanceSquared = Math.pow(sc.getMarriagesConfig().getDouble("perks.sharing.food.food-share-distance", 5), 2);
        maxShareInventDistanceSquared = Math.pow(sc.getMarriagesConfig().getDouble("perks.sharing.inv.inventory-share-distance", 5), 2);
        expMessageCooldown = VoxTimeUnit.getTicks(sc.getMarriagesConfig().getString("perks.sharing.xp.xp-message-cooldown"));
        coupleXPDistance = sc.getMarriagesConfig().getInt("perks.sharing.xp.xp-bonus-distance");
        coupleXPPercent = sc.getMarriagesConfig().getDouble("perks.sharing.xp.xp-bonus-percent");
        // PETNAMES
        petNameLoginMessage = ChatColor.translateAlternateColorCodes('&', sc.getMarriagesConfig().getString("perks.petname.login-message"));
        petNameLogoutMessage = ChatColor.translateAlternateColorCodes('&', sc.getMarriagesConfig().getString("perks.petname.logout-message"));
        petNameChangeSpouseMessage = ChatColor.translateAlternateColorCodes('&', sc.getMarriagesConfig().getString("perks.petname.change-name-message"));
    }
}