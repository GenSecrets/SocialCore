package com.nicholasdoherty.socialcore.utils;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by john on 8/11/14.
 */
public class SerializableUUID implements ConfigurationSerializable {
    private UUID uuid;

    public SerializableUUID(UUID uuid) {
        this.uuid = uuid;
    }
    public SerializableUUID(Map<String, Object> map) {
        uuid = UUID.fromString((String) map.get("uuid"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid",uuid.toString());
        return map;
    }

    public UUID asUUID() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerializableUUID that = (SerializableUUID) o;

        if (!uuid.equals(that.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
