package com.nicholasdoherty.socialcore.components.marriages.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.marriages.commands.admin.*;
import com.nicholasdoherty.socialcore.components.marriages.commands.admin.MarryCommand;
import com.nicholasdoherty.socialcore.components.marriages.commands.admin.UnEngageCommand;
import com.nicholasdoherty.socialcore.components.marriages.commands.main.listall.Engagements;
import com.nicholasdoherty.socialcore.components.marriages.commands.main.listall.Marriages;
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
        ChatColor gray = ChatColor.GRAY;
        ChatColor aqua = ChatColor.AQUA;
        ChatColor yellow = ChatColor.YELLOW;
        ChatColor red = ChatColor.RED;

        player.sendMessage(ChatColor.GOLD + "---------=Marriage Commands=---------");


        if(player.hasPermission("socialcore.marriage")) {
            player.sendMessage(aqua + "/marriage"+gray+" - "+yellow+ "View marriage commands & help");

            if((player.hasPermission("socialcore.marriage.listall")) &&
                    (player.hasPermission("socialcore.marriage.listall.engagements")) &&
                    (player.hasPermission("socialcore.marriage.listall.marriages"))) {
                player.sendMessage(aqua + "/marriage listall <marriages/engagements>"+gray+" - "+yellow+ "View all marriages or engagements on the server");
            }
            if(player.hasPermission("sc.propose")) {
                player.sendMessage(aqua + "/propose <player name> - propose to another player");
                player.sendMessage(aqua + "/propose accept - accept a proposal");
                player.sendMessage(aqua + "/propose deny - deny a proposal");
            }
            if(player.hasPermission("sc.unengage")) {
                player.sendMessage(aqua + "/unengage - To unengage your partner");
            }
            if(player.hasPermission("sc.priest")) {
                player.sendMessage(aqua + "/marry <player1> <player2> - marry two players");
            }
            if(player.hasPermission("socialcore.marriage.admin")) {
                player.sendMessage(red + "/marriage admin divorce <player1> <player2> - Divorce two players");
                player.sendMessage(red + "/marriage admin marry <player1> <player2> - Marry two players");
                player.sendMessage(red + "/marriage admin unengage <player1> <player2> - Unengage two players");
                player.sendMessage(red + "/marriage admin purgeinvalids" +gray+ " - " +yellow+ "Purge invalid marriages and engagements from the database. ONLY DO THIS IF INSTRUCTED.");
                player.sendMessage(red + "/marriage admin reload" +gray+ " - " +yellow+ "Reload the Marriage config files.");
            }

            player.sendMessage(aqua + "- Divorces" +gray+ ": "+yellow+" In order to divorce your spouse, visit the courts.");
            if(player.hasPermission("socialcore.lawyer")) {
                player.sendMessage(aqua + "- Judges" +gray+ ": "+yellow+" You may divorce people that have submitted cases through court.");
            }
        }
    }

    @Subcommand("admin")
    @CommandCompletion("divorce|marry|reload|unengage")
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
