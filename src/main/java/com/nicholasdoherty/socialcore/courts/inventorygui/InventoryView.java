package com.nicholasdoherty.socialcore.courts.inventorygui;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 1/3/15.
 */
public abstract class InventoryView<IG extends InventoryGUI> {
    IG inventoryGUI;
    protected Map<ClickItem, Integer> activeItems = new HashMap<>();
    protected Map<Integer, ClickItem> activeItemsBySlot = new HashMap<>();
    public InventoryView(IG inventoryGUI) {
        this.inventoryGUI = inventoryGUI;
    }
    public abstract void initActiveItems();
    public void onClick(int slot, boolean right) {
        if (activeItemsBySlot.containsKey(slot)) {
            activeItemsBySlot.get(slot).click(right);
        }
    }

    public Map<ClickItem, Integer> getActiveItems() {
        return activeItems;
    }
    public void clearActiveItems() {
        activeItems.clear();
        activeItemsBySlot.clear();
    }
    public void addActiveItem(int slot, ClickItem clickItem) {
        if (activeItemsBySlot.containsKey(slot)) {
            activeItems.remove(activeItemsBySlot.get(slot));
        }
        activeItems.put(clickItem,slot);
        activeItemsBySlot.put(slot,clickItem);
    }
    public void removeActiveItem(int slot) {
        if (activeItemsBySlot.containsKey(slot)) {
            ClickItem clickItem = activeItemsBySlot.get(slot);
            activeItemsBySlot.remove(slot);
            activeItems.remove(clickItem);
        }
    }
    public void removeActiveItem(ClickItem clickItem) {
        int slot = activeItems.get(clickItem);
        activeItems.remove(clickItem);
        activeItemsBySlot.remove(slot);
    }
    public IG getInventoryGUI() {
        return inventoryGUI;
    }
    public int getSlot(ClickItem clickItem) {
        return activeItems.get(clickItem);
    }

    public abstract void update();
    public void reactivate() {
        update();
        inventoryGUI.update(activeItems,true);
    }
    public  void update(ClickItem clickItem) {
        if (!activeItems.containsKey(clickItem)) {
            return;
        }
        getInventoryGUI().update(clickItem, activeItems.get(clickItem));
    }
    public void activate() {
        clearActiveItems();
        initActiveItems();
        inventoryGUI.update(activeItems,true);
    }
    public void onClose() {

    }
    public abstract Inventory getBaseInventory();
}
