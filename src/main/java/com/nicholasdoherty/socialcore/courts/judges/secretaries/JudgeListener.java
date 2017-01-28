package com.nicholasdoherty.socialcore.courts.judges.secretaries;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.JudgeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by john on 2/26/15.
 */
public class JudgeListener implements Listener {
    private JudgeManager judgeManager;
    private Courts courts;
    public JudgeListener(JudgeManager judgeManager) {
        this.judgeManager = judgeManager;
        courts = Courts.getCourts();
        courts.getPlugin().getServer().getPluginManager().registerEvents(this,courts.getPlugin());
    }

    @EventHandler
    public void login(PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        new BukkitRunnable(){
            @Override
            public void run() {
                if (p != null && p.isOnline()) {
                    judgeManager.setPrefix(p);
                }
            }
        }.runTaskLater(courts.getPlugin(),2);
        judgeManager.setPerms(p);
    }

    @EventHandler
    public void logout(PlayerQuitEvent event) {
        judgeManager.revertPrefix(event.getPlayer());
    }
}
