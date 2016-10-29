package com.nicholasdoherty.socialcore.courts.judges.secretaries.gui;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/6/15.
 */
public class SecretaryGUI extends InventoryGUI {
    private Courts courts;
    private Secretary secretary;

    public SecretaryGUI(Courts courts, Secretary secretary) {
        this.courts = courts;
        this.secretary = secretary;
        setCurrentView(new SecretaryCasePaginatedView(this));
    }

    public Secretary getSecretary() {
        return secretary;
    }

    public Courts getCourts() {
        return courts;
    }

    public static void createAndOpen(Player p, Secretary secretary) {
        SecretaryGUI secretaryGUI = new SecretaryGUI(Courts.getCourts(), secretary);
        secretaryGUI.setPlayer(p);
        secretaryGUI.open();
    }
}
