import java.util.HashMap;

/*package com.nicholasdoherty.socialcore;


import java.util.HashMap;
import java.util.Map;

import com.massivecraft.vampire.entity.UPlayer;
import com.massivecraft.vampire.entity.UPlayerColl;
import com.massivecraft.vampire.entity.UPlayerColls;
import com.massivecraft.vampire.event.VampirePlayerInfectionChangeEvent;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.SupernaturalUtils;
import com.nicholasdoherty.socialcore.libraries.ParticleEffect;
import com.nicholasdoherty.socialcore.races.Race;
import com.nicholasdoherty.werewolf.customevents.WerewolveInfectionEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachment;

import org.bukkit.util.Vector;

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
	public void vampireChange(VampirePlayerInfectionChangeEvent event) {
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
		if(p2.getWorld().getName().equalsIgnoreCase(p1.getWorld().getName())&&p1.getLocation().distance(p2.getLocation()) <= 5) {
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
			p.leaveVehicle();
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
				p2.setPassenger(p);
				p2.sendMessage(ChatColor.AQUA+"You are giving "+p.getName()+" a piggy-back ride!.");
				p.sendMessage(ChatColor.AQUA+p2.getName()+" is giving you a piggy-back ride.");
				riding.put(p.getName(), p2.getName());
			}
			
			return;
		}
		
		if (p.getLocation().distanceSquared(p2.getLocation()) > 4)
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
			ParticleEffect.HEART.display(loc,1,1,1,5,20);

			p.sendMessage(ChatColor.AQUA+"You have kissed "+p2.getName()+".");
			p2.sendMessage(ChatColor.AQUA+p.getName()+" kisses you.");
			try {
				if(p.getHealth()+1 <= p.getMaxHealth()) {
					p.setHealth(p.getHealth() + 1);
				}
				if(p2.getHealth()+1 <= p2.getMaxHealth()) {
					p2.setHealth(p2.getHealth() + 1);
				}
			} catch (Exception ignore) {}


			sc.marriages.kissPlayer(p.getName());
		}
		
	}

}                            */
