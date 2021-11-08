package com.nicholasdoherty.socialcore.listeners;

import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CourtTeleportationHandler implements Listener {
    SocialCore pl;

    public CourtTeleportationHandler(SocialCore plugin){
        pl = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClickWhileHolding(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();

        // Ensure item is written book or piece of paper
        if((main.getType() != Material.WRITTEN_BOOK &&
                main.getType() != Material.PAPER)){
            return;
        }

        // Ensure item is not triggering from offhand click
        if(event.getHand() == EquipmentSlot.OFF_HAND ||
                event.getItem() == off){
            return;
        }

        //Ensure Action is only left or right clicking air
        if(event.getAction() != Action.LEFT_CLICK_AIR ||
                ((event.getAction() == Action.RIGHT_CLICK_AIR) && (event.useItemInHand() == Event.Result.DEFAULT))) {
            return;
        }

        // If item in main hand has meta data, grab display name
        //  then check the item name to determine how the lore is going
        //  to be read in order to obtain the location from the lore
        if(main.hasItemMeta() && main.getItemMeta().hasDisplayName()){
            String name = main.getItemMeta().getDisplayName();

            // If display name contains "&9Building Permit"
            if(name.contains(ChatColor.BLUE + "Building Permit")){
                teleportBuildingPermit(event, main);
                event.setCancelled(true);


            // If display name contains "&4Court Case Document"
            //   and the Action is left clicking the air only
            } else if(name.contains(ChatColor.DARK_RED + "Court Case")) {
                teleportCourtCase(event, main);
                event.setCancelled(true);
            }
        }
    }

    // Parse lore off of the item "main" in order to grab the location
    //  then teleport player to the location that was on the item
    public void teleportBuildingPermit(PlayerInteractEvent event, ItemStack main){
        List<String> lore;

        if(main.getItemMeta().hasLore()){
            lore = main.getItemMeta().getLore();
        } else {
            return;
        }

        // Parse lore, the lore line is formatted like:
        // &2at world_name,<x_coordinate>,<y_coordinate>,<z_coordinate>
        for(String s : lore){
            if(s.startsWith(ChatColor.DARK_GREEN + "at ")){
                String rawLocation = ChatColor.stripColor(s).substring(3);
                String[] args = rawLocation.split(",");
                World world = pl.getServer().getWorld(args[0].replace(" ", "_"));
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);

                Location loc = new Location(world, x, y, z);
                if(loc.getWorld() != event.getPlayer().getWorld()){
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "You must be standing in the same world" +
                            " in order to teleport to this place!");
                    return;
                } else {
                    event.getPlayer().teleport(loc);
                }
            }
        }
    }

    // Parse lore off of the item "main" in order to grab the location
    //  then teleport player to the location that was on the item
    public void teleportCourtCase(PlayerInteractEvent event, ItemStack main){
        List<String> lore;

        if(main.getItemMeta().hasLore()){
            lore = main.getItemMeta().getLore();
        } else {
            return;
        }

        Location loc = null;
        World world = null;
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        // Parse lore, the two lore lines are formatted like:
        // &2Location: &eworld_name
        // &e<x_coordinate>&7x &e<y_coordinate>&7y &e<z_coordinate>&7z
        for(String s : lore){
            if(s.startsWith(ChatColor.DARK_GREEN + "Location: ")){
                String rawWorld = ChatColor.stripColor(s).substring(10);
                world = pl.getServer().getWorld(rawWorld.replace(" ", "_"));
            }

            if(s.startsWith(ChatColor.YELLOW + "")){
                char[] chars = new char[]{'x','y','z'};
                String rawCoords = ChatColor.stripColor(s);
                for (char c : chars){
                    rawCoords = rawCoords.replaceAll(String.valueOf(c), "");
                }
                rawCoords = rawCoords.replace(" ", ",");
                String[] args = rawCoords.split(",");

                try {
                    x = Double.parseDouble(args[0]);
                    y = Double.parseDouble(args[1]);
                    z = Double.parseDouble(args[2]);
                } catch (Exception nex){
                    nex.printStackTrace();
                }
            }
        }

        // Checking to ensure lore parsed before attempting to teleport
        if( x!= 0.0 && y != 0.0 && z != 0.0)
            loc = new Location(world, x, y, z);
        if(loc != null){
            if(loc.getWorld() != event.getPlayer().getWorld()){
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You must be standing in the same world" +
                        " in order to teleport to this place!");
            } else {
                event.getPlayer().teleport(loc);
            }
        }
    }
}
