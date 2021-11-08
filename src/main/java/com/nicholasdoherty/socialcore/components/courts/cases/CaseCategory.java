package com.nicholasdoherty.socialcore.components.courts.cases;

import com.nicholasdoherty.socialcore.components.courts.cases.category.AbandonedCategoryConfig;
import com.nicholasdoherty.socialcore.components.courts.cases.category.CategoryConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by john on 1/8/15.
 */
public enum CaseCategory {
    GENDER_CHANGE(Material.APPLE, "Gender Change"),
    DIVORCE(Material.REDSTONE, "Divorce"),
    CIVIL_MARRIAGE(Material.GOLD_BLOCK, "Private Civil Marriage"),
    ABANDONED(Material.STONE, "House/Town Landclaim or Annex"),
    MAYOR(Material.BOOK, "Transfer of Mayorship"),
    RESIDENT_EVICTION(Material.ENDER_CHEST, "Resident Eviction"),
    TRESPASSING(Material.DIRT, "Land Trespassing"),
    LAWSUIT(Material.BOOK, "Generic Lawsuit/Others");

    private final Material mat;
    private final String name;
    
    CaseCategory(Material mat, String name) {
        this.mat = mat;
        this.name = name;
    }
    
    public static CaseCategory fromString(String in) {
        in = in.toUpperCase().replace("_", "").replace("-", "").replace(" ", "");
        for(CaseCategory caseCategory : values()) {
            String caseCategoryString = caseCategory.toString().replace("_", "");
            String cleanedString = caseCategory.getName().toUpperCase().replace(" ", "");
            if(in.equals(caseCategoryString) || in.equals(cleanedString)) {
                return caseCategory;
            }
        }
        return null;
    }
    
    public Material getMat() {
        return mat;
    }
    
    public String getName() {
        return name;
    }
    
    public CategoryConfig categoryConfig(ConfigurationSection section) {
        switch(this) {
            case DIVORCE:
            case CIVIL_MARRIAGE:
            case GENDER_CHANGE:
                break;
            case TRESPASSING:
            case ABANDONED:
            case MAYOR:
            case RESIDENT_EVICTION:
            case LAWSUIT:
                return new AbandonedCategoryConfig(section);
        }
        return new CategoryConfig(section, this);
    }
}
