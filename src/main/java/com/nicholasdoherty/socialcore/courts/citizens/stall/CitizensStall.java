package com.nicholasdoherty.socialcore.courts.citizens.stall;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.cases.Case;
import com.nicholasdoherty.socialcore.courts.cases.CaseManager;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.courts.stall.Stall;
import com.nicholasdoherty.socialcore.courts.stall.StallType;
import com.nicholasdoherty.socialcore.utils.VLocation;
import com.nicholasdoherty.socialcore.utils.VaultUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Created by john on 1/6/15.
 */
public class CitizensStall extends Stall implements ConfigurationSerializable {

    public CitizensStall( VLocation vLocation) {
        super(StallType.CITIZEN, vLocation);
    }

    Set<UUID> firstClicks = new HashSet<>();
    Map<UUID, BukkitTask> timeoutRemove = new HashMap<>();

    Set<UUID> onCooldown = new HashSet<>();
    @Override
    public void onClick(Player p) {
        CaseManager caseManager = Courts.getCourts().getCaseManager();
        final UUID uuid = p.getUniqueId();
        ItemStack itemInHand = p.getItemInHand();
        if (onCooldown.contains(p.getUniqueId())) {
            p.sendMessage(Courts.getCourts().getCourtsLangManager().getCitizenCooldown());
            return;
        }
        if (p.getItemInHand() != null) {
            ItemStack item = p.getItemInHand();
            if (Case.isCaseBook(item)) {
                if (isBookEmpty(itemInHand)) {
                    p.sendMessage(getCaseBookEmpty());
                    return;
                }
                if (!Case.isEmptyCaseBook(item)) {
                    p.sendMessage(getCaseAlreadyFiled());
                    return;
                }
                Case caze = caseManager.newCase(item,p.getName());
                caze.setPlantiff(new Citizen(p));
                p.setItemInHand(null);
                p.sendMessage(getCaseSubmitted());
                return;
            }
        }
        int cost = Courts.getCourts().getCourtsConfig().getCaseFilingCost();
        if (!firstClicks.contains(uuid)) {
            p.sendMessage(getConfirmMessage(cost));
            firstClicks.add(uuid);
            BukkitTask removeTask = new BukkitRunnable(){
                @Override
                public void run() {
                    firstClicks.remove(uuid);
                }
            }.runTaskLater(Courts.getCourts().getPlugin(),300);
            timeoutRemove.put(uuid,removeTask);
        }else {
            if (timeoutRemove.containsKey(uuid)) {
                timeoutRemove.get(uuid).cancel();
                timeoutRemove.remove(uuid);
                firstClicks.remove(uuid);
            }
            try {
                if (!VaultUtil.charge(p,cost)) {
                    p.sendMessage(ChatColor.RED + "Failed to charge you " + cost + " voxels.");
                    return;
                }
            } catch (VaultUtil.NotSetupException e) {
                p.sendMessage(ChatColor.RED + "Failed to charge you " + cost + " voxels.");
                e.printStackTrace();
                return;
            }
            ItemStack copyOfBook = Case.baseItemStack();
            p.getInventory().addItem(copyOfBook);
            p.updateInventory();
            p.sendMessage(getGivedMessage());
            long cooldown = Courts.getCourts().getCourtsConfig().getCitizenStallDocumentCooldown();
            onCooldown.add(uuid);
            new BukkitRunnable(){
                @Override
                public void run() {
                    onCooldown.remove(uuid);
                }
            }.runTaskLater(Courts.getCourts().getPlugin(),cooldown);
        }
    }

    private String getGivedMessage() {
        return Courts.getCourts().getCourtsLangManager().getCitizenGaveCourtDocuments();
    }

    private String getConfirmMessage(int cost) {
        return Courts.getCourts().getCourtsLangManager().getCitizenConfirm().replace("{cost}",cost+"");
    }

    private String getCaseSubmitted() {
        return Courts.getCourts().getCourtsLangManager().getCitizenSubmitted();
    }

    private String getCaseAlreadyFiled() {
        return Courts.getCourts().getCourtsLangManager().getCitizenCaseAlreadyFiled();
    }

    private String getCaseBookEmpty() {
        return Courts.getCourts().getCourtsLangManager().getCitizenCaseBookEmpty();
    }

    private boolean isBookEmpty(ItemStack book) {
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        if (bookMeta.getPages().size() == 0)
            return true;
        for (String page : bookMeta.getPages()) {
            if (page.length() > 3) {
                return false;
            }
        }
        return true;
    }
    public CitizensStall(Map<String, Object> map) {
        super(map);
    }
}
