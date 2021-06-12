package com.nicholasdoherty.socialcore.courts.policies.gui;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.voxmc.voxlib.gui.InventoryGUI;
import com.voxmc.voxlib.gui.InventoryView;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.courts.policies.Policy;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by john on 9/12/16.
 */
public class UnconfirmedPolicyView extends InventoryView {
    private Citizen viewer;
    private Policy policy;

    public UnconfirmedPolicyView(InventoryGUI inventoryGUI, Citizen viewer, Policy policy) {
        super(inventoryGUI);
        this.viewer = viewer;
        this.policy = policy;
    }

    @Override
    public void initActiveItems() {
        update();
    }

    @Override
    public void update() {
        policy = Courts.getCourts().getPolicyManager().updateIfStale(policy);
        clearActiveItems();

        addActiveItem(4, new PolicyIcon(this, Optional.of(policy), viewer));
        List<Judge> judges = Courts.getCourts().getJudgeManager().getJudges()
                .stream().sorted((j1, j2) -> j1.getName().compareTo(j2.getName())).collect(Collectors.toList());
        for (int i = 0; i < judges.size(); i++) {
            int inventoryIndex = i + 9;
            Judge judge = judges.get(i);
            JudgeItem judgeItem = new JudgeItem(judge, policy.getConfirmApprovals().contains(new Citizen(judge)));
            addActiveItem(inventoryIndex, judgeItem);
            if (inventoryIndex > 54) {
                break;
            }
        }
    }

    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null,54,"Unconfirmed Policy");
    }
}
