package com.nicholasdoherty.socialcore.courts.policies;


import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.policies.commands.PoliciesCommand;
import com.nicholasdoherty.socialcore.courts.policies.commands.PolicyCommand;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by john on 9/12/16.
 */
public class PolicyManager {
    private Courts courts;
    private Map<Integer, Policy> cache;
    private PolicyConfig policyConfig;

    public PolicyManager(Courts courts) {
        this.courts = courts;
        policyConfig = new PolicyConfig(courts.getPlugin().getConfig().getConfigurationSection("courts.policy"));
        cache = Collections.synchronizedMap(new HashMap<>());
        new PolicyCommand(courts,this);
        new PoliciesCommand(courts,this);
        new BukkitRunnable(){
            @Override
            public void run() {
                allPolicies().forEach(policy -> checkStateChange(policy));
            }
        }.runTaskTimer(courts.getPlugin(), policyConfig.getPolicyCheckInterval(),policyConfig.getPolicyCheckInterval());
    }
    public List<Policy> allPolicies() {
        List<Long> ids =  courts
                .getSqlSaveManager()
                .allPoliciesIds();
        List<Policy> policies = new ArrayList<Policy>();
        for (int i = 0; i < ids.size(); i++) {
            int id = ids.get(i).intValue();
            Optional<Policy> policy = getPolicy(id);
            policy.ifPresent(policies::add);
        }
        return policies;
    }

    public PolicyConfig getPolicyConfig() {
        return policyConfig;
    }

    public Optional<Policy> getPolicy(int id) {
        return Optional.ofNullable(
                cache.computeIfAbsent(id,key -> courts.getSqlSaveManager().getPolicy(key).orElse(null)));
    }

    public Policy checkStateChange(Policy policy) {
        if (policy.getState() == Policy.State.UNCONFIRMED && policy.getConfirmApprovals().size()
                >= policyConfig.getJudgesRequiredToConfirm()) {
        	getServer().dispatchCommand((getServer.getConsoleSender(), "/mail sendall " + policyConfig.getPolicyStartVotingMessage());
            return courts.getSqlSaveManager().updatePolicyState(policy, Policy.State.MAIN_VOTING).orElse(policy);
        }else if (policy.getState() == Policy.State.MAIN_VOTING && policy.totalVotes() >= policyConfig.getPolicyRequiredVotes()){
            float percent = (policy.getApprovals().size()*100f) / policy.totalVotes();
            if (percent <= policyConfig.getPolicyApprovalRateRemoved()) {
                return courts.getSqlSaveManager().updatePolicyState(policy, Policy.State.FAILED).orElse(policy);
            }else if (percent >= policyConfig.getPolicyApprovalRateRequired()) {
                Bukkit.broadcastMessage(policyConfig.getPolicyOfficialApproveVotesMessage().replace("policy-number",policy.getId()+""));  
                return courts.getSqlSaveManager().updatePolicyState(policy, Policy.State.IN_EFFECT).orElse(policy);
            }
        }else if (policy.getState() == Policy.State.MAIN_VOTING && new Date().getTime() > policy.getCreationTime().getTime()
                 + VoxTimeUnit.TICK.toMillis(policyConfig.getPolicyAutoPassTicks())) {
        	//JACEK ADAMS (Quilipayun): we've decided that policies should auto fail instead of pass. The AutoPass message is now an auto-fail one, but I'm too scared of
        	//other dependencies to change it's variable name.
            Bukkit.broadcastMessage(policyConfig.getPolicyOfficialApproveAutoMessage().replace("policy-number",policy.getId()+""));
            return courts.getSqlSaveManager().updatePolicyState(policy, Policy.State.FAILED).orElse(policy);
        }else if (policy.getState() == Policy.State.UNCONFIRMED && new Date().getTime() > policy.getCreationTime().getTime()
                + com.voxmc.voxlib.VoxTimeUnit.TICK.toMillis(policyConfig.getPolicyConfirmTimeoutTicks())) {
            return courts.getSqlSaveManager().updatePolicyState(policy, Policy.State.FAILED).orElse(policy);
        }
        return policy;
    }

    public Policy updateIfStale(Policy policy) {
        if (policy.isStale()) {
            cache.remove(policy.getId());
            return getPolicy(policy.getId())
                    .orElseThrow(() -> new RuntimeException("Failed to update policy: " + policy.getId()));
        }else {
            return policy;
        }
    }
}
