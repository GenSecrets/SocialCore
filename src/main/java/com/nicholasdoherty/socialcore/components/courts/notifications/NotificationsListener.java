package com.nicholasdoherty.socialcore.components.courts.notifications;

import com.nicholasdoherty.socialcore.components.courts.Courts;
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
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class NotificationsListener implements Listener {
    private final Courts courts;
    private final NotificationManager notificationManager;
    
    public NotificationsListener(final Courts courts, final NotificationManager notificationManager) {
        this.courts = courts;
        this.notificationManager = notificationManager;
        final Plugin plugin = courts.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void login(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        CourtsTickLater.runTickLater(() -> {
            if(!p.isOnline()) {
                return;
            }
            notificationManager.onLogin(p);
        }, 3);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void join(final PlayerJoinEvent event) {
        notificationManager.login(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void leave(final PlayerQuitEvent event) {
        notificationManager.logout(event.getPlayer().getUniqueId());
    }
}
