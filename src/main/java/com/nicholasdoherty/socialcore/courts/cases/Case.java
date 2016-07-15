package com.nicholasdoherty.socialcore.courts.cases;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.notifications.Notification;
import com.nicholasdoherty.socialcore.courts.notifications.NotificationType;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Created by john on 1/3/15.
 */
public class Case implements ConfigurationSerializable{
    private int id;
    private CaseStatus caseStatus;
    private Citizen plantiff,defendent;
    private ItemStack caseBook;
    private boolean locked = false;
    private CaseCategory caseCategory;
    private CaseHistory caseHistory;
    private CourtDate courtDate;
    private Resolve resolve;

    private CaseMeta caseMeta;

    public Case(int id, CaseStatus caseStatus) {
        this.id = id;
        this.caseStatus = caseStatus;
        caseHistory = new CaseHistory();
    }

    public Case(int id, CaseStatus caseStatus, Citizen plantiff, Citizen defendent, ItemStack caseBook, boolean locked, CaseCategory caseCategory, CaseMeta caseMeta) {
        this.id = id;
        this.caseStatus = caseStatus;
        this.plantiff = plantiff;
        this.defendent = defendent;
        this.caseBook = caseBook;
        this.locked = locked;
        this.caseCategory = caseCategory;
        this.caseMeta = caseMeta;
    }

    public void setCaseHistory(CaseHistory caseHistory) {
        this.caseHistory = caseHistory;
    }

    public boolean isDone() {
        if (caseStatus == CaseStatus.MISTRIAL || caseStatus == CaseStatus.THROWN_OUT || caseStatus == CaseStatus.RESOLVED)
            return true;
        return false;
    }
    public void setPlantiff(Citizen plantiff) {
        this.plantiff = plantiff;
    }

    public void setDefendent(Citizen defendent) {
        this.defendent = defendent;
    }
    public String caseName() {
        return "Case " + TextUtil.numberWithLeadingZeros(id,5);
    }
    public void setCaseStatus(CaseStatus caseStatus, String name) {
        this.caseStatus = caseStatus;
        caseHistory.record(this, caseStatus, name);
    }

    public void setCaseBook(ItemStack caseBook) {
        this.caseBook = caseBook;
    }

    private Set<Citizen> participants() {
        Set<Citizen> part = new HashSet<>();
        if (courtDate != null && courtDate.getJudge() != null) {
            part.add(courtDate.getJudge());
        }
        if (getPlantiff() != null) {
            part.add(getPlantiff());
        }
        if (getDefendent() != null) {
            part.add(getDefendent());
        }
        return part;
    }
    public CourtDate getCourtDate() {
        return courtDate;
    }
    public void setCourtDateNotRegister(CourtDate courtDate) {
        this.courtDate = courtDate;
        if (Courts.getCourts().getDefaultDayGetter() != null) {
            if (courtDate == null && this.getCourtDate() != null) {
                Courts.getCourts().getDefaultDayGetter().remove(this.getCourtDate());
            }else {
                Courts.getCourts().getDefaultDayGetter().update(courtDate);
            }
        }
    }
    public void backToProcessed() {
        courtDate = null;
        if (caseStatus == CaseStatus.COURT_DATE_SET) {
            String name = "timeout";
            if (caseHistory != null && caseHistory.getProcessingEntry() != null && caseHistory.getProcessingEntry().getResponsible() != null) {
                name = caseHistory.getProcessingEntry().getResponsible();
            }
            setCaseStatus(CaseStatus.PROCESSED,name);
            this.updateSave();
        }
    }
    public void removeCourtDate() {
        courtDate = null;
    }

    public void setCourtDate(CourtDate courtDate) {
        this.courtDate = courtDate;
        if (courtDate != null && Courts.getCourts().getNotificationManager() != null) {
            Notification notification = Courts.getCourts().getNotificationManager().getNotificationTypeNotificationMap().get(NotificationType.CASE_ASSIGNED_TIME);
            if (notification != null) {
                Set<Citizen> citizenList = participants();
                notification.sendCitizens(new Object[]{this,courtDate.getJudge()},citizenList);
            }
        }
        if (Courts.getCourts().getDefaultDayGetter() != null) {
            if (courtDate == null && this.getCourtDate() != null) {
                Courts.getCourts().getDefaultDayGetter().remove(this.getCourtDate());
            }else {
                Courts.getCourts().getDefaultDayGetter().update(courtDate);
            }
            Courts.getCourts().getCourtSessionManager().registerSession(this);
        }
    }

