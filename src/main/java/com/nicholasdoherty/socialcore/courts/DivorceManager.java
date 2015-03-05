package com.nicholasdoherty.socialcore.courts;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.marriages.Divorce;
import com.nicholasdoherty.socialcore.utils.UUIDUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;
import java.util.UUID;

/**
 * Created by john on 3/2/15.
 */
public class DivorceManager {
    private SocialCore socialCore;
    private Courts courts;

    public DivorceManager(SocialCore socialCore, Courts courts) {
        this.socialCore = socialCore;
        this.courts = courts;
    }

    public void createCasesForExisting() {
        List<String> allDivorcesNames = socialCore.save.getAllDivorces();
        for (String divorceName : allDivorcesNames) {
            Divorce divorce = socialCore.save.getDivorce(divorceName);
            if (divorce != null) {
                if (!hasCase(divorce)) {
                    createCaseForDivorce(divorce);
                }
            }
        }
    }
    private ItemStack defaultDivorceBook(String exHusband, String exWife, ItemStack book) {
        if (book == null || book.getType() != Material.BOOK_AND_QUILL)
            return null;
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        String page = exHusband + " and " + exWife + " have requested divorce eachother.";
        bookMeta.setPages(page);
        book.setItemMeta(bookMeta);
        return book;
    }
    public Divorce getDivorce(Case caze) {
        if (caze.getCaseCategory() != null || caze.getPlantiff() == null || caze.getDefendent() == null)
            return null;
        SocialPlayer s1 = SocialCore.plugin.save.getSocialPlayer(caze.getPlantiff().getName());
        SocialPlayer s2 = SocialCore.plugin.save.getSocialPlayer(caze.getDefendent().getName());
        if (s1 == null || s2 == null)
            return null;
        Divorce divorce = SocialCore.plugin.save.getDivorce(s1,s2);
        if (divorce == null) {
            SocialCore.plugin.save.getDivorce(s2,s1);
        }
        return divorce;
    }
    public void createCaseForDivorce(Divorce divorce) {
        String plaintiffName = divorce.getExwife().getPlayerName();
        UUID plaintiffUUID = UUIDUtil.getUUID(plaintiffName);
        if (plaintiffUUID == null)
            return;
        String defendantName = divorce.getExhusband().getPlayerName();
        UUID defendantUUID = UUIDUtil.getUUID(defendantName);
        if (defendantUUID == null)
            return;
        Citizen plaintiff = new Citizen(plaintiffName,plaintiffUUID);
        Citizen defendant = new Citizen(defendantName,defendantUUID);
        ItemStack updatedBook = defaultDivorceBook(divorce.getExhusband().getPlayerName(),divorce.getExwife().getPlayerName(),Case.baseItemStack());
        Case caze = courts.getCaseManager().newCase(updatedBook,plaintiffName);
        caze.setPlantiff(plaintiff);
        caze.setDefendent(defendant);
        caze.setCaseCategory(CaseCategory.DIVORCE);
        caze.setCaseStatus(CaseStatus.PROCESSED,"Divorce Command");
    }
    public Case getCase(Divorce divorce) {
        String husbandName = divorce.getExhusband().getPlayerName();
        String wifeName = divorce.getExwife().getPlayerName();
        if (husbandName == null || wifeName == null)
            return null;
        for (Case caze : Courts.getCourts().getCaseManager().getCases()) {
            if (caze.getCaseCategory() == CaseCategory.DIVORCE && caze.getDefendent() != null && caze.getPlantiff() != null) {
                if ((caze.getPlantiff().getName().equalsIgnoreCase(wifeName) && caze.getDefendent().getName().equalsIgnoreCase(husbandName))
                        || (caze.getPlantiff().getName().equalsIgnoreCase(husbandName) && caze.getDefendent().getName().equalsIgnoreCase(wifeName))) {
                    return caze;
                }
            }
        }
        return null;
    }
    public boolean hasCase(Divorce divorce) {
        return getCase(divorce) != null;
    }
}
