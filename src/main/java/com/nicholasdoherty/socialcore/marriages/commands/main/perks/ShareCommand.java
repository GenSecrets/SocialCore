package com.nicholasdoherty.socialcore.marriages.commands.main.perks;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.utils.ErrorUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias("share")
@CommandPermission("socialcore.marriage.perks.share")
@Description("Manage sharing different things with your spouse!")
public class ShareCommand extends BaseCommand {
    SocialCore sc;

    public ShareCommand(SocialCore plugin) { this.sc = plugin; }

    @Default
    public void onCommand(CommandSender sender) {
        if(ErrorUtil.isNotPlayer(sender)){
            return;
        }

        final Player player = (Player) sender;
        final SocialPlayer sp1 = sc.save.getSocialPlayer(player.getName());

        final Player player2 = Bukkit.getServer().getPlayer(sp1.getMarriedTo());

        if(!sp1.isMarried()) {
            player.sendMessage(ChatColor.RED + "You are not married.");
            return;
        }
        if(player2 == null || !player2.isOnline()) {
            player.sendMessage(ChatColor.RED + "Your Significant Other is offline!");
            return;
        }
        if(!player.hasPermission("socialcore.marriage.perks.viewspouseinventory")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to view your spouse's inventory.");
            return;
        }
        final double maxDistanceSquared = sc.marriageConfig.maxShareInventDistanceSquared;
        if(player2.getWorld().getName().equalsIgnoreCase(player.getWorld().getName()) && player2.getLocation().distanceSquared(player2.getLocation()) <= maxDistanceSquared) {
            player.openInventory(player2.getInventory());
            player2.sendMessage(ChatColor.AQUA + player.getName() + " is viewing your inventory!");
            player.sendMessage(ChatColor.AQUA + player2.getName() + "'s inventory");
            //if(player2.getOpenInventory() != null) {
            //    player2.closeInventory();
            //}
        } else {
            player.sendMessage(ChatColor.RED + "Your Significant Other is too far away!");
        }
    }
}
