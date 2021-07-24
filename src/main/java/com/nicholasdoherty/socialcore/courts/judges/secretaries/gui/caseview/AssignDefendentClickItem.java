package com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.inputlib.InputLib;
import com.nicholasdoherty.socialcore.courts.inputlib.InputRunnable;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.gui.InventoryView;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/9/15.
 */
public class AssignDefendentClickItem implements ClickItem {
    private InventoryView inventoryView;
    private AssignDefendant assignDefendant;
    private Case caze;

    public AssignDefendentClickItem(InventoryView inventoryView, AssignDefendant assignDefendant, Case caze) {
        this.inventoryView = inventoryView;
        this.assignDefendant = assignDefendant;
        this.caze = caze;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        //todo check if inventory close is called on opening new inventory
        if (right)
            return;
        Player p = inventoryView.getInventoryGUI().getPlayer();
        inventoryView.getInventoryGUI().close();
        inventoryView.getInventoryGUI().setSpecialInterface(true);
        InputLib inputLib = Courts.getCourts().getPlugin().getInputLib();
        inputLib.add(p.getUniqueId(), new TextInputDefendantRunnable());
        inputLib.clearChat(p);
        inputLib.sendMessage(p, SecCaseView.replaceNouns(ChatColor.GREEN + "Please type the defendant's name exactly into chat and press enter.",caze.getCaseCategory()));
    }

    @Override
    public ItemStack itemstack() {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(SecCaseView.replaceNouns(ChatColor.GREEN + "File Defendant",caze.getCaseCategory()));
        List<String> lore = new ArrayList<>();
        if (caze.getDefendent() != null) {
            lore.add(SecCaseView.replaceNouns(ChatColor.GREEN + "Current defendant:",caze.getCaseCategory()));
            lore.add(ChatColor.GREEN + caze.getDefendent().getName());
        }
        lore.add(SecCaseView.replaceNouns(ChatColor.GRAY + "<Left click to file a defendant>",caze.getCaseCategory()));
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    class TextInputDefendantRunnable implements InputRunnable{
        @Override
        public void run(String input) {
            if (input.equalsIgnoreCase("cancel") || assignDefendant.assignDefendant(input)) {
                inventoryView.getInventoryGUI().setSpecialInterface(false);
                 inventoryView.getInventoryGUI().open();
            }else {
                InputLib inputLib = Courts.getCourts().getPlugin().getInputLib();
                inputLib.add(inventoryView.getInventoryGUI().getPlayer().getUniqueId(), this);
            }
        }
    }
    public interface AssignDefendant {
        public boolean assignDefendant(String name);
    }
}
