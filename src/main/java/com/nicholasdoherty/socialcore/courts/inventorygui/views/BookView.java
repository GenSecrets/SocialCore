package com.nicholasdoherty.socialcore.courts.inventorygui.views;

import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryView;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.BookPageItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.ChangeViewClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.GiveItemClickItem;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by john on 1/21/15.
 */
public class BookView extends PaginatedItemView {
    private BookMeta bookMeta;
    private InventoryView previousView;

    public BookView(InventoryGUI inventoryGUI, BookMeta bookMeta, InventoryView previousView) {
        super(inventoryGUI, 54);
        this.bookMeta = bookMeta;
        this.previousView = previousView;
        this.setStartEnd(9,44);
    }

    @Override
    public void initActiveItems() {
        if (bookMeta == null || bookMeta.getPages() == null || bookMeta.getPages().size() == 0)
            return;
        List<ClickItem> items = new ArrayList<>();
        List<List<String>> bookLoreLines = bookToLoreLines(bookMeta.getPages());
        for (List<String> lines : bookLoreLines) {
            BookPageItem bookPageItem = new BookPageItem(lines);
            items.add(bookPageItem);
        }
        setPaginatedItems(items);
        update();
    }

    @Override
    public void update() {
        addActiveItem(0, new ChangeViewClickItem(previousView) {
            @Override
            public ItemStack itemstack() {
                return new ItemStackBuilder(Material.PAPER).setName("Back").toItemStack();
            }
        });
        addActiveItem(4, new GiveItemClickItem(getInventoryGUI(),bookItem()));
        super.update();
    }
    private ItemStack bookItem() {
        ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);
        book.setItemMeta(bookMeta);
        return book;
    }
    @Override
    public Inventory getBaseInventory() {
        String name = "Book";
        if (bookMeta.getDisplayName() != null) {
            name = bookMeta.getDisplayName();
        }
        if (bookMeta.hasTitle()) {
            name = bookMeta.getTitle();
        }
        if (name.length() > 15) {
            name = name.substring(0,14);
        }
        return Bukkit.createInventory(null,54, name);
    }
    private static int MAX_CHARACTER = 50;
    private static List<List<String>> bookToLoreLines(List<String> pages) {
        List<List<String>> cappedPages = new ArrayList<>();
        for (String page : pages) {
            List<String> cappedLines = new ArrayList<>();
            List<String> temp = new ArrayList<>();
            Collections.addAll(temp, page.replace(ChatColor.COLOR_CHAR +"0","").split("\n"));
            for (String line : temp) {
                if (line.length() <= MAX_CHARACTER) {
                    cappedLines.add(line);
                }else {
                    Collections.addAll(cappedLines, splitIntoLine(line, MAX_CHARACTER));
                }
            }
            cappedPages.add(cappedLines);
        }
        return cappedPages;
    }
    private static String[] splitIntoLine(String input, int maxCharInLine){
        StringTokenizer tok = new StringTokenizer(input, " ");
        StringBuilder output = new StringBuilder(input.length());
        int lineLen = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();

            while(word.length() > maxCharInLine){
                output.append(word.substring(0, maxCharInLine-lineLen) + "\n");
                word = word.substring(maxCharInLine-lineLen);
                lineLen = 0;
            }

            if (lineLen + word.length() > maxCharInLine) {
                output.append("\n");
                lineLen = 0;
            }
            output.append(word + " ");

            lineLen += word.length() + 1;
        }
        return output.toString().split("\n");
    }
}
