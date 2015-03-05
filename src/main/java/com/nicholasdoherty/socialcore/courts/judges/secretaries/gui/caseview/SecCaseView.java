package com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.courts.cases.CaseStatus;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.CaseInfoBookClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.PaginatedItemView;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.SecretaryCasePaginatedView;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Created by john on 1/8/15.
 */
public class SecCaseView extends PaginatedItemView implements AssignCategoryClickItem.AssignCategory, AssignDefendentClickItem.AssignDefendant, ThrowoutClickItem.ThrowoutCase{
    private Case caze;
    private ClickItem caseInfoClickItem;
    private SecretaryCasePaginatedView secretaryCasePaginatedView;

    public SecCaseView(InventoryGUI inventoryGUI, Case caze, SecretaryCasePaginatedView secretaryCasePaginatedView) {
        super(inventoryGUI, 54);
        this.secretaryCasePaginatedView = secretaryCasePaginatedView;
        this.caze = caze;
        List<ClickItem> categoryItems = new ArrayList<>();
        for (CaseCategory caseCategory : CaseCategory.values()) {
            categoryItems.add(new AssignCategoryClickItem(this,this,caseCategory));
        }
        setPaginatedItems(categoryItems);
        setStartEnd(18,35);
    }

    @Override
    public void initActiveItems() {
        update();
        caze.setLocked(true);
    }

    @Override
    public void update() {
        caseInfoClickItem = new CaseInfoBookClickItem(caze,this);
        addActiveItem(0, caseInfoClickItem);

        addActiveItem(7, new AssignDefendentClickItem(this,this,caze));
        addActiveItem(8, new ProcessClickItem(this));
        addActiveItem(53, new ThrowoutClickItem(this));
        super.update();
    }

    public void assignCategory(CaseCategory caseCategory) {
        caze.setCaseCategory(caseCategory);
        getInventoryGUI().sendViewersMessage(ChatColor.GREEN + "Assigned case " + caze.getId() + " to category " + caseCategory.getName());
        update(caseInfoClickItem);
    }
    public boolean assignDefendant(String name) {
        UUID uuid = UUIDUtil.getUUID(name);
        if (uuid == null)
            return false;
        name = UUIDUtil.prettyName(name,uuid);
        caze.setDefendent(new Citizen(name,uuid));
        getInventoryGUI().sendViewersMessage(replaceNouns(ChatColor.GREEN + "Assigned defendant " + name + " to case " + caze.getId(),caze.getCaseCategory()));
        return true;
    }
    @Override
    public void onClose() {
        caze.setLocked(false);
    }
    private void backToPageView() {
        onClose();
        getInventoryGUI().setCurrentView(secretaryCasePaginatedView);
        secretaryCasePaginatedView.activate();
    }
    private String secName() {
        return getInventoryGUI().getPlayer().getName();
    }
    public void throwOut() {
        caze.setCaseStatus(CaseStatus.THROWN_OUT, secName());
        getInventoryGUI().sendViewersMessage(ChatColor.RED + "Threw out case " + caze.getId());
        backToPageView();
    }
    public void process() {
        if (caze.getCaseCategory() == CaseCategory.SAMESEX_MARRIAGE && caze.getDefendent() == null){
            getInventoryGUI().sendViewersMessage(ChatColor.RED + "You must select a spouse to process this case.");
            return;
        }
        caze.setCaseStatus(CaseStatus.PROCESSED, secName());
        getInventoryGUI().sendViewersMessage(ChatColor.GREEN + "Processed case " + caze.getId());
        Player p = getInventoryGUI().getPlayer();
        if (p != null) {
            for (ItemStack itemStack : Courts.getCourts().getCourtsConfig().getProcessReward()) {
                p.getInventory().addItem(itemStack);
            }
        }
        backToPageView();
    }
    public Case getCaze() {
        return caze;
    }
    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null, this.getSize(), "Secretary: " + caze.caseName());
    }
    public static String replaceNouns(String message, CaseCategory caseCategory) {
        if (message == null)
            return message;
        if (caseCategory != CaseCategory.SAMESEX_MARRIAGE)
            return message;
        return message.replace("defendant","spouse").replace("Defendant","Spouse").replace("plaintiff","petitioner").replace("Plaintiff","Petitioner");
    }
}
