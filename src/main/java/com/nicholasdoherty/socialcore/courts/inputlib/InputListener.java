package com.nicholasdoherty.socialcore.courts.inputlib;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by john on 1/9/15.
 */
public class InputListener implements Listener{
    private InputLib inputLib;
    private Plugin plugin;

    public InputListener(InputLib inputLib, Plugin plugin) {
        this.inputLib = inputLib;
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void asyncChat(final AsyncPlayerChatEvent event) {
        if (!inputLib.isActive(event.getPlayer().getUniqueId()))
            return;
        final String message = event.getMessage();
        new BukkitRunnable(){
            @Override
            public void run() {
                inputLib.perform(event.getPlayer().getUniqueId(),message);
            }
        }.runTask(plugin);
        event.setCancelled(true);
    }
    @EventHandler
    public void quit(PlayerQuitEvent event) {
        inputLib.remove(event.getPlayer().getUniqueId());
    }
}
