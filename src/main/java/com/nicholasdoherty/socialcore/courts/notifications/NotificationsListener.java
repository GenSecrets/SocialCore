package com.nicholasdoherty.socialcore.courts.notifications;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.utils.CourtsTickLater;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

/**
 * Created by john on 1/21/15.
 */
public class NotificationsListener implements Listener {
    private Courts courts;
    private NotificationManager notificationManager;

    public NotificationsListener(Courts courts, NotificationManager notificationManager) {
        this.courts = courts;
        this.notificationManager = notificationManager;
        Plugin plugin = courts.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void login(PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        CourtsTickLater.runTickLater(new Runnable() {
            @Override
            public void run() {
                if (!p.isOnline())
                    return;
                if (p == null)
                    return;
                notificationManager.onLogin(p);
            }
        },3);
    }

    @EventHandler(ignoreCancelled = true)
    public void join(PlayerJoinEvent event) {
        notificationManager.login(event.getPlayer().getUniqueId());
    }
    @EventHandler(ignoreCancelled = true)
    public void leave(PlayerQuitEvent event) {
        notificationManager.logout(event.getPlayer().getUniqueId());
    }
}
