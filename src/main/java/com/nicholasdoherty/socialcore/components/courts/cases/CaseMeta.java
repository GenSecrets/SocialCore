package com.nicholasdoherty.socialcore.components.courts.cases;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

/**
 * Created by john on 2/15/15.
 */
public class CaseMeta implements ConfigurationSerializable{
    private List<CaseLocation> caseLocations;

    public CaseMeta(List<CaseLocation> caseLocations) {
        this.caseLocations = caseLocations;
    }
    public CaseMeta(CaseMeta caseMeta) {
        this.caseLocations = caseMeta.caseLocations;
    }

    public CaseMeta() {
    }
    public CaseMeta(Map<String, Object> map) {
        if (map.containsKey("case-locations") && map.get("case-locations") != null) {
            caseLocations = new ArrayList<>((Collection<? extends CaseLocation>) map.get("case-locations"));
        }else {
            caseLocations = new ArrayList<>();
        }
    }
    public void setCaseLocation(CaseLocation loc) {
        if (loc == null) {
            caseLocations = null;
            return;
        }
        if (caseLocations == null) {
            caseLocations = new ArrayList<>();
        }
        caseLocations.clear();
        caseLocations.add(loc);
    }
    public CaseLocation getCaseLocation() {
        if (caseLocations == null || caseLocations.isEmpty()) {
            return null;
        }
        return caseLocations.get(0);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("case-locations",caseLocations);
        return map;
    }

}
