package com.nicholasdoherty.socialcore.courts.courtroom.voting;

import com.nicholasdoherty.socialcore.courts.courtroom.Restricter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

/**
 * Created by john on 1/16/15.
 */
public class YayNayVote extends ChatVote implements ConfigurationSerializable {
    public YayNayVote(Restricter restricter) {
        super(restricter);
    }

    @Override
    public String summarizeResults() {
        int approvals = approvals();
        int disapprovals = disapprovals();
        if (votes() == 0) {
            return ChatColor.RED + "The vote resulted in a tie because nobody voted";
        }
        if (approvals == disapprovals) {
            return ChatColor.YELLOW + "The vote was " + approvals + " to " + disapprovals + ", a tie.";
        }else if (approvals > disapprovals) {
            return ChatColor.GREEN + "The vote was " + approvals + " to " + disapprovals + ", yay winning with " + percentWinString() + " of votes.";
        }else {
            return ChatColor.RED + "The vote was " + disapprovals + " to " + approvals + ", nay winning with " + percentWinString() + " of votes.";
        }
    }

    @Override
    public String message(VoteValue voteValue, boolean changing) {
        String message;
        if (changing) {
            message = "You have changed your vote to ";
        }else {
            message = "You have voted ";
        }
        if (voteValue == VoteValue.APPROVE) {
            message = ChatColor.GREEN + message + "yay.";
        }else if (voteValue == VoteValue.DISAPPROVE) {
            message = ChatColor.RED + message + "nay.";
        }else {
            message = ChatColor.YELLOW + message + "abstain.";
        }
        return message;
    }

    @Override
    public VoteValue voteValue(String text) {
        if (text.equalsIgnoreCase("yay")) {
            return VoteValue.APPROVE;
        }
        if (text.equalsIgnoreCase("nay")) {
            return VoteValue.DISAPPROVE;
        }
        if (text.equalsIgnoreCase("abstain")) {
            return VoteValue.ABSTAIN;
        }
        return null;
    }
    @Override
    public String[] helpMessage() {
        String[] helpMessage = {ChatColor.GREEN + "A yay/nay vote has been started",
                ChatColor.GRAY + "Type " + ChatColor.GREEN + "yay" + ChatColor.GRAY + " to vote for the argument, ",
                ChatColor.RED + "nay" + ChatColor.GRAY +" to vote against the argument, or " + ChatColor.YELLOW + "abstain to not vote or withdraw your vote."};
        return helpMessage;
    }
    public YayNayVote(Map<String, Object> map) {
        super(map);
    }

    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }
}
