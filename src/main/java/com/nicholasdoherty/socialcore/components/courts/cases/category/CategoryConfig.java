package com.nicholasdoherty.socialcore.components.courts.cases.category;

import com.nicholasdoherty.socialcore.components.courts.cases.CaseCategory;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by john on 2/15/15.
 */
public class CategoryConfig {
    private CaseCategory caseCategory;
    public CategoryConfig(ConfigurationSection section, CaseCategory caseCategory) {
        this.caseCategory = caseCategory;
    }

    public CaseCategory getCaseCategory() {
        return caseCategory;
    }
}
