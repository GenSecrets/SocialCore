package com.nicholasdoherty.socialcore.courts.courtroom.voting;

import com.nicholasdoherty.socialcore.courts.courtroom.Restricter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

/**
 * Created by john on 1/16/15.
 */
public class GulityInnocentVote extends ChatVote implements ConfigurationSerializable{

    public GulityInnocentVote(Restricter restricter) {
        super(restricter);
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
            message = ChatColor.RED + message + "guilty.";
        }else if (voteValue == VoteValue.DISAPPROVE) {
            message = ChatColor.GREEN + message + "innocent.";
        }else {
            message = ChatColor.YELLOW + message + "abstain.";
        }
        return message;
    }

    @Override
    public VoteValue voteValue(String text) {
        if (text.equalsIgnoreCase("guilty")) {
            return VoteValue.APPROVE;
        }
        if (text.equalsIgnoreCase("innocent")) {
            return VoteValue.DISAPPROVE;
        }
        if (text.equalsIgnoreCase("abstain")) {
            return VoteValue.ABSTAIN;
        }
        return null;
    }

    @Override
    public String[] helpMessage() {
        String[] helpMessage = {ChatColor.GREEN + "A guilty/innocent vote has been started",
           ChatColor.GRAY + "Type " + ChatColor.GREEN + "innocent" + ChatColor.GRAY + " to vote the defendant innocent, ",
        ChatColor.RED + "guilty" + ChatColor.GRAY +" to vote guilty, or " + ChatColor.YELLOW + "abstain to not vote or withdraw your vote."};
        return helpMessage;
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
            return ChatColor.RED + "The defendant was voted guilty, " + approvals + " to " + disapprovals + ", " + percentWinString() + ".";
        }else {
            return ChatColor.GREEN + "The defendant was voted NOT guilty, " + disapprovals + " to " + approvals + ", " + percentWinString() + ".";
        }
    }
    public GulityInnocentVote(Map<String, Object> map) {
        super(map);
    }

    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }
}
