package com.nicholasdoherty.socialcore.courts.policies.gui;

import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.courts.policies.Policy;
import org.bukkit.entity.Player;

/**
 * Created by john on 9/12/16.
 */
public class UnconfirmedPolicyGUI extends InventoryGUI {
    private UnconfirmedPolicyView unconfirmedPolicyView;

    public UnconfirmedPolicyGUI(Citizen citizen, Policy policy) {
        this.unconfirmedPolicyView = new UnconfirmedPolicyView(this,citizen,policy);
        setCurrentView(unconfirmedPolicyView);
    }

    public static void createAndOpen(Player p, Citizen citizen, Policy policy) {
        UnconfirmedPolicyGUI unconfirmedPolicyGUI = new UnconfirmedPolicyGUI(citizen,policy);
        unconfirmedPolicyGUI.setPlayer(p);
        unconfirmedPolicyGUI.open();
    }
}
