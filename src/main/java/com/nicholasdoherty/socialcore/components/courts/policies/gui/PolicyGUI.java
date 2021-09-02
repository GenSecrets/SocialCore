package com.nicholasdoherty.socialcore.components.courts.policies.gui;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.voxmc.voxlib.gui.InventoryGUI;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import org.bukkit.entity.Player;

/**
 * Created by john on 9/13/16.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class PolicyGUI extends InventoryGUI {
    private MainPolicyView mainPolicyView;
    private final Citizen citizen;
    private final boolean failed;
    
    public PolicyGUI(final Citizen citizen, final boolean failed) {
        this.citizen = citizen;
        this.failed = failed;
        setCurrentView(new MainPolicyView(this, citizen, failed));
    }
    
    public static void createAndOpen(final Player p, final boolean failed) {
        final Citizen citizen = Courts.getCourts().getCitizenManager().toCitizen(p);
        final PolicyGUI policyGUI = new PolicyGUI(citizen, failed);
        policyGUI.setPlayer(p);
        policyGUI.open();
    }
}
