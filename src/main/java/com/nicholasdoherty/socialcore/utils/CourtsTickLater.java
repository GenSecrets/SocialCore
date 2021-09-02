package com.nicholasdoherty.socialcore.utils;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author amy
 * @since 7/26/18.
 */
@SuppressWarnings("unused")
public final class CourtsTickLater extends BukkitRunnable {
    private final Runnable run;
    
    private CourtsTickLater(final Runnable run) {
        this.run = run;
    }
    
    public static void runTickLater(final Runnable run) {
        runTickLater(run, 1);
    }
    
    public static void runTickLater(final Runnable run, final long ticks) {
        new CourtsTickLater(run).runTaskLater(Courts.getCourts().getPlugin(), ticks);
    }
    
    @Override
    public void run() {
        run.run();
    }
}