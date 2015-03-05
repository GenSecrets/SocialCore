package com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeCaseView;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.*;
import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items.ModifyPostActionsClickItem;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items.PostCourtActionsClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.CaseInfoBookClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.CaseInfoClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.ChangeViewClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.views.PaginatedItemView;
import com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeProcessedCasesView;
import com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeStallGUI;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.AssignCategoryClickItem;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.AssignDefendentClickItem;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.ThrowoutClickItem;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.ItemStackBuilder;
import com.nicholasdoherty.socialcore.utils.UUIDUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by john on 1/9/15.
 */
public class JudgeCaseView extends PaginatedItemView implements AssignCategoryClickItem.AssignCategory, AssignDefendentClickItem.AssignDefendant, ThrowoutClickItem.ThrowoutCase, PostCourtActionHolder{
    private JudgeProcessedCasesView judgeProcessedCasesView;
    private Case caze;
    private CaseInfoClickItem caseInfoClickItem;
    private List<PostCourtAction> postCourtActions = new ArrayList<>();
    public JudgeCaseView(Case caze, JudgeProcessedCasesView judgeProcessedCasesView) {
        super(judgeProcessedCasesView.getInventoryGUI(), 54);
        this.judgeProcessedCasesView = judgeProcessedCasesView;
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
        caze.setLocked(true);
        update();
    }
    public void addPostCourtAction(PostCourtAction postCourtAction) {
        postCourtActions.add(postCourtAction);
    }

    @Override
    public Case getCase() {
        return caze;
    }

    @Override
    public void removePostCourtAction(PostCourtAction postCourtAction) {
        postCourtActions.remove(postCourtAction);
    }

    @Override
    public void update() {
        caseInfoClickItem = new CaseInfoBookClickItem(caze,this);
        addActiveItem(0, caseInfoClickItem);
        addActiveItem(7, new AssignDefendentClickItem(this,this,caze));
        if (caze.getCaseCategory() != null && caze.getCaseCategory() == CaseCategory.DIVORCE || caze.getCaseCategory() == CaseCategory.SAMESEX_MARRIAGE) {
            addActiveItem(9, new ChangeViewClickItem(new CategorySpecificNoCourtActionsView(this,caze)) {
                @Override
                public ItemStack itemstack() {
                    return new ItemStackBuilder(Material.COMPASS)
                            .setName(ChatColor.GREEN + caze.getCaseCategory().getName() + " specific actions")
                            .addLore(ChatColor.GRAY + "<Left click to view",
                                    ChatColor.GRAY + caze.getCaseCategory().getName() + " specific actions>")
                            .toItemStack();
                }
            });
        }
        if (!postCourtActions.isEmpty()) {
            addActiveItem(10,new PostCourtActionsClickItem(this));
            addActiveItem(11, new ModifyPostActionsClickItem(this,this));
            addActiveItem(8, new ResolveWithActionsClickItem(this));
        }else {
            addActiveItem(53, new ThrowoutClickItem(this));
            addActiveItem(6, new AssignTimeClickItem(this));
            addActiveItem(8, new UpdateClickItem(this));
        }
        super.update();
    }

    @Override
    public void assignCategory(CaseCategory caseCategory) {
        caze.setCaseCategory(caseCategory);
        getInventoryGUI().sendViewersMessage(ChatColor.GREEN + "Assigned case " + caze.getId() + " to category " + caseCategory.getName());
        update(caseInfoClickItem);
    }
    @Override
    public boolean assignDefendant(String name) {
        UUID uuid = UUIDUtil.getUUID(name);
        if (uuid == null)
            return false;
        name = UUIDUtil.prettyName(name,uuid);
        caze.setDefendent(new Citizen(name, uuid));
        getInventoryGUI().sendViewersMessage(ChatColor.GREEN + "Assigned defendant " + name + " to case " + caze.getId());
        return true;
    }
    @Override
    public void throwOut() {
        caze.setCaseStatus(CaseStatus.THROWN_OUT, this.getInventoryGUI().getPlayer().getName());
        getInventoryGUI().sendViewersMessage(ChatColor.RED + "Threw out case " + caze.getId());
        backToPage();
    }
    public void assignDate(long date) {
        JudgeStallGUI judgeStallGUI = (JudgeStallGUI) judgeProcessedCasesView.getInventoryGUI();
        CourtDate courtDate = new CourtDate(date,judgeStallGUI.getJudge(), Courts.getCourts().getCourtsConfig().getDefaultCourtRoom());
        caze.setCaseStatus(CaseStatus.COURT_DATE_SET,courtDate.getJudge().getName());
        caze.setCourtDate(courtDate);
    }
    private void backToPage() {
        onClose();
        this.getInventoryGUI().setCurrentView(judgeProcessedCasesView);
        judgeProcessedCasesView.activate();
    }
    //todo force onclose, interface??
    @Override
    public void onClose() {
        caze.setLocked(false);
    }
    private void doPostCourtActions() {
        for (PostCourtAction postCourtAction : postCourtActions) {
            postCourtAction.doAction();
        }
    }

    public List<PostCourtAction> getPostCourtActions() {
        return postCourtActions;
    }
    public void resolveWithAction() {
        doPostCourtActions();
        caze.setCaseStatus(CaseStatus.RESOLVED, this.getInventoryGUI().getPlayer().getName());
        caze.setLocked(false);
        caze.setResolve(Resolve.fromPost(postCourtActions));
        Player p = getInventoryGUI().getPlayer();
        if (p != null) {
            for (ItemStack itemStack : Courts.getCourts().getCourtsConfig().getProcessReward()) {
                p.getInventory().addItem(itemStack);
            }
        }
        backToPage();
    }
    public void notifyAndUpdate() {
        //todo Notifications
        backToPage();
        caze.setLocked(false);
    }
}
