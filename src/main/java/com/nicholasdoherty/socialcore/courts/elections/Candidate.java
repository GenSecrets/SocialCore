package com.nicholasdoherty.socialcore.courts.elections;

import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.utils.SerializableUUID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by john on 1/6/15.
 */
public class Candidate extends ApprovedCitizen implements ConfigurationSerializable {
    private boolean elected;

    public Candidate(String name, UUID uuid, Set<SerializableUUID> approvals, Set<SerializableUUID> disapprovals, boolean elected) {
        super(name,uuid, approvals, disapprovals);
        this.elected = elected;
    }

    public Candidate(String name, UUID uuid) {
        super(name,uuid, new HashSet<SerializableUUID>(), new HashSet<SerializableUUID>());
        elected = false;
    }

    public void setElected(boolean elected) {
        this.elected = elected;
    }

    public boolean isElected() {
        return elected;
    }
    public Candidate(Map<String, Object> map) {
        super(map);
        this.elected = (boolean) map.get("elected");
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("elected",elected);
        return map;
    }
}
