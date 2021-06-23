package com.nicholasdoherty.socialcore.marriages;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by john on 2/1/15.
 */
public class PetnameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length != 1) {
            return false;
        }
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player p = (Player) commandSender;
        SocialPlayer socialPlayer = SocialCore.plugin.save.getSocialPlayer(p.getName());
        if (!socialPlayer.isMarried() || socialPlayer.getMarriedTo() == null) {
            p.sendMessage(ChatColor.RED + "You are not married.");
            return true;
        }
        SocialPlayer marriedTo = SocialCore.plugin.save.getSocialPlayer(socialPlayer.getMarriedTo());
        if (marriedTo == null) {
            p.sendMessage(ChatColor.RED + "Could not find your SO, notify admins.");
            return true;
        }
        String petName = strings[0];
        if (petName.length() > 29) {
            p.sendMessage(ChatColor.RED + "Pet name too long");
            return true;
        }
        marriedTo.setPetName(petName);
        SocialCore.plugin.save.saveSocialPlayer(marriedTo);
        p.sendMessage(ChatColor.GREEN + marriedTo.getPlayerName() + "'s pet name has been set to " + petName);
        Player marrietToP = Bukkit.getPlayer(marriedTo.getPlayerName());
        if (marrietToP != null && marrietToP.isOnline()) {
            String spousePetname = socialPlayer.getPetName();
            if (spousePetname == null)
                spousePetname = socialPlayer.getPlayerName();
            marrietToP.sendMessage(SocialCore.plugin.marriageConfig.petNameChangeSpouseMessage.replace("{spouse-petname}",spousePetname).replace("{new-petname}",petName));
        }
        return true;
    }
}
