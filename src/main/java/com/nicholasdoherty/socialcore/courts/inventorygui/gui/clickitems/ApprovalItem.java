package com.nicholasdoherty.socialcore.courts.inventorygui.gui.clickitems;

import com.nicholasdoherty.socialcore.courts.Courts;
import com.nicholasdoherty.socialcore.courts.inventorygui.ClickItem;
import com.nicholasdoherty.socialcore.courts.inventorygui.InventoryView;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.utils.TextUtil;
import com.nicholasdoherty.socialcore.utils.VoxStringUtils;
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
public class ApprovalItem implements ClickItem {
    protected  InventoryView inventoryView;
    protected  ApprovedCitizen approvedCitizen;

    public ApprovalItem(InventoryView inventoryView, ApprovedCitizen approvedCitizen) {
        this.inventoryView = inventoryView;
        this.approvedCitizen = approvedCitizen;
    }

    @Override
    public void click(boolean right, final boolean shift) {
        boolean approve = !right;
        UUID uuid = inventoryView.getInventoryGUI().getPlayer().getUniqueId();
        if (!approvedCitizen.hasVoted(uuid)) {
           Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                for (ItemStack itemStack : Courts.getCourts().getCourtsConfig().getCourtVoteReward()) {
                    if (itemStack != null) {
                        p.getInventory().addItem(itemStack);
                    }
                }
            }
        }

        if (approve) {
            approvedCitizen.approve(uuid);
        }else {
            approvedCitizen.disapprove(uuid);
        }
        if (approvedCitizen instanceof Judge) {
            Judge judge = (Judge) approvedCitizen;
            Courts.getCourts().getJudgeManager().update(judge);
        }

        inventoryView.update();
        inventoryView.update(this);
    }

    @Override
    public ItemStack itemstack() {
        ItemStack head = new ItemStack(Material.SKULL_ITEM);
        head.setDurability((short) 3);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwner(approvedCitizen.getName());
        skullMeta.setDisplayName(ChatColor.GOLD + approvedCitizen.getName());
        skullMeta.setLore(lore());
        head.setItemMeta(skullMeta);
        return head;
    }
    protected List<String> lore() {
        List<String> lore = new ArrayList<>();
        UUID uuid = inventoryView.getInventoryGUI().getPlayer().getUniqueId();
        boolean hasVoted = approvedCitizen.hasVoted(uuid);
        if (approvedCitizen instanceof Judge) {
            Judge judg = Courts.getCourts().getJudgeManager().getJudge(approvedCitizen.getUuid());
            if (judg != null) {
                Set<Secretary> secretarySet = judg.getSecretaries();
                lore.add(ChatColor.YELLOW + "Secretaries: ");
                lore.addAll(VoxStringUtils.splitLoreFormat(VoxStringUtils.formatToString(VoxStringUtils.toStringList(secretarySet, new VoxStringUtils.ToStringConverter() {
                    @Override
                    public String convertToString(Object o) {
                        if (o == null) {
                            return "";
                        }
                        if (o instanceof Citizen) {
                            return ChatColor.WHITE + ((Citizen) o).getName();
                        }
                        return o.toString();
                    }
                }))));
            }
        }
        lore.add("");
        String approvalRating = ChatColor.GREEN + "Approval Rating: " + TextUtil.formatDouble(approvedCitizen.approvalPercentage(), 2) + "%";
        lore.add(approvalRating);
        lore.add("");
        if (hasVoted) {
            String voteString;
            boolean vote = approvedCitizen.vote(uuid);
            if (vote) {
                voteString = ChatColor.GREEN + "You have approved " + approvedCitizen.getName();
            }else {
                voteString = ChatColor.RED + "You have disapproved " + approvedCitizen.getName();
            }
            lore.add(voteString);
        }
        lore.add(ChatColor.GRAY + "<Left Click To Approve>");
        lore.add(ChatColor.GRAY + "<Right Click To Disapprove>");

        return lore;
    }

}
