package com.nicholasdoherty.socialcore.components.courts.mastercases;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.inventorygui.gui.clickitems.CaseInfoBookClickItem;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.gui.InventoryGUI;
import com.voxmc.voxlib.gui.views.PaginatedItemView;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/11/15.
 */
public class MasterCaseView extends PaginatedItemView {
    public MasterCaseView(InventoryGUI inventoryGUI) {
        super(inventoryGUI, 54);
        List<ClickItem> items = new ArrayList<>();
        for (Case caze : Courts.getCourts().getCaseManager().getCases()) {
            items.add(0,new CaseInfoBookClickItem(caze, this));
        }
        this.setPaginatedItems(items);
    }
    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null, this.getSize(), "Master Case View");
    }
}
