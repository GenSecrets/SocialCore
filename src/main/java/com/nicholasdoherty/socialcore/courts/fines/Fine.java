package com.nicholasdoherty.socialcore.courts.fines;

import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.VaultUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by john on 2/15/15.
 */
public class Fine implements ConfigurationSerializable{
    private Citizen sender,rec;
    private double amount;
    private double amountPaid;

    public Fine(Citizen sender, Citizen rec, double amount) {
        this.sender = sender;
        this.rec = rec;
        this.amount = amount;
    }
    public Fine(Map<String, Object> map) {
        this.sender = (Citizen) map.get("sender");
        this.rec = (Citizen) map.get("rec");
        this.amount = (double) map.get("amount");
        this.amountPaid = (double) map.get("amount-paid");
    }
    public boolean isPaid() {
        return amountPaid >= amount;
    }
    public double amountLeft() {
        return Math.max(0,amount-amountPaid);
    }
    public boolean pay(double amountToPay) {
        double amountLeft = amountLeft();
        if (amountToPay > amountLeft) {
            amountToPay = amountLeft;
        }
        OfflinePlayer senderO = Bukkit.getOfflinePlayer(sender.getUuid());
        if (rec == null) {
            try {
                return VaultUtil.charge(senderO,amountToPay);
            } catch (VaultUtil.NotSetupException e) {
                e.printStackTrace();
                return false;
            }
        }
        OfflinePlayer recO = Bukkit.getOfflinePlayer(rec.getUuid());
        if (senderO == null || recO == null)
            return false;
        boolean transfered;
        try {
            transfered = VaultUtil.transfer(senderO,recO,amountToPay);
        } catch (VaultUtil.NotSetupException e) {
            e.printStackTrace();
            transfered = false;
        }
        return transfered;
    }
    public Citizen getSender() {
        return sender;
    }

    public Citizen getRec() {
        return rec;
    }

    public double getAmount() {
        return amount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("sender",sender);
        map.put("rec",rec);
        map.put("amount",amount);
        map.put("amount-paid",amountPaid);
        return map;
    }
}
