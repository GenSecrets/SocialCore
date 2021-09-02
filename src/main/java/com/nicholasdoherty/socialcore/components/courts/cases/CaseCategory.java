package com.nicholasdoherty.socialcore.components.courts.cases;

import com.nicholasdoherty.socialcore.components.courts.cases.category.AbandonedCategoryConfig;
import com.nicholasdoherty.socialcore.components.courts.cases.category.AbandonedChestCategoryConfig;
import com.nicholasdoherty.socialcore.components.courts.cases.category.CategoryConfig;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by john on 1/8/15.
 */
public enum CaseCategory {
    DIVORCE(Material.REDSTONE, "Divorce"), TRESPASSING(Material.DIRT, "Trespassing"), ABANDONED(Material.STONE, "Abandoned House/Town"), OTHER(Material.BOOK, "Lawsuit"),
    SEX_CHANGE(Material.APPLE, "Sex Change"), CIVIL_MARRIAGE(Material.GOLD_BLOCK, "Civil Marriage"), ABANDONED_CHEST(Material.ENDER_CHEST, "Abandoned Chest");
    private Material mat;
    private String name;
    
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
                break;
            case TRESPASSING:
                break;
            case ABANDONED:
                return new AbandonedCategoryConfig(section);
            case ABANDONED_CHEST:
                return new AbandonedChestCategoryConfig(section);
        }
        return new CategoryConfig(section, this);
    }
}
