package com.nicholasdoherty.socialcore.titles;

import com.nicholasdoherty.socialcore.utils.VoxStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 7/2/15.
 */
public class TitleCommand implements CommandExecutor {
    private TitleManager titleManager;

    public TitleCommand(TitleManager titleManager) {
        this.titleManager = titleManager;
        titleManager.getPlugin().getCommand("titles").setExecutor(this);
        titleManager.getPlugin().getCommand("title").setExecutor(this);
        titleManager.getPlugin().getCommand("reloadtitles").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("reloadtitles")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("clearcache")) {
                titleManager.clearCache();
                commandSender.sendMessage(ChatColor.GREEN + "Cleared cache.");
                return true;
            }
            titleManager.loadTitles();
            commandSender.sendMessage(ChatColor.GREEN +"Reloaded. Use \"/reloadtitles clearcache\" to clear the cache.");
            return true;
        }
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player p = (Player) commandSender;
        if (command.getName().equalsIgnoreCase("titles")) {
            sendAllowableTitles(p);
            return true;
        }else {
            if (args.length == 0) {
                p.sendMessage(ChatColor.RED + "Usage: /title TITLE-NAME or /title off");
            }else {
                String titleName = args[0];
                if (titleName.equalsIgnoreCase("off")) {
                    if (!titleManager.hasTitle(p)) {
                        p.sendMessage(ChatColor.RED + "You don't have a title.");
                        return true;
                    }
                    titleManager.turnOffTitle(p);
                    p.sendMessage(ChatColor.GREEN +"Title turned off");
                }else {
                    if (titleManager.isOncooldown(p.getUniqueId())) {
                        p.sendMessage(ChatColor.RED + "Please wait before using /title again...");
                        return true;
                    }
                    titleName = titleName.toLowerCase();
                    Title title = titleManager.getTitles().get(titleName);
                    if (title == null || !p.hasPermission("sc.title." + title.getName().toLowerCase())) {
                        p.sendMessage(ChatColor.RED + "You can't use a title by that name.");
                        return true;
                    }
                    titleManager.setTitle(p,title);
                    p.sendMessage(ChatColor.GREEN +"Set your title to: " + title.getName());
                }
            }
            return true;
        }
    }

    private void sendAllowableTitles(Player p) {
        StringBuilder stringBuilder = new StringBuilder(ChatColor.GREEN + "You may use the following titles: ");
        List<String> allowedTitleNames = new ArrayList<>();
        for (Title title : titleManager.getTitles().values()) {
            if (p.hasPermission("sc.title." + title.getName())) {
                allowedTitleNames.add(title.getName());
            }
        }
        stringBuilder.append(VoxStringUtils.formatToString(allowedTitleNames));
        p.sendMessage(stringBuilder.toString());
    }
}
