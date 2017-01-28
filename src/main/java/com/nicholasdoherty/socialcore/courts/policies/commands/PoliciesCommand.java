package com.nicholasdoherty.socialcore.courts.policies.commands;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.policies.PolicyManager;
import com.nicholasdoherty.socialcore.courts.policies.gui.PolicyGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by john on 9/12/16.
 */
public class PoliciesCommand implements CommandExecutor{
    private Courts courts;
    private PolicyManager policyManager;

    public PoliciesCommand(Courts courts, PolicyManager policyManager) {
        this.courts = courts;
        this.policyManager = policyManager;
        courts.getPlugin().getCommand("policies").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        PolicyGUI.createAndOpen((Player) commandSender);
        return true;
    }
}
