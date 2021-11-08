package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.components.courts.policies.Policy;
import com.nicholasdoherty.socialcore.components.courts.policies.Policy.State;
import com.nicholasdoherty.socialcore.components.courts.policies.PolicyManager;
import com.nicholasdoherty.socialcore.components.courts.policies.gui.UnconfirmedPolicyGUI;
import com.voxmc.voxlib.util.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

/**
 * Created by john on 9/12/16.
 */
public class CourtPolicyCmd {
    private final Courts courts;
    private final PolicyManager policyManager;
    private final CommandSender commandSender;
    private final String[] args;
    
    public CourtPolicyCmd(final Courts courts, final PolicyManager policyManager, final CommandSender commandSender, final String[] args) {
        this.courts = courts;
        this.policyManager = policyManager;
        this.commandSender = commandSender;
        this.args = args;
    }
    
    private boolean isDraftBook(final ItemStack itemStack) {
        if(itemStack == null) {
            return false;
        }
        if(itemStack.getType() != Material.WRITABLE_BOOK) {
            return false;
        }
        final ItemMeta itemMeta = itemStack.getItemMeta();
        return !(itemMeta == null || !itemMeta.hasDisplayName() || !itemMeta.getDisplayName().equalsIgnoreCase("Policy Draft"));
    }
    
    @SuppressWarnings("TypeMayBeWeakened")
    private void finishBook(final Player p, final ItemStack itemStack, final Citizen citizen) {
        final BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        final String text = ChatColor.DARK_AQUA + ChatColor.stripColor(String.join(" ", bookMeta.getPages()).trim());
        if(text.length() > policyManager.getPolicyConfig().getPolicyMaxCharacters()) {
            p.sendMessage(policyManager.getPolicyConfig().getPolicyFinishCharactersMessage());
            return;
        }
        final Optional<Policy> policyId = courts.getSqlSaveManager().createPolicy(text, citizen);
        if(policyId.isPresent()) {
            p.sendMessage(policyManager.getPolicyConfig().getPolicyFinishMessage());
            p.getInventory().remove(itemStack);
        } else {
            p.sendMessage(ChatColor.RED + "An error occurred while creating your policy... Error Code: 900");
        }
    }

    public boolean runCommand() {
        if(commandSender instanceof Player) {
            final Player p = (Player) commandSender;
            final Citizen citizen = courts.getCitizenManager().toCitizen(p);
            if(courts.getJudgeManager().isJudge(citizen.getUuid())) {
                final Optional<Policy> currentPolicy =
                        Courts.getCourts().getPolicyManager().allPolicies()
                                .stream().filter(policy -> policy.getState() == State.UNCONFIRMED)
                                .findFirst();
                if(args.length == 0) {
                    if(currentPolicy.isPresent()) {
                        UnconfirmedPolicyGUI.createAndOpen(p, citizen, currentPolicy.get());
                    } else {
                        commandSender.sendMessage(ChatColor.GRAY + "/court policy new - Creates a book to draft a new policy in.");
                        commandSender.sendMessage(ChatColor.GRAY + "/court policy finish - Finalizes the policy book you're holding.");
                    }
                } else {
                    if(args[0].equalsIgnoreCase("new")) {
                        final ItemStack give = new ItemStackBuilder(Material.WRITABLE_BOOK).setName("Policy Draft")
                                .toItemStack();
                        p.getInventory().addItem(give);
                        p.sendMessage(policyManager.getPolicyConfig().getPolicyNewMessage());
                        return true;
                    } else if(args[0].equalsIgnoreCase("finish")) {
                        if(currentPolicy.isPresent()) {
                            p.sendMessage(policyManager.getPolicyConfig().getPolicyDraftAlreadyPendingMessage());
                            return true;
                        }
                        final ItemStack itemInHand = p.getInventory().getItemInMainHand();
                        if(isDraftBook(itemInHand)) {
                            finishBook(p, itemInHand, citizen);
                            return true;
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "Command not recognized, try /court policy to view commands.");
                    }
                }
            }
            return true;
        }
        commandSender.sendMessage(ChatColor.RED + "This command is only for judges, try /court policies.");
        return true;
    }
}
