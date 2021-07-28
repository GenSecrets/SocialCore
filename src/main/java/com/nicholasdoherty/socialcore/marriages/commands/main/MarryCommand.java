package com.nicholasdoherty.socialcore.marriages.commands.main;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.marriages.MarriageGem;
import com.nicholasdoherty.socialcore.marriages.types.Engagement;
import com.nicholasdoherty.socialcore.marriages.types.Marriage;
import com.nicholasdoherty.socialcore.utils.ErrorUtil;
import com.nicholasdoherty.socialcore.utils.MarriagesUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@CommandAlias("marry")
@CommandPermission("socialcore.marriage.marry")
@Description("Marry two engaged players together!")
public class MarryCommand extends BaseCommand {
    SocialCore sc;

    public MarryCommand(SocialCore plugin) { this.sc = plugin; }

    @Default
    public void onCommand(CommandSender sender, String[] args) {
        if(ErrorUtil.isNotPlayer(sender)){
            return;
        }

        final Player player = (Player) sender;
        if(args.length > 1) {
            final Player p1 = Bukkit.getServer().getPlayer(args[0]);
            if(p1 == null) {
                player.sendMessage(ChatColor.RED + "Player '" + args[0] + "' cannot be found. Are they online?");
                return;
            }
            final Player p2 = Bukkit.getServer().getPlayer(args[1]);
            if(p2 == null) {
                player.sendMessage(ChatColor.RED + "Player '" + args[1] + "' cannot be found. Are they online?");
                return;
            }

            final SocialPlayer player1 = sc.save.getSocialPlayer(args[0]);
            final SocialPlayer player2 = sc.save.getSocialPlayer(args[1]);

            if(!player1.isEngaged()) {
                player.sendMessage(ChatColor.RED + player1.getPlayerName() + " is not engaged!");
                return;
            }
            if(!player2.isEngaged()) {
                player.sendMessage(ChatColor.RED + player2.getPlayerName() + " is not engaged!");
                return;
            }
            if(!player1.getEngagedTo().equalsIgnoreCase(player2.getPlayerName())) {
                player.sendMessage(ChatColor.RED + player1.getPlayerName() + " is not engaged to " + player2.getPlayerName());
                return;
            }
            if(!player2.getEngagedTo().equalsIgnoreCase(player1.getPlayerName())) {
                player.sendMessage(ChatColor.RED + player2.getPlayerName() + " is not engaged to " + player1.getPlayerName());
                return;
            }

            p1.sendMessage(ChatColor.GREEN + "Father " + player.getName() + " is beginning the ceremony...");
            if(player.getLocation().distance(p1.getLocation()) > sc.marriageConfig.priestDistance) {
                player.sendMessage(ChatColor.RED + "The priest is too far away from " + player1.getPlayerName() + '!');
                p1.sendMessage(ChatColor.RED + "The priest is too far away from " + player1.getPlayerName() + '!');
                p2.sendMessage(ChatColor.RED + "The priest is too far away from " + player1.getPlayerName() + '!');
                return;
            }
            if(player.getLocation().distance(p2.getLocation()) > sc.marriageConfig.priestDistance) {
                player.sendMessage(ChatColor.RED + "The priest is too far away from " + player2.getPlayerName() + '!');
                p1.sendMessage(ChatColor.RED + "The priest is too far away from " + player2.getPlayerName() + '!');
                p2.sendMessage(ChatColor.RED + "The priest is too far away from " + player2.getPlayerName() + '!');
                return;
            }
            if(p1.getLocation().distance(p2.getLocation()) > sc.marriageConfig.coupleDistance) {
                player.sendMessage(ChatColor.RED + player1.getPlayerName() + " is too far away from " + player2.getPlayerName() + '!');
                p1.sendMessage(ChatColor.RED + player1.getPlayerName() + " is too far away from " + player2.getPlayerName() + '!');
                p2.sendMessage(ChatColor.RED + player1.getPlayerName() + " is too far away from " + player2.getPlayerName() + '!');
                return;
            }

            final Engagement e = sc.save.getEngagement(player1, player2);
            final Marriage m = new Marriage(e.getFutureSpouse1(), e.getFutureSpouse2());
            m.setPriest(player.getName());
            final String dateBuilder = MarriagesUtil.getMonth() + ' ' + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ", " + Calendar.getInstance().get(Calendar.YEAR);
            m.setDate(dateBuilder);

            boolean okay = false;
            out:
            for(final ItemStack item : p1.getInventory().getContents()) {
                if(item != null) {
                    for(final MarriageGem gem : sc.marriageConfig.marriageGems) {
                        if(gem.getBlock() == item.getType()) {
                            if(item.getItemMeta() != null) {
                                if(item.getItemMeta().getDisplayName() != null) {
                                    if(item.getItemMeta().getDisplayName().contains(gem.getName())) {
                                        okay = true;
                                        break out;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(!okay) {
                player.sendMessage(ChatColor.RED + "Uh oh! " + player1.getPlayerName() + " has misplaced their marriage gem!");
                p1.sendMessage(ChatColor.RED + "Uh oh! " + player1.getPlayerName() + " has misplaced their marriage gem!");
                p2.sendMessage(ChatColor.RED + "Uh oh! " + player1.getPlayerName() + " has misplaced their marriage gem!");
                return;
            }

            okay = false;
            out:
            for(final ItemStack item : p2.getInventory().getContents()) {
                if(item != null) {
                    for(final MarriageGem gem : sc.marriageConfig.marriageGems) {
                        if(gem.getBlock() == item.getType()) {
                            if(item.getItemMeta() != null) {
                                if(item.getItemMeta().getDisplayName() != null) {
                                    if(item.getItemMeta().getDisplayName().contains(gem.getName())) {
                                        ItemMeta meta = MarriagesUtil.removeEngagementLine(item.getItemMeta(), player1, player2, m);
                                        item.setItemMeta(meta);
                                        p2.updateInventory();
                                        okay = true;
                                        break out;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if(!okay) {
                player.sendMessage(ChatColor.RED + "Uh oh! " + player2.getPlayerName() + " has misplaced their marriage gem!");
                p1.sendMessage(ChatColor.RED + "Uh oh! " + player2.getPlayerName() + " has misplaced their marriage gem!");
                p2.sendMessage(ChatColor.RED + "Uh oh! " + player2.getPlayerName() + " has misplaced their marriage gem!");
                return;
            }
            out:
            for(final ItemStack item : p1.getInventory().getContents()) {
                if(item != null) {
                    for(final MarriageGem gem : sc.marriageConfig.marriageGems) {
                        if(gem.getBlock() == item.getType()) {
                            if(item.getItemMeta() != null) {
                                if(item.getItemMeta().getDisplayName() != null) {
                                    if(item.getItemMeta().getDisplayName().contains(gem.getName())) {
                                        ItemMeta meta = MarriagesUtil.removeEngagementLine(item.getItemMeta(), player1, player2, m);
                                        item.setItemMeta(meta);
                                        p1.updateInventory();
                                        break out;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            sc.save.removeEngagement(e);
            player1.setEngaged(false);
            player1.setEngagedTo("");
            player1.setMarried(true);
            player1.setMarriedTo(player2.getPlayerName());
            player2.setEngaged(false);
            player2.setEngagedTo("");
            player2.setMarried(true);
            player2.setMarriedTo(player1.getPlayerName());
            sc.save.saveSocialPlayer(player1);
            sc.save.saveSocialPlayer(player2);
            sc.save.saveMarriage(m);

            player.sendMessage(ChatColor.GREEN + "You have married " + player1.getPlayerName() + " and " + player2.getPlayerName() + '!');
            String toSendPlayer1 = "You have taken " + player2.getPlayerName() + " to be your lawfully wedded spouse. Happy ever after!";
            String toSendPlayer2 = "You have taken " + player1.getPlayerName() + " to be your lawfully wedded spouse. Happy ever after!";
            p1.sendMessage(toSendPlayer1);
            p2.sendMessage(toSendPlayer2);
            for(final Player p : Bukkit.getServer().getOnlinePlayers()) {
                p.sendMessage(ChatColor.YELLOW + "[SocialCore] Father " + player.getName() + " has married " + player1.getPlayerName() + " and " + player2.getPlayerName() + "! Wish them a happy ever after!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /marry <player1> <player2>");
        }
    }
}
