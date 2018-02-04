package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.CalendarGUI;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.CalanderRunnable;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.CancelAction;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.ValidTimeSelector;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/14/15.
 */
public class RescheduleCourtDateClickItem implements ClickItem {
    JudgeBaseView judgeBaseView;

    public RescheduleCourtDateClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (right)
            return;
        Player p = judgeBaseView.getInventoryGUI().getPlayer();
        CalendarGUI.createAndOpen(p, new CalanderRunnable() {
            @Override
            public void run(long time) {
                judgeBaseView.assignDate(time);
                judgeBaseView.getInventoryGUI().open();
            }
        }, new ValidTimeSelector() {
            @Override
            public boolean isValid(DateTime dateTime) {
                if (dateTime.isAfter(DateTime.now().plusMinutes(20))) {
                    return true;
                }
                return false;
            }
        }, new CancelAction() {
            @Override
            public void onCancel() {
                judgeBaseView.getInventoryGUI().open();
            }
        }, Courts.getCourts().getDefaultDayGetter());
    }

    @Override
    public ItemStack itemstack() {
        ItemStack itemStack = new ItemStack(Material.WATCH);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Set court date");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "<Left click to set a court date>");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
