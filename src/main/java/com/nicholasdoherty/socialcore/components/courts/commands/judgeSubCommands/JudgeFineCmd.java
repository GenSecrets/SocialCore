package com.nicholasdoherty.socialcore.components.courts.commands.judgeSubCommands;

import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.commands.JudgeCommandHandler;
import com.nicholasdoherty.socialcore.components.courts.courtroom.CourtSession;
import com.nicholasdoherty.socialcore.components.courts.judges.Judge;
import com.nicholasdoherty.socialcore.components.courts.judges.JudgeManager;
import com.nicholasdoherty.socialcore.utils.VaultUtil;
import com.voxmc.voxlib.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JudgeFineCmd {
    private final Courts courts;
    private final CommandSender commandSender;
    private final JudgeManager judgeManager;
    private final String[] args;

    public JudgeFineCmd(Courts courts, CommandSender commandSender, JudgeManager judgeManager, String[] args) {
        this.courts = courts;
        this.commandSender = commandSender;
        this.judgeManager = judgeManager;
        this.args = args;
    }

    public boolean runCommand() {
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player.");
            return true;
        }
        final Player p = (Player) commandSender;
        final Judge judge = judgeManager.getJudge(p.getUniqueId());

        if(judge == null) {
            p.sendMessage(ChatColor.RED + "You are not a judge.");
            return true;
        }

        CourtSession courtSession = null;
        for(final CourtSession courtSession1 : courts.getCourtSessionManager().getInSession()) {
            if(courtSession1.getJudge() != null && courtSession1.getJudge().equals(judge)) {
                courtSession = courtSession1;
                break;
            }
        }
        if(args.length == 0 || courtSession == null) {
            if(args.length >= 1) {
                p.sendMessage(courts.getCourtsLangManager().getCourtNotInSession());
            }
            JudgeCommandHandler.sendJudgeHelp(commandSender);
            return true;
        }

        final String name = args[0];
        final UUID uuid = UUIDUtil.getUUID(name);
        if(uuid == null) {
            p.sendMessage(ChatColor.RED + "Could not find player: " + name);
            return true;
        }
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if(offlinePlayer == null) {
            p.sendMessage(ChatColor.RED + "Could not find player: " + name);
            return true;
        }
        double amount = 0;
        try {
            amount = Math.round(Double.parseDouble(args[1]));
        } catch(final Exception e) {
            p.sendMessage(ChatColor.RED + "Failed to parse amount: " + args[1]);
        }
        final double maxFine = Courts.getCourts().getCourtsConfig().getMaxFine();
        if(amount > maxFine) {
            p.sendMessage(ChatColor.RED + "Could not fine because the maximum fine allowed is " + maxFine);
            return true;
        }
        boolean didIt = false;
        try {
            didIt = VaultUtil.charge(offlinePlayer, amount);
        } catch(final Exception e) {
            e.printStackTrace();
        }
        if(didIt) {
            p.sendMessage(ChatColor.GREEN + "Fined " + args[0] + ' ' + amount + " voxels.");
        } else {
            p.sendMessage(ChatColor.RED + "Could not fine " + args[0] + ' ' + amount + " voxels.");
        }
        Courts.getCourts().getPlugin().getLogger().info("Judge " + judge.getName() + " fined " + args[0] + ' ' + amount + " voxels.");
        return true;
    }
}
