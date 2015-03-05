package com.nicholasdoherty.socialcore.utils;

import com.massivecraft.vampire.entity.UPlayer;
import com.nicholasdoherty.werewolf.Lang;
import com.nicholasdoherty.werewolf.WPlayer;
import com.nicholasdoherty.werewolf.WStore;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 2/1/15.
 */
public class VampWWUtil {
    public static boolean canEat(Player p, ItemStack food) {
        if (isVampire(p))
            return false;
        if (food == null)
            return false;
        if (isTransformedWerewolf(p) && isWWDisabledFood(food)) {
            return false;
        }
        return true;
    }
    public static boolean isVampire(Player p) {
        UPlayer uPlayer = UPlayer.get(p);
        if (uPlayer == null)
            return false;
        return uPlayer.isVampire();
    }
    public static boolean isTransformedWerewolf(Player p) {
        WPlayer wPlayer = WStore.getWPlayerFromPlayer(p.getName());
        if (wPlayer == null || wPlayer.attributes == null)
            return false;
        if (!wPlayer.attributes.isWerewolf())
            return false;
        if (!wPlayer.attributes.isTransformed())
            return false;
        return true;
    }
    public static boolean isWWDisabledFood(ItemStack itemStack) {
        int item = itemStack.getType().getId();
        for (int i = 0; i < Lang.disabledFood.length; i++) {
            if (item == Lang.disabledFood[i]) {
                return true;
            }
        }
        return false;
    }
}
