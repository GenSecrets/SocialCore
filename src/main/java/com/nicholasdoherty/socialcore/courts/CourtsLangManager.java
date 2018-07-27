package com.nicholasdoherty.socialcore.courts;

import com.nicholasdoherty.socialcore.courts.cases.CaseCategory;
import com.voxmc.voxlib.util.VoxStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 2/15/15.
 */
public class CourtsLangManager {
    private Map<CaseCategory, String> caseCategoryDescriptions = new HashMap<>();
    private String confirmNominateSelfMessage;
    private String citizenSubmitted,citizenCooldown,citizenCaseAlreadyFiled,citizenCaseBookEmpty,citizenConfirm,citizenGaveCourtDocuments;
    private String secretaryRequestingMessage,secretaryRequestedConfirmMessage, secretaryRequestedAcceptMessage, secretaryRequestedDenyMessage,
        secretaryRequesterAcceptedMessage,secretaryRequesterDeniedMessage,secretaryRemovedMessage,notSecretaryMessage;
    private String judgePrefix,courtSessionPlaintiffPrefix,courtSessionDefendantPrefix;
    private String electionNoSlots;
    private String courtNotInSession;
    private String policyIcon;
    public CourtsLangManager(ConfigurationSection langSection) {
        if (langSection.contains("case-category-descriptions")) {
            ConfigurationSection caseCategoryDescripitonsSection = langSection.getConfigurationSection("case-category-descriptions");
            for (String key : caseCategoryDescripitonsSection.getKeys(false)) {
                CaseCategory caseCategory = CaseCategory.fromString(key.toUpperCase());
                if (caseCategory != null) {
                    caseCategoryDescriptions.put(caseCategory, VoxStringUtils.color(caseCategoryDescripitonsSection.getString(key)));
                }
            }
        }
        confirmNominateSelfMessage = VoxStringUtils.color(langSection.getString("confirm-nominate-self"));
        ConfigurationSection citizenStallSection = langSection.getConfigurationSection("citizen-stall");
        citizenCaseAlreadyFiled = VoxStringUtils.color(citizenStallSection.getString("case-already-filed"));
        citizenCaseBookEmpty = VoxStringUtils.color(citizenStallSection.getString("case-book-empty"));
        citizenConfirm = VoxStringUtils.color(citizenStallSection.getString("confirm"));
        citizenGaveCourtDocuments = VoxStringUtils.color(citizenStallSection.getString("gave-court-documents"));
        citizenCooldown = VoxStringUtils.color(citizenStallSection.getString("on-cooldown"));
        citizenSubmitted = VoxStringUtils.color(citizenStallSection.getString("submitted"));
        judgePrefix = color(langSection,"judge-prefix");
        electionNoSlots = color(langSection,"election-no-slots");


        courtSessionPlaintiffPrefix = color(langSection, "court-session-plaintiff-prefix");
        courtSessionDefendantPrefix = color(langSection, "court-session-defendant-prefix");
        ConfigurationSection secretarySection = langSection.getConfigurationSection("secretary");
        secretaryRequestingMessage = color(secretarySection,"requesting-message");
        secretaryRequestedAcceptMessage = color(secretarySection,"requested-accept-message");
        secretaryRequestedDenyMessage = color(secretarySection,"requested-deny-message");
        secretaryRequestedConfirmMessage = color(secretarySection,"requested-confirm-message");
        secretaryRequesterAcceptedMessage = color(secretarySection,"requester-accepted-message");
        secretaryRequesterDeniedMessage = color(secretarySection,"requester-denied-message");
        secretaryRemovedMessage = color(secretarySection,"removed-message");
        notSecretaryMessage = color(secretarySection,"not-secretary-message");
        courtNotInSession = color(langSection,"command-not-in-session");

    }
    public String caseCategoryDescription(CaseCategory caseCategory) {
        if (caseCategoryDescriptions.containsKey(caseCategory)) {
            return caseCategoryDescriptions.get(caseCategory);
        }
        return ChatColor.WHITE + "No description is defined for this category";
    }
    private String color(ConfigurationSection configurationSection, String key) {
        return VoxStringUtils.color(configurationSection.getString(key));
    }

    public String getCourtNotInSession() {
        return courtNotInSession;
    }

    public String getElectionNoSlots() {
        return electionNoSlots;
    }

    public String getJudgePrefix() {
        return judgePrefix;
    }

    public Map<CaseCategory, String> getCaseCategoryDescriptions() {
        return caseCategoryDescriptions;
    }

    public String getCitizenCaseAlreadyFiled() {
        return citizenCaseAlreadyFiled;
    }

    public String getCitizenCaseBookEmpty() {
        return citizenCaseBookEmpty;
    }

    public String getCitizenConfirm() {
        return citizenConfirm;
    }

    public String getCitizenCooldown() {
        return citizenCooldown;
    }

    public String getCitizenGaveCourtDocuments() {
        return citizenGaveCourtDocuments;
    }

    public String getCourtSessionPlaintiffPrefix() {
        return courtSessionPlaintiffPrefix;
    }

    public String getCourtSessionDefendantPrefix() {
        return courtSessionDefendantPrefix;
    }

    public String getCitizenSubmitted() {
        return citizenSubmitted;
    }

    public String getConfirmNominateSelfMessage() {
        return confirmNominateSelfMessage;
    }

    public String getSecretaryRequestingMessage() {
        return secretaryRequestingMessage;
    }

    public String getSecretaryRequestedConfirmMessage() {
        return secretaryRequestedConfirmMessage;
    }

    public String getSecretaryRequestedAcceptMessage() {
        return secretaryRequestedAcceptMessage;
    }

    public String getSecretaryRequestedDenyMessage() {
        return secretaryRequestedDenyMessage;
    }

    public String getSecretaryRequesterAcceptedMessage() {
        return secretaryRequesterAcceptedMessage;
    }

    public String getSecretaryRequesterDeniedMessage() {
        return secretaryRequesterDeniedMessage;
    }

    public String getSecretaryRemovedMessage() {
        return secretaryRemovedMessage;
    }

    public String getNotSecretaryMessage() {
        return notSecretaryMessage;
    }
}
