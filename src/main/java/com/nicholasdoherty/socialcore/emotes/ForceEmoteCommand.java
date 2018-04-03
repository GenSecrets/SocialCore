package com.nicholasdoherty.socialcore.emotes;

import com.nicholasdoherty.socialcore.NearbyAPI;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.emotes.extend.EmoteExtender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by john on 5/2/15.
 */
@SuppressWarnings("unused")
public class ForceEmoteCommand implements CommandExecutor {
    private final EmoteExtender extender = new EmoteExtender();
    @SuppressWarnings("FieldCanBeLocal")
    private final SocialCore plugin;
    private final Emotes emotes;
    
    public ForceEmoteCommand(final SocialCore plugin) {
        this.plugin = plugin;
        emotes = plugin.emotes;
        plugin.getCommand("forceemote").setExecutor(this);
    }
    
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
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
            if(target == null || !target.isOnline() || VanishWrapper.isVanished(target)) {
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
        final int radius = SocialCore.plugin.emotes.getRadius();
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
