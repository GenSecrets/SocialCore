package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.gui.views.CalendarGUI;
import org.joda.time.DateTime;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/14/15.
 */
public class RescheduleCourtDateClickItem implements ClickItem {
    JudgeBaseView judgeBaseView;
    
    public RescheduleCourtDateClickItem(final JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }
    
    @Override
    public void click(final boolean right, final boolean shift) {
        if(right) {
            return;
        }
        final Player p = judgeBaseView.getInventoryGUI().getPlayer();
        CalendarGUI.createAndOpen(p, time -> {
            judgeBaseView.assignDate(time);
            judgeBaseView.getInventoryGUI().open();
        }, dateTime -> dateTime.isAfter(DateTime.now().plusMinutes(3)),
                () -> judgeBaseView.getInventoryGUI().open(), Courts.getCourts().getDefaultDayGetter());
    }
    
    @Override
    public ItemStack itemstack() {
        final ItemStack itemStack = new ItemStack(Material.CLOCK);
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Set court date");
        final List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "<Left click to set a court date>");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
