package com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.components.courts.judges.gui.approvalgui.JudgesApprovalGUI;
import com.nicholasdoherty.socialcore.utils.NameLookup;
import com.voxmc.voxlib.VoxTimeUnit;
import com.voxmc.voxlib.util.UUIDUtil;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CourtPlayerInfoCmd {
    private final CommandSender commandSender;
    private final String[] args;

    public CourtPlayerInfoCmd(CommandSender commandSender, String[] args) {
        this.commandSender = commandSender;
        this.args = args;
    }

    public boolean runCommand(){
        Bukkit.getScheduler().runTaskAsynchronously(SocialCore.getPlugin(), () -> {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(ChatColor.RED + "You must be a player.");
                return;
            }
            if(args.length != 1){
                commandSender.sendMessage(ChatColor.RED + "You must pass a player name! Format: /court playerinfo <name>");
                return;
            }

            OfflinePlayer op = Bukkit.getOfflinePlayer(UUIDUtil.getUUID(args[0]));

            if (!op.hasPlayedBefore()){
                commandSender.sendMessage(ChatColor.RED + "Looks like player " + args[0] + " has never played before on this server!");
                return;
            }

            Date d = new Date();
            d.setTime(op.getLastPlayed());
            NameLookup.PreviousPlayerNameEntry[] previousNames = new NameLookup.PreviousPlayerNameEntry[0];

            try {
                previousNames = NameLookup.getPlayerPreviousNames(op.getUniqueId());
            } catch (IOException e) {
                e.printStackTrace();
            }

            commandSender.sendMessage(ChatColor.GOLD  + "" + ChatColor.BOLD + "Player Information");
            commandSender.sendMessage(ChatColor.YELLOW + "Last online " + ChatColor.GRAY + " - " + ChatColor.GREEN + new SimpleDateFormat("hh:mm MMM dd, yyyy").format(d));
            commandSender.sendMessage(ChatColor.GOLD + "Previous Names " + ChatColor.GRAY + ":");
            if(previousNames.length == 1 && previousNames[0].getChangeTime().equalsIgnoreCase("none")) {
                commandSender.sendMessage(ChatColor.GREEN + "None, this player has never changed usernames!");
            } else if(previousNames.length > 0) {
                for(NameLookup.PreviousPlayerNameEntry entry : previousNames) {
                    commandSender.sendMessage(ChatColor.GREEN + entry.getPlayerName() + ChatColor.GRAY + " - ("+ ChatColor.GREEN + entry.getChangeTime() + ChatColor.GRAY + ")");
                }
            } else {
                commandSender.sendMessage(ChatColor.GREEN + "None, this player has never changed usernames!");
            }

        });
        return true;
    }
}
