package com.nicholasdoherty.socialcore.courts.inventorygui;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

/**
 * Created by john on 1/3/15.
 */
public class InventoryGUI {
    private InventoryView currentView;
    private Player player;
    private Inventory inventory;
    private boolean specialInterface = false;

    public InventoryGUI() {
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setCurrentView(InventoryView currentView) {
        if (this.currentView != null) {
            this.currentView.onClose();
        }
        this.currentView = currentView;
    }

    public void onClick(int slot, boolean right) {
        if (currentView != null) {
            currentView.onClick(slot,right);
        }
    }

    public Player getPlayer() {
        return player;
    }


    public Inventory getInventory() {
        return inventory;
    }

    public void update(Map<ClickItem, Integer> toUpdate, boolean clear) {
        if (clear) {
            inventory.clear();
        }
        for (ClickItem clickItem : toUpdate.keySet()) {
            int slot = toUpdate.get(clickItem);
            update(clickItem,slot);
        }
    }
    public void update(ClickItem clickItem, int slot) {
        inventory.setItem(slot,clickItem.itemstack());
    }
    public void updateInv() {
        if (player == null || inventory == null)
            return;
        player.updateInventory();
    }
    public void sendViewersMessage(String message) {
        if (inventory != null) {
            for (HumanEntity humanEntity : inventory.getViewers()) {
                if (humanEntity instanceof Player) {
                    Player p = (Player) humanEntity;
                    p.sendMessage(message);
                }
            }
        }
    }
    public void onClose() {
        if (currentView != null) {
            currentView.onClose();
        }
        UpdateInventoryTask.updateInv(getPlayer());
    }
    public void close() {
        getPlayer().closeInventory();
    }

    public void open() {
        inventory = currentView.getBaseInventory();
        currentView.activate();

        org.bukkit.inventory.InventoryView inventoryView = new org.bukkit.inventory.InventoryView() {
            @Override
            public Inventory getTopInventory() {
                return inventory;
            }

            @Override
            public Inventory getBottomInventory() {
                return bottomInventory();
            }

            @Override
            public HumanEntity getPlayer() {
                return player;
            }

            @Override
            public InventoryType getType() {
                return inventory.getType();
            }
        };
        player.openInventory(inventoryView);
        SocialCore.plugin.getInventoryGUIManager().add(player,this);
    }
    private  Inventory bottomInventory() {
        if (player == null) {
            return Bukkit.createInventory(null,InventoryType.PLAYER);
        }else {
            return player.getInventory();
        }
    }
    public void specialClose() {
        setSpecialInterface(true);
        close();
    }
    public void setSpecialInterface(boolean specialInterface) {
        this.specialInterface = specialInterface;
    }

    public boolean inSpecialInterface() {
        return specialInterface;
    }

   private static class UpdateInventoryTask extends BukkitRunnable {
       private Player p;

       public UpdateInventoryTask(Player p) {
           this.p = p;
       }

       @Override
       public void run() {
           if (p != null && p.isOnline()) {
               p.updateInventory();
           }
       }
       public static void updateInv(Player p) {
           new UpdateInventoryTask(p).runTaskLater(Courts.getCourts().getPlugin(),3);
       }
   }
}
