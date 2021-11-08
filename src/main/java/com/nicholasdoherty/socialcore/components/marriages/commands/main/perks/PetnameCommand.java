package com.nicholasdoherty.socialcore.components.marriages.commands.main.perks;

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

/**
 * Created by john on 2/1/15.
 */
@CommandAlias("petname")
@CommandPermission("socialcore.marriage.perks.petname")
@Description("Give your spouse a petname only they can see!")
public class PetnameCommand extends BaseCommand {
    SocialCore sc;

    public PetnameCommand(SocialCore plugin) { this.sc = plugin; }

    @Default
    public void onCommand(CommandSender sender, String[] strings) {
        if(ErrorUtil.isNotPlayer(sender)){
            return;
        }
        if (strings.length != 1) {
            sender.sendMessage(ChatColor.RED + "Petnames can only be one word! '/petname YourPetNameHere");
            return;
        }

        Player p = (Player) sender;
        SocialPlayer socialPlayer = sc.save.getSocialPlayer(p.getUniqueId().toString());
        if (!socialPlayer.isMarried() || socialPlayer.getMarriedTo() == null) {
            p.sendMessage(ChatColor.RED + "You are not married.");
            return;
        }

        SocialPlayer marriedTo = sc.save.getSocialPlayer(socialPlayer.getMarriedTo());
        if (marriedTo == null) {
            p.sendMessage(ChatColor.RED + "Could not find your SO, notify admins.");
            return;
        }

        String petName = strings[0];
        if (petName.length() > 29) {
            p.sendMessage(ChatColor.RED + "Pet name too long");
            return;
        }

        marriedTo.setPetName(petName);
        sc.save.saveSocialPlayer(marriedTo);
        p.sendMessage(ChatColor.GREEN + marriedTo.getPlayerName() + "'s pet name has been set to " + petName);
        Player marrietToP = Bukkit.getPlayer(marriedTo.getPlayerName());
        if (marrietToP != null && marrietToP.isOnline()) {
            String spousePetname = socialPlayer.getPetName();
            if (spousePetname == null)
                spousePetname = socialPlayer.getPlayerName();
            marrietToP.sendMessage(sc.marriageConfig.petNameChangeSpouseMessage.replace("{spouse-petname}",spousePetname).replace("{new-petname}",petName));
        }
    }
}
