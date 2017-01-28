package com.nicholasdoherty.socialcore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionAttachmentInfo;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 12/26/13
 * Time: 1:51
 * To change this template use File | Settings | File Templates.
 */
public class ViewPermissionsCommand
		implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (strings.length == 1) {
			boolean test = commandSender.hasPermission("test.test");
			commandSender.addAttachment(SocialCore.plugin).setPermission("test.test", !test);
			commandSender.sendMessage(""+ commandSender.hasPermission("test.test"));
			return true;
		}
		System.out.println("Permissions:");
		for (PermissionAttachmentInfo pai :commandSender.getEffectivePermissions()) {
			System.out.println(pai.getPermission() + " " + pai.getValue());
			if (pai.getAttachment() != null) {
				System.out.print(" " +pai.getAttachment().getPlugin().getName());
			}
		}
		return true;
	}
}
