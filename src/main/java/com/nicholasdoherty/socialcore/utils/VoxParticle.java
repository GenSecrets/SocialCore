package com.nicholasdoherty.socialcore.utils;


import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by john on 6/24/14.
 */
public class VoxParticle {
    private static boolean enabled = true;
    private Effect effect;
    private int data, radius;

    public VoxParticle(Effect effect, int data, int radius) {
        this.effect = effect;
        this.data = data;
        this.radius = radius;
    }


    public static VoxParticle fromConfig(ConfigurationSection configurationSection) {
        Effect effect = Effect.valueOf(configurationSection.getName().toUpperCase());
        int data = 0;
        int radius = -1;
        if (configurationSection.contains("data")) {
            data = configurationSection.getInt("data");
        }
        if (configurationSection.contains("radius")) {
            radius = configurationSection.getInt("radius");
        }
        return new VoxParticle(effect, data, radius);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        VoxParticle.enabled = enabled;
    }

    public void play(Location loc) {
        loc = loc.add(0, .6, 0);
        if (radius > 0)
            loc.getWorld().playEffect(loc, effect, data, radius);
        else
            loc.getWorld().playEffect(loc, effect, data);
    }

}
