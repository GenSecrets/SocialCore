package com.nicholasdoherty.socialcore.components.marriages;

import org.bukkit.Material;

public class MarriageGem {
    private final Material block;
    private final String name;
    
    public MarriageGem(final Material block, final String name) {
        this.block = block;
        this.name = name;
    }
    
    public Material getBlock() {
        return block;
    }
    
    public String getName() {
        return name;
    }
}
