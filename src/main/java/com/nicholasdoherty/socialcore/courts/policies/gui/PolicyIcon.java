package com.nicholasdoherty.socialcore.courts.policies.gui;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryView;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.courts.policies.Policy;
import com.nicholasdoherty.socialcore.courts.policies.PolicyConfig;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import com.nicholasdoherty.socialcore.utils.ItemUtil;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by john on 9/12/16.
 */
public class PolicyIcon implements ClickItem {
    private InventoryView inventoryView;
    private Optional<Policy> policy;
    private Citizen viewer;

    public PolicyIcon(InventoryView inventoryView, Optional<Policy> policy, Citizen viewer) {
        this.inventoryView = inventoryView;
        this.policy = policy;
        this.viewer = viewer;
    }

    @Override
    public void click(boolean right) {
        policy.ifPresent(policyInstance -> {
            boolean approve = !right;
            if (canConfirmVote()) {
                Judge judge = Courts.getCourts().getJudgeManager().getJudge(viewer.getUuid());
                if (judge != null) {
                    Courts.getCourts().getSqlSaveManager().setJudgeConfirmation(judge,policyInstance,approve).ifPresent(
                            policy1 -> Courts.getCourts().getPolicyManager().checkStateChange(policy1)
                    );
                    policy = policy.map(Courts.getCourts().getPolicyManager()::updateIfStale);
                    if (policy.isPresent() && policy.get().getState() != Policy.State.UNCONFIRMED) {
                        inventoryView.getInventoryGUI().close();
                    }
                }
            }else if (canVote()) {
                Courts.getCourts().getSqlSaveManager().setCitizenVote(viewer,policyInstance,approve).ifPresent(
                        policy1 -> Courts.getCourts().getPolicyManager().checkStateChange(policy1)
                );
            }
        });
        inventoryView.update();
        inventoryView.update(this);
        inventoryView.getInventoryGUI().open();
    }

    private boolean canConfirmVote() {
        return Courts.getCourts().getJudgeManager().isJudge(viewer.getUuid())
                && policy.map(policyInstance -> policyInstance.getState() == Policy.State.UNCONFIRMED).orElse(false);
    }

    private boolean canVote() {
        return policy.map(policyInstance -> policyInstance.getState() == Policy.State.MAIN_VOTING).orElse(false);
    }

    @Override
    public ItemStack itemstack() {
        policy = policy.map(Courts.getCourts().getPolicyManager()::updateIfStale);
        return policy.map(policyInstance -> {
            PolicyConfig policyConfig = Courts.getCourts().getPolicyManager().getPolicyConfig();
            String template;
            if (policyInstance.getState() == Policy.State.IN_EFFECT) {
                template = policyConfig.getPassedPolicyIconBaseItem();
            }else {
                template = policyConfig.getUnpassedPolicyIconBaseItem();
            }
            boolean isJudge = Courts.getCourts().getJudgeManager().isJudge(viewer.getUuid());
            Optional<String> yourVoteStatus;
            switch (policyInstance.getState()) {
                case UNCONFIRMED:
                    if (isJudge) {
                        if (policyInstance.getConfirmApprovals().contains(viewer)) {
                            yourVoteStatus = Optional.ofNullable(policyConfig.getVoteStatusConfirmed());
                        }else {
                            yourVoteStatus = Optional.ofNullable(policyConfig.getVoteStatusUnconfirmed());
                        }
                        break;
                    }
                case MAIN_VOTING:
                    if (policyInstance.getApprovals().contains(viewer)) {
                        yourVoteStatus = Optional.ofNullable(policyConfig.getVoteStatusApproved());
                    }else if (policyInstance.getDisapprovals().contains(viewer)) {
                        yourVoteStatus = Optional.ofNullable(policyConfig.getVoteStatusDisapproved());
                    }else {
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
                    .replace("{policy-text}", wrapColor(WordUtils.wrap(policyInstance.getText(),26).replace(" ","_")).replace("\n","|"))
                    .replace("{policy-status}", policyInstance.getState().toString().replace(" ","_"))
                    .replace("{vote-status}",yourVoteStatus.orElse("").replace(" ","_"))
                    .replace("{approval-rating}",policyInstance.approvalRating()+"")
                    .replace("{vote-progress}",""+policyInstance.voteProgress())
                    .replace("{policy-status-description}",
                            Courts.getCourts().getPolicyManager().getPolicyConfig()
                                    .getStateDescriptions()
                                    .getOrDefault(policyInstance.getState(), "No description specified")
                    .replace(" ","_"));

            ItemStackBuilder itemStackBuilder = new ItemStackBuilder(ItemUtil.getFromEssentialsString(template));
            if (canConfirmVote()) {
                itemStackBuilder = itemStackBuilder.addLore(
                        ChatColor.GRAY + "<Left click to confirm>",
                        ChatColor.GRAY + "<Right click to unconfirm>");
            }else if (canVote()) {
                itemStackBuilder = itemStackBuilder.addLore(
                        ChatColor.GRAY + "<Left click to approve>",
                        ChatColor.GRAY + "<Right click to disapprove>");
            }
            return itemStackBuilder.toItemStack();
        }).orElse(new ItemStack(Material.BEDROCK));
    }
    private static String wrapColor(String input) {
        String[] in = input.split("\\|");
        if (in.length < 2) {
            return input;
        }
        for (int i = 0; i < in.length-1; i++) {
            String line = in[i];
            String currentColor = ChatColor.getLastColors(line);
            in[i+1] = currentColor + in[i+1];
        }
        return String.join("|",in);
    }
}
