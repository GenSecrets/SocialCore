package com.nicholasdoherty.socialcore.courts.stall;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.Courts;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
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
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void fix183(PlayerEditBookEvent event) {
        final Player p = event.getPlayer();
        final int slot = event.getSlot();
        final ItemStack orig = event.getPlayer().getInventory().getItem(slot).clone();
        final ItemMeta oldMeta = orig.getItemMeta().clone();
        if (orig != null && orig.getType() == Material.BOOK_AND_QUILL) {
            final BookMeta newMeta = event.getNewBookMeta();
            if (!event.isSigning()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (p.isOnline()) {
                            ItemStack newItem = p.getInventory().getItem(slot);
                            if (newItem != null && newItem.getType() == Material.BOOK_AND_QUILL) {
                                ItemMeta itemMeta = newItem.getItemMeta();
                                if (oldMeta.getDisplayName() != null) {
                                    itemMeta.setDisplayName(oldMeta.getDisplayName());
                                }
                                if (oldMeta.hasLore()) {
                                    itemMeta.setLore(oldMeta.getLore());
                                }
                                newItem.setItemMeta(itemMeta);
                                for (Enchantment enchantment : orig.getEnchantments().keySet()) {
                                    int level = orig.getEnchantments().get(enchantment);
                                    newItem.addUnsafeEnchantment(enchantment,level);
                                }
                                p.updateInventory();
                            }
                        }
                    }
                }.runTaskLater(courts.getPlugin(),1);
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void preventCaseSigning(PlayerEditBookEvent event) {
        BookMeta bookMeta = event.getPreviousBookMeta();
        if (bookMeta != null && bookMeta.hasDisplayName() && bookMeta.getDisplayName().contains("Court Case") &&
                event.isSigning()) {
            event.setSigning(false);
        }
    }
}
