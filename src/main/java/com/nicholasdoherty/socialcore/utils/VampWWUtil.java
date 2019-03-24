package com.nicholasdoherty.socialcore.utils;

import com.massivecraft.vampire.entity.UPlayer;
import com.nicholasdoherty.werewolf.core.WPlayer;
import com.nicholasdoherty.werewolf.core.storage.WStore;
import com.nicholasdoherty.werewolf.util.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author amy
 * @since 7/26/18.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
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
        final Material item = itemStack.getType();
        for(int i = 0; i < Lang.getDisabledFood().length; i++) {
            if(item == Lang.getDisabledFood()[i]) {
                return true;
            }
        }
        return false;
    }
}
