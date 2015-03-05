package com.nicholasdoherty.socialcore.utils.title;

import net.minecraft.server.v1_8_R1.*;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by john on 1/14/15.
 */
public class TitleUtil {

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        if (title != null) {
            try {
                title = TellRawConverterLite.convertToJSON(title);
                IChatBaseComponent titleJSON = ChatSerializer.a(title);
                PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
                connection.sendPacket(titlePacket);
            }catch (Exception e) {

            }
        }
        if (subtitle != null) {
            try {
            subtitle = TellRawConverterLite.convertToJSON(subtitle);
            IChatBaseComponent subtitleJSON = ChatSerializer.a(subtitle);
            PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);
            connection.sendPacket(subtitlePacket);
        }catch (Exception e) {

        }
        }
    }
}
