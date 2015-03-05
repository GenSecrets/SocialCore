package com.nicholasdoherty.socialcore.courts.mastercases;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.CaseInfoClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.PaginatedItemView;
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
            items.add(0,new CaseInfoClickItem(caze));
        }
        this.setPaginatedItems(items);
    }
    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null, this.getSize(), "Master Case View");
    }
}
