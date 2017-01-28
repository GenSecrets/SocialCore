package com.nicholasdoherty.socialcore.courts.elections.gui;

import com.nicholasdoherty.socialcore.courts.elections.Election;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryGUI;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/6/15.
 */
public class ElectionGUI extends InventoryGUI{
    Election election;
    public ElectionGUI(Election election) {
        super();
        this.election = election;
        setCurrentView(new ElectionJudgeView(this));
    }

    public Election getElection() {
        return election;
    }
    public static void createAndOpen(Player p, Election election) {
        ElectionGUI electionGUI = new ElectionGUI(election);
        electionGUI.setPlayer(p);
        electionGUI.open();
    }
}
