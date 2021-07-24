package com.nicholasdoherty.socialcore.courts.inputlib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by john on 1/9/15.
 */
public class ProtocolLibHook {
    public static String KEY = "**^^!";
    Set<UUID> prevent = new HashSet<>();
    Plugin plugin;

    public ProtocolLibHook(Plugin plugin) {
        this.plugin = plugin;
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, PacketType.Play.Server.CHAT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        if (prevent.contains(event.getPlayer().getUniqueId())) {
                            if (event.getPacket().getChatComponents().size() > 0) {
                                StructureModifier<WrappedChatComponent> c = event.getPacket().getChatComponents();
                                List<WrappedChatComponent> cc = c.getValues();
                                if (cc.size() > 0) {
                                    WrappedChatComponent wrappedChatComponent = cc.get(0);
                                    String json = wrappedChatComponent.getJson();
                                    if (!json.contains(KEY)) {
                                        event.setCancelled(true);
                                    }else {
                                        json = json.replace(KEY,"");
                                        wrappedChatComponent.setJson(json);
                                        event.getPacket().getChatComponents().write(0,wrappedChatComponent);
                                    }
                                }
                            }
                        }
                    }
                });
    }
    public void add(UUID uuid) {
        prevent.add(uuid);
    }
    public void remove(UUID uuid) {
        prevent.remove(uuid);
    }
}
