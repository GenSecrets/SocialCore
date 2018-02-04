package com.nicholasdoherty.socialcore.courts.elections.gui;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.ApprovalItem;
import com.nicholasdoherty.socialcore.utils.TextUtil;
import org.bukkit.ChatColor;

import java.util.List;

/**
 * Created by john on 1/6/15.
 */
public class JudgeApprovalItem extends ApprovalItem {
    ElectionJudgeView electionJudgeView;
    public JudgeApprovalItem(ElectionJudgeView inventoryView, Candidate approvedCitizen) {
        super(inventoryView, approvedCitizen);
        this.electionJudgeView = inventoryView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        super.click(right, shift);
        Courts.getCourts().getElectionManager().checkWin(Courts.getCourts().getElectionManager().getCurrentElection(), (Candidate) approvedCitizen);
        //electionJudgeView.update(this);
        electionJudgeView.getInventoryGUI().updateInv();
    }

    @Override
    protected List<String> lore() {
        List<String> lore = super.lore();
        int votes = approvedCitizen.votes();
        int requiredVotes = Courts.getCourts().getCourtsConfig().getJudgeRequiredVotes();
        String electPercent = ChatColor.DARK_AQUA + "Election Percent: " + TextUtil.formatDouble(approvedCitizen.electPercentage(), 2) + "%";
        lore.add(1,"(" + votes + "/" + requiredVotes + ") votes");
        lore.add(1,electPercent);
        return lore;
    }


}
