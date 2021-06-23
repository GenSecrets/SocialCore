package com.nicholasdoherty.socialcore.utils;

import org.bukkit.ChatColor;

public class ColorUtil {
    public static String registerColors(final String msg) {
        String newMsg = msg.replaceAll("&0", ChatColor.BLACK + "");
        newMsg = newMsg.replaceAll("&0", ChatColor.BLACK + "");
        newMsg = newMsg.replaceAll("&1", ChatColor.DARK_BLUE + "");
        newMsg = newMsg.replaceAll("&2", ChatColor.DARK_GREEN + "");
        newMsg = newMsg.replaceAll("&3", ChatColor.DARK_AQUA + "");
        newMsg = newMsg.replaceAll("&4", ChatColor.DARK_RED + "");
        newMsg = newMsg.replaceAll("&5", ChatColor.DARK_PURPLE + "");
        newMsg = newMsg.replaceAll("&6", ChatColor.GOLD + "");
        newMsg = newMsg.replaceAll("&7", ChatColor.GRAY + "");
        newMsg = newMsg.replaceAll("&8", ChatColor.DARK_GRAY + "");
        newMsg = newMsg.replaceAll("&9", ChatColor.BLUE + "");
        newMsg = newMsg.replaceAll("&a", ChatColor.GREEN + "");
        newMsg = newMsg.replaceAll("&b", ChatColor.AQUA + "");
        newMsg = newMsg.replaceAll("&c", ChatColor.RED + "");
        newMsg = newMsg.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
        newMsg = newMsg.replaceAll("&e", ChatColor.YELLOW + "");
        newMsg = newMsg.replaceAll("&f", ChatColor.WHITE + "");

        newMsg = newMsg.replaceAll("&l", ChatColor.BOLD + "");
        newMsg = newMsg.replaceAll("&n", ChatColor.UNDERLINE + "");
        newMsg = newMsg.replaceAll("&o", ChatColor.ITALIC + "");
        newMsg = newMsg.replaceAll("&k", ChatColor.STRIKETHROUGH + "");
        newMsg = newMsg.replaceAll("&m", ChatColor.RESET + "");
        newMsg = newMsg.replaceAll("&newline", "\n");
        return newMsg;
    }

    public static ChatColor convertToChatColor(final String chatcolor) {
        switch (chatcolor) {
            case "&0": return ChatColor.BLACK;
            case "&1": return ChatColor.DARK_BLUE;
            case "&2": return ChatColor.DARK_GREEN;
            case "&3": return ChatColor.DARK_AQUA;
            case "&4": return ChatColor.DARK_RED;
            case "&5": return ChatColor.DARK_PURPLE;
            case "&6": return ChatColor.GOLD;
            case "&7": return ChatColor.GRAY;
            case "&8": return ChatColor.DARK_GRAY;
            case "&9": return ChatColor.BLUE;
            case "&a": return ChatColor.GREEN;
            case "&b": return ChatColor.AQUA;
            case "&c": return ChatColor.RED;
            case "&d": return ChatColor.LIGHT_PURPLE;
            case "&e": return ChatColor.YELLOW;
            case "&f": return ChatColor.WHITE;

            case "&l": return ChatColor.BOLD;
            case "&n": return ChatColor.UNDERLINE;
            case "&o": return ChatColor.ITALIC;
            case "&k": return ChatColor.STRIKETHROUGH;
            default: return ChatColor.RESET;
        }
    }
}
