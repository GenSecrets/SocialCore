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
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class PoliciesCommand implements CommandExecutor {
    private final Courts courts;
    private final PolicyManager policyManager;
    
    public PoliciesCommand(final Courts courts, final PolicyManager policyManager) {
        this.courts = courts;
        this.policyManager = policyManager;
        courts.getPlugin().getCommand("policies").setExecutor(this);
    }
    
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
        boolean failed = false;
        if(strings.length > 0) {
            if(strings[0].equalsIgnoreCase("failed")) {
                failed = true;
            }
        }
        PolicyGUI.createAndOpen((Player) commandSender, failed);
        return true;
    }
}
