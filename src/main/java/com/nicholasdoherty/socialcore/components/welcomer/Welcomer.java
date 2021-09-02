package com.nicholasdoherty.socialcore.components.welcomer;

import java.io.File;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Welcomer extends JavaPlugin implements Listener {
    public static String lastJoined;
    public static String welcomeMessage;

    public Welcomer() {
    }

    public void onDisable() {
    }

    public void onEnable() {
        File config = new File(this.getDataFolder(), "config.yml");
        if (!config.exists()) {
            this.saveDefaultConfig();
            System.out.println("[Welcomer] No config.yml detected, config.yml created");
        }

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    public void welcome(Player welcomer) {
        if (lastJoined == null) {
            welcomer.sendMessage(ChatColor.RED + "No one to be welcomed!");
        }

        List<String> welcomeMessageList = this.getConfig().getStringList("Welcomer.welcomer-messages");
        welcomeMessage = ChatColor.translateAlternateColorCodes('&', (String)welcomeMessageList.get((new Random()).nextInt(welcomeMessageList.size())));
        if (welcomer != null) {
            welcomer.chat(welcomeMessage.replace("%player%", lastJoined));
        }
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPlayedBefore()) {
            lastJoined = event.getPlayer().getName();
        }

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("wel") && !label.equalsIgnoreCase("welcomer")) {
            return false;
        } else {
            this.welcome((Player)sender);
            return true;
        }
    }
}
