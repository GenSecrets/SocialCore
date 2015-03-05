package com.nicholasdoherty.socialcore.utils;

import com.nicholasdoherty.socialcore.courts.Courts;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by john on 1/21/15.
 */
public class CourtsTickLater extends BukkitRunnable{
    private Runnable run;

    private CourtsTickLater(Runnable run) {
        this.run = run;
    }

    @Override
    public void run() {
        run.run();
    }
    public static void runTickLater(Runnable run) {
        runTickLater(run,1);
    }
    public static void runTickLater(Runnable run, long ticks) {
        new CourtsTickLater(run).runTaskLater(Courts.getCourts().getPlugin(),ticks);
    }
}
