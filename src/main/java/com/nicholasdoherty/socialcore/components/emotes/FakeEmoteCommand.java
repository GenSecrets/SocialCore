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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandAlias("fakeemote")
@CommandPermission("socialcore.emotes.fakeemote")
@Description("Force a player to run a fake emote; essentially creating an emote on the spot.")
public class FakeEmoteCommand extends BaseCommand {
    private final EmoteExtender extender = new EmoteExtender();
    private final SocialCore sc;
    private final Emotes emotes;

    public FakeEmoteCommand(final SocialCore plugin) {
        this.sc = plugin;
        emotes = plugin.emotes;
    }

    @Default
    @CommandCompletion("@players")
    public boolean onCommand(final CommandSender commandSender, final String[] args) {
        if(args.length < 2) {
            commandSender.sendMessage("Usage: /fakeemote <player> <emote>");
            return true;
        }
        final Player target = Bukkit.getPlayer(args[0]);
        if(target == null || !target.isOnline()) {
            commandSender.sendMessage(ChatColor.RED + "Could not find player: " + args[0]);
            return true;
        }

        List<String> list = new ArrayList<>(Arrays.asList(args));
        list.remove(args[0]);
        String[] fakeEmoteArgs = list.toArray(new String[0]);
        StringBuilder fakeEmoteBldr = new StringBuilder();
        for (String s : fakeEmoteArgs){
            fakeEmoteBldr.append(s).append(" ");
        }
        String fakeEmoteString = fakeEmoteBldr.substring(0, fakeEmoteBldr.toString().length()-1);

        final Emote emote = new Emote(
                "", "", "", "&3**" + fakeEmoteString + "**", "&3**" + fakeEmoteString + "**", "",
                "&9**" + fakeEmoteString + "**", "&d**" + fakeEmoteString + "**",
                "&9**" + fakeEmoteString + "**", "&d**" + fakeEmoteString + "**",
                new ArrayList<String>(), "");

        if(target != (Player)commandSender) {
            final Player player = (Player)commandSender;
            if(!emote.isCanBetargeted()) {
                commandSender.sendMessage(ChatColor.RED + "This emote may be not be targeted.");
                return true;
            }
            if(target == null || !target.isOnline() || VanishUtil.isVanished(target)) {
                commandSender.sendMessage(ChatColor.RED + "Target " + args[0] + " is not currently online.");
                return true;
            }
            final String message = emote.getEmoteMessageTargeted(player, target);
            sendMessage(player, message);
        } else {
            if(!emote.isCanBeUntargeted()) {
                commandSender.sendMessage(ChatColor.RED + "This emote must be targeted.");
                return true;
            }
            final String message = emote.getEmoteMessage(target);
            sendMessage(target, extender.process(message));
        }
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
        }
    }
}