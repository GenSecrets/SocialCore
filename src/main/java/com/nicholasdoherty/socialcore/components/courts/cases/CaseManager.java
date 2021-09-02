package com.nicholasdoherty.socialcore.components.courts.cases;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by john on 1/6/15.
 */
@SuppressWarnings({"TypeMayBeWeakened", "unused"})
public class CaseManager{
    private final Map<Integer,Case> cases;

    public CaseManager(final List<Case> cases) {
        this.cases = new HashMap<>();
        for (final Case caze : cases) {
            this.cases.put(caze.getId(),caze);
        }
    }
    private int nextCaseNumber() {
        return cases.size();
    }

    public Case newCase(ItemStack book, final String resp) {
        final Case caze = Courts.getCourts().getSqlSaveManager().createNewCase();
        book = Case.assignCaseNumberToBook(book.clone(),caze.getId());
        caze.setCaseBook(book);
        caze.setCaseStatus(CaseStatus.UNPROCESSED,resp);
        cases.put(caze.getId(),caze);
        caze.updateSave();
        return caze;
    }
    public Case getCase(final int id) {
        return cases.get(id);
    }
    public List<Case> casesByStatus(final CaseStatus caseStatus) {
        final List<Case> unprocessed = new ArrayList<>();
        for (final Case caze : cases.values()) {
            if (caze.getCaseStatus() == caseStatus) {
                unprocessed.add(caze);
            }
        }
        return unprocessed;
    }
    public List<Case> involvedCases(final UUID uuid, final boolean includeJudge) {
        final List<Case> cases = new ArrayList<>();
        for (final Case caze : futureScheduledCases()) {
            final CourtDate courtDate = caze.getCourtDate();
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
        final List<Case> futureCases = new ArrayList<>();
        for (final Case caze : cases.values()) {
            if (caze.getCourtDate() != null && caze.getCourtDate().getTime() > new Date().getTime()-1000*60*60) {
                futureCases.add(caze);
            }
        }
        return futureCases;
    }
    public List<Case> allScheduledCases() {
        final List<Case> scheduledCases = new ArrayList<>();
        for (final Case caze : cases.values()) {
            if (caze.getCourtDate() != null) {
                scheduledCases.add(caze);
            }
        }
        return scheduledCases;
    }
    public Case caseByBook(final ItemStack itemInHand) {
        if (itemInHand != null && itemInHand.getType() == Material.WRITABLE_BOOK && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()) {
            final String displayName = itemInHand.getItemMeta().getDisplayName();
            if (!displayName.contains(ChatColor.WHITE + "Court Case ")) {
                return null;
            }
            final int caseNumber = Integer.parseInt(displayName.replace(ChatColor.WHITE + "Court Case ","").trim());
            if (caseNumber >= nextCaseNumber()) {
                return null;
            }
            return cases.get(caseNumber);
        }
        return null;
    }
    public void onJudgeDemoted(final Judge judge) {
        for (final Case caze : cases.values()) {
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
