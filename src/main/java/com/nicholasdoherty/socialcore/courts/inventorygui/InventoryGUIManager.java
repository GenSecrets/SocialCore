package com.nicholasdoherty.socialcore.courts.inventorygui;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.inventorygui.listeners.InventoryGUIListener;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/6/15.
 */
public class InventoryGUIManager {
    Map<Player, InventoryGUI> openGUIs = new HashMap<>();
    SocialCore plugin;


    public InventoryGUIManager(SocialCore plugin) {
        this.plugin = plugin;
        new InventoryGUIListener(plugin,this);
    }
    public void add(Player p, InventoryGUI inventoryGUI) {
        openGUIs.put(p,inventoryGUI);
    }
    public InventoryGUI getGUI(Player p) {
        return openGUIs.get(p);
    }

    public void closeAll() {
        for (Player p : openGUIs.keySet()) {
            if (p.isOnline()) {
                p.closeInventory();
            }
        }
        openGUIs.clear();
    }
    public void remove(Player p) {
        if (!openGUIs.containsKey(p))
            return;
        openGUIs.remove(p);
    }
    public void close(Player p) {
        if (!openGUIs.containsKey(p))
            return;
        if (p.isOnline()) {
            p.closeInventory();
        }
        openGUIs.remove(p);
    }
    public boolean hasOpen(Player p) {
        return openGUIs.containsKey(p);
    }
}
