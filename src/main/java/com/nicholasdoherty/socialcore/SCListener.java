package com.nicholasdoherty.socialcore;


import com.massivecraft.vampire.entity.UPlayer;
import com.massivecraft.vampire.event.EventVampirePlayerInfectionChange;
import com.nicholasdoherty.socialcore.utils.VampWWUtil;
import com.nicholasdoherty.werewolf.customevents.WerewolveInfectionEvent;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SCListener implements Listener {
	public static HashMap<String,String> riding = new HashMap<String,String>();
	SocialCore sc;
	public SCListener(SocialCore sc) {
		this.sc = sc;
	}
	@EventHandler
	public void werewolfChange(WerewolveInfectionEvent event) {
		String name = event.getpName();
		if (SupernaturalUtils.isVampire(name)) {
			event.setCancelled(true);
			return;
		}
		SocialPlayer socialPlayer = sc.save.getSocialPlayer(name);
		if (socialPlayer.getRace() == null)
			return;
		if (!socialPlayer.getRace().getName().equalsIgnoreCase("human")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onlogoutInventory(PlayerQuitEvent event) {
		event.getPlayer().getInventory().getViewers().stream().forEach(v -> {
				new BukkitRunnable(){
					@Override
					public void run() {
						v.closeInventory();
					}
				}.runTaskLater(sc,1);
		});
	}

	@EventHandler
	public void vampireChange(EventVampirePlayerInfectionChange event) {
		if (event.getInfection() == 0)
			return;
		UPlayer uPlayer = event.getUplayer();
		String name = uPlayer.getId();
		if (SupernaturalUtils.isWerewolf(name)) {
            System.out.println(1);
			event.setInfection(0);
			uPlayer.setVampire(false);
			return;
		}
		//uPlayer.sendMessage(socialPlayer.getRace().getLoreName() + " " + sc.races.getDefaultRace().getLoreName());
		if (!SupernaturalUtils.isHuman(name)) {
			event.setInfection(0);
            System.out.println(2);
            uPlayer.setVampire(false);
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void playerJoin(PlayerJoinEvent event) {
		final Player p = event.getPlayer();
		final String ign = p.getName();
		PermissionAttachment permissionAttachment = p.addAttachment(sc);
		p.setMetadata("pa", new FixedMetadataValue(sc, permissionAttachment));
		sc.store.create(p.getName());
		SocialPlayer socialPlayer = sc.save.getSocialPlayer(p.getName());
		if (p.hasPermission("sc.race.issupernatural") || SupernaturalUtils.isSupernatural(p.getName())) {
			socialPlayer.setRace(sc.races.getDefaultRace().getName());
			sc.races.getDefaultRace().applyRace(p,permissionAttachment);
			sc.save.saveSocialPlayer(socialPlayer);
			return;
		}
		if (socialPlayer.getRaceString() == null) {
			socialPlayer.setRace(sc.races.getDefaultRace().getName());
		}
		socialPlayer.applyRace(permissionAttachment);
	}


	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void consume(PlayerItemConsumeEvent event) {
		Player p1 = event.getPlayer();
		SocialPlayer sp = sc.save.getSocialPlayer(p1.getName());
		if(!sp.isMarried()) return;
		Player p2 = Bukkit.getPlayer(sp.getMarriedTo());
		if(p2 == null) {
			return;
		}
		p1.setMetadata("last-ate",new FixedMetadataValue(SocialCore.plugin,event.getItem()));
	}
	@EventHandler
	public void petnameJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		SocialPlayer socialPlayer = sc.save.getSocialPlayer(p.getName());
		if (socialPlayer == null) {
			return;
		}
		if (!socialPlayer.isMarried()) {
			return;
		}
		if (socialPlayer.getMarriedTo() == null) {
			return;
		}
		Player marriedToP = Bukkit.getPlayer(socialPlayer.getMarriedTo());
		if (marriedToP != null && marriedToP.isOnline() && socialPlayer.getPetName() != null) {
			marriedToP.sendMessage(SocialCore.plugin.lang.petNameLoginMessage.replace("{pet-name}",socialPlayer.getPetName()));
		}
	}
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerLeavePetname(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        SocialPlayer socialPlayer = sc.save.getSocialPlayer(p.getName());
        if (socialPlayer == null) {
            return;
        }
        if (!socialPlayer.isMarried()) {
            return;
        }
        if (socialPlayer.getMarriedTo() == null) {
            return;
        }
        Player marriedToP = Bukkit.getPlayer(socialPlayer.getMarriedTo());
        if (marriedToP != null && marriedToP.isOnline() && socialPlayer.getPetName() != null) {
            marriedToP.sendMessage(SocialCore.plugin.lang.petNameLogoutMessage.replace("{pet-name}",socialPlayer.getPetName()));
        }
    }

    @EventHandler(ignoreCancelled = true)
	public void onPlayerItemConsume(FoodLevelChangeEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		Player p1 =(Player) e.getEntity();
		if (e.getFoodLevel() < p1.getFoodLevel()) {
			return;
		}
		int changeby = e.getFoodLevel() - p1.getFoodLevel();
		SocialPlayer sp = sc.save.getSocialPlayer(p1.getName());
		if(!sp.isMarried()) return;
		Player p2 = Bukkit.getPlayer(sp.getMarriedTo());
		if(p2 == null) {
			return;
		}
		double distanceSquared = sc.lang.maxConsumeDistanceSquared;
		if(p2.getWorld().getName().equalsIgnoreCase(p1.getWorld().getName())&&p1.getLocation().distanceSquared(p2.getLocation()) <= distanceSquared) {
			if (p1.hasMetadata("last-ate")) {
				ItemStack lastAte = (ItemStack) p1.getMetadata("last-ate").get(0).value();
				if (lastAte != null && !VampWWUtil.canEat(p2,lastAte)) {
					return;
				}
			}
			e.setCancelled(true);
			p1.setFoodLevel(p1.getFoodLevel() + (changeby)/2);
			p2.setFoodLevel(p2.getFoodLevel() + (changeby)/2);
			p1.sendMessage(ChatColor.AQUA+"You shared your food with "+p2.getName());

			p2.sendMessage(ChatColor.AQUA+ p1.getName()+ " shared their food with you");
		}
	}
	@EventHandler
	public void onExpChange(PlayerExpChangeEvent e) {
		
		double ran = Math.random();
		if (ran <= sc.lang.coupleXPPercent) {
			Player p = e.getPlayer();
			if (p == null)
				return;
			
			SocialPlayer sp = sc.save.getSocialPlayer(p.getName());
			if (sp.isMarried()) {
				Player p2 = Bukkit.getServer().getPlayer(sp.getMarriedTo());
				if (p2 == null)
					return;
				if(!p.getWorld().equals(p2.getWorld())) return;
				if (p.getLocation().distanceSquared(p2.getLocation()) > Math.pow(sc.lang.coupleXPDistance,2))
					return;
				
				p2.giveExp(e.getAmount());
			}
		}
	}
	@EventHandler
	public void playerSneakEvent(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if (p == null)
			return;
		if(riding.containsKey(p.getName())) {
			riding.remove(p.getName());
			if (p.getVehicle() != null && p.getVehicle() instanceof Player) {
				Player other = (Player) p.getVehicle();
				activatePiggyBackCooldown(p,other);
			}
			p.leaveVehicle();
		}
	}
	@EventHandler
	public void PlayerDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player damaged = (Player) event.getEntity();
		Player other;
		if (damaged.getPassenger() != null && damaged.getPassenger() instanceof Player) {
			other = (Player) damaged.getPassenger();
		}else if (damaged.isInsideVehicle() && damaged.getVehicle() != null && damaged.getVehicle() instanceof Player) {
			other = (Player) damaged.getVehicle();
		}else {
			return;
		}
		if (riding.containsKey(damaged.getName()) && damaged.isInsideVehicle()) {
			damaged.leaveVehicle();
			riding.remove(damaged.getName());
			if (riding.containsKey(other.getName())) {
				riding.remove(other.getName());
			}
			activatePiggyBackCooldown(damaged,other);
		}
		if (riding.containsKey(other.getName()) && other.isInsideVehicle()) {
			other.leaveVehicle();
			riding.remove(other.getName());
			if (riding.containsKey(damaged.getName())) {
				riding.remove(damaged.getName());
			}
			activatePiggyBackCooldown(damaged,other);
		}
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
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
	Set<UUID> onPiggyBackCooldown = new HashSet<>();
	private boolean isOnPiggybackcooldown(Player p1) {
		return onPiggyBackCooldown.contains(p1.getUniqueId());
	}
	private void activatePiggyBackCooldown(Player p1, Player p2) {
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
	public void onPlayerInteract(PlayerInteractEntityEvent e) {
		if (!(e.getRightClicked() instanceof Player))
			return;
					
		Player p = e.getPlayer();
		if (p == null)
			return;
		
		Player p2  = (Player)e.getRightClicked();
		if (p2 == null)
			return;
		
		if (!p.isSneaking()) {
			if(riding.containsKey(p2.getName())) {
				return;
			}
			if(p.getItemInHand().getType() != Material.SADDLE)
				return;
			SocialPlayer player1 = sc.save.getSocialPlayer(p.getName());
			SocialPlayer player2 = sc.save.getSocialPlayer(p2.getName());
			
			if (player1.getMarriedTo().equalsIgnoreCase(player2.getPlayerName())) {
				if (isOnPiggybackcooldown(p)) {
					p.sendMessage(ChatColor.RED + "Piggy-back is currently on cooldown.");
				}else {
					p2.setPassenger(p);
					p2.sendMessage(ChatColor.AQUA+"You are giving "+p.getName()+" a piggy-back ride!.");
					p.sendMessage(ChatColor.AQUA+p2.getName()+" is giving you a piggy-back ride.");
					riding.put(p.getName(), p2.getName());
				}
			}
			
			return;
		}
		
		if (p.getLocation().distanceSquared(p2.getLocation()) > 16)
			return;
		
		if (!sc.marriages.canPlayerKiss(p.getName()))
			return;
		
		SocialPlayer player1 = sc.save.getSocialPlayer(p.getName());
		SocialPlayer player2 = sc.save.getSocialPlayer(p2.getName());
		
		if (player1.getMarriedTo().equalsIgnoreCase(player2.getPlayerName())) {
			
			Vector v = p.getEyeLocation().toVector();
			Vector v2 = p2.getEyeLocation().toVector();
			Vector v3 = v.midpoint(v2);
			Location loc = v3.toLocation(p.getWorld());
			//ParticleEffect.HEART.display(loc,1,1,1,5,20);
			loc.getWorld().playEffect(loc, Effect.HEART, 1);
			p.sendMessage(ChatColor.AQUA+"You have kissed "+p2.getName()+".");
			p2.sendMessage(ChatColor.AQUA+p.getName()+" kisses you.");
			int healAmount = sc.lang.kissHealAmount;
			if (healAmount > 0) {
				try {
					if(p.getHealth()+healAmount <= p.getMaxHealth()) {
						p.setHealth(p.getHealth() + healAmount);
					}else {
						p.setHealth(p.getMaxHealth());
					}
					if(p2.getHealth()+healAmount <= p2.getMaxHealth()) {
						p2.setHealth(p2.getHealth() + healAmount);
					}else {
						p2.setHealth(p2.getMaxHealth());
					}
				} catch (Exception ignore) {}
			}


			sc.marriages.kissPlayer(p.getName());
		}
		
	}

}
