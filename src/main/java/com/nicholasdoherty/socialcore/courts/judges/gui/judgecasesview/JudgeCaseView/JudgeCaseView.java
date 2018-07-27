package com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeCaseView;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.*;
import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.courts.courtroom.actions.SexChange;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.PostCourtActionHolder;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items.ModifyPostActionsClickItem;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items.PostCourtActionsClickItem;
import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.gui.inventorygui.gui.clickitems.CaseInfoBookClickItem;
import com.voxmc.voxlib.gui.inventorygui.gui.clickitems.CaseInfoClickItem;
import com.voxmc.voxlib.gui.inventorygui.gui.clickitems.ChangeViewClickItem;
import com.voxmc.voxlib.gui.inventorygui.views.PaginatedItemView;
import com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeProcessedCasesView;
import com.nicholasdoherty.socialcore.courts.judges.gui.judgecasesview.JudgeStallGUI;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.AssignCategoryClickItem;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.AssignDefendentClickItem;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.SecCaseView;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.ThrowoutClickItem;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.voxmc.voxlib.util.ItemStackBuilder;
import com.voxmc.voxlib.util.UUIDUtil;
import com.voxmc.voxlib.util.VaultUtil;
import com.voxmc.voxlib.util.VoxStringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
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
        if (postCourtAction instanceof SexChange) {
            for (PostCourtAction postCourtAction1 : new HashSet<>(postCourtActions)) {
                if (postCourtAction1 instanceof SexChange) {
                    postCourtActions.remove(postCourtAction1);
                }
            }
        }
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
        if (caze.getCaseCategory() != null && caze.getCaseCategory() == CaseCategory.DIVORCE || caze.getCaseCategory() == CaseCategory.SAMESEX_MARRIAGE || caze.getCaseCategory() == CaseCategory.SEX_CHANGE || caze.getCaseCategory() == CaseCategory.CIVIL_MARRIAGE) {
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
        if (caseCategory == CaseCategory.DIVORCE) {
            if (caze.getPlantiff() == null || caze.getDefendent() == null) {
                getInventoryGUI().sendViewersMessage(ChatColor.RED + "A plaintiff and defendant are required to assign a case to divorce!");
                return;
            }
            String plaintiffName = caze.getPlantiff().getName();
            SocialPlayer socialPlayer = SocialCore.plugin.save.getSocialPlayer(plaintiffName);
            if (socialPlayer == null || !socialPlayer.isMarried() || !socialPlayer.getMarriedTo().equalsIgnoreCase(caze.getDefendent().getName())) {
                getInventoryGUI().sendViewersMessage(ChatColor.RED + "A plaintiff and defendant must be married assign a case to divorce!");
                return;
            }
        }
        caze.setCaseCategory(caseCategory);
        postCourtActions.clear();
        getInventoryGUI().sendViewersMessage(ChatColor.GREEN + "Assigned case " + caze.getId() + " to category " + caseCategory.getName());
        update(caseInfoClickItem);
    }
    @Override
    public boolean assignDefendant(String name) {
        UUID uuid = UUIDUtil.getUUID(name);
        if (uuid == null)
            return false;
        name = UUIDUtil.prettyName(name,uuid);
        caze.setDefendent(Courts.getCourts().getCitizenManager().toCitizen(name,uuid));
        caze.updateSave();
        getInventoryGUI().sendViewersMessage(SecCaseView.replaceNouns(ChatColor.GREEN + "Assigned defendant " + name + " to case " + caze.getId(),caze.getCaseCategory()));
        return true;
    }
    @Override
    public void throwOut(boolean refund) {
        caze.setCaseStatus(CaseStatus.THROWN_OUT, getInventoryGUI().getPlayer().getName());
        if (refund) {
            boolean refundWork = false;
            Citizen submitter = caze.getCaseHistory().getSubmitter();
            if (submitter != null) {
                OfflinePlayer sO = submitter.toOfflinePlayer();
                if (sO != null) {
                    try {
                        refundWork = VaultUtil.give(sO, Courts.getCourts().getCourtsConfig().getCaseFilingCost());
                    } catch (VaultUtil.NotSetupException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (refundWork) {
                getInventoryGUI().getPlayer().sendMessage(ChatColor.GREEN + "Refunded");
            }else {
                getInventoryGUI().getPlayer().sendMessage(ChatColor.RED + "Was unable to refund");
            }
        }
        getInventoryGUI().sendViewersMessage(ChatColor.RED + "Threw out case " + caze.getId());
        backToPage();
        caze.updateSave();
    }
    public void assignDate(long date) {
        JudgeStallGUI judgeStallGUI = (JudgeStallGUI) judgeProcessedCasesView.getInventoryGUI();
        CourtDate courtDate = new CourtDate(date,judgeStallGUI.getJudge().getJudgeId());
        caze.setCaseStatus(CaseStatus.COURT_DATE_SET,courtDate.getJudge().getName());
        caze.setCourtDate(courtDate);
        caze.updateSave();
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
        if (postCourtActions.size() > 0) {
            String actionsString = VoxStringUtils.formatToString(VoxStringUtils.toStringList(postCourtActions, new VoxStringUtils.ToStringConverter() {
                @Override
                public String convertToString(Object o) {
                    PostCourtAction postCourtAction = (PostCourtAction) o;
                    return postCourtAction.prettyDescription();
                }
            }));
            getInventoryGUI().sendViewersMessage(ChatColor.GREEN + "The following actions have been performed: ");
            getInventoryGUI().sendViewersMessage(ChatColor.GREEN + actionsString);
        }
    }

    public List<PostCourtAction> getPostCourtActions() {
        return postCourtActions;
    }
    public void resolveWithAction() {
        if (caze.getCaseStatus() == CaseStatus.RESOLVED) {
            backToPage();
            return;
        }
        caze.setCaseStatus(CaseStatus.RESOLVED, this.getInventoryGUI().getPlayer().getName());
        doPostCourtActions();
        caze.setLocked(false);
        caze.setResolve(Resolve.fromPost(postCourtActions));
        Player p = getInventoryGUI().getPlayer();
        if (p != null) {
            for (ItemStack itemStack : Courts.getCourts().getCourtsConfig().getProcessReward()) {
                p.getInventory().addItem(itemStack);
            }
        }
        caze.updateSave();
        backToPage();
    }
    public void notifyAndUpdate() {
        //todo Notifications
        backToPage();
        caze.setLocked(false);
    }
}
