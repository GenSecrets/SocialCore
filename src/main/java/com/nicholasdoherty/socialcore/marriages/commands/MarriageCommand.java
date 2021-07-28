package com.nicholasdoherty.socialcore.marriages.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.marriages.commands.admin.*;
import com.nicholasdoherty.socialcore.marriages.commands.admin.MarryCommand;
import com.nicholasdoherty.socialcore.marriages.commands.admin.UnEngageCommand;
import com.nicholasdoherty.socialcore.marriages.commands.main.listall.Engagements;
import com.nicholasdoherty.socialcore.marriages.commands.main.listall.Marriages;
import com.nicholasdoherty.socialcore.utils.ErrorUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandAlias("marriage")
@CommandPermission("socialcore.marriage")
@Description("View base commands for marriage, including help pages!")
public class MarriageCommand extends BaseCommand {

    SocialCore sc;

    public MarriageCommand(SocialCore plugin) {
        this.sc = plugin;
    }


    @Default
    public void onCommand(CommandSender sender) {
        if(ErrorUtil.isNotPlayer(sender)){
            return;
        }

        Player player = (Player)sender;
        player.sendMessage(ChatColor.GOLD + "---------=Marriage Commands=---------");
        if(player.hasPermission("sc.marriage")) {
            player.sendMessage(ChatColor.AQUA + "/marriage - view list of marriage commands");
        }
        if(player.hasPermission("sc.view.marriages")) {
            player.sendMessage(ChatColor.AQUA + "/marriages - view all marriages on the server");
        }
        if(player.hasPermission("sc.view.engagements")) {
            player.sendMessage(ChatColor.AQUA + "/engagements - view all engagements on the server");
        }
        if(player.hasPermission("sc.propose")) {
            player.sendMessage(ChatColor.AQUA + "/propose <player name> - propose to another player");
            player.sendMessage(ChatColor.AQUA + "/propose accept - accept a proposal");
            player.sendMessage(ChatColor.AQUA + "/propose deny - deny a proposal");
        }
        if(player.hasPermission("sc.unengage")) {
            player.sendMessage(ChatColor.AQUA + "/unengage - To unengage your partner");
        }
        if(player.hasPermission("sc.priest")) {
            player.sendMessage(ChatColor.AQUA + "/marry <player1> <player2> - marry two players");
        }
        if(player.hasPermission("sc.view.divorces")) {
            player.sendMessage(ChatColor.AQUA + "/engagements - view all divorces on the server");
        }
        if(player.hasPermission("sc.fileDivorce")) {
            player.sendMessage(ChatColor.AQUA + "/divorce - divorce your spouse");
            player.sendMessage(ChatColor.AQUA + "/divorce cancel - cancel a pending divorce");
        }
        if(player.hasPermission("sc.lawyer")) {
            player.sendMessage(ChatColor.AQUA + "/divorce <player1> <player2> - divorce two players");
        }
    }

    @Subcommand("admin")
    @CommandCompletion("divorce|marry|purgeinvalids|reload|unengage")
    @CommandPermission("socialcore.marriage.admin")
    public void onCommandAdmin(CommandSender sender, String[] args) {
        if(ErrorUtil.isNotPlayer(sender)){
            return;
        }

        Player player = (Player)sender;
        if(args.length > 0){
            switch(args[0]){
                case "divorce":
                    DivorceCommand d = new DivorceCommand(sc);
                    d.runCommand(player, args);
                    break;
                case "marry":
                    MarryCommand e = new MarryCommand(sc);
                    e.runCommand(player, args);
                    break;
                case "purgeinvalids":
                    PurgeInvalidCommand p = new PurgeInvalidCommand(sc);
                    p.runCommand(player);
                    break;
                case "reload":
                    ReloadCommand r = new ReloadCommand(sc);
                    r.runCommand(player);
                    break;
                case "unengage":
                    UnEngageCommand u = new UnEngageCommand(sc);
                    u.runCommand(player, args);
                    break;
                default:

            }
        }
    }

    @Subcommand("listall")
    @CommandCompletion("engagements|marriages")
    @CommandPermission("socialcore.marriage.listall")
    public void onCommandList(CommandSender sender, String[] args) {
        if(ErrorUtil.isNotPlayer(sender)){
            return;
        }

        Player player = (Player)sender;
        if(args.length > 0){
            switch(args[0]){
                case "engagements":
                    Engagements e = new Engagements(sc);
                    e.runCommand(player, args);
                    break;
                case "marriages":
                    Marriages m = new Marriages(sc);
                    m.runCommand(player, args);
                    break;
                default:

            }
        } else {
            player.sendMessage(ChatColor.RED + "You can list all marriages and engagements on the server.");
        }
    }
}
