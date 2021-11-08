package com.nicholasdoherty.socialcore.listeners;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

@SuppressWarnings({"unused", "deprecation"})
public class SCListener implements Listener {
    public static Map<String, String> riding = new HashMap<>();
    SocialCore sc;
    Set<UUID> onPiggyBackCooldown = new HashSet<>();
    Set<UUID> onExpMsgCooldown = new HashSet<>();
    Set<UUID> onFoodMsgCooldown = new HashSet<>();

    public SCListener(final SocialCore sc) {
        this.sc = sc;
    }
    
    @EventHandler
    public void onlogoutInventory(final PlayerQuitEvent event) {
        event.getPlayer().getInventory().getViewers().forEach(v -> new BukkitRunnable() {
            @Override
            public void run() {
                v.closeInventory();
            }
        }.runTaskLater(sc, 1));
    }

    @EventHandler //(priority = EventPriority.MONITOR)
    public void petnameJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        SocialPlayer sp = sc.save.getSocialPlayer(p.getUniqueId().toString());
        final PermissionAttachment permissionAttachment = p.addAttachment(sc);

        p.setMetadata("pa", new FixedMetadataValue(sc, permissionAttachment));
        if(sc.save.getSocialPlayer(p.getUniqueId().toString()) == null){
            sc.store.create(p.getUniqueId().toString());
        }

        if (!event.getPlayer().hasPlayedBefore()) {
            sc.welcomerLastJoined = event.getPlayer().getName();
            String welcomeMessage = ChatColor.translateAlternateColorCodes('&', sc.getWelcomerConfig().getString("broadcast-message"));
            sc.getServer().broadcastMessage(sc.prefix + ChatColor.RESET + welcomeMessage.replace("%player%", event.getPlayer().getName()));
        }

