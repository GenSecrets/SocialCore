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
@SuppressWarnings({"unchecked", "unused"})
public class PaginatedItemView extends InventoryView {
    private final int size;
    private List<ClickItem> paginatedItems; //Max 45 per page
    private int page;
    private int start = -1;
    private int end = -1;
    
    public PaginatedItemView(final InventoryGUI inventoryGUI, final List<ClickItem> paginatedItems, final int size, final int page) {
        super(inventoryGUI);
        this.paginatedItems = paginatedItems;
        this.page = page;
        this.size = size;
    }
    
    public PaginatedItemView(final InventoryGUI inventoryGUI, final int size) {
        super(inventoryGUI);
        this.size = size;
    }
    
    public void setStartEnd(final int start, final int end) {
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
    
    public void setPage(final int page) {
        this.page = page;
    }
    
    public int perPage() {
        if(start == -1) {
            return size - 9;
        }
        return end - start + 1 - 9;
    }
    
    public int getStart() {
        if(start == -1) {
            return 0;
        }
        return start;
    }
    
    public List<ClickItem> getPaginatedItems() {
        return paginatedItems;
    }
    
    public void setPaginatedItems(final List<ClickItem> paginatedItems) {
        this.paginatedItems = paginatedItems;
    }
    
    public int getEnd() {
        if(end == -1) {
            return size - 1;
        }
        return end;
    }
    
    @Override
    public void update() {
        if(paginatedItems == null || paginatedItems.isEmpty()) {
            return;
        }
        final int perPage = perPage();
        final int start = getStart();
        final int end = getEnd();
        boolean hasNextPage = false;
        if(paginatedItems.size() > perPage * (page + 1)) {
            hasNextPage = true;
        }
        boolean hasPrevPage = false;
        if(page > 0) {
            hasPrevPage = true;
        }
        final int prevId = end - 8;
        if(hasPrevPage) {
            addActiveItem(prevId, new PrevPageClickItem(this));
        } else {
            removeActiveItem(prevId);
        }
        if(hasNextPage) {
            addActiveItem(end, new NextPageClickItem(this));
        } else {
            removeActiveItem(end);
        }
        final int listStart = page * perPage;
        for(int i = 0; i < perPage; i++) {
            final int itemI = i + listStart;
            if(itemI < paginatedItems.size()) {
                addActiveItem(i + start, paginatedItems.get(itemI));
            } else {
                removeActiveItem(i + start);
            }
        }
        getInventoryGUI().update(activeItems, true);
    }
    
    public int getSize() {
        return size;
    }
    
    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null, size);
    }
}
