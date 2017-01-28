package com.nicholasdoherty.socialcore.utils;

import net.minecraft.server.v1_11_R1.Block;
import org.bukkit.Material;

import java.lang.reflect.Field;

/**
 * Created by john on 8/11/14.
 */
public class BlockUtil {
    public static void setHardness(Material mat, float hardness) {
        Block b = Block.getByName(mat.name().toLowerCase());
        if (b == null) {
            throw new Error("Invalid material: " + mat);
        }
        try {
            Field f = getStrengthField();
            f.setAccessible(true);
            f.setFloat(b,hardness);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private static Field getStrengthField() {
        try {
            return Block.class.getDeclaredField("strength");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
