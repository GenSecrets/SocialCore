package com.nicholasdoherty.socialcore.marriages.commands.main.listall;

import co.aikar.commands.annotation.CommandPermission;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.marriages.types.Marriage;
import com.nicholasdoherty.socialcore.utils.MarriagesUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class Marriages {
    SocialCore sc;

    public Marriages(SocialCore plugin){ this.sc = plugin; }

    @CommandPermission("socialcore.marriage.listall.marriages")
    public void runCommand(Player player, String[] args){
        final List<String> allMarriages = sc.save.getAllMarriageNames();
        int page = 0;
        int[] bounds = MarriagesUtil.paginateLists(page, args, allMarriages);
        int upperBound = bounds[0];
        int lowerBound = bounds[1];

        player.sendMessage(ChatColor.GOLD + "These are the marriages on the server: (Page " + (page + 1) + ')');
        if(upperBound - lowerBound > 0) {
            for(final String s : allMarriages.subList(lowerBound, upperBound)) {
                final Marriage m = sc.save.getMarriage(s);
                if(m != null) {
                    player.sendMessage(ChatColor.GRAY+"- " +ChatColor.GREEN + m.getSpouse1().getPlayerName() + " to " + m.getSpouse2().getPlayerName() + " on " + m.getDate() + " by " + m.getPriest());
                } else {
                    sc.save.removeMarriage(s);
                }
            }
            if(allMarriages.size() - 1 > upperBound) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Type &c/marriages " + (page + 2) + "&6 to read the next page."));
            } else {
                player.sendMessage(ChatColor.GOLD + "No more marriages to show");
            }
        } else {
            player.sendMessage(ChatColor.GREEN + "None");
        }
    }
}
