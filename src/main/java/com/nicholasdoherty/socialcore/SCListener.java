package com.nicholasdoherty.socialcore;

import com.massivecraft.vampire.entity.UPlayer;
import com.massivecraft.vampire.event.EventVampirePlayerInfectionChange;
import com.nicholasdoherty.socialcore.utils.VampWWUtil;
import com.nicholasdoherty.werewolf.customevents.WerewolveInfectionEvent;
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

@SuppressWarnings({"unused", "TypeMayBeWeakened", "deprecation"})
public class SCListener implements Listener {
    public static Map<String, String> riding = new HashMap<>();
    SocialCore sc;
    Set<UUID> onPiggyBackCooldown = new HashSet<>();
    
    public SCListener(final SocialCore sc) {
        this.sc = sc;
    }
    
    @EventHandler
    public void werewolfChange(final WerewolveInfectionEvent event) {
        final String name = event.getpName();
        if(SupernaturalUtils.isVampire(name)) {
            event.setCancelled(true);
            return;
        }
        final SocialPlayer socialPlayer = sc.save.getSocialPlayer(name);
        if(socialPlayer.getRace() == null) {
            return;
        }
        if(!socialPlayer.getRace().getName().equalsIgnoreCase("human")) {
            event.setCancelled(true);
        }
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
    
    @EventHandler
    public void vampireChange(final EventVampirePlayerInfectionChange event) {
        if(event.getInfection() == 0) {
            return;
        }
        final UPlayer uPlayer = event.getUplayer();
        final String name = uPlayer.getMakerId();
        if(SupernaturalUtils.isWerewolf(name)) {
            System.out.println(1);
            event.setInfection(0);
            uPlayer.setVampire(false);
            return;
        }
        //uPlayer.sendMessage(socialPlayer.getRace().getLoreName() + " " + sc.races.getDefaultRace().getLoreName());
        if(!SupernaturalUtils.isHuman(name)) {
            event.setInfection(0);
            System.out.println(2);
            uPlayer.setVampire(false);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        final String ign = p.getName();
        final PermissionAttachment permissionAttachment = p.addAttachment(sc);
        p.setMetadata("pa", new FixedMetadataValue(sc, permissionAttachment));
        sc.store.create(p.getName());
        final SocialPlayer socialPlayer = sc.save.getSocialPlayer(p.getName());
        if(p.hasPermission("sc.race.issupernatural") || SupernaturalUtils.isSupernatural(p.getName())) {
            socialPlayer.setRace(sc.races.getDefaultRace().getName());
            sc.races.getDefaultRace().applyRace(p, permissionAttachment);
            sc.save.saveSocialPlayer(socialPlayer);
            return;
        }
        if(socialPlayer.getRaceString() == null) {
            socialPlayer.setRace(sc.races.getDefaultRace().getName());
        }
        socialPlayer.applyRace(permissionAttachment);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void consume(final PlayerItemConsumeEvent event) {
        final Player p1 = event.getPlayer();
        final SocialPlayer sp = sc.save.getSocialPlayer(p1.getName());
        if(!sp.isMarried()) {
            return;
        }
        final Player p2 = Bukkit.getPlayer(sp.getMarriedTo());
        if(p2 == null) {
            return;
        }
        p1.setMetadata("last-ate", new FixedMetadataValue(SocialCore.plugin, event.getItem()));
    }
    
    @EventHandler
    public void petnameJoin(final PlayerJoinEvent event) {
        final Player p = event.getPlayer();
        final SocialPlayer socialPlayer = sc.save.getSocialPlayer(p.getName());
        if(socialPlayer == null) {
            return;
        }
        if(!socialPlayer.isMarried()) {
            return;
        }
        if(socialPlayer.getMarriedTo() == null) {
            return;
        }
        final Player marriedToP = Bukkit.getPlayer(socialPlayer.getMarriedTo());
        if(marriedToP != null && marriedToP.isOnline() && socialPlayer.getPetName() != null) {
            marriedToP.sendMessage(SocialCore.plugin.lang.petNameLoginMessage.replace("{pet-name}", socialPlayer.getPetName()));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerLeavePetname(final PlayerQuitEvent event) {
        final Player p = event.getPlayer();
        final SocialPlayer socialPlayer = sc.save.getSocialPlayer(p.getName());
        if(socialPlayer == null) {
            return;
        }
        if(!socialPlayer.isMarried()) {
            return;
        }
        if(socialPlayer.getMarriedTo() == null) {
            return;
        }
        final Player marriedToP = Bukkit.getPlayer(socialPlayer.getMarriedTo());
        if(marriedToP != null && marriedToP.isOnline() && socialPlayer.getPetName() != null) {
            marriedToP.sendMessage(SocialCore.plugin.lang.petNameLogoutMessage.replace("{pet-name}", socialPlayer.getPetName()));
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
        final SocialPlayer sp = sc.save.getSocialPlayer(p1.getName());
        if(!sp.isMarried()) {
            return;
        }
        final Player p2 = Bukkit.getPlayer(sp.getMarriedTo());
        if(p2 == null) {
            return;
        }
        
        // If either partner in the marriage has the permission node, let it happen.
        if(p1.hasPermission("sc.marriage.sharefood") || p2.hasPermission("sc.marriage.sharefood")) {
            final double distanceSquared = sc.lang.maxConsumeDistanceSquared;
            if(p2.getWorld().getName().equalsIgnoreCase(p1.getWorld().getName()) && p1.getLocation().distanceSquared(p2.getLocation()) <= distanceSquared) {
                if(p1.hasMetadata("last-ate")) {
                    final ItemStack lastAte = (ItemStack) p1.getMetadata("last-ate").get(0).value();
                    if(lastAte != null && !VampWWUtil.canEat(p2, lastAte)) {
                        return;
                    }
                }
                e.setCancelled(true);
                p1.setFoodLevel(p1.getFoodLevel() + changeby / 2);
                p2.setFoodLevel(p2.getFoodLevel() + changeby / 2);
                p1.sendMessage(ChatColor.AQUA + "You shared your food with " + p2.getName());
                
                p2.sendMessage(ChatColor.AQUA + p1.getName() + " shared their food with you");
            }
        }
    }
    
    @EventHandler
    public void onExpChange(final PlayerExpChangeEvent e) {
        
        final double ran = Math.random();
        if(ran <= sc.lang.coupleXPPercent) {
            final Player p = e.getPlayer();
            if(p == null) {
                return;
            }
            
            final SocialPlayer sp = sc.save.getSocialPlayer(p.getName());
            if(sp.isMarried()) {
                final Player p2 = Bukkit.getServer().getPlayer(sp.getMarriedTo());
                if(p2 == null) {
                    return;
                }
                if(!p.getWorld().equals(p2.getWorld())) {
                    return;
                }
                if(p.getLocation().distanceSquared(p2.getLocation()) > Math.pow(sc.lang.coupleXPDistance, 2)) {
                    return;
                }
                
                p2.giveExp(e.getAmount());
            }
        }
    }
    
    @EventHandler
    public void playerSneakEvent(final PlayerToggleSneakEvent e) {
        final Player p = e.getPlayer();
        if(p == null) {
            return;
        }
        if(riding.containsKey(p.getName())) {
            riding.remove(p.getName());
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
            if(riding.containsKey(other.getName())) {
                riding.remove(other.getName());
            }
            activatePiggyBackCooldown(damaged, other);
        }
        if(riding.containsKey(other.getName()) && other.isInsideVehicle()) {
            other.leaveVehicle();
            riding.remove(other.getName());
            if(riding.containsKey(damaged.getName())) {
                riding.remove(damaged.getName());
            }
            activatePiggyBackCooldown(damaged, other);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if(event.getPlayer().isInsideVehicle()) {
            event.getPlayer().leaveVehicle();
            if(riding.containsKey(event.getPlayer().getName())) {
                riding.remove(event.getPlayer().getName());
            }
        }
        if(event.getPlayer().getPassenger() != null) {
            event.getPlayer().getPassenger().leaveVehicle();
            if(riding.containsKey(event.getPlayer().getName())) {
                riding.remove(event.getPlayer().getName());
            }
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
        }.runTaskLater(SocialCore.plugin, SocialCore.plugin.lang.piggybackCooldown);
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
        if(p2 == null) {
            return;
        }
        
        if(!p.isSneaking()) {
            if(riding.containsKey(p2.getName())) {
                return;
            }
            if(p.getItemInHand().getType() != Material.SADDLE) {
                return;
            }
            final SocialPlayer player1 = sc.save.getSocialPlayer(p.getName());
            final SocialPlayer player2 = sc.save.getSocialPlayer(p2.getName());
            
            if(player1.getMarriedTo().equalsIgnoreCase(player2.getPlayerName())) {
                if(!p.hasPermission("sc.marriage.piggyback") && !p2.hasPermission("sc.marriage.piggyback")) {
                    p.sendMessage(ChatColor.RED + "Your couple does not have permission to piggyback.");
                    return;
                }
                if(!SocialCore.plugin.whitelistPiggybackWorlds.contains(p.getWorld().getName())) {
                    p.sendMessage(ChatColor.RED + "Piggybacking is disabled in your world.");
                    return;
                }
                
                if(isOnPiggybackcooldown(p)) {
                    p.sendMessage(ChatColor.RED + "Piggy-back is currently on cooldown.");
                } else {
                    p2.setPassenger(p);
                    p2.sendMessage(ChatColor.AQUA + "You are giving " + p.getName() + " a piggy-back ride!.");
                    p.sendMessage(ChatColor.AQUA + p2.getName() + " is giving you a piggy-back ride.");
                    riding.put(p.getName(), p2.getName());
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
        
        final SocialPlayer player1 = sc.save.getSocialPlayer(p.getName());
        final SocialPlayer player2 = sc.save.getSocialPlayer(p2.getName());
        
        if(player1.getMarriedTo().equalsIgnoreCase(player2.getPlayerName())) {
            
            final Vector v = p.getEyeLocation().toVector();
            final Vector v2 = p2.getEyeLocation().toVector();
            final Vector v3 = v.midpoint(v2);
            final Location loc = v3.toLocation(p.getWorld());
            //ParticleEffect.HEART.display(loc,1,1,1,5,20);
            loc.getWorld().playEffect(loc, Effect.HEART, 1);
            p.sendMessage(ChatColor.AQUA + "You have kissed " + p2.getName() + '.');
            p2.sendMessage(ChatColor.AQUA + p.getName() + " kisses you.");
            final int healAmount = sc.lang.kissHealAmount;
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
