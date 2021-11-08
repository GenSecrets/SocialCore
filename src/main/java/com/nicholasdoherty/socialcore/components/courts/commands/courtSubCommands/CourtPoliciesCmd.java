package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.policies.PolicyManager;
import com.nicholasdoherty.socialcore.components.courts.policies.gui.PolicyGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by john on 9/12/16.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class CourtPoliciesCmd {
    private final Courts courts;
    private final PolicyManager policyManager;
    private final CommandSender commandSender;
    private final String[] args;

    public CourtPoliciesCmd(final Courts courts, final PolicyManager policyManager, final CommandSender commandSender, final String[] args) {
        this.courts = courts;
        this.policyManager = policyManager;
        this.commandSender = commandSender;
        this.args = args;
    }

    public boolean runCommand() {
        boolean failed = false;
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("failed")) {
                failed = true;
            }
        }
        PolicyGUI.createAndOpen((Player) commandSender, failed);
        return true;
    }
}
