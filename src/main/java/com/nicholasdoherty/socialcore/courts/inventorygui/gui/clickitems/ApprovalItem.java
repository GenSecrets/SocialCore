package com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.voxmc.voxlib.util.TextUtil;
import com.voxmc.voxlib.util.VoxStringUtils;
import com.voxmc.voxlib.gui.inventorygui.ClickItem;
import com.voxmc.voxlib.gui.inventorygui.InventoryView;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by john on 1/6/15.
 */
@SuppressWarnings("unused")
public class ApprovalItem implements ClickItem {
    protected InventoryView inventoryView;
    protected ApprovedCitizen approvedCitizen;
    
    public ApprovalItem(final InventoryView inventoryView, final ApprovedCitizen approvedCitizen) {
        this.inventoryView = inventoryView;
        this.approvedCitizen = approvedCitizen;
    }
    
    @Override
    public void click(final boolean right, final boolean shift) {
        final boolean approve = !right;
        final UUID uuid = inventoryView.getInventoryGUI().getPlayer().getUniqueId();
        if(!approvedCitizen.hasVoted(uuid)) {
            final Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) {
                for(final ItemStack itemStack : Courts.getCourts().getCourtsConfig().getCourtVoteReward()) {
                    if(itemStack != null) {
                        p.getInventory().addItem(itemStack);
                    }
                }
            }
        }
        
        if(approve) {
            approvedCitizen.approve(uuid);
        } else {
            approvedCitizen.disapprove(uuid);
        }
        if(approvedCitizen instanceof Judge) {
            final Judge judge = (Judge) approvedCitizen;
            Courts.getCourts().getJudgeManager().update(judge);
        }
        
        inventoryView.update();
        inventoryView.update(this);
    }
    
    @Override
    public ItemStack itemstack() {
        final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        head.setDurability((short) 3);
        final SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwner(approvedCitizen.getName());
        skullMeta.setDisplayName(ChatColor.GOLD + approvedCitizen.getName());
        skullMeta.setLore(lore());
        head.setItemMeta(skullMeta);
        return head;
    }
    
    protected List<String> lore() {
        final List<String> lore = new ArrayList<>();
        final UUID uuid = inventoryView.getInventoryGUI().getPlayer().getUniqueId();
        final boolean hasVoted = approvedCitizen.hasVoted(uuid);
        if(approvedCitizen instanceof Judge) {
            final Judge judg = Courts.getCourts().getJudgeManager().getJudge(approvedCitizen.getUuid());
            if(judg != null) {
                final Set<Secretary> secretarySet = judg.getSecretaries();
                lore.add(ChatColor.YELLOW + "Secretaries: ");
                lore.addAll(VoxStringUtils.splitLoreFormat(VoxStringUtils.formatToString(VoxStringUtils.toStringList(secretarySet, o -> {
                    if(o == null) {
                        return "";
                    }
                    if(o instanceof Citizen) {
                        return ChatColor.WHITE + ((Citizen) o).getName();
                    }
                    return o.toString();
                }))));
            }
        }
        lore.add("");
        final String approvalRating = ChatColor.GREEN + "Approval Rating: " + TextUtil.formatDouble(approvedCitizen.approvalPercentage(), 2) + '%';
        lore.add(approvalRating);
        lore.add("");
        if(hasVoted) {
            final String voteString;
            final boolean vote = approvedCitizen.vote(uuid);
            if(vote) {
                voteString = ChatColor.GREEN + "You have approved " + approvedCitizen.getName();
            } else {
                voteString = ChatColor.RED + "You have disapproved " + approvedCitizen.getName();
            }
            lore.add(voteString);
        }
        lore.add(ChatColor.GRAY + "<Left Click To Approve>");
        lore.add(ChatColor.GRAY + "<Right Click To Disapprove>");
        
        return lore;
    }
}
