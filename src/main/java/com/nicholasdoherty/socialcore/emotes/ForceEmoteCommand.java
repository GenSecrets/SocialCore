package com.nicholasdoherty.socialcore.emotes;

import com.nicholasdoherty.socialcore.NearbyAPI;
import com.nicholasdoherty.socialcore.SocialCore;
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
public class ForceEmoteCommand implements CommandExecutor {
    private SocialCore plugin;
    private Emotes emotes;

    public ForceEmoteCommand(SocialCore plugin) {
        this.plugin = plugin;
        this.emotes = plugin.emotes;
        plugin.getCommand("forceemote").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage("Usage: /forceemote <player> <emote>");
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null || !player.isOnline()) {
            commandSender.sendMessage(ChatColor.RED + "Could not find player: " + args[0]);
            return true;
        }
        String emoteCommand = "/"+args[1].toLowerCase();
        Emote emote = emotes.getEmote(emoteCommand);
        if (emote == null) {
            commandSender.sendMessage(ChatColor.RED +"Could not find emote: " + args[1]);
            return true;
        }
        if (args.length > 2) {
            Player target = Bukkit.getPlayer(args[2]);
            if (!emote.isCanBetargeted()) {
                commandSender.sendMessage(ChatColor.RED + "This emote may be not be targeted.");
                return true;
            }
            if (target == null || !target.isOnline() || VanishWrapper.isVanished(target)) {
                commandSender.sendMessage(ChatColor.RED + "Target " + args[2] + " is not currently online.");
                return true;
            }
            String message = emote.getEmoteMessageTargeted(player, target);
            sendMessage(player,message);
        }else {
            if (!emote.isCanBeUntargeted()) {
                commandSender.sendMessage(ChatColor.RED + "This emote must be targeted.");
                return true;
            }
            String message = emote.getEmoteMessage(player);
            sendMessage(player,message);
        }
        commandSender.sendMessage(ChatColor.GREEN +"Did the emote");
        return true;
    }
    public void sendMessage(Player p, String message) {
        int radius = SocialCore.plugin.emotes.getRadius();
        World world = p.getWorld();
        if (radius == 0) {
            for (Player toSend : world.getPlayers()) {
                toSend.sendMessage(message);
            }
        } else {
            for (Entity e : NearbyAPI.getNearbyPlayers(p.getLocation(),radius)) {
                if (e.getType().equals(EntityType.PLAYER)) {
                    Player toSend = (Player) e;
                    toSend.sendMessage(message);
                }
            }
            //p.sendMessage(message);
        }
    }
}
