package com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryView;

/**
 * Created by john on 1/9/15.
 */
public abstract class ChangeViewClickItem implements ClickItem {
    InventoryView inventoryView;

    public ChangeViewClickItem(InventoryView inventoryView) {
        this.inventoryView = inventoryView;
    }

    @Override
    public void click(boolean right) {
        inventoryView.getInventoryGUI().setCurrentView(inventoryView);
        inventoryView.activate();
    }
}
