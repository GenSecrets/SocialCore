package com.nicholasdoherty.socialcore.marriages.commands.admin;

import co.aikar.commands.annotation.CommandPermission;
import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by john on 2/1/15.
 */
public class PurgeInvalidCommand {
    SocialCore sc;
    public PurgeInvalidCommand(SocialCore plugin) { this.sc = plugin; }

    @CommandPermission("socialcore.marriage.admin.purgeinvalids")
    public void runCommand(Player player) {
        Thread thread = new Thread(() -> {
            sc.save.purgeInvalids();
            player.sendMessage(ChatColor.GREEN + "Removed invalid marriages.");
        });
        thread.start();
    }
}
