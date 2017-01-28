package com.nicholasdoherty.socialcore.courts.courtroom.actions;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.courts.cases.CaseLocation;
import com.nicholasdoherty.socialcore.courts.cases.category.AbandonedChestCategoryConfig;
import com.nicholasdoherty.socialcore.courts.courtroom.PostCourtAction;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 2/15/15.
 */
public class GrantChestPermit implements PostCourtAction,ConfigurationSerializable {
    private CaseLocation caseLocation;
    private Judge judge;
    private int cazeId;

    public GrantChestPermit(Case caze, CaseLocation caseLocation, Judge judge) {
        this.cazeId = caze.getId();
        this.caseLocation = caseLocation;
        this.judge = judge;
    }

    @Override
    public void doAction() {
        Case caze = Courts.getCourts().getCaseManager().getCase(cazeId);
        Player plaintiffPlayer = caze.getPlantiff().getPlayer();
        if (plaintiffPlayer == null || !plaintiffPlayer.isOnline()) {
            return;
        }
        AbandonedChestCategoryConfig abandonedCategoryConfig = (AbandonedChestCategoryConfig) Courts.getCourts().getCourtsConfig().getCategoryConfgi(CaseCategory.ABANDONED);
        if (abandonedCategoryConfig == null)
            return;
        ItemStack item = abandonedCategoryConfig.permitItem(caseLocation.getvLocation(),caze.getPlantiff(),judge);
        if (item == null)
            return;
        plaintiffPlayer.getInventory().addItem(item);
    }

    @Override
    public String prettyDescription() {
        return ChatColor.GREEN + "A chest permit will be granted to the plaintiff at " + caseLocation.getvLocation().toPrettyString();
    }
    public GrantChestPermit(Map<String, Object> map) {
        if (map.containsKey("case")) {
            Case caze = (Case) map.get("case");
            cazeId = caze.getId();
        }else if (map.containsKey("case-id")) {
            cazeId = (int) map.get("case-id");
        }
        this.caseLocation = (CaseLocation) map.get("case-location");
        this.judge = (Judge) map.get("judge");
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("case-id",cazeId);
        map.put("case-location",caseLocation);
        map.put("judge",judge);
        return map;
    }
}
