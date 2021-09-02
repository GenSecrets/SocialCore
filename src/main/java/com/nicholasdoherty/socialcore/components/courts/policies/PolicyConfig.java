package com.nicholasdoherty.socialcore.components.courts.policies;

import com.nicholasdoherty.socialcore.components.courts.policies.Policy.State;
import com.voxmc.voxlib.VoxTimeUnit;
import com.voxmc.voxlib.util.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 9/12/16.
 */
@SuppressWarnings("MapReplaceableByEnumMap")
public class PolicyConfig {
    private final String passedPolicyIconBaseItem;
    private final String unpassedPolicyIconBaseItem;
    private final Map<State, String> stateDescriptions;
    private final String voteStatusUnconfirmed;
    private final String voteStatusConfirmed;
    private final String voteStatusApproved;
    private final String voteStatusDisapproved;
    private final String voteStatusAbstained;
    private final String policyDraftAlreadyPendingMessage;
    private final String policyNewMessage;
    private final String policyFinishMessage;
    private final String policyFinishCharactersMessage;
    private final String policyOfficialApproveVotesMessage;
    private final String policyOfficialApproveAutoMessage;
    private final String policyStartVotingMessage;
    private final long policyCheckInterval;
    private final long policyAutoPassTicks;
    private final long policyConfirmTimeoutTicks;
    private final int policyApprovalRateRemoved;
    private final int policyApprovalRateRequired;
    private final int policyRequiredVotes;
    private final int policyMaxCharacters;
    private final int judgesRequiredToConfirm;
    
    public PolicyConfig(final ConfigurationSection section) {
        policyApprovalRateRemoved = section.getInt("policy-approval-rate-removed");
        policyApprovalRateRequired = section.getInt("policy-approval-rate-required");
        policyRequiredVotes = section.getInt("policy-required-votes");
        policyMaxCharacters = section.getInt("character-max");
        judgesRequiredToConfirm = section.getInt("judges-required-to-confirm");
        policyCheckInterval = VoxTimeUnit.getTicks(section.getString("policy-check-interval"));
        policyAutoPassTicks = VoxTimeUnit.getTicks(section.getString("policy-autopass-time"));
        policyConfirmTimeoutTicks = VoxTimeUnit.getTicks(section.getString("policy-confirm-timeout"));
        
        unpassedPolicyIconBaseItem = section.getString("unpassed-policy-icon-base-item");
        passedPolicyIconBaseItem = section.getString("passed-policy-icon-base-item");
        final ConfigurationSection langSection = section.getConfigurationSection("lang");
        policyDraftAlreadyPendingMessage = ConfigUtil.getColorizedString(langSection, "policy-draft-already-pending");
        policyNewMessage = ConfigUtil.getColorizedString(langSection, "policy-new");
        policyFinishCharactersMessage = ConfigUtil.getColorizedString(langSection, "policy-finish-characters");
        policyFinishMessage = ConfigUtil.getColorizedString(langSection, "policy-finish");
        policyOfficialApproveVotesMessage = ConfigUtil.getColorizedString(langSection, "policy-official-votes");
        policyOfficialApproveAutoMessage = ConfigUtil.getColorizedString(langSection, "policy-official-auto");
        policyStartVotingMessage = ConfigUtil.getColorizedString(langSection, "policy-start-voting");
        stateDescriptions = new HashMap<>();
        final ConfigurationSection voteStatusSection = langSection.getConfigurationSection("vote-status");
        voteStatusUnconfirmed = ConfigUtil.getColorizedString(voteStatusSection, "unconfirmed");
        voteStatusConfirmed = ConfigUtil.getColorizedString(voteStatusSection, "confirmed");
        voteStatusApproved = ConfigUtil.getColorizedString(voteStatusSection, "approved");
        voteStatusDisapproved = ConfigUtil.getColorizedString(voteStatusSection, "disapproved");
        voteStatusAbstained = ConfigUtil.getColorizedString(voteStatusSection, "abstained");
        
        final ConfigurationSection stateDescriptionsSection = langSection.getConfigurationSection("state-descriptions");
        stateDescriptionsSection.getKeys(false).forEach(stateName -> {
            final State state = State.valueOf(stateName.toUpperCase());
            stateDescriptions.put(state, ChatColor.translateAlternateColorCodes('&', stateDescriptionsSection.getString(stateName)));
        });
    }
    
    public int getPolicyMaxCharacters() {
        return policyMaxCharacters;
    }
    
    public int getJudgesRequiredToConfirm() {
        return judgesRequiredToConfirm;
    }
    
    public long getPolicyCheckInterval() {
        return policyCheckInterval;
    }
    
    public long getPolicyAutoPassTicks() {
        return policyAutoPassTicks;
    }
    
    public long getPolicyConfirmTimeoutTicks() {
        return policyConfirmTimeoutTicks;
    }
    
    public String getPassedPolicyIconBaseItem() {
        return passedPolicyIconBaseItem;
    }
    
    public String getUnpassedPolicyIconBaseItem() {
        return unpassedPolicyIconBaseItem;
    }
    
    public String getVoteStatusUnconfirmed() {
        return voteStatusUnconfirmed;
    }
    
    public String getVoteStatusConfirmed() {
        return voteStatusConfirmed;
    }
    
    public String getVoteStatusApproved() {
        return voteStatusApproved;
    }
    
    public String getVoteStatusDisapproved() {
        return voteStatusDisapproved;
    }
    
    public String getVoteStatusAbstained() {
        return voteStatusAbstained;
    }
    
    public String getPolicyDraftAlreadyPendingMessage() {
        return policyDraftAlreadyPendingMessage;
    }
    
    public String getPolicyNewMessage() {
        return policyNewMessage;
    }
    
    public String getPolicyFinishMessage() {
        return policyFinishMessage;
    }
    
    public String getPolicyFinishCharactersMessage() {
        return policyFinishCharactersMessage;
    }
    
    public String getPolicyOfficialApproveVotesMessage() {
        return policyOfficialApproveVotesMessage;
    }
    
    public String getPolicyOfficialApproveAutoMessage() {
        return policyOfficialApproveAutoMessage;
    }
    
    public String getPolicyStartVotingMessage() {
        return policyStartVotingMessage;
    }
    
    public int getPolicyApprovalRateRemoved() {
        return policyApprovalRateRemoved;
    }
    
    public int getPolicyApprovalRateRequired() {
        return policyApprovalRateRequired;
    }
    
    public int getPolicyRequiredVotes() {
        return policyRequiredVotes;
    }
    
    public Map<State, String> getStateDescriptions() {
        return stateDescriptions;
    }
}
