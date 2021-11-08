package com.nicholasdoherty.socialcore.utils;

import com.nicholasdoherty.socialcore.SocialCore;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.gestern.gringotts.api.impl.VaultConnector;

public class VaultUtil {
    static SocialCore pl;
    //static Eco bank;

    public VaultUtil(){
    }

    public static void setupVaultUtil(SocialCore plugin){
        pl = plugin;
    }

    public static boolean charge(OfflinePlayer p, double amount) {
        if (p instanceof Player && ((Player)p).hasPermission("courts.nopay")) {
            return false;
        }

        VaultConnector vc = new VaultConnector();


        try {
            if(vc.hasAccount(p.getName())){
                vc.withdrawPlayer(p.getName(), amount);
            }
            return true;
            //return result == TransactionResult.SUCCESS;
            //Economy.substract(p.getName(), BigDecimal.valueOf(amount));
        //} catch (NoLoanPermittedException ex){
        //    SocialCore.getStaticLogger().severe("User does not have enough money, unable to complete charge for SC VaultUtil.");
        //} catch (UserDoesNotExistException uEx){
        //    SocialCore.getStaticLogger().severe("User does not exist, unable to complete charge for SC VaultUtil.");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean give(OfflinePlayer p, double amount) {
        if (p instanceof Player && ((Player)p).hasPermission("courts.nopay")) {
            return false;
        }
        VaultConnector vc = new VaultConnector();


        try {
            if(vc.hasAccount(p.getName())){
                vc.depositPlayer(p.getName(), amount);
            }
            return true;
            //return result == TransactionResult.SUCCESS;
            //Economy.add(p.getName(), BigDecimal.valueOf(amount));
        //} catch (NoLoanPermittedException ex){
        //    SocialCore.getStaticLogger().severe("User does not have enough money, unable to complete charge for SC VaultUtil.");
        //} catch (UserDoesNotExistException uEx){
        //    SocialCore.getStaticLogger().severe("User does not exist, unable to complete charge for SC VaultUtil.");
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean transfer(OfflinePlayer p1, OfflinePlayer p2, double amount) {
        if (p1 instanceof Player && ((Player)p1).hasPermission("courts.nopay")) {
            return false;
        }

        boolean took = charge(p1, amount);
        if (!took) {
            return false;
        } else {
            boolean gave = give(p2, amount);
            if (!gave) {
                boolean gaveBack = give(p1, amount);
                if (!gaveBack) {
                    SocialCore.getStaticLogger().severe("Failed to give " + p1.getName() + " back " + amount + " from SC VaultUtil.");
                }
                return false;
            } else {
                return true;
            }
        }
    }

    public static void addPermission(final PermissionAttachment permissionAttachment, String perm) {
        boolean value = true;
        if(perm.contains("-")) {
            value = false;
            perm = perm.replace("-", "");
        }
        permissionAttachment.setPermission(perm, value);
    }
}