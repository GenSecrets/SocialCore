package com.nicholasdoherty.socialcore.courts.policies.gui;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.courts.policies.Policy;
import com.nicholasdoherty.socialcore.courts.policies.Policy.State;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by john on 9/12/16.
 */
public class MainPolicyView extends PolicyView {
    private final boolean failed;
    
    public MainPolicyView(final InventoryGUI inventoryGUI, final Citizen viewer, final boolean failed) {
        super(inventoryGUI, viewer);
        this.failed = failed;
    }
    
    @Override
    List<Policy> getPolicies() {
        if(failed) {
            return Courts.getCourts().getPolicyManager().allPolicies().stream()
                    .filter(policy -> policy.getState() == State.FAILED)
                    .sorted((policy1, policy2) -> {
                        if(policy1.getState() == State.FAILED) {
                            if(policy2.getState() == State.FAILED) {
                                return policy1.getId() - policy2.getId();
                            }
                            return -1;
                        }
                        return policy1.getId() - policy2.getId();
                    }).collect(Collectors.toList());
        } else {
            return Courts.getCourts().getPolicyManager().allPolicies().stream()
                    .filter(policy -> policy.getState() != State.UNFINISHED
                            && policy.getState() != State.UNCONFIRMED && policy.getState() != State.FAILED)
                    .sorted((policy1, policy2) -> {
                        if(policy1.getState() == State.FAILED) {
                            if(policy2.getState() == State.FAILED) {
                                return policy1.getId() - policy2.getId();
                            }
                            return -1;
                        }
                        return policy1.getId() - policy2.getId();
                    }).collect(Collectors.toList());
        }
    }
}
