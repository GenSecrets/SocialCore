package com.nicholasdoherty.socialcore.courts.cases.category;

import com.nicholasdoherty.socialcore.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.ItemUtil;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 2/15/15.
 */
public class AbandonedCategoryConfig extends CategoryConfig{
    String buildingPermitEssentialsString;

    public AbandonedCategoryConfig(ConfigurationSection section) {
        super(section, CaseCategory.ABANDONED);
        buildingPermitEssentialsString = section.getString("permit-item");
    }
    public ItemStack permitItem(VLocation vLocation, Citizen citizen, Judge judge) {
        String ess = buildingPermitEssentialsString;
        ess = ess.replace("{citizen_name}",citizen.getName()).replace("{judge-name}",judge.getName()).replace("{location}",vLocation.toPrettyString());
        ItemStack itemStack = ItemUtil.getFromEssentialsString(ess);
        return itemStack;
    }
}
