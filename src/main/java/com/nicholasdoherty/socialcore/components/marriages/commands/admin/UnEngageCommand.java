package com.nicholasdoherty.socialcore.components.marriages.commands.admin;

import co.aikar.commands.annotation.CommandPermission;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.marriages.types.Engagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UnEngageCommand {
    SocialCore sc;
    public UnEngageCommand(SocialCore plugin) { this.sc = plugin; }

    @CommandPermission("socialcore.marriage.admin.unengage")
    public void runCommand(Player player, String[] args){
        if(args.length != 1) {
            player.sendMessage("Usage: /marriage admin unengage <player>");
            return;
        }
        final Player p1 = Bukkit.getPlayer(args[0]);
        if(p1 == null) {
            player.sendMessage(ChatColor.RED + args[0] + " is not a valid player.");
            return;
        }
        Engagement engagement = null;
        for(final String eName : sc.save.getAllEngagements()) {
            final Engagement engagement1 = sc.save.getEngagement(eName);
            if(engagement1.getFutureSpouse1().getPlayerName().equalsIgnoreCase(p1.getName())) {
                engagement = engagement1;
            }
            if(engagement1.getFutureSpouse2().getPlayerName().equalsIgnoreCase(p1.getName())) {
                engagement = engagement1;
            }
        }
        if(engagement == null) {
            player.sendMessage(ChatColor.RED + p1.getName() + " is not engaged.");
            return;
        }
        engagement.getFutureSpouse1().setEngaged(false);
        engagement.getFutureSpouse2().setEngaged(false);
        final Player hus = Bukkit.getPlayer(engagement.getFutureSpouse1().getPlayerName());
        final Player wife = Bukkit.getPlayer(engagement.getFutureSpouse2().getPlayerName());
        if(hus.isOnline()) {
            hus.sendMessage(ChatColor.BLUE + "You have been unengaged.");
        }
        if(wife.isOnline()) {
            hus.sendMessage(ChatColor.BLUE + "You have been unengaged.");
        }
        sc.save.removeEngagement(engagement);
        player.sendMessage(ChatColor.GREEN + "You have successfully unengaged " + engagement.getFutureSpouse1().getPlayerName() + " and " + engagement.getFutureSpouse2().getPlayerName());
    }
}
