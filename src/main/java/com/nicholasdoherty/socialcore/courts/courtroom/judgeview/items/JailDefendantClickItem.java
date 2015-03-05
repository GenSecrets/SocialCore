package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.courtroom.actions.JailDefendent;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.CalendarGUI;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.CalanderRunnable;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.CancelAction;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.calander.ValidTimeSelector;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joda.time.DateTime;

/**
 * Created by john on 1/14/15.
 */
public class JailDefendantClickItem implements ClickItem {
    JudgeBaseView judgeBaseView;

    public JailDefendantClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right) {
        if (right)
            return;
        Player p = judgeBaseView.getInventoryGUI().getPlayer();
        CalendarGUI.createAndOpen(p, new CalanderRunnable() {
            @Override
            public void run(long time) {
                judgeBaseView.getCourtSession().addPostCourtAction(new JailDefendent(time));
                judgeBaseView.getInventoryGUI().open();
            }
        }, new ValidTimeSelector() {
            @Override
            public boolean isValid(DateTime dateTime) {
                if (dateTime.isAfter(DateTime.now())) {
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
        return new ItemStackBuilder(Material.IRON_FENCE)
                .setName(ChatColor.RED + "Jail Defendant")
                .addLore(ChatColor.GRAY + "<Left click to jail",
                        ChatColor.GRAY + "the defendant>")
                .toItemStack();
    }
}
