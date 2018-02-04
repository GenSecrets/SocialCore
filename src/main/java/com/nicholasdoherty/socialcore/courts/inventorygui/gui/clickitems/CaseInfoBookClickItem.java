package com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems;

import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryView;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.BookView;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

/**
 * Created by john on 1/21/15.
 */
public class CaseInfoBookClickItem extends CaseInfoClickItem {
    private InventoryView view;

    public CaseInfoBookClickItem(Case caze, InventoryView view) {
        super(caze);
        this.view = view;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (caze != null && caze.getCaseBook() != null && caze.getCaseBook().getItemMeta() != null &&
                caze.getCaseBook().getItemMeta() instanceof BookMeta) {
            BookMeta bookMeta = (BookMeta) caze.getCaseBook().getItemMeta();
            BookView bookView = new BookView(view.getInventoryGUI(),bookMeta,view);
            view.getInventoryGUI().setCurrentView(bookView);
            bookView.activate();
        }
    }

    @Override
    public List<String> lore() {
        List<String> lore =  super.lore();
        lore.add(ChatColor.GRAY + "<Left click to view the details of the case>");
        return lore;
    }
}
