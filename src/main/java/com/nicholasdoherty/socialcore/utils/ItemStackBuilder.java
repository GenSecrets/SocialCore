package com.nicholasdoherty.socialcore.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 1/13/15.
 */
public class ItemStackBuilder {
    ItemStack itemStack;

    public ItemStackBuilder(Material mat) {
        itemStack = new ItemStack(mat);
    }

    public ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStackBuilder() {
        this.itemStack = new ItemStack(Material.STONE);
    }
    public ItemStackBuilder setColoredWool(DyeColor dyeColor) {
        itemStack.setType(Material.WOOL);
        Wool wool = new Wool(Material.WOOL);
        wool.setColor(dyeColor);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemStack = wool.toItemStack(itemStack.getAmount());
        itemStack.setItemMeta(itemMeta);
        return this;
    }
    public ItemStackBuilder setPlayerHead(String name) {
        setType(Material.SKULL_ITEM);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwner(name);
        itemStack.setDurability((short) 3);
        itemStack.setItemMeta(skullMeta);
        return this;
    }
    public ItemStackBuilder setDurability(short data) {
        itemStack.setDurability(data);
        return this;
    }

    public ItemStackBuilder amount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }
    public ItemStackBuilder setType(Material mat) {
        itemStack.setType(mat);
        return this;
    }

    public ItemStackBuilder addLore(String... lore) {
        List<String> curLore;
        if (itemStack.getItemMeta().hasLore()) {
            curLore = new ArrayList<>(itemStack.getItemMeta().getLore());
        }else {
            curLore = new ArrayList<>();
        }
        for (String loreI : lore) {
            curLore.add(loreI);
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(curLore);
        itemStack.setItemMeta(itemMeta);
        return this;
    }
    public ItemStackBuilder addLore(List<String> lore) {
        return addLore(lore.toArray(new String[lore.size()]));
    }
    public ItemStackBuilder addLore(int line, String... lore) {
        List<String> curLore;
        if (itemStack.getItemMeta().hasLore()) {
            curLore = new ArrayList<>(itemStack.getItemMeta().getLore());
        }else {
            curLore = new ArrayList<>();
        }
        for (int j = lore.length-1; j >= 0; j--) {
            String loreI = lore[j];
            curLore.add(line,loreI);
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(curLore);
        itemStack.setItemMeta(itemMeta);
        return this;
    }
    public ItemStackBuilder setName(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return this;
    }
    public ItemStackBuilder addEnchant(Enchantment enchantment,int level) {
        itemStack.addUnsafeEnchantment(enchantment,level);
        return this;
    }
    public ItemStack toItemStack() {
        return itemStack;
    }
}
