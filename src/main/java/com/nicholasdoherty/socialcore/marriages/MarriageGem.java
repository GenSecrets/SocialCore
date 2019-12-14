package com.nicholasdoherty.socialcore.marriages;

import org.bukkit.Material;

public class MarriageGem {
    private Material blockID;
    private String name;
    
    public MarriageGem(final Material blockID, final String name) {
        this.blockID = blockID;
        this.name = name;
    }
    
    public Material getBlockID() {
        return blockID;
    }
    
    public String getName() {
        return name;
    }
}
