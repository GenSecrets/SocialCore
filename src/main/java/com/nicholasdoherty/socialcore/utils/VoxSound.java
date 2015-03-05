package com.nicholasdoherty.socialcore.utils;

import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by john on 6/24/14.
 */
public class VoxSound {
    private static boolean enabled = true;
    private Sound sound;
    private float volume, pitch;
    private int amount,interval;

    public VoxSound(Sound sound, float volume, float pitch, int amount, int interval) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.amount = amount;
        this.interval = interval;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        VoxSound.enabled = enabled;
    }

    public static VoxSound fromConfig(ConfigurationSection configurationSection) {
        Sound sound = Sound.valueOf(configurationSection.getName().toUpperCase());
        float volume = 1;
        float pitch = 1;
        if (configurationSection.contains("volume")) {
            volume = (float) configurationSection.getDouble("volume");
        }
        if (configurationSection.contains("pitch")) {
            pitch = (float) configurationSection.getDouble("pitch");
        }
        int amount = 1;
        if (configurationSection.contains("amount")) {
            amount = configurationSection.getInt("amount");
        }
        int interval = 1;
        if (configurationSection.contains("interval")) {
            interval = configurationSection.getInt("interval");
        }
        return new VoxSound(sound, volume, pitch,amount,interval);
    }

    public void play(Location loc) {
        new PlaySoundRunnable(loc).runTaskTimer(SocialCore.plugin, 0, interval);
    }
    private class PlaySoundRunnable extends BukkitRunnable {
        int times = 0;
        Location loc;

        public PlaySoundRunnable(Location loc) {
            this.loc = loc;
        }

        @Override
        public void run() {
            loc.getWorld().playSound(loc, sound, volume, pitch);
            times += 1;
            if (times >= amount) {
                this.cancel();
            }
        }
    }
}
