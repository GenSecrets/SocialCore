package com.nicholasdoherty.socialcore.courts.objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by john on 1/3/15.
 */
public class Citizen implements ConfigurationSerializable {
    private int id;
    private String name;
    private UUID uuid;

    public Citizen(int id, String name, UUID uuid) {
        this.id = id;
        this.name = name;
        this.uuid = uuid;
    }
    public Citizen(Citizen citizen) {
        this.id = citizen.getId();
        this.uuid = citizen.getUuid();
        this.name = citizen.getName();
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

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }
    public Citizen(Map<String, Object> map) {
        this.name = (String) map.get("name");
        this.uuid = UUID.fromString((String) map.get("uuid"));
    }
    public OfflinePlayer toOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
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
