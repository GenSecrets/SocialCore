package com.nicholasdoherty.socialcore.marriages.commands.admin;

import co.aikar.commands.annotation.CommandPermission;
import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ReloadCommand {
    SocialCore sc;
    public ReloadCommand(SocialCore plugin) { this.sc = plugin; }

    @CommandPermission("socialcore.marriage.admin.reload")
    public void runCommand(Player player){
        sc.marriageConfig.loadConfig();
        player.sendMessage(ChatColor.GREEN + "Reloaded marriages.");
    }
}
