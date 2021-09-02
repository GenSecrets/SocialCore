package com.nicholasdoherty.socialcore.components.marriages.commands.main;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.marriages.types.Engagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("unengage")
@CommandPermission("socialcore.marriage.unengage")
@Description("Break up with the person you are engaged to.")
public class UnEngageCommand extends BaseCommand {
    SocialCore sc;

    public UnEngageCommand(SocialCore plugin) { this.sc = plugin; }

    @Default
    public void onCommand(CommandSender sender, String[] args) {
        final Player p1 = (Player) sender;
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
            sender.sendMessage(ChatColor.RED + "You are not engaged.");
            return;
        }
        engagement.getFutureSpouse1().setEngaged(false);
        engagement.getFutureSpouse2().setEngaged(false);
        sc.save.removeEngagement(engagement);
        final Player hus = Bukkit.getPlayer(engagement.getFutureSpouse1().getPlayerName());
        final Player wife = Bukkit.getPlayer(engagement.getFutureSpouse2().getPlayerName());
        if(hus.isOnline()) {
            hus.sendMessage(ChatColor.BLUE + "You have been unengaged.");
        }
        if(wife.isOnline()) {
            hus.sendMessage(ChatColor.BLUE + "You have been unengaged.");
        }
    }
}
