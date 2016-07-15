package com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeCaseView;

import com.nicholasdoherty.socialcore.courts.Courts;
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
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/9/15.
 */
public class AssignTimeClickItem implements ClickItem {
    JudgeCaseView judgeCaseView;

    public AssignTimeClickItem(JudgeCaseView judgeCaseView) {
        this.judgeCaseView = judgeCaseView;
    }

    @Override
    public void click(boolean right) {
        if (right)
            return;
        ////todo remove test
        //if (!right) {
        //    judgeCaseView.assignDate(new Date().getTime()+10*1000);
        //    judgeCaseView.activate();
        //    return;
        //}
        Player p = judgeCaseView.getInventoryGUI().getPlayer();
        CalendarGUI.createAndOpen(p, new CalanderRunnable() {
            @Override
            public void run(long time) {
                if (Courts.getCourts().getDefaultDayGetter().hasCorutDate(new DateTime(time))) {
                    return;
                }
                judgeCaseView.assignDate(time);
                judgeCaseView.getInventoryGUI().open();
            }
        }, new ValidTimeSelector() {
            @Override
            public boolean isValid(DateTime dateTime) {
                if (dateTime.isAfter(DateTime.now().plusMinutes(20))) {
                    return true;
                }
                if (Courts.getCourts().getDefaultDayGetter().calendarEvents(new LocalDate(dateTime)).length > 1) {
                    return false;
                }
                if (Courts.getCourts().getDefaultDayGetter().hasCorutDate(dateTime)) {
                    return false;
                }
                return false;
            }
        }, new CancelAction() {
            @Override
            public void onCancel() {
                judgeCaseView.getInventoryGUI().open();
            }
        }, Courts.getCourts().getDefaultDayGetter());
        //judgeCaseView.getInventoryGUI().close();
        //InputLib inputLib = Courts.getCourts().getPlugin().getInputLib();
        //inputLib.add(p.getUniqueId(), new TextInputTimeRunnable());
        //inputLib.clearChat(p);
        //inputLib.sendMessage(p,ChatColor.RED + "All times " + TextUtil.getServerTimeZoneDisplayName());
        //inputLib.sendMessage(p, ChatColor.GREEN + "Please the desired court date in this format: " + TextUtil.dateFormat());
        //inputLib.sendMessage(p, ChatColor.GREEN + "For example, it is currently " + TextUtil.formatDate(new Date().getTime()));
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
    //class TextInputTimeRunnable implements InputRunnable {
    //    @Override
    //    public void run(String input) {
    //        long timeParsed = TextUtil.dateFromString(input);
    //        boolean valid = verifyTime(timeParsed);
    //        if (valid) {
    //            judgeCaseView.assignDate(timeParsed);
    //            judgeCaseView.getInventoryGUI().sendViewersMessage(ChatColor.GREEN + "Set court date to " + TextUtil.formatDate(timeParsed));
    //            judgeCaseView.getInventoryGUI().open();
    //            return;
    //        }
    //        if (input.equalsIgnoreCase("cancel")) {
    //            judgeCaseView.getInventoryGUI().open();
    //        }else {
    //            judgeCaseView.getInventoryGUI().getPlayer().sendMessage(ChatColor.RED + "Time invalid. (Too soon?)");
    //            InputLib inputLib = Courts.getCourts().getPlugin().getInputLib();
    //            inputLib.add(judgeCaseView.getInventoryGUI().getPlayer().getUniqueId(), this);
    //        }
    //    }
    //    public boolean verifyTime(long time) {
    //        long currentTime = new Date().getTime();
    //        if (time < currentTime+60000) {
    //            return false;
    //        }
    //        return true;
    //    }
    //}
}
