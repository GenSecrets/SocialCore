package com.nicholasdoherty.socialcore.utils.time;

import com.nicholasdoherty.socialcore.SocialCore;
import com.voxmc.voxlib.util.ConfigAccessor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 8/6/14.
 * Uses a semi annoying singleton for ease of use.
 */
public class Clock {
    private static Clock clock;
    private long time;
    private boolean running;
    private BukkitTask task;
    private SocialCore plugin;
    private ConfigAccessor file;
    private List<Runnable> run = new ArrayList<Runnable>();
    public Clock(SocialCore plugin) {
        this.plugin = plugin;
        file = new ConfigAccessor(plugin, "time.yml");
        file.saveDefaultConfig();
        file.reloadConfig();
        time = file.getConfig().getLong("time");
    }

    private void startClock() {
        task = new BukkitRunnable(){
            @Override
            public void run() {
                time += 1;
                for (Runnable runnable : run) {
                    runnable.run();
                }
            }
        }.runTaskTimer(plugin, 1, 1);
        running = true;
    }
    private void registerInternel(Runnable runnable) {
        run.add(runnable);
    }
    private boolean isRunning() {
        return running;
    }
    public long getTimeInternel() {
        return time;
    }

    private void saveInternel() {
        file.getConfig().set("time",time);
        file.saveConfig();
    }
    //Public
    public static void start(SocialCore plugin) {
        clock = new Clock(plugin);
        clock.startClock();
    }
    public static void save() {
        if (clock == null || !clock.isRunning()) {
            throw new ClockNotStartedException();
        }
        clock.saveInternel();
    }
    public static long getTime() {
        if (clock == null || !clock.isRunning()) {
            throw new ClockNotStartedException();
        }
        return clock.getTimeInternel();
    }
    public static void register(Runnable runnable) {
        if (clock == null || !clock.isRunning()) {
            throw new ClockNotStartedException();
        }
        clock.registerInternel(runnable);
    }
}
