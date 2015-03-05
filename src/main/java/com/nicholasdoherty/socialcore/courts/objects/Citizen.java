package com.nicholasdoherty.socialcore.courts.objects;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by john on 1/3/15.
 */
public class Citizen implements ConfigurationSerializable {
    private String name;
    private UUID uuid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Citizen citizen = (Citizen) o;

        if (!uuid.equals(citizen.uuid)) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Citizen{" +
                "name='" + name + '\'' +
                ", uuid=" + uuid +
                '}';
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public Citizen(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }
    public Citizen(Player p) {
        this.name = p.getName();
        this.uuid = p.getUniqueId();
    }
    public boolean isSameUUID(UUID uuid) {
        return this.uuid.equals(uuid);
    }
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }
    public Citizen(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.uuid = UUID.fromString((String) map.get("uuid"));
    }
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("uuid",uuid.toString());
        return map;
    }
    public boolean isOnline() {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null || !p.isOnline())
            return false;
        return true;
    }
}
