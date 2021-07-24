package com.nicholasdoherty.socialcore.courts.stall;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.Courts;
import org.bukkit.ChatColor;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by john on 1/6/15.
 */
public class StallListener implements Listener {
    Courts courts;
    StallManager stallManager;
    Set<UUID> recentlyClicked = new HashSet<>();
    
    public StallListener(final Courts courts, final StallManager stallManager) {
        this.courts = courts;
        this.stallManager = stallManager;
        final SocialCore plugin = courts.getPlugin();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void interactStallEvent(final PlayerInteractEvent event) {
        final Action a = event.getAction();
        if(a != Action.LEFT_CLICK_BLOCK && a != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Block b = event.getClickedBlock();
        if(b == null) {
            return;
        }
        final Location loc = b.getLocation();
        final Stall stall = stallManager.getStall(loc);
        if(stall == null) {
            return;
        }
        final UUID uuid = event.getPlayer().getUniqueId();
        if(recentlyClicked.contains(uuid)) {
            return;
        }
        event.setCancelled(true);
        stall.onClick(event.getPlayer());
        recentlyClicked.add(uuid);
        new BukkitRunnable() {
            @Override
            public void run() {
                recentlyClicked.remove(uuid);
            }
        }.runTaskLater(courts.getPlugin(), 2);
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void fix183(final PlayerEditBookEvent event) {
        final Player p = event.getPlayer();
        final int slot = event.getSlot();
        final ItemStack orig = event.getPlayer().getInventory().getItem(slot).clone();
        final ItemMeta oldMeta = orig.getItemMeta().clone();
        if(orig.getType() == Material.WRITABLE_BOOK) {
            if(!event.isSigning()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(p.isOnline()) {
                            final ItemStack newItem = p.getInventory().getItem(slot);
                            if(newItem != null && newItem.getType() == Material.WRITABLE_BOOK) {
                                final ItemMeta itemMeta = newItem.getItemMeta();
                                if(oldMeta.getDisplayName() != null) {
                                    itemMeta.setDisplayName(oldMeta.getDisplayName());
                                }
                                if(oldMeta.hasLore()) {
                                    itemMeta.setLore(oldMeta.getLore());
                                }
                                newItem.setItemMeta(itemMeta);
                                for(final Enchantment enchantment : orig.getEnchantments().keySet()) {
                                    final int level = orig.getEnchantments().get(enchantment);
                                    newItem.addUnsafeEnchantment(enchantment, level);
                                }
                                p.updateInventory();
                            }
                        }
                    }
                }.runTaskLater(courts.getPlugin(), 1);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void preventCaseSigning(final PlayerEditBookEvent event) {
        final BookMeta bookMeta = event.getPreviousBookMeta();
        if(bookMeta != null && bookMeta.hasDisplayName() && bookMeta.getDisplayName().contains("Court Case") &&
                event.isSigning()) {
            Location loc = event.getPlayer().getLocation();
            bookMeta.setDisplayName(ChatColor.DARK_RED + "Court Case Document");
            bookMeta.setTitle(ChatColor.DARK_RED + "Court Case");
            bookMeta.setAuthor(event.getPlayer().getName());
            bookMeta.setPages(event.getNewBookMeta().getPages());
            bookMeta.setGeneration(BookMeta.Generation.TATTERED);
            bookMeta.setLore(
                    new ArrayList<String>(){{
                        add(ChatColor.DARK_GREEN+"Name: "+ChatColor.YELLOW+ event.getPlayer().getName());
                        add(ChatColor.DARK_GREEN + "Date: " + ChatColor.YELLOW + new SimpleDateFormat("MMM d, yyyy").format(new Date()));
                        add(ChatColor.DARK_GREEN + "Location: " + ChatColor.YELLOW + loc.getWorld().getName());
                        add(ChatColor.YELLOW + String.valueOf(loc.getBlockX()) + ChatColor.GRAY + "x "
                                + ChatColor.YELLOW + String.valueOf(loc.getBlockY()) + ChatColor.GRAY + "y "
                                + ChatColor.YELLOW + String.valueOf(loc.getBlockZ()) + ChatColor.GRAY + "z");
                        add(ChatColor.GOLD + "Official Court Document");
                        add(ChatColor.BLUE + "" + ChatColor.ITALIC + "\"This case booklet is");
                        add(ChatColor.BLUE + "" + ChatColor.ITALIC + "official deed used by");
                        add(ChatColor.BLUE + "" + ChatColor.ITALIC + "the court system to");
                        add(ChatColor.BLUE + "" + ChatColor.ITALIC + "define your request");
                        add(ChatColor.BLUE + "" + ChatColor.ITALIC + "for any court issues.\"");
                        }
                    }
            );
            //bookMeta.setPages(event.getPreviousBookMeta().getPages());
            bookMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            event.setNewBookMeta(bookMeta);
        }
    }
}
