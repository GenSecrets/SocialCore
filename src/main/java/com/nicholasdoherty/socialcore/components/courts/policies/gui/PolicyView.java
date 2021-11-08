package com.nicholasdoherty.socialcore.components.courts.policies.gui;

import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.components.courts.policies.Policy;
import com.voxmc.voxlib.gui.InventoryGUI;
import com.voxmc.voxlib.gui.views.PaginatedItemView;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by john on 9/12/16.
 */
public abstract class PolicyView extends PaginatedItemView {
    private Citizen viewer;
    
    public PolicyView(InventoryGUI inventoryGUI, Citizen viewer) {
        super(inventoryGUI, 54);
        this.viewer = viewer;
    }
    
    abstract List<Policy> getPolicies();
    
    @Override
    public void update() {
        setPaginatedItems(getPolicies().stream()
                .map(policy -> new PolicyIcon(this, Optional.of(policy), viewer)).collect(Collectors.toList()));
        super.update();
    }
    
    protected String getName() {
        return "Policies";
    }
    
    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null, 54, getName());
    }
    
    public Citizen getViewer() {
        return viewer;
    }
}
