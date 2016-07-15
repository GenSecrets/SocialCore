package com.nicholasdoherty.socialcore.titles;

import com.nicholasdoherty.socialcore.utils.ColoredTagsUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by john on 7/2/15.
 */
public class TitleListener implements Listener {
    private TitleManager titleManager;

    public TitleListener(TitleManager titleManager) {
        this.titleManager = titleManager;
        Bukkit.getServer().getPluginManager().registerEvents(this,titleManager.getPlugin());
    }

    @EventHandler
    public void logout(PlayerQuitEvent event) {
        ColoredTagsUtil.removeTitle(event.getPlayer());
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void login(PlayerJoinEvent event) {
        titleManager.addTitle(event.getPlayer());
    }
}
