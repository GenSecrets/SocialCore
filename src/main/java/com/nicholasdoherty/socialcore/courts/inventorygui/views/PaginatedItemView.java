package com.nicholasdoherty.socialcore.courts.inventorygui.views;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryView;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.NextPageClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.PrevPageClickItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.List;

/**
 * Created by john on 1/6/15.
 */
public class PaginatedItemView extends InventoryView {
    private List<ClickItem> paginatedItems; //Max 45 per page
    private int page,size;
    private int start = -1;
    private int end = -1;

    public PaginatedItemView(InventoryGUI inventoryGUI, List<ClickItem> paginatedItems, int size, int page) {
        super(inventoryGUI);
        this.paginatedItems = paginatedItems;
        this.page = page;
        this.size = size;
    }

    public PaginatedItemView(InventoryGUI inventoryGUI, int size) {
        super(inventoryGUI);
        this.size = size;
    }

    public void setPaginatedItems(List<ClickItem> paginatedItems) {
        this.paginatedItems = paginatedItems;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setStartEnd(int start, int end) {
        this.start = start;
        this.end = end;
    }


    @Override
    public void initActiveItems() {
        update();
    }

    public int getPage() {
        return page;
    }
    public int perPage() {
        if (start == -1) {
            return size-9;
        }
        return end-start+1-9;
    }

    public int getStart() {
        if (start == -1) {
            return 0;
        }
        return start;
    }

    public List<ClickItem> getPaginatedItems() {
        return paginatedItems;
    }

    public int getEnd() {
        if (end == -1) {
            return size-1;
        }
        return end;
    }

    @Override
    public void update() {
        if (paginatedItems == null || paginatedItems.isEmpty()) {
            return;
        }
        int perPage = perPage();
        int start = getStart();
        int end = getEnd();
        boolean hasNextPage = false;
        if (paginatedItems.size() > perPage*(page+1)) {
            hasNextPage = true;
        }
        boolean hasPrevPage = false;
        if (page > 0) {
            hasPrevPage = true;
        }
        int prevId = end-8;
        if (hasPrevPage) {
            this.addActiveItem(prevId,new PrevPageClickItem(this));
        }else {
            this.removeActiveItem(prevId);
        }
        int nextId = end;
        if (hasNextPage) {
            this.addActiveItem(nextId,new NextPageClickItem(this));
        }else {
            this.removeActiveItem(nextId);
        }
        int listStart = page*perPage;
        for (int i = 0; i < perPage; i++) {
            int itemI = i + listStart;
            if (itemI < paginatedItems.size()) {
                this.addActiveItem(i+start, paginatedItems.get(itemI));
            }else {
                this.removeActiveItem(i+start);
            }
        }
        getInventoryGUI().update(activeItems,true);
    }

    public int getSize() {
        return size;
    }

    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null, size);
    }
}
