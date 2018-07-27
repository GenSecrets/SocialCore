package com.nicholasdoherty.socialcore.courts.cases.category;

import com.nicholasdoherty.socialcore.courts.cases.CaseCategory;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.voxmc.voxlib.util.ItemUtil;
import com.voxmc.voxlib.VLocation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * Created by john on 2015/06/10.
 */
public class AbandonedChestCategoryConfig extends CategoryConfig{
    String chestPermitEssentialsString;

    public AbandonedChestCategoryConfig(final ConfigurationSection section) {
        super(section, CaseCategory.ABANDONED_CHEST);
        chestPermitEssentialsString = section.getString("permit-item");
    }
    public ItemStack permitItem(final VLocation vLocation, final Citizen citizen, final Judge judge) {
        String ess = chestPermitEssentialsString;
        ess = ess.replace("{citizen_name}",citizen.getName()).replace("{judge-name}",judge.getName()).replace("{location}",vLocation.toPrettyString());
        return ItemUtil.getFromEssentialsString(ess);
    }
}
