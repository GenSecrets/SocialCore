package com.nicholasdoherty.socialcore.utils.title;

import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

/**
 * Created by john on 2/27/15.
 */
public class VoxTitle {
    private String title,subtitle;
    private int fadeIn,stay,fadeOut;

    public VoxTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }
    public void send(Collection<Player> playerCollection, Map<String, String> replacements) {
        if (replacements != null) {
            for (String key : replacements.keySet()) {
                String value = replacements.get(key);
                if (title != null) {
                    title = title.replace(key,value);
                }
                if (subtitle != null) {
                    subtitle = subtitle.replace(key,value);
                }
            }
        }
        for (Player p : playerCollection) {
            TitleUtil.sendTitle(p,title,subtitle,fadeIn,stay,fadeOut);
        }
    }
    public static VoxTitle fromConfig(ConfigurationSection section) {
        String title = section.getString("title");
        if (title != null) {
            title = ChatColor.translateAlternateColorCodes('&',title);
        }
        String subtitle = section.getString("subtitle");
        if (subtitle != null) {
            subtitle = ChatColor.translateAlternateColorCodes('&',subtitle);
        }
        int fadeIn = 20;
        if (section.contains("fade-in")) {
            fadeIn = (int) VoxTimeUnit.getTicks(section.getString("fade-in"));
        }
        int stay = 20;
        if (section.contains("stay")) {
            stay = (int) VoxTimeUnit.getTicks(section.getString("stay"));
        }
        int fadeout = 20;
        if (section.contains("fade-out")) {
            fadeout = (int) VoxTimeUnit.getTicks(section.getString("fade-out"));
        }
        return new VoxTitle(title,subtitle,fadeIn,stay,fadeout);
    }
}
