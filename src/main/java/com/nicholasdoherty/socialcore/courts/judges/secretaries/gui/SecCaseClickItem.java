package com.nicholasdoherty.socialcore.courts.judges.secretaries.gui;

import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.voxmc.voxlib.gui.inventorygui.gui.clickitems.CaseInfoClickItem;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.gui.caseview.SecCaseView;
import org.bukkit.ChatColor;

/**
 * Created by john on 1/8/15.
 */
public class SecCaseClickItem extends CaseInfoClickItem {
    SecretaryGUI secretaryGUI;
    SecretaryCasePaginatedView secretaryCasePaginatedView;

    public SecCaseClickItem(SecretaryGUI secretaryGUI, Case caze, SecretaryCasePaginatedView secretaryCasePaginatedView) {
        super(caze);
        this.secretaryGUI = secretaryGUI;
        this.secretaryCasePaginatedView = secretaryCasePaginatedView;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        if (getCaze().isLocked()) {
            secretaryGUI.getPlayer().sendMessage(ChatColor.RED + "That case is currently locked.");
        }else {
            SecCaseView secCaseView = new SecCaseView(secretaryGUI,getCaze(),secretaryCasePaginatedView);
            secretaryGUI.setCurrentView(secCaseView);
            secCaseView.activate();
        }
    }

}
