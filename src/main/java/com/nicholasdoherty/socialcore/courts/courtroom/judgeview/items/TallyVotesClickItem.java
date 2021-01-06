package com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items;

import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.JudgeBaseView;
import com.nicholasdoherty.socialcore.courts.courtroom.voting.Vote;
import com.voxmc.voxlib.gui.ClickItem;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 1/14/15.
 */
public class TallyVotesClickItem implements ClickItem {
    JudgeBaseView judgeBaseView;

    public TallyVotesClickItem(JudgeBaseView judgeBaseView) {
        this.judgeBaseView = judgeBaseView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        CourtSession courtSession = judgeBaseView.getCourtSession();
        Vote vote = courtSession.getVote();
        if (vote == null) {
            judgeBaseView.getInventoryGUI().sendViewersMessage(ChatColor.RED + " No vote ongoing");
            return;
        }
        courtSession.closeVote();
        String results = vote.summarizeResults();
        courtSession.getCourtRoom().sendMessage(results);
    }

    @Override
    public ItemStack itemstack() {
        return new ItemStackBuilder(Material.HOPPER)
                .setName(ChatColor.AQUA + "Tally Up Votes")
                .addLore(ChatColor.GRAY + "<Left click to ",
                        ChatColor.GRAY + "tally up the votes",
                        ChatColor.GRAY + "and announce them",
                        ChatColor.GRAY +"in chat>").toItemStack();
    }
}