    public CaseStatus getCaseStatus() {
        return caseStatus;
    }

    public Citizen getPlantiff() {
        return plantiff;
    }

    public Citizen getDefendent() {
        return defendent;
    }

    public int getId() {
        return id;
    }

    public boolean isLocked() {
        return locked;
    }

    public CaseHistory getCaseHistory() {
        return caseHistory;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public CaseCategory getCaseCategory() {
        return caseCategory;
    }

    public void setCaseCategory(CaseCategory caseCategory) {
        this.caseCategory = caseCategory;
    }

    public ItemStack getCaseBook() {
        return caseBook;
    }

    public Resolve getResolve() {
        return resolve;
    }

    public void setResolve(Resolve resolve) {
        this.resolve = resolve;
    }

    //public void submitDetails(ItemStack book, Citizen plantiff) {
    //    caseBook = book.clone();
    //    //this.plantiff = plantiff;
    //    setCaseStatus(CaseStatus.UNPROCESSED, plantiff.getName());
    //}

    public CaseMeta getCaseMeta() {
        if (caseMeta == null) {
            this.caseMeta = new CaseMeta();
        }
        return caseMeta;
    }
    public static boolean isCaseBook(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.BOOK_AND_QUILL) {
            return false;
        }
        if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }
        String displayName = itemStack.getItemMeta().getDisplayName();
        if (displayName.contains(ChatColor.WHITE + "Court Case")) {
            return true;
        }
        return false;
    }
    public static boolean isEmptyCaseBook(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.BOOK_AND_QUILL) {
            return false;
        }
        if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }
        String displayName = itemStack.getItemMeta().getDisplayName();
        if (displayName.equals(ChatColor.WHITE + "Court Case")) {
            return true;
        }
        return false;
    }
    public static ItemStack assignCaseNumberToBook(ItemStack book, int id) {
        String bookTitle = ChatColor.WHITE + "Court Case " + TextUtil.numberWithLeadingZeros(id,5);
        ItemMeta itemMeta = book.getItemMeta();
        itemMeta.setDisplayName(bookTitle);
        book.setItemMeta(itemMeta);
        return book;
    }
    public static ItemStack baseItemStack() {
        ItemStack itemStack = new ItemStack(Material.BOOK_AND_QUILL);
        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
        String bookTitle = ChatColor.WHITE + "Court Case";
        bookMeta.setDisplayName(bookTitle);
        if (bookMeta.getPages().size() == 0) {
            bookMeta.addPage(" ");
        }else {
            bookMeta.setPage(0," ");
        }
        itemStack.setItemMeta(bookMeta);
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY,1);
        return itemStack;
    }
    public void updateSave() {
        Courts.getCourts().getSqlSaveManager().updateCase(this);
    }
    public Case(Map<String, Object> map) {
        this.id = (int) map.get("id");
        if (map.containsKey("case-status"))
            this.caseStatus = CaseStatus.valueOf(""+map.get("case-status"));
        this.plantiff = (Citizen) map.get("plantiff");
        this.defendent = (Citizen) map.get("defendant");
        this.caseBook = (ItemStack) map.get("case-book");
        if (map.containsKey("case-category"))
            this.caseCategory = CaseCategory.valueOf((String) map.get("case-category"));
        this.caseHistory = (CaseHistory) map.get("case-history");
        this.courtDate = (CourtDate) map.get("court-date");
        if (map.containsKey("resolve")) {
            this.resolve = (Resolve) map.get("resolve");
        }
        if (map.containsKey("case-meta")) {
            this.caseMeta = (CaseMeta) map.get("case-meta");
        }
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id",id);
        if (caseStatus != null)
             map.put("case-status",caseStatus.toString());
        map.put("plantiff",plantiff);
        map.put("defendant",defendent);
        map.put("case-book",caseBook);
        if (caseCategory != null)
            map.put("case-category",caseCategory.toString());
        map.put("case-history",caseHistory);
        map.put("court-date",courtDate);
        map.put("resolve",resolve);
        map.put("case-meta",caseMeta);
        return map;
    }
}
