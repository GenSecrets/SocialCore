package com.nicholasdoherty.socialcore.utils;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Created by john on 2/15/15.
 */
public class VaultUtil {
    private static Economy econ;
    private static Chat chat;
    public static void setup(Server server) throws NotSetupException {
        if (server.getPluginManager().getPlugin("Vault") == null) {
            throw new NotSetupException();
        }
        RegisteredServiceProvider<Economy> rsp = server.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new NotSetupException();
        }
        econ = rsp.getProvider();
        RegisteredServiceProvider<Chat> rspc = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (rspc == null) {
            throw new NotSetupException();
        }
        chat = rspc.getProvider();
    }
    public static String getPrefix(Player p) {
        if (chat == null) {
            return "";
        }
        return chat.getPlayerPrefix(p);
    }
    public static void setPrefix(Player p, String prefix) {
        chat.setPlayerPrefix(p,prefix);
    }
    public static boolean charge(OfflinePlayer p, double amount) throws NotSetupException {
        if (p instanceof Player && ((Player) p).hasPermission("courts.nopay")) {
            return true;
        }
        if (econ == null) {
            throw new NotSetupException();
        }
        EconomyResponse economyResponse = econ.withdrawPlayer(p,amount);
        return economyResponse.transactionSuccess();
    }
    public static boolean give(OfflinePlayer p, double amount) throws NotSetupException {
        if (p instanceof Player && ((Player) p).hasPermission("courts.nopay")) {
            return true;
        }
        if (econ == null) {
            throw new NotSetupException();
        }

        EconomyResponse economyResponse = econ.depositPlayer(p,amount);
        return economyResponse.transactionSuccess();
    }

    public static boolean transfer(OfflinePlayer p1, OfflinePlayer p2, double amount) throws NotSetupException {
        if (p1 instanceof Player && ((Player) p1).hasPermission("courts.nopay")) {
            return true;
        }
        if (econ == null) {
            throw new NotSetupException();
        }
        boolean took = charge(p1,amount);
        if (!took)
            return false;
        boolean gave = give(p2,amount);
        if (!gave) {
            boolean gaveBack = give(p1,amount);
            if (!gaveBack) {
                System.out.println("FAILED TO GIVE " + p1.getName() + " back " + amount);
            }
            return false;
        }
        return true;
    }
    public static void givePermission(OfflinePlayer p, String permission) {
        RegisteredServiceProvider<Permission>permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        Permission pp = permissionProvider.getProvider();
        pp.playerAdd(null,p,permission);
    }
    public static class NotSetupException extends Exception {

    }
}
