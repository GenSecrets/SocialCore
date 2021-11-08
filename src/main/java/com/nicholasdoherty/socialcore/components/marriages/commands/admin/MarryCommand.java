package com.nicholasdoherty.socialcore.components.marriages.commands.admin;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.components.marriages.types.Marriage;
import com.nicholasdoherty.socialcore.utils.MarriagesUtil;
import com.voxmc.voxlib.util.UUIDUtil;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class MarryCommand {
    SocialCore sc;
    public MarryCommand(SocialCore plugin) { this.sc = plugin; }

    @CommandCompletion("@players")
    @CommandPermission("socialcore.marriage.admin.marry")
    public void runCommand(Player player, String[] args) {
        if(args.length > 2) {
            OfflinePlayer op1 = sc.getServer().getOfflinePlayer(UUIDUtil.getUUID(args[1]));
            OfflinePlayer op2 = sc.getServer().getOfflinePlayer(UUIDUtil.getUUID(args[2]));

            if(op1.getPlayer() == null || op2.getPlayer() == null){
                player.sendMessage("Could not find those players!");
                return;
            }

            final SocialPlayer sp1 = sc.save.getSocialPlayer(op1.getPlayer().getUniqueId().toString());
            final SocialPlayer sp2 = sc.save.getSocialPlayer(op2.getPlayer().getUniqueId().toString());

            sp1.setEngaged(false);
            sp1.setEngagedTo("");
            sp1.setMarried(true);
            sp1.setMarriedTo(sp2.getUUID());
            sp2.setEngaged(false);
            sp2.setEngagedTo("");
            sp2.setMarried(true);
            sp2.setMarriedTo(sp1.getUUID());

            sc.save.saveSocialPlayer(sp1);
            sc.save.saveSocialPlayer(sp2);

            final Marriage marriage = new Marriage(sp1, sp2);
            marriage.setPriest(player.getName());
            final String dateBuilder = MarriagesUtil.getMonth() + ' ' + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ", " + Calendar.getInstance().get(Calendar.YEAR);
            marriage.setDate(dateBuilder);

            sc.save.saveMarriage(marriage);

            player.sendMessage(ChatColor.GREEN + "A marriage has been forced between " + sp1.getPlayerName() + " and " + sp2.getPlayerName() + ".");
        } else {
            player.sendMessage(ChatColor.RED + "Usage: /marriage admin marry <player1> <player2>");
        }
    }
}
