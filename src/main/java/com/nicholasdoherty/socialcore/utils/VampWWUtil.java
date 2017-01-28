package com.nicholasdoherty.socialcore.utils;

import com.massivecraft.vampire.entity.UPlayer;
import com.nicholasdoherty.werewolf.core.WPlayer;
import com.nicholasdoherty.werewolf.core.storage.WStore;
import com.nicholasdoherty.werewolf.util.Lang;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 2/1/15.
 */
public final class VampWWUtil {
    private VampWWUtil() {
    }
    
    public static boolean canEat(final Player p, final ItemStack food) {
        return !isVampire(p) && food != null && !(isTransformedWerewolf(p) && isWWDisabledFood(food));
    }
    
    public static boolean isVampire(final Player p) {
        final UPlayer uPlayer = UPlayer.get(p);
        return uPlayer != null && uPlayer.isVampire();
    }
    
    @SuppressWarnings("TypeMayBeWeakened")
    public static boolean isTransformedWerewolf(final Player p) {
        final WPlayer wPlayer = WStore.getWPlayerFromPlayer(p.getName());
        return !(wPlayer == null || wPlayer.attributes == null) && wPlayer.attributes.isWerewolf() && wPlayer.attributes.isTransformed();
    }
    
    @SuppressWarnings("deprecation")
    public static boolean isWWDisabledFood(final ItemStack itemStack) {
        final int item = itemStack.getType().getId();
        for(int i = 0; i < Lang.disabledFood.length; i++) {
            if(item == Lang.disabledFood[i]) {
                return true;
            }
        }
        return false;
    }
}
