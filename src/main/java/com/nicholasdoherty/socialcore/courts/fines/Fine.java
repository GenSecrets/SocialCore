package com.nicholasdoherty.socialcore.courts.fines;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.VaultUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Created by john on 2/15/15.
 */
public class Fine {
    private int id;
    private Citizen sender,rec;
    private double amount;
    private double amountPaid;

    public Fine(int id, Citizen sender, Citizen rec, double amount) {
        this.id = id;
        this.sender = sender;
        this.rec = rec;
        this.amount = amount;
    }

    public Fine(int id, Citizen sender, Citizen rec, double amount, double amountPaid) {
        this.id = id;
        this.sender = sender;
        this.rec = rec;
        this.amount = amount;
        this.amountPaid = amountPaid;
    }

    public int getId() {
        return id;
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
        amountToPay = Math.floor(amountToPay);
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
        if (transfered) {
             amountPaid += amountToPay;
        }
        Courts.getCourts().getSqlSaveManager().updateFine(this);
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

}
