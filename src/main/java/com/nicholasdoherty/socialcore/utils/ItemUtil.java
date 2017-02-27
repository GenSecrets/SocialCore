package com.nicholasdoherty.socialcore.utils;

import com.earth2me.essentials.MetaItemStack;
import com.voxmc.voxlib.EssentialsItem;
import net.ess3.api.IEssentials;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 6/29/14.
 */
public class ItemUtil {
    public static List<EssentialsItem> itemsFromSection(List<String> rawList) {
        List<EssentialsItem> items = new ArrayList<>();
        if (rawList == null || rawList.isEmpty())
            return items;
        for (String rawItem : rawList) {
            items.add(new EssentialsItem(rawItem));
        }
        return items;
    }
    public static ItemStack getFromEssentialsString(String s) {
        ItemStack item = null;
        IEssentials essentials = (IEssentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        String[] parts = s.split(" ");

        try {
            item = essentials.getItemDb().get(parts[0], 1);
            try {
                int amount = Integer.parseInt(parts[1]);
                item.setAmount(amount);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (parts.length > 2) {
                MetaItemStack metaStack = new MetaItemStack(item);
                metaStack.parseStringMeta(null, true, parts, 2, essentials);
                item = metaStack.getItemStack();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
}
