package com.nicholasdoherty.socialcore.courts;

import com.nicholasdoherty.socialcore.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.courts.cases.category.CategoryConfig;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtRoom;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import com.nicholasdoherty.socialcore.utils.ItemUtil;
import com.nicholasdoherty.socialcore.utils.VLocation;
import com.nicholasdoherty.socialcore.utils.VoxEffects;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by john on 1/3/15.
 */
public class CourtsConfig {
    private int maxJudges,secretariesPerJudge,judgeInactiveDaysAllowed,judgeApprovalRateRequired,
         judgeApprovalRateDemoted,judgeRequiredVotes,caseFilingCost,silenceLength;
    private long maxJudgeOfflineTicks,autoSaveInterval,finePaymentInterval,citizenStallDocumentCooldown;
    private List<ItemStack> processReward,courtVoteReward,judgementReward;
    private VoxEffects silenceCourtEffects;
    private Map<String, CourtRoom> courtRoomMap;
    private double nominateSelfCost,finePaymentPercentage;
    private Map<CaseCategory, CategoryConfig> categoryConfigMap;
    private long timeBetweenVoteMessages;
    private Set<String> judgePermissions,secretaryPermissions;
    private VoxEffects startSessionEffects;



    public CourtsConfig(int maxJudges, int secretariesPerJudge, int judgeInactiveDaysAllowed, int judgeApprovalRateRequired,
                        int judgeApprovalRateDemoted, int judgeRequiredVotes, int caseFilingCost, List<ItemStack> processReward,
                        List<ItemStack> courtVoteReward, List<ItemStack> judgementReward,VoxEffects silenceCourtEffects,
                        int silenceLength, Map<String, CourtRoom> courtRoomMap, long maxJudgeOfflineTicks, long autoSaveInterval,
                        double nominateSelfCost, Map<CaseCategory, CategoryConfig> categoryConfigMap, long finePaymentInterval,
                        double finePaymentPercentage, long citizenStallDocumentCooldown, long timeBetweenVoteMessages,
                        Set<String> judgePermissions, Set<String> secretaryPermissions, VoxEffects startSessionEffects) {
        this.maxJudges = maxJudges;
        this.secretariesPerJudge = secretariesPerJudge;
        this.judgeInactiveDaysAllowed = judgeInactiveDaysAllowed;
        this.judgeApprovalRateRequired = judgeApprovalRateRequired;
        this.judgeApprovalRateDemoted = judgeApprovalRateDemoted;
        this.judgeRequiredVotes = judgeRequiredVotes;
        this.caseFilingCost = caseFilingCost;
        this.processReward = processReward;
        this.courtVoteReward = courtVoteReward;
        this.judgementReward = judgementReward;
        this.silenceCourtEffects = silenceCourtEffects;
        this.silenceLength = silenceLength;
        this.courtRoomMap = courtRoomMap;
        this.maxJudgeOfflineTicks = maxJudgeOfflineTicks;
        this.autoSaveInterval = autoSaveInterval;
        this.nominateSelfCost = nominateSelfCost;
        this.categoryConfigMap = categoryConfigMap;
        this.finePaymentInterval = finePaymentInterval;
        this.finePaymentPercentage = finePaymentPercentage;
        this.citizenStallDocumentCooldown = citizenStallDocumentCooldown;
        this.timeBetweenVoteMessages = timeBetweenVoteMessages;
        this.judgePermissions = judgePermissions;
        this.secretaryPermissions = secretaryPermissions;
        this.startSessionEffects = startSessionEffects;
    }
    public CourtRoom getCourtRoom(String name) {
        if (courtRoomMap.containsKey(name))
            return courtRoomMap.get(name);
        return null;
    }
    public long getMaxJudgeOfflineTicks() {
        return maxJudgeOfflineTicks;
    }

    public int getMaxJudges() {
        return maxJudges;
    }

    public int getSecretariesPerJudge() {
        return secretariesPerJudge;
    }

    public int getJudgeInactiveDaysAllowed() {
        return judgeInactiveDaysAllowed;
    }

    public int getJudgeApprovalRateRequired() {
        return judgeApprovalRateRequired;
    }

    public int getJudgeApprovalRateDemoted() {
        return judgeApprovalRateDemoted;
    }

    public int getJudgeRequiredVotes() {
        return judgeRequiredVotes;
    }

    public List<ItemStack> getProcessReward() {
        return processReward;
    }

    public List<ItemStack> getCourtVoteReward() {
        return courtVoteReward;
    }

    public long getCitizenStallDocumentCooldown() {
        return citizenStallDocumentCooldown;
    }

    public List<ItemStack> getJudgementReward() {
        return judgementReward;
    }

    public long getFinePaymentInterval() {
        return finePaymentInterval;
    }

    public double getFinePaymentPercentage() {
        return finePaymentPercentage;
    }

    public int getCaseFilingCost() {
        return caseFilingCost;
    }

    public VoxEffects getSilenceCourtEffects() {
        return silenceCourtEffects;
    }

    public int getSilenceLength() {
        return silenceLength;
    }

    public CourtRoom getDefaultCourtRoom() {
        return courtRoomMap.values().iterator().next();
    }
    public CategoryConfig getCategoryConfgi(CaseCategory caseCategory) {
        return categoryConfigMap.get(caseCategory);
    }
    public long getAutoSaveInterval() {
        return autoSaveInterval;
    }

    public long getTimeBetweenVoteMessages() {
        return timeBetweenVoteMessages;
    }

