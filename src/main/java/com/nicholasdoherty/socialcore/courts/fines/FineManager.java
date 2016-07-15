package com.nicholasdoherty.socialcore.courts.fines;

import com.nicholasdoherty.socialcore.courts.Courts;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by john on 2/15/15.
 */
public class FineManager {
    Set<Fine> fines;
    Map<UUID,List<Fine>> finesBySenderUUID;

    public FineManager(Set<Fine> fines) {
        this.fines = fines;
        finesBySenderUUID = new HashMap<>();
    }

    public void startTimer() {
        long interval = Courts.getCourts().getCourtsConfig().getFinePaymentInterval();
        new BukkitRunnable(){
            @Override
            public void run() {
                processFines();
            }
        }.runTaskTimer(Courts.getCourts().getPlugin(),interval,interval);
    }
    public void addFine(Fine fine) {
        fines.add(fine);
        if (!finesBySenderUUID.containsKey(fine.getSender().getUuid())) {
            finesBySenderUUID.put(fine.getSender().getUuid(), new ArrayList<Fine>());
        }
        finesBySenderUUID.get(fine.getSender().getUuid()).add(fine);
    }
    public void removeFine(Fine fine) {
        if (fines.contains(fine)) {
            fines.remove(fine);
        }
        if (finesBySenderUUID.containsKey(fine.getSender().getUuid())) {
            finesBySenderUUID.get(fine.getSender().getUuid()).remove(fine);
            if (finesBySenderUUID.get(fine.getSender().getUuid()).isEmpty()) {
                finesBySenderUUID.remove(fine.getSender().getUuid());
            }
        }
        Courts.getCourts().getSqlSaveManager().removeFine(fine);
    }
    public void processFines() {
        if (fines.isEmpty())
            return;
        List<Fine> toRemove = null;
        double percent = Courts.getCourts().getCourtsConfig().getFinePaymentPercentage();
        for (Fine fine : fines) {
            double toPay = Math.floor(fine.getAmount()*(percent/100));
            fine.pay(toPay);
            if (fine.isPaid()) {
                if (toRemove == null) {
                    toRemove = new ArrayList<>();
                }
                toRemove.add(fine);
            }
        }
        if (toRemove != null) {
            for (Fine fine : toRemove) {
                removeFine(fine);
            }
        }
    }
}
