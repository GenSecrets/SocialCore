package com.nicholasdoherty.socialcore.components.marriages.commands.admin;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.components.marriages.types.Divorce;
import com.nicholasdoherty.socialcore.components.marriages.types.Marriage;
import com.sk89q.minecraft.util.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DivorceCommand {
    SocialCore sc;
    public DivorceCommand(SocialCore plugin) { this.sc = plugin; }

    @CommandCompletion("@players")
    @CommandPermission("socialcore.marriage.admin.divorce")
    public void runCommand(Player player, String[] args) {
            if(args.length > 1) {
                OfflinePlayer op1 = sc.getServer().getOfflinePlayer(UUID.fromString(args[1]));
                OfflinePlayer op2 = sc.getServer().getOfflinePlayer(UUID.fromString(args[2]));

                //if(op1.getPlayer() == null || op2.getPlayer() == null){
                //    player.sendMessage("Could not find those players!");
                //    return;
                //}

                final SocialPlayer sp1 = sc.save.getSocialPlayer(op1.getUniqueId().toString());
                final SocialPlayer sp2 = sc.save.getSocialPlayer(op2.getUniqueId().toString());
                sp1.setEngaged(false);
                sp1.setEngagedTo("");
                sp1.setMarried(false);
                sp1.setMarriedTo("");
                sp2.setEngaged(false);
                sp2.setEngagedTo("");
                sp2.setMarried(false);
                sp2.setMarriedTo("");

                sc.save.saveSocialPlayer(sp1);
                sc.save.saveSocialPlayer(sp2);

                final Divorce divorce = sc.save.getDivorce(sp1, sp2);
                if(divorce == null) {
                    player.sendMessage(ChatColor.RED + "This is not a valid divorce");
                    return;
                }
                sc.save.removeDivorce(divorce);

                final Marriage marriage = sc.save.getMarriage(sp1, sp2);
                if(marriage == null) {
                    player.sendMessage(ChatColor.RED + "This is not a valid marriage");
                    return;
                }
                sc.save.removeMarriage(marriage);

                player.sendMessage(ChatColor.GREEN + "Forced divorce.");
            } else {
                player.sendMessage(ChatColor.RED + "Usage: /marriage admin divorce <player1> <player2>");
            }
    }
}