    public VoxEffects getStartSessionEffects() {
        return startSessionEffects;
    }

    public double getNominateSelfCost() {
        return nominateSelfCost;
    }

    public Set<String> getJudgePermissions() {
        return judgePermissions;
    }

    public Set<String> getSecretaryPermissions() {
        return secretaryPermissions;
    }

    public static CourtsConfig fromConfig(ConfigurationSection section) {
        int maxJudges = section.getInt("max-judges");
        int secretariesPerJudge = section.getInt("secretaries-per-judge");
        int judgeInactiveDaysAllowed = section.getInt("judge-inactive-days-allowed");
        int judgeApprovalRateRequired = section.getInt("judge-approval-rate-required");
        int judgeApprovalRateDemoted = section.getInt("judge-approval-rate-demoted");
        int judgeRequiredVotes = section.getInt("judge-required-votes");
        ConfigurationSection rewardsSection = section.getConfigurationSection("rewards");
        List<ItemStack> processReward = ItemUtil.itemsFromSection(rewardsSection.getStringList("process"));
        List<ItemStack> courtVoteReward = ItemUtil.itemsFromSection(rewardsSection.getStringList("court-vote"));
        List<ItemStack> judgementReward = ItemUtil.itemsFromSection(rewardsSection.getStringList("judgement"));
        int caseFilingCost = section.getInt("case-filing-cost");
        VoxEffects voxEffects = VoxEffects.fromConfig(section.getConfigurationSection("silence-court-effects"));
        int silenceLength = section.getInt("silence-ticks",100);
        Map<String, CourtRoom> courtRoomMap = new HashMap<>();
        if (section.contains("court-rooms")) {
            ConfigurationSection courtRoomsSectino = section.getConfigurationSection("court-rooms");
            for (String key : courtRoomsSectino.getKeys(false)) {
                String name = key;
                ConfigurationSection courtRoomSection = courtRoomsSectino.getConfigurationSection(key);
                String regionName = courtRoomSection.getString("region");
                VLocation tpLoc = VLocation.fromString(courtRoomSection.getString("tp-location"));
                ProtectedRegion region = WGBukkit.getRegionManager(tpLoc.getLocation().getWorld()).getRegion(regionName);
                VLocation center = VLocation.fromString(courtRoomSection.getString("effects-location"));
                VLocation judgeChairLoc = VLocation.fromString(courtRoomSection.getString("judge-chair-location"));
                CourtRoom courtRoom = new CourtRoom(name,region,tpLoc,center,judgeChairLoc);
                courtRoomMap.put(name, courtRoom);
            }
        }
        long maxJudgeOfflineTicks = VoxTimeUnit.getTicks(section.getString("max-judge-offline-time"));
        long autoSaveInterval = 7000;
        if (section.contains("auto-save-interval")) {
            autoSaveInterval = VoxTimeUnit.getTicks(section.getString("auto-save-interval"));
        }
        double nominateSelfCost = section.getDouble("nominate-cost",1000);
        Map<CaseCategory, CategoryConfig> categoryConfigMap = new HashMap<>();
        if (section.contains("category-config")) {
            ConfigurationSection categorySeciton = section.getConfigurationSection("category-config");
            for (String categoryName : categorySeciton.getKeys(false)) {
                CaseCategory caseCategory = CaseCategory.fromString(categoryName.toUpperCase());
                if (caseCategory != null) {
                    categoryConfigMap.put(caseCategory,caseCategory.categoryConfig(categorySeciton.getConfigurationSection(categoryName)));
                }else {
                    System.out.println("Invalid case category in config: " + categoryName);
                }
            }
        }
        double finePaymentPercentage = section.getDouble("fine-payment-percentage",5);
        long finePaymentInterval = VoxTimeUnit.getTicks(section.getString("fine-payment-interval"));
        long citizenStallDocumentCooldown = VoxTimeUnit.getTicks(section.getString("citizen-stall-document-cooldown"));
        long timeBetweenVoteMessages = 0;
        if (section.contains("time-between-vote-messages")) {
            timeBetweenVoteMessages = VoxTimeUnit.getTicks(section.getString("time-between-vote-messages"));
        }
        Set<String> judgePermissions = new HashSet<>();
        if (section.contains("judge-permissions")) {
            judgePermissions.addAll(section.getStringList("judge-permissions"));
        }
        Set<String> secretaryPermissions = new HashSet<>();
        if (section.contains("secretary-permissions")) {
            secretaryPermissions.addAll(section.getStringList("secretary-permissions"));
        }
        VoxEffects startSessionEffects = null;
        if (section.contains("court-session-start-effects")) {
            ConfigurationSection startSessionEffectsSection = section.getConfigurationSection("court-session-start-effects");
            startSessionEffects = VoxEffects.fromConfig(startSessionEffectsSection);
        }
        return new CourtsConfig(maxJudges,secretariesPerJudge,judgeInactiveDaysAllowed,judgeApprovalRateRequired,judgeApprovalRateDemoted,judgeRequiredVotes,caseFilingCost,processReward,courtVoteReward,
                judgementReward,voxEffects,silenceLength, courtRoomMap,maxJudgeOfflineTicks,autoSaveInterval,nominateSelfCost,categoryConfigMap,finePaymentInterval,finePaymentPercentage,citizenStallDocumentCooldown,
                timeBetweenVoteMessages,judgePermissions,secretaryPermissions,startSessionEffects);
    }
}
