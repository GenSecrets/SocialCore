package com.nicholasdoherty.socialcore.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2/15/15.
 */
public class VoxStringUtils {
    public static List<String> splitLoreFormat(String input) {
        List<String> lore = new ArrayList<>();
        StringBuffer current = new StringBuffer();
        for (String part : input.split(" ")) {
            current.append(part + " ");
            if (current.length() > 40) {
                String loreToAdd = current.toString().trim();
                lore.add(loreToAdd);
                current = new StringBuffer(ChatColor.getLastColors(loreToAdd));
            }
        }
        if (current.length() > 0) {
            lore.add(current.toString());
        }
        return lore;
    }
    public static String formatToString(List<String> strings) {
        StringBuffer stringBuffer = new StringBuffer();
        if (strings.size() == 0) {
            return "none";
        }
        if (strings.size() == 1) {
            return strings.get(0);
        }
        if (strings.size() == 2) {
            return strings.get(0) + " and " + strings.get(1);
        }
        for (int i = 0; i < strings.size(); i++) {
            if (i < strings.size() - 1) {
                stringBuffer.append(strings.get(i) +", ");
            }else {
                stringBuffer.append("and " + strings.get(i));
            }
        }
        return stringBuffer.toString();
    }
    public static List<String> toStringList(Iterable list, ToStringConverter toStringConverter) {
        List<String> stringList = new ArrayList<>();
        for (Object o : list) {
            stringList.add(toStringConverter.convertToString(o));
        }
        return stringList;
    }
    public static interface ToStringConverter<T> {
        public String convertToString(T o);
    }
    public static String color(String in) {
        return ChatColor.translateAlternateColorCodes('&',in);
    }
}
