package com.nicholasdoherty.socialcore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/18/13
 * Time: 20:38
 * To change this template use File | Settings | File Templates.
 */
public class FixerCommand implements CommandExecutor{
   	SocialCore plugin;

	public FixerCommand(SocialCore plugin) {
		this.plugin = plugin;
		plugin.getCommand("fixmarriages").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		File file = new File(".isFixed");
		Fixer fixer = new Fixer(plugin);
		if (!file.exists() || (strings.length ==1 && strings[0].equals("force"))) {
			fixer.fix();
			commandSender.sendMessage("Fixed!");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}else {
			commandSender.sendMessage("Already fixed!");
		}
		return true;
	}
}
