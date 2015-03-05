package com.nicholasdoherty.socialcore.utils;

import com.nicholasdoherty.socialcore.utils.title.VoxTitle;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by john on 6/24/14.
 */
public class VoxEffects {
    private List<VoxParticle> particles;
    private List<VoxSound> sounds;
    private VoxTitle voxTitle;

    public VoxEffects(List<VoxParticle> particles, List<VoxSound> sounds, VoxTitle voxTitle) {
        this.particles = particles;
        this.sounds = sounds;
        this.voxTitle = voxTitle;
    }

    public static VoxEffects fromConfig(ConfigurationSection configurationSection) {
        List<VoxParticle> particles = new ArrayList<VoxParticle>();
        List<VoxSound> sounds = new ArrayList<VoxSound>();
        if (configurationSection.contains("sounds")) {
            ConfigurationSection soundsSection = configurationSection.getConfigurationSection("sounds");
            for (String key : soundsSection.getKeys(false)) {
                ConfigurationSection soundSection = soundsSection.getConfigurationSection(key);
                VoxSound voxSound = VoxSound.fromConfig(soundSection);
                sounds.add(voxSound);
            }
        }
        if (configurationSection.contains("particles")) {
            ConfigurationSection particlesSection = configurationSection.getConfigurationSection("particles");
            for (String key : particlesSection.getKeys(false)) {
                ConfigurationSection particleSection = particlesSection.getConfigurationSection(key);
                VoxParticle voxParticle = VoxParticle.fromConfig(particleSection);
                particles.add(voxParticle);
            }
        }
        VoxTitle voxTitle = null;
        if (configurationSection.contains("title")) {
            ConfigurationSection titleSection = configurationSection.getConfigurationSection("title");
            voxTitle = VoxTitle.fromConfig(titleSection);
        }
        return new VoxEffects(particles, sounds,voxTitle);
    }

    public void play(Location loc) {
        play(loc, PlayerUtil.playersAround(loc,20));
    }
    public void play(Location loc, List<Player> players) {
        play(loc,players,null);
    }
    public void play(Location loc, List<Player> players, Map<String, String> replacements) {
        for (VoxParticle voxParticle : particles) {
            voxParticle.play(loc);
        }
        for (VoxSound voxSound : sounds) {
            voxSound.play(loc);
        }
        if (voxTitle != null) {
            voxTitle.send(players,replacements);
        }
    }
}
