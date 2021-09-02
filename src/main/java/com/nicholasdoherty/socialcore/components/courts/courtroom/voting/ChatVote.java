package com.nicholasdoherty.socialcore.components.courts.courtroom.voting;

import com.nicholasdoherty.socialcore.components.courts.courtroom.Restricter;
import com.nicholasdoherty.socialcore.utils.CourtsTickLater;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

/**
 * Created by john on 1/16/15.
 */
public abstract class ChatVote extends Vote {
    public ChatVote(final Restricter restricter) {
        super(restricter);
    }
    
    public ChatVote(final Map<String, Object> map) {
        super(map);
    }
    
    public synchronized void onChat(final Player p, final String text) {
        final UUID uuid = p.getUniqueId();
        if(!canVote(uuid)) {
            return;
        }
        boolean changing = false;
        if(hasVoted(uuid)) {
            changing = true;
        }
        final VoteValue approves = voteValue(text);
        if(approves == null) {
            return;
        }
        vote(uuid, approves);
        final String message = message(approves, changing);
        CourtsTickLater.runTickLater(() -> {
            if(!p.isOnline()) {
                return;
            }
            p.sendMessage(message);
        });
    }
    
    public abstract String message(VoteValue voteValue, boolean changing);
    
    public abstract VoteValue voteValue(String text);
}
