package com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.courtroom.actions.JailDefendent;
import com.nicholasdoherty.socialcore.components.courts.courtroom.judgeview.JudgeBaseView;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.gui.views.CalendarGUI;
import org.joda.time.DateTime;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
@SuppressWarnings("unused")
public class JailDefendantClickItem implements ClickItem {
    JudgeBaseView judgeBaseView;
    
    public JailDefendantClickItem(final JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }
    
    @Override
    public void click(final boolean right, final boolean shift) {
        if(right) {
            return;
        }
        final Player p = judgeBaseView.getInventoryGUI().getPlayer();
        CalendarGUI.createAndOpen(p, time -> {
            judgeBaseView.getCourtSession().addPostCourtAction(new JailDefendent(time));
            judgeBaseView.getInventoryGUI().open();
        }, dateTime -> dateTime.isAfter(DateTime.now()), () -> judgeBaseView.getInventoryGUI().open(), Courts.getCourts().getDefaultDayGetter());
    }
    
    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.IRON_BARS)
                .setName(ChatColor.RED + "Jail Defendant")
                .addLore(ChatColor.GRAY + "<Left click to jail",
                        ChatColor.GRAY + "the defendant>")
                .toItemStack();
    }
}
