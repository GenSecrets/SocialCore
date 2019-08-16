package com.nicholasdoherty.socialcore.courts.cases;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.notifications.Notification;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.voxmc.voxlib.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by john on 1/3/15.
 */
@SuppressWarnings("unused")
public class Case implements ConfigurationSerializable {
    private final int id;
    private CaseStatus caseStatus;
    private Citizen plantiff;
    private Citizen defendent;
    private ItemStack caseBook;
    private boolean locked;
    private CaseCategory caseCategory;
    private CaseHistory caseHistory;
    private CourtDate courtDate;
    private Resolve resolve;
    
    private CaseMeta caseMeta;
    
    public Case(final int id, final CaseStatus caseStatus) {
        this.id = id;
        this.caseStatus = caseStatus;
        caseHistory = new CaseHistory();
    }
    
    public Case(final int id, final CaseStatus caseStatus, final Citizen plantiff, final Citizen defendent, final ItemStack caseBook, final boolean locked, final CaseCategory caseCategory, final CaseMeta caseMeta) {
        this.id = id;
        this.caseStatus = caseStatus;
        this.plantiff = plantiff;
        this.defendent = defendent;
        this.caseBook = caseBook;
        this.locked = locked;
        this.caseCategory = caseCategory;
        this.caseMeta = caseMeta;
    }
    
    public Case(final Map<String, Object> map) {
        id = (int) map.get("id");
        if(map.containsKey("case-status")) {
            caseStatus = CaseStatus.valueOf("" + map.get("case-status"));
        }
        plantiff = (Citizen) map.get("plantiff");
        defendent = (Citizen) map.get("defendant");
        caseBook = (ItemStack) map.get("case-book");
        if(map.containsKey("case-category")) {
            caseCategory = CaseCategory.valueOf((String) map.get("case-category"));
        }
        caseHistory = (CaseHistory) map.get("case-history");
        courtDate = (CourtDate) map.get("court-date");
        if(map.containsKey("resolve")) {
            resolve = (Resolve) map.get("resolve");
        }
        if(map.containsKey("case-meta")) {
            caseMeta = (CaseMeta) map.get("case-meta");
        }
    }
    
    public static boolean isCaseBook(final ItemStack itemStack) {
        if(itemStack == null || itemStack.getType() != Material.WRITABLE_BOOK) {
            return false;
        }
        if(itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }
        final String displayName = itemStack.getItemMeta().getDisplayName();
        return displayName.contains(ChatColor.WHITE + "Court Case");
    }
    
    public static boolean isEmptyCaseBook(final ItemStack itemStack) {
        if(itemStack == null || itemStack.getType() != Material.WRITABLE_BOOK) {
            return false;
        }
        if(itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }
        final String displayName = itemStack.getItemMeta().getDisplayName();
        return displayName.equals(ChatColor.WHITE + "Court Case");
    }
    
    public static ItemStack assignCaseNumberToBook(final ItemStack book, final int id) {
        final String bookTitle = ChatColor.WHITE + "Court Case " + TextUtil.numberWithLeadingZeros(id, 5);
        final ItemMeta itemMeta = book.getItemMeta();
        itemMeta.setDisplayName(bookTitle);
        book.setItemMeta(itemMeta);
        return book;
    }
    
    public static ItemStack baseItemStack() {
        final ItemStack itemStack = new ItemStack(Material.WRITABLE_BOOK);
        final BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        final String bookTitle = ChatColor.WHITE + "Court Case";
        bookMeta.setDisplayName(bookTitle);
        if(bookMeta.getPages().isEmpty()) {
            bookMeta.addPage(" ");
        } else {
            bookMeta.setPage(0, " ");
        }
        itemStack.setItemMeta(bookMeta);
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return itemStack;
    }
    
    public boolean isDone() {
        return caseStatus == CaseStatus.MISTRIAL || caseStatus == CaseStatus.THROWN_OUT || caseStatus == CaseStatus.RESOLVED;
    }
    
    public String caseName() {
        return "Case " + TextUtil.numberWithLeadingZeros(id, 5);
    }
    
    public void setCaseStatus(final CaseStatus caseStatus, final String name) {
        this.caseStatus = caseStatus;
        caseHistory.record(this, caseStatus, name);
    }
    
