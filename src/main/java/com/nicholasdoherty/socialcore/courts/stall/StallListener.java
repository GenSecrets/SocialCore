package com.nicholasdoherty.socialcore.courts.stall;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.Courts;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by john on 1/6/15.
 */
public class StallListener implements Listener {
    Courts courts;
    StallManager stallManager;
    public StallListener(Courts courts, StallManager stallManager) {
        this.courts = courts;
        this.stallManager = stallManager;
        SocialCore plugin = courts.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    Set<UUID> recentlyClicked = new HashSet<>();
    @EventHandler
    public void interactStallEvent(PlayerInteractEvent event) {
        Action a = event.getAction();
        if (a != Action.LEFT_CLICK_BLOCK && a != Action.RIGHT_CLICK_BLOCK)
            return;
        Block b = event.getClickedBlock();
        if (b == null)
            return;
        Location loc = b.getLocation();
        Stall stall = stallManager.getStall(loc);
        if (stall == null) {
            return;
        }
        final UUID uuid = event.getPlayer().getUniqueId();
        if (recentlyClicked.contains(uuid))
            return;
        event.setCancelled(true);
        stall.onClick(event.getPlayer());
        recentlyClicked.add(uuid);
        new BukkitRunnable(){
            @Override
            public void run() {
                recentlyClicked.remove(uuid);
            }
        }.runTaskLater(courts.getPlugin(),2);
    }

    @EventHandler
    public void preventCaseSigning(PlayerEditBookEvent event) {
        BookMeta bookMeta = event.getPreviousBookMeta();
        if (bookMeta != null && bookMeta.hasDisplayName() && bookMeta.getDisplayName().contains("Court Case") &&
                event.isSigning()) {
            event.setSigning(false);
        }
    }
}
