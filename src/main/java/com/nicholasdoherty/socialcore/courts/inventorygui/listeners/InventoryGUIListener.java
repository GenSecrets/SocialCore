package com.nicholasdoherty.socialcore.courts.inventorygui.listeners;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * Created by john on 1/6/15.
 */
public class InventoryGUIListener implements Listener {
    SocialCore plugin;
    InventoryGUIManager inventoryGUIManager;

    public InventoryGUIListener(SocialCore plugin, InventoryGUIManager inventoryGUIManager) {
        this.plugin = plugin;
        this.inventoryGUIManager = inventoryGUIManager;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }
    @EventHandler
    public void disable(PluginDisableEvent event) {
        if (event.getPlugin() == SocialCore.plugin) {
            inventoryGUIManager.closeAll();
        }
    }
    @EventHandler
    public void logout(PlayerQuitEvent event) {
        inventoryGUIManager.close(event.getPlayer());
    }
    @EventHandler
    public void in(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        Player p = (Player) event.getPlayer();
        InventoryGUI inventoryGUI = inventoryGUIManager.getGUI(p);
        if (inventoryGUI == null)
            return;
        if (inventoryGUI != null && inventoryGUI.inSpecialInterface()) {
            return;
        }
        if (inventoryGUIManager.hasOpen(p)) {
            p.updateInventory();
        }
        inventoryGUI.onClose();
        inventoryGUIManager.remove((Player) event.getPlayer());
    }
    @EventHandler
    public void click(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        Player p = (Player) event.getWhoClicked();
        if (!inventoryGUIManager.hasOpen(p))
            return;
        event.setCancelled(true);
        boolean right = false;
        InventoryAction action = event.getAction();
        if (action == InventoryAction.PICKUP_HALF) {
            right = true;
        }
        InventoryGUI inventoryGUI = inventoryGUIManager.getGUI(p);
        inventoryGUI.onClick(event.getSlot(),right);
    }

}
