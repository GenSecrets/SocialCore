package com.nicholasdoherty.socialcore.races;

import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/14/13
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */
public class Race {
	private String name,loreName, shortInfo, longInfo,permissionToChose, joinInfo;
	private Map<String,Boolean> permissionsGiven;

	public Race(String name, String loreName, String shortInfo, String longInfo, String permissionToChose, Map<String, Boolean> permissionsGiven, String joinInfo) {
		this.name = name;
		this.loreName = loreName;
		this.shortInfo = shortInfo;
		this.permissionToChose = permissionToChose;
		this.longInfo = longInfo;
		this.permissionsGiven = permissionsGiven;
		this.joinInfo = joinInfo;
	}

	public String getJoinInfo() {
		return joinInfo;
	}

	public void applyRace(Player p) {
	  	PermissionAttachment pa = (PermissionAttachment) p.getMetadata("pa").get(0).value();
		applyRace(p,pa);
	}
	public void applyRace(final Player p, final PermissionAttachment pa) {
		new BukkitRunnable(){
			@Override
			public void run() {
				if (permissionsGiven != null) {

					for (String permission : permissionsGiven.keySet()) {
						boolean setTo = true;
						String origPermission = permission;
						if (permission.charAt(0) == '-') {
							setTo = false;
							permission = permission.substring(1, permission.length()-1);
						}else {
							setTo = true;
						}
						if (permission.length() >=1) {
							if (!p.isPermissionSet(permission)){
								pa.setPermission(permission, setTo);
								//System.out.println("1 " + permission);
							}
							else {
								PermissionAttachmentInfo info = null;
								for (PermissionAttachmentInfo pai : p.getEffectivePermissions()) {
									if (pai.getPermission().equalsIgnoreCase(permission)) {
										if (pai.getAttachment() == null || pai.getAttachment().getPlugin().getName() == null || pai.getAttachment().getPlugin().getName().equals("PermissionsBukkit")){
											pa.setPermission(permission, setTo);
											//System.out.println("2 " + permission);
										}
										else if (permissionsGiven.get(origPermission)) {
											pa.setPermission(permission, setTo);
											//System.out.println("3 " + permission);
										}
									}
								}
							}
						}
					}
				}
			}
		}.runTaskLater(SocialCore.plugin, 2);

	}
	public void unapplyRace(Player p) {
		PermissionAttachment pa = (PermissionAttachment) p.getMetadata("pa").get(0).value();
		/*if (permissionsGiven != null) {
			for (String permission : permissionsGiven.keySet()) {
				pa.unsetPermission(permission);
			}
		} */
		for (String permission :pa.getPermissions().keySet()) {
			pa.unsetPermission(permission);
		}
	}

	public String getName() {
		return name;
	}

	public String getLongInfo() {
		return longInfo;
	}

	public String getLoreName() {
		return loreName;
	}

	public String getShortInfo() {
		return shortInfo;
	}

	public String getPermissionToChose() {
		return permissionToChose;
	}

	public Map<String, Boolean> getPermissionsGiven() {
		return permissionsGiven;
	}

	@Override
	public String toString() {
		return "Race{" +
				"name='" + name + '\'' +
				", loreName='" + loreName + '\'' +
				", shortInfo='" + shortInfo + '\'' +
				", longInfo='" + longInfo + '\'' +
				", permissionToChose='" + permissionToChose + '\'' +
				", permissionsGiven=" + permissionsGiven +
				'}';
	}
}
