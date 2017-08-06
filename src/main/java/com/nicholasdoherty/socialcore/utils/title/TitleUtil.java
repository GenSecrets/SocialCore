package com.nicholasdoherty.socialcore.utils.title;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/14/15.
 */
public class TitleUtil {
    
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        if(title != null) {
            try {
                title = TellRawConverterLite.convertToJSON(title);
                IChatBaseComponent titleJSON = IChatBaseComponent.ChatSerializer.a(title);
                PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
                connection.sendPacket(titlePacket);
            } catch(Exception e) {
            
            }
        }
        if(subtitle != null) {
            try {
                subtitle = TellRawConverterLite.convertToJSON(subtitle);
                IChatBaseComponent subtitleJSON = IChatBaseComponent.ChatSerializer.a(subtitle);
                PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON);
                connection.sendPacket(subtitlePacket);
            } catch(Exception e) {
            
            }
        }
    }
}
