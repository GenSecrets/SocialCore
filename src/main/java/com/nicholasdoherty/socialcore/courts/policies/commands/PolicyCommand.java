package com.nicholasdoherty.socialcore.courts.policies.commands;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.courts.policies.Policy;
import com.nicholasdoherty.socialcore.courts.policies.PolicyManager;
import com.nicholasdoherty.socialcore.courts.policies.gui.UnconfirmedPolicyGUI;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

/**
 * Created by john on 9/12/16.
 */
public class PolicyCommand implements CommandExecutor {
    private Courts courts;
    private PolicyManager policyManager;

    public PolicyCommand(Courts courts, PolicyManager policyManager) {
        this.courts = courts;
        this.policyManager = policyManager;
        courts.getPlugin().getCommand("policy").setExecutor(this);
    }

    private boolean isDraftBook(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        if (itemStack.getType() != Material.BOOK_AND_QUILL) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasDisplayName() || !itemMeta.getDisplayName().equalsIgnoreCase("Policy Draft")) {
            return false;
        }
        return true;
    }

    private void finishBook(Player p, ItemStack itemStack, Citizen citizen) {
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        String text = String.join(" ", bookMeta.getPages()).trim();
        if (text.length() > policyManager.getPolicyConfig().getPolicyMaxCharacters()) {
            p.sendMessage(policyManager.getPolicyConfig().getPolicyFinishCharactersMessage());
            return;
        }
        Optional<Policy> policyId = courts.getSqlSaveManager().createPolicy(text, citizen);
        if (policyId.isPresent()) {
            p.sendMessage(policyManager.getPolicyConfig().getPolicyFinishMessage());
            p.getInventory().remove(itemStack);
        } else {
            p.sendMessage(ChatColor.RED + "An error occurred while creating your policy... Error Code: 900");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Citizen citizen = courts.getCitizenManager().toCitizen(p);
            if (courts.getJudgeManager().isJudge(citizen.getUuid())) {
                Optional<Policy> currentPolicy =
                        Courts.getCourts().getPolicyManager().allPolicies()
                                .stream().filter(policy -> policy.getState() == Policy.State.UNCONFIRMED)
                                .findFirst();
                if (args.length == 0) {
                    if (currentPolicy.isPresent()) {
                        UnconfirmedPolicyGUI.createAndOpen(p, citizen, currentPolicy.get());
                    } else {
                        sender.sendMessage(ChatColor.GRAY + "/policy new - Creates a book to draft a new policy in.");
                        sender.sendMessage(ChatColor.GRAY + "/policy finish - Finalizes the policy book you're holding.");
                    }
                } else {
                    if (args[0].equalsIgnoreCase("new")) {
                        ItemStack give = new ItemStackBuilder(Material.BOOK_AND_QUILL).setName("Policy Draft")
                                .toItemStack();
                        p.getInventory().addItem(give);
                        p.sendMessage(policyManager.getPolicyConfig().getPolicyNewMessage());
                        return true;
                    } else if (args[0].equalsIgnoreCase("finish")) {
                        if (currentPolicy.isPresent()) {
                            p.sendMessage(policyManager.getPolicyConfig().getPolicyDraftAlreadyPendingMessage());
                            return true;
                        }
                        ItemStack itemInHand = p.getInventory().getItemInMainHand();
                        if (isDraftBook(itemInHand)) {
                            finishBook(p, itemInHand, citizen);
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Command not recognized, try /policy to view commands.");
                    }
                }
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED + "This command is only for judges, try /policies.");
        return true;
    }

}
