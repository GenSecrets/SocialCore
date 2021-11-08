package com.nicholasdoherty.socialcore.components.emotes;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.emotes.extend.EmoteExtender;
import com.nicholasdoherty.socialcore.utils.NearbyAPI;
import com.nicholasdoherty.socialcore.utils.VanishUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;


@CommandAlias("forceemote")
@CommandPermission("socialcore.emotes.forceemote")
@Description("Force a player to run an emote; similar to sudo but for emotes.")
public class ForceEmoteCommand extends BaseCommand {
    private final EmoteExtender extender = new EmoteExtender();
    private final SocialCore sc;
    private final Emotes emotes;
    
    public ForceEmoteCommand(final SocialCore plugin) {
        this.sc = plugin;
        emotes = plugin.emotes;
    }

    @Default
    @CommandCompletion("@players")
    public boolean onCommand(final CommandSender commandSender, final String[] args) {
        if(args.length < 2) {
            commandSender.sendMessage("Usage: /forceemote <player> <emote>");
            return true;
        }
        final Player player = Bukkit.getPlayer(args[0]);
        if(player == null || !player.isOnline()) {
            commandSender.sendMessage(ChatColor.RED + "Could not find player: " + args[0]);
            return true;
        }
        final String emoteCommand = '/' + args[1].toLowerCase();
        final Emote emote = emotes.getEmote(emoteCommand);
        if(emote == null) {
            commandSender.sendMessage(ChatColor.RED + "Could not find emote: " + args[1]);
            return true;
        }
        if(args.length > 2) {
            final Player target = Bukkit.getPlayer(args[2]);
            if(!emote.isCanBetargeted()) {
                commandSender.sendMessage(ChatColor.RED + "This emote may be not be targeted.");
                return true;
            }
            if(target == null || !target.isOnline() || VanishUtil.isVanished(target)) {
                commandSender.sendMessage(ChatColor.RED + "Target " + args[2] + " is not currently online.");
                return true;
            }
            final String message = emote.getEmoteMessageTargeted(player, target);
            sendMessage(player, message);
        } else {
            if(!emote.isCanBeUntargeted()) {
                commandSender.sendMessage(ChatColor.RED + "This emote must be targeted.");
                return true;
            }
            final String message = emote.getEmoteMessage(player);
            sendMessage(player, extender.process(message));
        }
        commandSender.sendMessage(ChatColor.GREEN + "Did the emote");
        return true;
    }
    
    @SuppressWarnings("TypeMayBeWeakened")
    public void sendMessage(final Player p, final String message) {
        final int radius = sc.emotes.getRadius();
        final World world = p.getWorld();
        if(radius == 0) {
            for(final Player toSend : world.getPlayers()) {
                toSend.sendMessage(message);
            }
        } else {
            for(final Entity e : NearbyAPI.getNearbyPlayers(p.getLocation(), radius)) {
                if(e.getType() == EntityType.PLAYER) {
                    e.sendMessage(message);
                }
            }
            //p.sendMessage(message);
        }
    }
}
