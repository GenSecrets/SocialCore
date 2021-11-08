package com.nicholasdoherty.socialcore.components.courts.courtroom.voting;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by john on 1/16/15.
 */
public class VotingListener implements Listener {
    VotingManager votingManager;

    public VotingListener(VotingManager votingManager) {
        this.votingManager = votingManager;
        Bukkit.getPluginManager().registerEvents(this, Courts.getCourts().getPlugin());
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onVote(AsyncPlayerChatEvent event) {
        for (Vote vote : votingManager.getVotes()) {
            if (vote instanceof ChatVote) {
                ChatVote chatVote = (ChatVote) vote;
                chatVote.onChat(event.getPlayer(),event.getMessage());
            }
        }
    }
}
