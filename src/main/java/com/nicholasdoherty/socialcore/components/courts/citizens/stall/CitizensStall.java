package com.nicholasdoherty.socialcore.components.courts.citizens.stall;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.cases.Case;
import com.nicholasdoherty.socialcore.components.courts.cases.CaseManager;
import com.nicholasdoherty.socialcore.components.courts.stall.Stall;
import com.nicholasdoherty.socialcore.components.courts.stall.StallType;
import com.nicholasdoherty.socialcore.utils.VaultUtil;
import com.voxmc.voxlib.VLocation;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Created by john on 1/6/15.
 */
public class CitizensStall extends Stall {
    
    Set<UUID> firstClicks = new HashSet<>();
    Map<UUID, BukkitTask> timeoutRemove = new HashMap<>();
    Set<UUID> onCooldown = new HashSet<>();
    
    public CitizensStall(final int id, final VLocation vLocation) {
        super(id, StallType.CITIZEN, vLocation);
    }
    
    @Override
    public void onClick(final Player p) {
        final CaseManager caseManager = Courts.getCourts().getCaseManager();
        final UUID uuid = p.getUniqueId();
        final ItemStack itemInHand = p.getInventory().getItemInMainHand();
        if(onCooldown.contains(p.getUniqueId())) {
            p.sendMessage(Courts.getCourts().getCourtsLangManager().getCitizenCooldown());
            return;
        }
        if(p.getInventory().getItemInMainHand() != null) {
            final ItemStack item = p.getInventory().getItemInMainHand();
            if(Case.isCaseBook(item)) {
                if(isBookEmpty(itemInHand)) {
                    p.sendMessage(getCaseBookEmpty());
                    return;
                }
                if(!Case.isEmptyCaseBook(item)) {
                    p.sendMessage(getCaseAlreadyFiled());
                    return;
                }
                final Case caze = caseManager.newCase(item, p.getName());
                caze.setPlantiff(Courts.getCourts().getCitizenManager().toCitizen(p));
                caze.updateSave();
                p.getInventory().setItemInMainHand(null);
                p.sendMessage(getCaseSubmitted());
                return;
            }
        }
        final int cost = Courts.getCourts().getCourtsConfig().getCaseFilingCost();
        if(!firstClicks.contains(uuid)) {
            p.sendMessage(getConfirmMessage(cost));
            firstClicks.add(uuid);
            final BukkitTask removeTask = new BukkitRunnable() {
                @Override
                public void run() {
                    firstClicks.remove(uuid);
                }
            }.runTaskLater(Courts.getCourts().getPlugin(), 100);
            timeoutRemove.put(uuid, removeTask);
        } else {
            if(timeoutRemove.containsKey(uuid)) {
                timeoutRemove.get(uuid).cancel();
                timeoutRemove.remove(uuid);
                firstClicks.remove(uuid);
            }
            try {
                if(!VaultUtil.charge(p, cost)) {
                    p.sendMessage(ChatColor.RED + "Failed to charge you " + cost + " voxels.");
                    return;
                }
            } catch(final Exception e) {
                p.sendMessage(ChatColor.RED + "Failed to charge you " + cost + " voxels.");
                e.printStackTrace();
                return;
            }
            final ItemStack copyOfBook = Case.baseItemStack();
            p.getInventory().addItem(copyOfBook);
            p.updateInventory();
            p.sendMessage(getGivedMessage());
            final long cooldown = Courts.getCourts().getCourtsConfig().getCitizenStallDocumentCooldown();
            onCooldown.add(uuid);
            new BukkitRunnable() {
                @Override
                public void run() {
                    onCooldown.remove(uuid);
                }
            }.runTaskLater(Courts.getCourts().getPlugin(), cooldown);
        }
    }
    
    private String getGivedMessage() {
        return Courts.getCourts().getCourtsLangManager().getCitizenGaveCourtDocuments();
    }
    
    private String getConfirmMessage(final int cost) {
        return Courts.getCourts().getCourtsLangManager().getCitizenConfirm().replace("{cost}", cost + "");
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
    
    private boolean isBookEmpty(final ItemStack book) {
        final BookMeta bookMeta = (BookMeta) book.getItemMeta();
        if(bookMeta.getPages().isEmpty()) {
            return true;
        }
        for(final String page : bookMeta.getPages()) {
            if(page.length() > 3) {
                return false;
            }
        }
        return true;
    }
}

