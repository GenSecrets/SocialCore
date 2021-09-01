package com.nicholasdoherty.socialcore.marriages.commands.main;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.marriages.MarriageGem;
import com.nicholasdoherty.socialcore.marriages.Marriages;
import com.nicholasdoherty.socialcore.marriages.types.Divorce;
import com.nicholasdoherty.socialcore.marriages.types.Engagement;
import com.nicholasdoherty.socialcore.utils.ErrorUtil;
import com.nicholasdoherty.socialcore.utils.MarriagesUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@CommandAlias("propose")
@CommandPermission("socialcore.marriage.propose")
@Description("Propose to another player in attempt to become engaged!")
public class ProposeCommand extends BaseCommand {
    SocialCore sc;

    public ProposeCommand(SocialCore plugin) { this.sc = plugin; }

    @Default
    public void onCommand(CommandSender sender, String[] args) {
        if(ErrorUtil.isNotPlayer(sender)){
            return;
        }

        Player player = (Player)sender;

        if(args.length > 0) {
            final Player p = Bukkit.getServer().getPlayer(args[0]);
            if(p == null) {
                player.sendMessage(ChatColor.RED + "Could not find player '" + args[0] + "'. Are they online?");
                return;
            }
            if(p.getUniqueId() == ((Player) sender).getUniqueId()) {
                player.sendMessage(ChatColor.RED + "You aren't allowed to marry yourself!");
                return;
            }

            final SocialPlayer proposeTo = sc.save.getSocialPlayer(player.getUniqueId().toString());
            final SocialPlayer proposeFrom = sc.save.getSocialPlayer(p.getUniqueId().toString());
            for(final String divorceName : sc.save.getAllDivorces()) {
                if(divorceName.contains(proposeFrom.getPlayerName()) || divorceName.contains(proposeTo.getPlayerName())) {
                    final Divorce divorce = sc.save.getDivorce(divorceName);
                    if(divorce != null) {
                        try {
                            final Date date = MarriagesUtil.parserSDF().parse(divorce.getDate());
                            final long divorceTime = date.getTime();
                            final long currentTime = new Date().getTime();
                            final long elapsedMillis = currentTime - divorceTime;
                            if(elapsedMillis < sc.marriageConfig.divorceProposeCooldownMillis) {
                                if(divorceName.contains(proposeFrom.getPlayerName())) {
                                    player.sendMessage(ChatColor.RED + "You have divorced too recently to propose!");
                                } else {
                                    player.sendMessage(ChatColor.RED + "The player you are proposing too has divorced too recently!");
                                }
                                return;
                            }
                        } catch(final ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            switch(sc.marriages.getStatus(proposeTo)) {
                case Married:
                    player.sendMessage(ChatColor.RED + "You are already married! This isn't a polygamy state!");
                    return;
                case Engaged:
                    player.sendMessage(ChatColor.RED + "You are already engaged! This isn't a polygamy state!");
                    return;
                case ProposeTo:
                    player.sendMessage(ChatColor.RED + "You have already proposed to someone! This isn't a polygamy state!");
                    return;
                case ProposeFrom:
                    player.sendMessage(ChatColor.RED + "You have already been proposed to! Type /propose deny to crush your previous lover's heart!");
                    return;
                default:
                    break;
            }

            switch(sc.marriages.getStatus(proposeFrom)) {
                case Married:
                    player.sendMessage(ChatColor.RED + "That player is already married! This isn't a polygamy state!");
                    return;
                case Engaged:
                    player.sendMessage(ChatColor.RED + "That player is already engaged! This isn't a polygamy state!");
                    return;
                case ProposeTo:
                    player.sendMessage(ChatColor.RED + "That player has already proposed to someone! This isn't a polygamy state!");
                    return;
                case ProposeFrom:
                    player.sendMessage(ChatColor.RED + "That player has already been proposed to! This isn't a polygamy state!");
                    return;
                default:
                    break;
            }

            if(!player.hasPermission("sc.propose")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to marry another player!");
                return;
            }
            if(!p.hasPermission("sc.propose")) {
                player.sendMessage(ChatColor.RED + proposeFrom.getPlayerName() + " does not have permission to marry!");
                return;
            }

            boolean okay = false;

            out:
            for(final ItemStack item : player.getInventory().getContents()) {
                if(item != null) {
                    for(final MarriageGem gem : sc.marriageConfig.marriageGems) {
                        if(gem.getBlock() == item.getType()) {
                            if(item.getItemMeta() != null) {
                                if(item.getItemMeta().getDisplayName() != null) {
                                    if(item.getItemMeta().getDisplayName().contains(gem.getName())) {
                                        boolean used = false;
                                        if(item.getItemMeta().hasLore()) {
                                            if(!item.getItemMeta().getLore().isEmpty()) {
                                                if(item.getItemMeta().getLore().get(0).contains("4ever")) {
                                                    used = true;
                                                }
                                            }
                                        }
                                        if(!used) {
                                            okay = true;
                                            break out;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(!okay) {
                player.sendMessage(ChatColor.RED + "You must have a marriage gem in order to propose!");
                return;
            }

            sc.marriages.proposals.put(proposeFrom, proposeTo);
            player.sendMessage(ChatColor.GREEN + "You have asked " + p.getName() + " for their hand in marriage!");
            p.sendMessage(ChatColor.GREEN + player.getName() + " is asking for your hand in marriage! Type '/propose accept' to accept it, or '/propose deny' to crush their heart!");
            return;
        }
    }

    @Subcommand("cancel")
    @Description("Cancel an active proposal invitation.")
    public void onCommandCancel(CommandSender sender, String[] args) {

    }

    @Subcommand("deny")
    @Description("Deny a proposal.")
    public void onCommandDeny(CommandSender sender, String[] args) {
        if(ErrorUtil.isNotPlayer(sender)){
            return;
        }

        Player player = (Player)sender;
        final SocialPlayer proposeFrom = sc.save.getSocialPlayer(player.getUniqueId().toString());
        if(sc.marriages.getStatus(proposeFrom) == Marriages.Status.ProposeFrom) {
            final SocialPlayer proposeTo = sc.marriages.proposals.get(proposeFrom);
            final Player p = Bukkit.getServer().getPlayer(proposeTo.getPlayerName());
            if(p != null) {
                p.sendMessage(ChatColor.DARK_RED + proposeFrom.getPlayerName() + " has declined your hand in marriage! :(");
            }
            player.sendMessage(ChatColor.DARK_RED + "You have declined " + proposeTo.getPlayerName() + "'s hand in marriage!");
            sc.marriages.proposals.remove(proposeFrom);
        } else {
            player.sendMessage(ChatColor.RED + "You have not been proposed to!");
        }
    }

    @Subcommand("accept")
    @Description("Accept a proposal.")
    public void onCommandAccept(CommandSender sender, String[] args) {
        if(ErrorUtil.isNotPlayer(sender)){
            return;
        }

        Player player = (Player)sender;
        final SocialPlayer proposeFrom = sc.save.getSocialPlayer(player.getUniqueId().toString());
        if(sc.marriages.getStatus(proposeFrom) == Marriages.Status.ProposeFrom) {
            final SocialPlayer proposeTo = sc.marriages.proposals.get(proposeFrom);
            final Player p = Bukkit.getServer().getPlayer(proposeTo.getPlayerName());
            if(p != null) {
                p.sendMessage(ChatColor.GREEN + proposeFrom.getPlayerName() + " has accepted your hand in marriage! Congratulations!");
            }
            player.sendMessage(ChatColor.GREEN + "You have accepted " + proposeTo.getPlayerName() + "'s hand in marriage! Congratulations!");
            sc.marriages.proposals.remove(proposeFrom);

            final Engagement e = new Engagement(proposeTo, proposeFrom);
            final String dateBuilder = MarriagesUtil.getMonth() + ' ' + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ", " + Calendar.getInstance().get(Calendar.YEAR);
            e.setDate(dateBuilder);
            e.setTime(System.currentTimeMillis());

            proposeFrom.setEngaged(true);
            proposeFrom.setEngagedTo(proposeTo.getPlayerName());
            proposeTo.setEngaged(true);
            proposeTo.setEngagedTo(proposeFrom.getPlayerName());

            sc.save.saveEngagement(e);
            sc.save.saveSocialPlayer(proposeFrom);
            sc.save.saveSocialPlayer(proposeTo);

            out:
            for(final ItemStack item : p.getInventory().getContents()) {
                if(item != null) {
                    for(final MarriageGem gem : sc.marriageConfig.marriageGems) {
                        if(gem.getBlock() == item.getType()) {
                            if(item.getItemMeta() != null) {
                                if(item.getItemMeta().getDisplayName() != null) {
                                    if(item.getItemMeta().getDisplayName().contains(gem.getName())) {
                                        final ItemMeta meta = item.getItemMeta();
                                        final List<String> l = new ArrayList<>();
                                        if(meta.getLore() != null) {
                                            l.addAll(meta.getLore());
                                        }
                                        l.add(proposeTo.getPlayerName() + " + " + proposeFrom.getPlayerName() + " 4ever");//
                                        l.add("Engaged on " + e.getDate());
                                        meta.setLore(l);
                                        item.setItemMeta(meta);
                                        player.getInventory().addItem(item);
                                        p.updateInventory();
                                        player.updateInventory();
                                        break out;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Bukkit.getServer().getOnlinePlayers().stream().filter(pr -> pr.hasPermission("sc.priest")).forEach(pr -> pr.sendMessage(ChatColor.GREEN + proposeTo.getPlayerName() + " and " + proposeFrom.getPlayerName() + " have become engaged!"));
        } else {
            player.sendMessage(ChatColor.RED + "You have not been proposed to!");
        }
    }
}