    private Set<Citizen> participants() {
        final Set<Citizen> part = new HashSet<>();
        if(courtDate != null && courtDate.getJudge() != null) {
            part.add(courtDate.getJudge());
        }
        if(getPlantiff() != null) {
            part.add(getPlantiff());
        }
        if(getDefendent() != null) {
            part.add(getDefendent());
        }
        return part;
    }
    
    public CourtDate getCourtDate() {
        return courtDate;
    }
    
    public void setCourtDate(final CourtDate courtDate) {
        this.courtDate = courtDate;
        if(courtDate != null && Courts.getCourts().getNotificationManager() != null) {
            final Notification notification = Courts.getCourts().getNotificationManager().getNotificationTypeNotificationMap().get(NotificationType.CASE_ASSIGNED_TIME);
            if(notification != null) {
                final Set<Citizen> citizenList = participants();
                notification.sendCitizens(new Object[] {this, courtDate.getJudge()}, citizenList);
            }
        }
        if(Courts.getCourts().getDefaultDayGetter() != null) {
            if(courtDate == null && getCourtDate() != null) {
                Courts.getCourts().getDefaultDayGetter().remove(getCourtDate());
            } else {
                Courts.getCourts().getDefaultDayGetter().update(courtDate);
            }
            Courts.getCourts().getCourtSessionManager().registerSession(this);
        }
    }
    
    public void setCourtDateNotRegister(final CourtDate courtDate) {
        this.courtDate = courtDate;
        if(Courts.getCourts().getDefaultDayGetter() != null) {
            if(courtDate == null && getCourtDate() != null) {
                Courts.getCourts().getDefaultDayGetter().remove(getCourtDate());
            } else {
                Courts.getCourts().getDefaultDayGetter().update(courtDate);
            }
        }
    }
    
    public void backToProcessed() {
        courtDate = null;
        if(caseStatus == CaseStatus.COURT_DATE_SET) {
            String name = "timeout";
            if(caseHistory != null && caseHistory.getProcessingEntry() != null && caseHistory.getProcessingEntry().getResponsible() != null) {
                name = caseHistory.getProcessingEntry().getResponsible();
            }
            setCaseStatus(CaseStatus.PROCESSED, name);
            updateSave();
        }
    }
    
    public void removeCourtDate() {
        courtDate = null;
    }
    
    public CaseStatus getCaseStatus() {
        return caseStatus;
    }
    
    public Citizen getPlantiff() {
        return plantiff;
    }
    
    public void setPlantiff(final Citizen plantiff) {
        this.plantiff = plantiff;
    }
    
    public Citizen getDefendent() {
        return defendent;
    }
    
    public void setDefendent(final Citizen defendent) {
        this.defendent = defendent;
    }
    
    public int getId() {
        return id;
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }
    
    public CaseHistory getCaseHistory() {
        return caseHistory;
    }
    
    public void setCaseHistory(final CaseHistory caseHistory) {
        this.caseHistory = caseHistory;
    }
    
    public CaseCategory getCaseCategory() {
        return caseCategory;
    }
    
    //public void submitDetails(ItemStack book, Citizen plantiff) {
    //    caseBook = book.clone();
    //    //this.plantiff = plantiff;
    //    setCaseStatus(CaseStatus.UNPROCESSED, plantiff.getName());
    //}
    
    public void setCaseCategory(final CaseCategory caseCategory) {
        this.caseCategory = caseCategory;
    }
    
    public ItemStack getCaseBook() {
        return caseBook;
    }
    
    public void setCaseBook(final ItemStack caseBook) {
        this.caseBook = caseBook;
    }
    
    public Resolve getResolve() {
        return resolve;
    }
    
    public void setResolve(final Resolve resolve) {
        this.resolve = resolve;
    }
    
    public CaseMeta getCaseMeta() {
        if(caseMeta == null) {
            caseMeta = new CaseMeta();
        }
        return caseMeta;
    }
    
    public void updateSave() {
        Courts.getCourts().getSqlSaveManager().updateCase(this);
    }
    
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        if(caseStatus != null) {
            map.put("case-status", caseStatus.toString());
        }
        map.put("plantiff", plantiff);
        map.put("defendant", defendent);
        map.put("case-book", caseBook);
        if(caseCategory != null) {
            map.put("case-category", caseCategory.toString());
        }
        map.put("case-history", caseHistory);
        map.put("court-date", courtDate);
        map.put("resolve", resolve);
        map.put("case-meta", caseMeta);
        return map;
    }
}
