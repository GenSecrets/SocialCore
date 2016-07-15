package com.nicholasdoherty.socialcore.courts.cases;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by john on 1/6/15.
 */
public class CaseManager{
    private Map<Integer,Case> cases;

    public CaseManager(List<Case> cases) {
        this.cases = new HashMap<>();
        for (Case caze : cases) {
            this.cases.put(caze.getId(),caze);
        }
    }
    private int nextCaseNumber() {
        return cases.size();
    }

    public Case newCase(ItemStack book, String resp) {
        Case caze = Courts.getCourts().getSqlSaveManager().createNewCase();
        book = Case.assignCaseNumberToBook(book.clone(),caze.getId());
        caze.setCaseBook(book);
        caze.setCaseStatus(CaseStatus.UNPROCESSED,resp);
        cases.put(caze.getId(),caze);
        caze.updateSave();
        return caze;
    }
    public Case getCase(int id) {
        return cases.get(id);
    }
    public List<Case> casesByStatus(CaseStatus caseStatus) {
        List<Case> unprocessed = new ArrayList<>();
        for (Case caze : cases.values()) {
            if (caze.getCaseStatus() == caseStatus) {
                unprocessed.add(caze);
            }
        }
        return unprocessed;
    }
    public List<Case> involvedCases(UUID uuid, boolean includeJudge) {
        List<Case> cases = new ArrayList<>();
        for (Case caze : futureScheduledCases()) {
            CourtDate courtDate = caze.getCourtDate();
            if (includeJudge && courtDate.getJudge().isSameUUID(uuid)) {
                cases.add(caze);
            }else if (caze.getPlantiff() != null && caze.getPlantiff().isSameUUID(uuid)) {
                cases.add(caze);
            }else if (caze.getDefendent() != null && caze.getDefendent().isSameUUID(uuid)) {
                cases.add(caze);
            }
        }
        return cases;
    }
    public List<Case> futureScheduledCases() {
        List<Case> futureCases = new ArrayList<>();
        for (Case caze : cases.values()) {
            if (caze.getCourtDate() != null && caze.getCourtDate().getTime() > new Date().getTime()-1000*60*60) {
                futureCases.add(caze);
            }
        }
        return futureCases;
    }
    public List<Case> allScheduledCases() {
        List<Case> scheduledCases = new ArrayList<>();
        for (Case caze : cases.values()) {
            if (caze.getCourtDate() != null) {
                scheduledCases.add(caze);
            }
        }
        return scheduledCases;
    }
    public Case caseByBook(ItemStack itemInHand) {
        if (itemInHand != null && itemInHand.getType() == Material.BOOK_AND_QUILL && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()) {
            String displayName = itemInHand.getItemMeta().getDisplayName();
            if (!displayName.contains(ChatColor.WHITE + "Court Case ")) {
                return null;
            }
            int caseNumber = Integer.parseInt(displayName.replace(ChatColor.WHITE + "Court Case ","").trim());
            if (caseNumber >= nextCaseNumber()) {
                return null;
            }
            return cases.get(caseNumber);
        }
        return null;
    }
    public void onJudgeDemoted(Judge judge) {
        for (Case caze : cases.values()) {
            if (caze.getCaseStatus() == CaseStatus.COURT_DATE_SET && caze.getCourtDate() != null && caze.getCourtDate().getJudge().equals(judge)) {
                caze.setCourtDate(null);
                String responsible = "Server";
                if (caze.getCaseHistory().getProcessingEntry() != null) {
                    responsible = caze.getCaseHistory().getProcessingEntry().getResponsible();
                }
                caze.setCaseStatus(CaseStatus.PROCESSED, responsible);
                caze.updateSave();
            }
        }
    }
    public int amount() {
        return cases.size();
    }

    public Collection<Case> getCases() {
        return cases.values();
    }

}
