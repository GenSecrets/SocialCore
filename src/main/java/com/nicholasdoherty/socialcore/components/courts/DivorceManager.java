package com.nicholasdoherty.socialcore.components.courts;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.components.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.components.marriages.types.Divorce;
import com.voxmc.voxlib.util.UUIDUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;
import java.util.UUID;

/**
 * Created by john on 3/2/15.
 */
@SuppressWarnings("unused")
public class DivorceManager {
    private final SocialCore socialCore;
    private final Courts courts;
    
    public DivorceManager(final SocialCore socialCore, final Courts courts) {
        this.socialCore = socialCore;
        this.courts = courts;
    }
    
    public void createCasesForExisting() {
        final List<String> allDivorcesNames = socialCore.save.getAllDivorces();
        for(final String divorceName : allDivorcesNames) {
            final Divorce divorce = socialCore.save.getDivorce(divorceName);
            if(divorce != null) {
                if(!hasCase(divorce)) {
                    createCaseForDivorce(divorce);
                }
            }
        }
    }
    
    private ItemStack defaultDivorceBook(final String exHusband, final String exWife, final ItemStack book) {
        if(book == null || book.getType() != Material.WRITABLE_BOOK) {
            return null;
        }
        final BookMeta bookMeta = (BookMeta) book.getItemMeta();
        final String page = exHusband + " and " + exWife + " have requested divorce eachother.";
        bookMeta.setPages(page);
        book.setItemMeta(bookMeta);
        return book;
    }
    
    public Divorce getDivorce(final Case caze) {
        if(caze.getCaseCategory() != null || caze.getPlantiff() == null || caze.getDefendent() == null) {
            return null;
        }
        final SocialPlayer s1 = SocialCore.plugin.save.getSocialPlayer(caze.getPlantiff().getUuid().toString());
        final SocialPlayer s2 = SocialCore.plugin.save.getSocialPlayer(caze.getDefendent().getUuid().toString());
        if(s1 == null || s2 == null) {
            return null;
        }
        final Divorce divorce = SocialCore.plugin.save.getDivorce(s1, s2);
        if(divorce == null) {
            SocialCore.plugin.save.getDivorce(s2, s1);
        }
        return divorce;
    }
    
    public void createCaseForDivorce(final Divorce divorce) {
        final String plaintiffName = divorce.getExSpouse2().getPlayerName();
        final UUID plaintiffUUID = UUIDUtil.getUUID(plaintiffName);
        if(plaintiffUUID == null) {
            return;
        }
        final String defendantName = divorce.getExSpouse1().getPlayerName();
        final UUID defendantUUID = UUIDUtil.getUUID(defendantName);
        if(defendantUUID == null) {
            return;
        }
        final Citizen plaintiff = Courts.getCourts().getCitizenManager().toCitizen(plaintiffName, plaintiffUUID);
        final Citizen defendant = Courts.getCourts().getCitizenManager().toCitizen(defendantName, defendantUUID);
        final ItemStack updatedBook = defaultDivorceBook(divorce.getExSpouse1().getPlayerName(), divorce.getExSpouse2().getPlayerName(), Case.baseItemStack());
        final Case caze = courts.getCaseManager().newCase(updatedBook, plaintiffName);
        caze.setPlantiff(plaintiff);
        caze.setDefendent(defendant);
        caze.setCaseCategory(CaseCategory.DIVORCE);
        caze.setCaseStatus(CaseStatus.PROCESSED, "Divorce Command");
        caze.updateSave();
    }
    
    public Case getCase(final Divorce divorce) {
        final String spouse1Name = divorce.getExSpouse1().getPlayerName();
        final String spouse2Name = divorce.getExSpouse2().getPlayerName();
        if(spouse1Name == null || spouse2Name == null) {
            return null;
        }
        for(final Case caze : Courts.getCourts().getCaseManager().getCases()) {
            if(caze.getCaseCategory() == CaseCategory.DIVORCE && caze.getDefendent() != null && caze.getPlantiff() != null) {
                if(caze.getPlantiff().getName().equalsIgnoreCase(spouse2Name) && caze.getDefendent().getName().equalsIgnoreCase(spouse1Name)
                        || caze.getPlantiff().getName().equalsIgnoreCase(spouse1Name) && caze.getDefendent().getName().equalsIgnoreCase(spouse2Name)) {
                    return caze;
                }
            }
        }
        return null;
    }
    
    public boolean hasCase(final Divorce divorce) {
        return getCase(divorce) != null;
    }
}
