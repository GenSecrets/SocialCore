package com.nicholasdoherty.socialcore.courts.policies.gui;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.courts.policies.Policy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by john on 9/12/16.
 */
public class MainPolicyView extends PolicyView {
    public MainPolicyView(InventoryGUI inventoryGUI, Citizen viewer) {
        super(inventoryGUI, viewer);
    }

    @Override
    List<Policy> getPolicies() {
        return Courts.getCourts().getPolicyManager().allPolicies().stream()
                .filter(policy -> policy.getState() != Policy.State.UNFINISHED
                && policy.getState() != Policy.State.UNCONFIRMED)
                .sorted((policy1,policy2) -> {
                    if (policy1.getState() == Policy.State.FAILED) {
                        if (policy2.getState() == Policy.State.FAILED) {
                            return policy1.getId() - policy2.getId();
                        }
                        return -1;
                    }
                    return policy1.getId() - policy2.getId();
                }).collect(Collectors.toList());
    }
}
