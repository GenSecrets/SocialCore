package com.nicholasdoherty.socialcore.components.marriages.commands.main.listall;

import co.aikar.commands.annotation.CommandPermission;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.marriages.types.Engagement;
import com.nicholasdoherty.socialcore.utils.MarriagesUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class Engagements {
    SocialCore sc;

    public Engagements(SocialCore plugin) { this.sc = plugin; }

    @CommandPermission("socialcore.marriage.listall.engagements")
    public void runCommand(Player player, String[] args) {
        final List<String> allEngagements = sc.save.getAllEngagements();
        int page = 0;
        int[] bounds = MarriagesUtil.paginateLists(page, args, allEngagements);
        int upperBound = bounds[0];
        int lowerBound = bounds[1];

        player.sendMessage(ChatColor.GOLD + "These are the engagements on the server: (Page " + (page + 1) + ')');
        if(upperBound - lowerBound > 0) {
            for(final String s : allEngagements.subList(lowerBound, upperBound)) {
                final Engagement e = sc.save.getEngagement(s);
                if(e != null){
                    player.sendMessage(ChatColor.GRAY+"- " +ChatColor.GREEN + e.getFutureSpouse1().getPlayerName() + " and " + e.getFutureSpouse2().getPlayerName() + " on " + e.getDate());
                } else {
                    sc.save.removeEngagement(s);
                }
            }
            if(allEngagements.size() - 1 > upperBound) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Type &c/engagements " + (page + 2) + "&6 to read the next page."));
            } else {
                player.sendMessage(ChatColor.GOLD + "No more engagements to show");
            }
        } else {
            player.sendMessage(ChatColor.GREEN + "None");
        }
    }
}
