package com.nicholasdoherty.socialcore.courts.inputlib;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by john on 1/9/15.
 */
public class InputLib {
    private Plugin plugin;
    private ProtocolLibHook protocolLibHook;
    private Set<UUID> uuids = new ConcurrentSkipListSet<>();
    private Map<UUID, InputRunnable> runnableMap = new HashMap<>();
    private Map<UUID, BukkitTask> removeTasks = new HashMap<>();
    public InputLib(Plugin plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            protocolLibHook = new ProtocolLibHook(plugin);
        }
        new InputListener(this,plugin);
    }
    public void remove(UUID uuid) {
        if (uuids.contains(uuid)) {
            uuids.remove(uuid);
        }
        if (protocolLibHook != null) {
            protocolLibHook.remove(uuid);
        }
        if (runnableMap.containsKey(uuid)) {
            runnableMap.remove(uuid);
        }
        if (removeTasks.containsKey(uuid)) {
            removeTasks.get(uuid).cancel();
            removeTasks.remove(uuid);
        }
    }
    public void clearChat(Player p) {
        for (int i = 0; i < 8; i++) {
            sendMessage(p," ");
        }
    }
    public void sendMessage(Player p, String message) {
        if (protocolLibHook == null) {
            p.sendMessage(message);
        }else {
            p.sendMessage(ProtocolLibHook.KEY + message);
        }
    }
    public boolean isActive(UUID uuid) {
        return uuids.contains(uuid);
    }
    public void perform(UUID uuid, String input) {
        if (!runnableMap.containsKey(uuid))
            return;
        InputRunnable inputRunnable = runnableMap.get(uuid);
        remove(uuid);
        inputRunnable.run(input);
    }
    public void add(final UUID uuid, InputRunnable runnable) {
        if (isActive(uuid)) {
            remove(uuid);
        }
        uuids.add(uuid);
        if (protocolLibHook != null)
             protocolLibHook.add(uuid);
        runnableMap.put(uuid,runnable);
        removeTasks.put(uuid, new BukkitRunnable(){
            @Override
            public void run() {
                remove(uuid);
            }
        }.runTaskLater(plugin,10*60*1000));
    }
}