        if(!sp.isMarried()) {
            return;
        }
        if(sp.getMarriedTo() == null) {
            return;
        }
        final OfflinePlayer marriedToP = Bukkit.getOfflinePlayer(UUID.fromString(sp.getMarriedTo()));
        if(marriedToP.getPlayer() != null && marriedToP.isOnline() && sp.getPetName() != null) {
            marriedToP.getPlayer().sendMessage(SocialCore.plugin.marriageConfig.petNameLoginMessage.replace("{pet-name}", sp.getPetName()));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerLeavePetname(final PlayerQuitEvent event) {
        final Player p = event.getPlayer();
        final SocialPlayer socialPlayer = sc.save.getSocialPlayer(p.getUniqueId().toString());
        if(socialPlayer == null) {
            return;
        }
        if(!socialPlayer.isMarried()) {
            return;
        }
        if(socialPlayer.getMarriedTo() == null) {
            return;
        }
        final OfflinePlayer marriedToP = Bukkit.getOfflinePlayer(UUID.fromString(socialPlayer.getMarriedTo()));
        if(marriedToP.getPlayer() != null && marriedToP.isOnline() && socialPlayer.getPetName() != null) {
            marriedToP.getPlayer().sendMessage(SocialCore.plugin.marriageConfig.petNameLogoutMessage.replace("{pet-name}", socialPlayer.getPetName()));
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemConsume(final FoodLevelChangeEvent e) {
        if(!(e.getEntity() instanceof Player)) {
            return;
        }
        final Player p1 = (Player) e.getEntity();
        
        if(e.getFoodLevel() < p1.getFoodLevel()) {
            return;
        }
        final int changeby = e.getFoodLevel() - p1.getFoodLevel();
        final SocialPlayer sp = sc.save.getSocialPlayer(p1.getUniqueId().toString());
        if(!sp.isMarried()) {
            return;
        }
        final OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(sp.getMarriedTo()));
        if(op.getPlayer() == null) {
            return;
        }
        Player p2 = op.getPlayer();
        if(!p2.isOnline()) {
            return;
        }
        
        // If either partner in the marriage has the permission node, let it happen.
        if(p1.hasPermission("socialcore.marriage.perks.sharefood") || p2.hasPermission("socialcore.marriage.perks.sharefood")) {
            final double distanceSquared = sc.marriageConfig.maxConsumeDistanceSquared;
            if(p2.getWorld().getName().equalsIgnoreCase(p1.getWorld().getName()) && p1.getLocation().distanceSquared(p2.getLocation()) <= distanceSquared) {
                if(p1.hasMetadata("last-ate")) {
                    final ItemStack lastAte = (ItemStack) p1.getMetadata("last-ate").get(0).value();
                    if(lastAte != null) {
                        return;
                    }
                }
                e.setCancelled(true);
                p1.setFoodLevel(p1.getFoodLevel() + changeby / 2);
                p2.setFoodLevel(p2.getFoodLevel() + changeby / 2);
                if(!onFoodMsgCooldown.contains(p1.getUniqueId())){
                    p1.sendMessage(ChatColor.AQUA + "You shared your food with " + p2.getName() + " since they are nearby.");
                    onFoodMsgCooldown.add(p1.getUniqueId());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            onFoodMsgCooldown.remove(p1.getUniqueId());
                        }
                    }.runTaskLater(SocialCore.plugin, SocialCore.plugin.marriageConfig.foodMessageCooldown);
                }

                if(!onFoodMsgCooldown.contains(p2.getUniqueId())){
                    p2.sendMessage(ChatColor.AQUA + p1.getName() + " is nearby and has shared their food with you.");
                    onFoodMsgCooldown.add(p2.getUniqueId());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            onFoodMsgCooldown.remove(p2.getUniqueId());
                        }
                    }.runTaskLater(SocialCore.plugin, SocialCore.plugin.marriageConfig.foodMessageCooldown);
                }
            }
        }
    }
    
    @EventHandler
    public void onExpChange(final PlayerExpChangeEvent e) {
        
        final double ran = Math.random();
        if(ran <= sc.marriageConfig.coupleXPPercent) {
            final Player p = e.getPlayer();
            if(p == null) {
                return;
            }
            
            final SocialPlayer sp = sc.save.getSocialPlayer(p.getUniqueId().toString());
            if(sp.isMarried()) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(sp.getMarriedTo()));
                if(op.getPlayer() == null){
                    return;
                }
                final Player p2 = op.getPlayer();
                if(p2 == null || !(p2.isOnline())) {
                    return;
                }

                if(!p.getWorld().equals(p2.getWorld())) {
                    return;
                }
                if(p.getLocation().distanceSquared(p2.getLocation()) > Math.pow(sc.marriageConfig.coupleXPDistance, 2)) {
                    return;
                }

                p2.giveExp(e.getAmount());
                if(!onExpMsgCooldown.contains(p.getUniqueId())){
                    p.sendMessage(ChatColor.AQUA + "You shared your exp with " + p2.getName() + " since they are nearby.");
                    onExpMsgCooldown.add(p.getUniqueId());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            onExpMsgCooldown.remove(p.getUniqueId());
                        }
                    }.runTaskLater(SocialCore.plugin, SocialCore.plugin.marriageConfig.expMessageCooldown);
                }

                if(!onExpMsgCooldown.contains(p2.getUniqueId())){
                    p2.sendMessage(ChatColor.AQUA + p.getName() + " is nearby and has shared their exp with you.");
                    onExpMsgCooldown.add(p2.getUniqueId());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            onExpMsgCooldown.remove(p2.getUniqueId());
                        }
                    }.runTaskLater(SocialCore.plugin, SocialCore.plugin.marriageConfig.expMessageCooldown);
                }
                onExpMsgCooldown.add(p2.getUniqueId());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        onExpMsgCooldown.remove(p2.getUniqueId());
                    }
                }.runTaskLater(SocialCore.plugin, SocialCore.plugin.marriageConfig.expMessageCooldown);
            }
        }
    }
    
    @EventHandler
    public void playerSneakEvent(final PlayerToggleSneakEvent e) {
        final Player p = e.getPlayer();
        if(p == null) {
            return;
        }
        if(riding.containsKey(p.getUniqueId().toString())) {
            riding.remove(p.getUniqueId().toString());
            if(p.getVehicle() != null && p.getVehicle() instanceof Player) {
                final Player other = (Player) p.getVehicle();
                activatePiggyBackCooldown(p, other);
            }
            p.leaveVehicle();
        }
    }
    
    @EventHandler
    public void PlayerDamage(final EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player damaged = (Player) event.getEntity();
        final Player other;
        if(damaged.getPassenger() != null && damaged.getPassenger() instanceof Player) {
            other = (Player) damaged.getPassenger();
        } else if(damaged.isInsideVehicle() && damaged.getVehicle() != null && damaged.getVehicle() instanceof Player) {
            other = (Player) damaged.getVehicle();
        } else {
            return;
        }
        if(riding.containsKey(damaged.getName()) && damaged.isInsideVehicle()) {
            damaged.leaveVehicle();
            riding.remove(damaged.getName());
            riding.remove(other.getName());
            activatePiggyBackCooldown(damaged, other);
        }
        if(riding.containsKey(other.getName()) && other.isInsideVehicle()) {
            other.leaveVehicle();
            riding.remove(other.getName());
            riding.remove(damaged.getName());
            activatePiggyBackCooldown(damaged, other);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if(event.getPlayer().isInsideVehicle()) {
            event.getPlayer().leaveVehicle();
            riding.remove(event.getPlayer().getName());
        }
        if(event.getPlayer().getPassenger() != null) {
            event.getPlayer().getPassenger().leaveVehicle();
            riding.remove(event.getPlayer().getName());
        }
    }
    
    private boolean isOnPiggybackcooldown(final Player p1) {
        return onPiggyBackCooldown.contains(p1.getUniqueId());
    }
    
    private void activatePiggyBackCooldown(final Player p1, final Player p2) {
        final UUID uuid1 = p1.getUniqueId();
        final UUID uuid2 = p2.getUniqueId();
        onPiggyBackCooldown.add(uuid1);
        onPiggyBackCooldown.add(uuid2);
        new BukkitRunnable() {
            @Override
            public void run() {
                onPiggyBackCooldown.remove(uuid1);
                onPiggyBackCooldown.remove(uuid2);
            }
        }.runTaskLater(SocialCore.plugin, SocialCore.plugin.marriageConfig.piggybackCooldown);
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEntityEvent e) {
        if(!(e.getRightClicked() instanceof Player)) {
            return;
        }
        
        final Player p = e.getPlayer();
        if(p == null) {
            return;
        }
        
        final Player p2 = (Player) e.getRightClicked();
        if(p2 == null || !sc.getServer().getOnlinePlayers().contains(p2)) {
            return;
        }
        
        if(!p.isSneaking()) {
            if(riding.containsKey(p2.getUniqueId().toString())) {
                return;
            }
            if(p.getItemInHand().getType() != Material.SADDLE) {
                return;
            }
            final SocialPlayer player1 = sc.save.getSocialPlayer(p.getUniqueId().toString());
            final SocialPlayer player2 = sc.save.getSocialPlayer(p2.getUniqueId().toString());
            
            if(player1.getMarriedTo().equalsIgnoreCase(player2.getPlayerName())) {
                if(!p.hasPermission("socialcore.marriage.perks.piggyback") && !p2.hasPermission("socialcore.marriage.perks.piggyback")) {
                    p.sendMessage(ChatColor.RED + "Your couple does not have permission to piggyback.");
                    return;
                }
                if(!sc.marriageConfig.whitelistPiggybackWorlds.contains(p.getWorld().getName())) {
                    p.sendMessage(ChatColor.RED + "Piggybacking is disabled in your world.");
                    return;
                }
                
                if(isOnPiggybackcooldown(p)) {
                    p.sendMessage(ChatColor.RED + "Piggy-back is currently on cooldown.");
                } else {
                    p2.setPassenger(p);
                    p2.sendMessage(ChatColor.AQUA + "You are giving " + p.getName() + " a piggy-back ride!.");
                    p.sendMessage(ChatColor.AQUA + p2.getName() + " is giving you a piggy-back ride.");
                    riding.put(p.getUniqueId().toString(), p2.getUniqueId().toString());
                }
            } else {
                p.sendMessage(ChatColor.RED + "You can only ride your significant other.");
            }
            
            return;
        }
        
        if(p.getLocation().distanceSquared(p2.getLocation()) > 16) {
            return;
        }
        
        if(!sc.marriages.canPlayerKiss(p.getName())) {
            return;
        }
        
        final SocialPlayer player1 = sc.save.getSocialPlayer(p.getUniqueId().toString());
        final SocialPlayer player2 = sc.save.getSocialPlayer(p2.getUniqueId().toString());
        
        if(player1.getMarriedTo().equalsIgnoreCase(player2.getPlayerName())) {
            
            final Vector v = p.getEyeLocation().toVector();
            final Vector v2 = p2.getEyeLocation().toVector();
            final Vector v3 = v.midpoint(v2);
            final Location loc = v3.toLocation(p.getWorld());
            p.getWorld().spawnParticle(Particle.HEART, loc, 1, 1, 1, 5, 20);
            //ParticleEffect.HEART.display(loc, 1, 1, 1, 5, 20);
            p.sendMessage(ChatColor.AQUA + "You have kissed " + p2.getName() + '.');
            p2.sendMessage(ChatColor.AQUA + p.getName() + " kisses you.");
            final int healAmount = sc.marriageConfig.kissHealAmount;
            if(healAmount > 0) {
                try {
                    if(p.getHealth() + healAmount <= p.getMaxHealth()) {
                        p.setHealth(p.getHealth() + healAmount);
                    } else {
                        p.setHealth(p.getMaxHealth());
                    }
                    if(p2.getHealth() + healAmount <= p2.getMaxHealth()) {
                        p2.setHealth(p2.getHealth() + healAmount);
                    } else {
                        p2.setHealth(p2.getMaxHealth());
                    }
                } catch(final Exception ignore) {
                }
            }
            sc.marriages.kissPlayer(p.getName());
        }
    }
}
