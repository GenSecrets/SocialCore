package com.nicholasdoherty.socialcore.courts.elections.gui;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.ApprovalItem;
import com.voxmc.voxlib.util.TextUtil;
import org.bukkit.ChatColor;

import java.util.List;

/**
 * Created by john on 1/6/15.
 */
public class JudgeApprovalItem extends ApprovalItem {
    ElectionJudgeView electionJudgeView;
    
    public JudgeApprovalItem(final ElectionJudgeView inventoryView, final Candidate approvedCitizen) {
        super(inventoryView, approvedCitizen);
        electionJudgeView = inventoryView;
    }
    
    @Override
    public void click(final boolean right, final boolean shift) {
        super.click(right, shift);
        Courts.getCourts().getElectionManager().checkWin(Courts.getCourts().getElectionManager().getCurrentElection(), (Candidate) approvedCitizen);
        //electionJudgeView.update(this);
        electionJudgeView.getInventoryGUI().updateInv();
    }
    
    @Override
    protected List<String> lore() {
        final List<String> lore = super.lore();
        final int votes = approvedCitizen.votes();
        final int requiredVotes = Courts.getCourts().getCourtsConfig().getJudgeRequiredVotes();
        final String electPercent = ChatColor.DARK_AQUA + "Election Percent: " + TextUtil.formatDouble(approvedCitizen.electPercentage(), 2) + '%';
        lore.add(1, "(" + votes + '/' + requiredVotes + ") votes");
        lore.add(1, electPercent);
        return lore;
    }
}
