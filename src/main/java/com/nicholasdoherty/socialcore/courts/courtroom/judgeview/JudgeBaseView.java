package com.nicholasdoherty.socialcore.courts.courtroom.judgeview;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.courts.cases.CourtDate;
import com.nicholasdoherty.socialcore.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.courts.courtroom.actions.RescheduleCase;
import com.nicholasdoherty.socialcore.courts.courtroom.actions.ThrowoutCase;
import com.nicholasdoherty.socialcore.courts.courtroom.judgeview.items.*;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryView;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.CaseInfoBookClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems.CaseInfoClickItem;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.AssignCategoryClickItem;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.AssignDefendentClickItem;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.ThrowoutClickItem;
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
 * Created by john on 1/13/15.
 */
public class JudgeBaseView extends InventoryView implements AssignCategoryClickItem.AssignCategory, AssignDefendentClickItem.AssignDefendant, ThrowoutClickItem.ThrowoutCase{
    private JudgeCourtGUI judgeCourtGUI;
    private CaseInfoClickItem caseInfoClickItem;
    private Case caze;
    private CourtSession courtSession;
    public JudgeBaseView(JudgeCourtGUI judgeCourtGUI) {
        super(judgeCourtGUI);
        this.judgeCourtGUI = judgeCourtGUI;
        courtSession = judgeCourtGUI.getCourtSession();
        caze = judgeCourtGUI.getCaze();
        List<ClickItem> categoryItems = new ArrayList<>();
        for (CaseCategory caseCategory : CaseCategory.values()) {
            categoryItems.add(new AssignCategoryClickItem(this,this,caseCategory));
        }
    }

    @Override
    public void initActiveItems() {
        caze.setLocked(true);
        update();
    }

    @Override
    public void update() {
        clearActiveItems();
        caseInfoClickItem = new CaseInfoBookClickItem(caze,this);
        addActiveItem(0, caseInfoClickItem);
        addActiveItem(1, new PostCourtActionsClickItem(judgeCourtGUI.getCourtSession()));
        addActiveItem(2, new ModifyPostActionsClickItem(this,courtSession));

        addActiveItem(6, new RescheduleCourtDateClickItem(this));
        addActiveItem(7, new AssignDefendentClickItem(this,this,caze));
        addActiveItem(8, new CallVerdictClickItem(this));

        addActiveItem(18, new CallYayNayVoteClickItem(this));
        addActiveItem(19, new CallGuiltyInnocentVoteClickItem(this));

        addActiveItem(21, new TallyVotesClickItem(this));
        addActiveItem(22, new ClearVotesClickItem(this));

        addActiveItem(24, new JailPlaintiffClickItem(this));
        addActiveItem(25, new JailDefendantClickItem(this));
        if (caze.getCaseCategory() != null) {
            addActiveItem(26, new CategorySpecificClickItem(this));
        }

        addActiveItem(27, new AffirmYayClickItem(this));
        addActiveItem(28, new FindPlantiffGuiltyClickItem(this));

        addActiveItem(33, new FinePlaintiffClickItem(this));
        addActiveItem(34, new FineDefendantClickItem(this));

        addActiveItem(36, new AffirmNayClickItem(this));
        addActiveItem(37, new FindDefendantGuiltyClickItem(this));

        if (courtSession.isInRecess()) {
            addActiveItem(49, new EndRecessClickItem(this));
        }else {
            addActiveItem(49, new RecessClickItem(this));
        }

        addActiveItem(51, new QuietCourtClickItem(this));

        addActiveItem(53, new ThrowOutCaseClickItem(this));
    }

    public CourtSession getCourtSession() {
        return courtSession;
    }

    @Override
    public void assignCategory(CaseCategory caseCategory) {
        caze.setCaseCategory(caseCategory);
        getInventoryGUI().sendViewersMessage(ChatColor.GREEN + "Assigned case " + caze.getId() + " to category " + caseCategory);
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
        courtSession.addPostCourtAction(new ThrowoutCase(courtSession));
    }
    public void assignDate(long date) {
        CourtDate courtDate = new CourtDate(date, courtSession.getJudge(),caze.getCourtDate().getCourtRoom());
        courtSession.addPostCourtAction(new RescheduleCase(courtSession, courtDate));
    }
    @Override
    public void onClose() {
        caze.setLocked(false);
    }

    @Override
    public Inventory getBaseInventory() {
        return Bukkit.createInventory(null,54,"Judge View");
    }

    public void callVerdict() {
        courtSession.end();
        caze.setLocked(false);
        Player p = getInventoryGUI().getPlayer();
        if (p != null) {
            for (ItemStack itemStack : Courts.getCourts().getCourtsConfig().getJudgementReward()) {
                p.getInventory().addItem(itemStack);
            }
        }
        judgeCourtGUI.close();
    }
}