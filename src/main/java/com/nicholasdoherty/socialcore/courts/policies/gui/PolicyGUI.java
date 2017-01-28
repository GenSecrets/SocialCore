package com.nicholasdoherty.socialcore.courts.policies.gui;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import org.bukkit.entity.Player;

/**
 * Created by john on 9/13/16.
 */
public class PolicyGUI extends InventoryGUI {
    private MainPolicyView mainPolicyView;
    private Citizen citizen;

    public PolicyGUI(Citizen citizen) {
        this.citizen = citizen;
        setCurrentView(new MainPolicyView(this,citizen));
    }

    public static void createAndOpen(Player p) {
        Citizen citizen = Courts.getCourts().getCitizenManager().toCitizen(p);
        PolicyGUI policyGUI = new PolicyGUI(citizen);
        policyGUI.setPlayer(p);
        policyGUI.open();
    }
}
