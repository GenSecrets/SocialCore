package com.nicholasdoherty.socialcore.components.courts.policies;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.policies.Policy.State;
import com.nicholasdoherty.socialcore.components.courts.policies.commands.PoliciesCommand;
import com.nicholasdoherty.socialcore.components.courts.policies.commands.PolicyCommand;
import com.nicholasdoherty.socialcore.utils.time.VoxTimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

/**
 * Created by john on 9/12/16.
 */
public class PolicyManager {
    private final Courts courts;
    private final Map<Integer, Policy> cache;
    private final PolicyConfig policyConfig;
    
    public PolicyManager(final Courts courts) {
        this.courts = courts;
        policyConfig = new PolicyConfig(courts.getPlugin().getCourtsConfig().getConfigurationSection("policy"));
        cache = Collections.synchronizedMap(new HashMap<>());
        new PolicyCommand(courts, this);
        new PoliciesCommand(courts, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                allPolicies().forEach(policy -> checkStateChange(policy));
            }
        }.runTaskTimer(courts.getPlugin(), policyConfig.getPolicyCheckInterval(), policyConfig.getPolicyCheckInterval());
    }
    
    public List<Policy> allPolicies() {
        final List<Long> ids = courts
                .getSqlSaveManager()
                .allPoliciesIds();
        final List<Policy> policies = new ArrayList<>();
        for(final Long id1 : ids) {
            final int id = id1.intValue();
            final Optional<Policy> policy = getPolicy(id);
            policy.ifPresent(policies::add);
        }
        return policies;
    }
    
    public PolicyConfig getPolicyConfig() {
        return policyConfig;
    }
    
    public Optional<Policy> getPolicy(final int id) {
        return Optional.ofNullable(
                cache.computeIfAbsent(id, key -> courts.getSqlSaveManager().getPolicy(key).orElse(null)));
    }
    
    @SuppressWarnings("UnusedReturnValue")
    public Policy checkStateChange(final Policy policy) {
        if(policy.getState() == State.UNCONFIRMED && policy.getConfirmApprovals().size()
                >= policyConfig.getJudgesRequiredToConfirm()) {
            getServer().dispatchCommand(getServer().getConsoleSender(), "/mail sendall " + policyConfig.getPolicyStartVotingMessage());
            return courts.getSqlSaveManager().updatePolicyState(policy, State.MAIN_VOTING).orElse(policy);
        } else if(policy.getState() == State.MAIN_VOTING && policy.totalVotes() >= policyConfig.getPolicyRequiredVotes()) {
            final float percent = policy.getApprovals().size() * 100f / policy.totalVotes();
            if(percent <= policyConfig.getPolicyApprovalRateRemoved()) {
                return courts.getSqlSaveManager().updatePolicyState(policy, State.FAILED).orElse(policy);
            } else if(percent >= policyConfig.getPolicyApprovalRateRequired()) {
                Bukkit.broadcastMessage(policyConfig.getPolicyOfficialApproveVotesMessage().replace("policy-number", policy.getId() + ""));
                return courts.getSqlSaveManager().updatePolicyState(policy, State.IN_EFFECT).orElse(policy);
            }
        } else if(policy.getState() == State.MAIN_VOTING && new Date().getTime() > policy.getCreationTime().getTime()
                + VoxTimeUnit.TICK.toMillis(policyConfig.getPolicyAutoPassTicks())) {
            //JACEK ADAMS (Quilipayun): we've decided that policies should auto fail instead of pass.
            // The AutoPass message is now an auto-fail one, but I'm too scared of
            // other dependencies to change it's variable name.
            Bukkit.broadcastMessage(policyConfig.getPolicyOfficialApproveAutoMessage().replace("policy-number", policy.getId() + ""));
            return courts.getSqlSaveManager().updatePolicyState(policy, State.FAILED).orElse(policy);
        } else if(policy.getState() == State.UNCONFIRMED && new Date().getTime() > policy.getCreationTime().getTime()
                + com.voxmc.voxlib.VoxTimeUnit.TICK.toMillis(policyConfig.getPolicyConfirmTimeoutTicks())) {
            return courts.getSqlSaveManager().updatePolicyState(policy, State.FAILED).orElse(policy);
        }
        return policy;
    }
    
    public Policy updateIfStale(final Policy policy) {
        if(policy.isStale()) {
            cache.remove(policy.getId());
            return getPolicy(policy.getId())
                    .orElseThrow(() -> new RuntimeException("Failed to update policy: " + policy.getId()));
        } else {
            return policy;
        }
    }
}
