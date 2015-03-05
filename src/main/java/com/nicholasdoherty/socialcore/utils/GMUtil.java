package com.nicholasdoherty.socialcore.utils;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Created by john on 3/2/15.
 */
public class GMUtil {
    public static void addPermission(String name, String perm) {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        final Plugin GMplugin = pluginManager.getPlugin("GroupManager");
        if (GMplugin == null)
            return;
        GroupManager groupManager = (GroupManager) GMplugin;
        for (World world : Bukkit.getWorlds()) {
            OverloadedWorldHolder dataHolder = groupManager.getWorldsHolder().getWorldData(world.getName());
            if (dataHolder != null) {
                User user = dataHolder.getUser(name);
                if (user != null) {
                    user.addPermission(perm);
                }
            }
        }
    }
}
