package com.nicholasdoherty.socialcore.courts.cases.category;

import com.nicholasdoherty.socialcore.courts.cases.CaseCategory;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Created by john on 3/2/15.
 */
public class SameSexCategoryConfig extends CategoryConfig {
    private List<String> permissions;
    public SameSexCategoryConfig(ConfigurationSection section) {
        super(section, CaseCategory.SAMESEX_MARRIAGE);
        permissions = section.getStringList("permissions");
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
