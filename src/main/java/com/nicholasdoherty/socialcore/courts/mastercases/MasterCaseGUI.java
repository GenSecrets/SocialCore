package com.nicholasdoherty.socialcore.courts.mastercases;

import com.voxmc.voxlib.gui.inventorygui.InventoryGUI;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/11/15.
 */
public class MasterCaseGUI extends InventoryGUI {
    public MasterCaseGUI() {
        this.setCurrentView(new MasterCaseView(this));
    }
    public static void createAndOpen(Player p) {
        MasterCaseGUI masterCaseGUI = new MasterCaseGUI();
        masterCaseGUI.setPlayer(p);
        masterCaseGUI.open();
    }
}
