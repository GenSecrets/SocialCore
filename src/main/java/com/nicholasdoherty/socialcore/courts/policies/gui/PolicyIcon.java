package com.nicholasdoherty.socialcore.courts.policies.gui;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.courts.Courts;
import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.gui.inventorygui.InventoryView;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.courts.policies.Policy;
import com.nicholasdoherty.socialcore.courts.policies.Policy.State;
import com.nicholasdoherty.socialcore.courts.policies.PolicyConfig;
import com.voxmc.voxlib.util.ItemStackBuilder;
import com.voxmc.voxlib.util.ItemUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by john on 9/12/16.
 */
public class PolicyIcon implements ClickItem {
    private final InventoryView inventoryView;
    private final Citizen viewer;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Policy> policy;
    
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public PolicyIcon(final InventoryView inventoryView, final Optional<Policy> policy, final Citizen viewer) {
        this.inventoryView = inventoryView;
        this.policy = policy;
        this.viewer = viewer;
    }
    
    private static String wrapColor(final String input) {
        final String[] in = input.split("\\|");
        if(in.length < 2) {
            return input;
        }
        for(int i = 0; i < in.length - 1; i++) {
            final String line = ChatColor.DARK_AQUA + in[i];
            final String currentColor = ChatColor.getLastColors(line);
            in[i + 1] = currentColor + in[i + 1];
        }
        return String.join("|", in);
    }
    
    @Override
    public void click(final boolean right, final boolean shift) {
        policy.ifPresent(policyInstance -> {
            final boolean approve = !right;
            if(canConfirmVote()) {
                final Judge judge = Courts.getCourts().getJudgeManager().getJudge(viewer.getUuid());
                if(judge != null) {
                    Courts.getCourts().getSqlSaveManager().setJudgeConfirmation(judge, policyInstance, approve).ifPresent(
                            policy1 -> Courts.getCourts().getPolicyManager().checkStateChange(policy1)
                    );
                    policy = policy.map(Courts.getCourts().getPolicyManager()::updateIfStale);
                    if(policy.isPresent() && policy.get().getState() != State.UNCONFIRMED) {
                        inventoryView.getInventoryGUI().close();
                    }
                }
            } else if(canVote()) {
                Courts.getCourts().getSqlSaveManager().setCitizenVote(viewer, policyInstance, approve).ifPresent(
                        policy1 -> Courts.getCourts().getPolicyManager().checkStateChange(policy1)
                );
            } else if(shift) {
                if(Courts.getCourts().getJudgeManager().isJudge(viewer.getUuid())
                        || viewer.getPlayer().hasPermission("courts.policy.delete")) {
                    final int id = policyInstance.getId();
                    Courts.getCourts().getSqlSaveManager().deletePolicy((long) id);
                    final Player p = viewer.getPlayer();
                    if(p.isOnline()) {
                        inventoryView.update();
                        p.sendMessage(ChatColor.GREEN + "Policy has been deleted!");
                        Bukkit.getServer().getOnlinePlayers()
                                .forEach(e -> e.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        String.format("&6[&eCourt&6] Judge %s&a has removed policy %s.", p.getName(), id))));
                    }
                }
            }
        });
        inventoryView.update();
        inventoryView.update(this);
        inventoryView.getInventoryGUI().open();
    }
    
    private boolean canConfirmVote() {
        return Courts.getCourts().getJudgeManager().isJudge(viewer.getUuid())
                && policy.map(policyInstance -> policyInstance.getState() == State.UNCONFIRMED).orElse(false);
    }
    
    private boolean canVote() {
        return policy.map(policyInstance -> policyInstance.getState() == State.MAIN_VOTING).orElse(false);
    }
    
    @Override
    public ItemStack itemstack() {
        policy = policy.map(Courts.getCourts().getPolicyManager()::updateIfStale);
        return policy.map(policyInstance -> {
            PolicyConfig policyConfig = Courts.getCourts().getPolicyManager().getPolicyConfig();
            String template;
            if(policyInstance.getState() == State.IN_EFFECT) {
                template = policyConfig.getPassedPolicyIconBaseItem();
            } else {
                template = policyConfig.getUnpassedPolicyIconBaseItem();
            }
            boolean isJudge = Courts.getCourts().getJudgeManager().isJudge(viewer.getUuid());
            Optional<String> yourVoteStatus;
            switch(policyInstance.getState()) {
                case UNCONFIRMED:
                    if(isJudge) {
                        if(policyInstance.getConfirmApprovals().contains(viewer)) {
                            yourVoteStatus = Optional.ofNullable(policyConfig.getVoteStatusConfirmed());
                        } else {
                            yourVoteStatus = Optional.ofNullable(policyConfig.getVoteStatusUnconfirmed());
                        }
                        break;
                    }
                case MAIN_VOTING:
                    if(policyInstance.getApprovals().contains(viewer)) {
                        yourVoteStatus = Optional.ofNullable(policyConfig.getVoteStatusApproved());
                    } else if(policyInstance.getDisapprovals().contains(viewer)) {
                        yourVoteStatus = Optional.ofNullable(policyConfig.getVoteStatusDisapproved());
                    } else {
                        yourVoteStatus = Optional.ofNullable(policyConfig.getVoteStatusAbstained());
                    }
                    break;
                case FAILED:
                case IN_EFFECT:
                case REPEALED:
                case UNFINISHED:
                default:
                    yourVoteStatus = Optional.empty();
                    break;
            }
            template = template.replace("{policy-number}", policyInstance.getId() + "")
                    .replace("{policy-text}", wrapColor(WordUtils.wrap(policyInstance.getText(), 26).replace(" ", "_")).replace("\n", "|"))
                    .replace("{policy-status}", policyInstance.getState().toString().replace(" ", "_"))
                    .replace("{vote-status}", yourVoteStatus.orElse("").replace(" ", "_"))
                    .replace("{approval-rating}", policyInstance.approvalRating() + "")
                    .replace("{vote-progress}", "" + policyInstance.voteProgress())
                    .replace("{policy-status-description}",
                            Courts.getCourts().getPolicyManager().getPolicyConfig()
                                    .getStateDescriptions()
                                    .getOrDefault(policyInstance.getState(), "No description specified")
                                    .replace(" ", "_"));
            
            ItemStackBuilder itemStackBuilder = new ItemStackBuilder(ItemUtil.getFromEssentialsString(template));
            if(canConfirmVote()) {
                itemStackBuilder = itemStackBuilder.addLore(
                        ChatColor.GRAY + "<Left click to confirm>",
                        ChatColor.GRAY + "<Right click to unconfirm>");
            } else if(canVote()) {
                itemStackBuilder = itemStackBuilder.addLore(
                        ChatColor.GRAY + "<Left click to approve>",
                        ChatColor.GRAY + "<Right click to disapprove>");
            } else if(inventoryView instanceof PolicyView) {
                if(SocialCore.plugin.getCourts().getJudgeManager().isJudge(viewer.getUuid())) {
                    itemStackBuilder = itemStackBuilder.addLore(ChatColor.RED + "<Shift-click to delete>");
                }
            }
            return itemStackBuilder.toItemStack();
        }).orElse(new ItemStack(Material.BEDROCK));
    }
}
