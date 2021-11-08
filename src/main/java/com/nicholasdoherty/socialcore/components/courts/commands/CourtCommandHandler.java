package com.nicholasdoherty.socialcore.components.courts.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.components.courts.Courts;
import com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.*;
import com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.*;
import com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.election.*;
import com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.stall.CourtStallCreateCmd;
import com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.admin.stall.CourtStallRemoveCmd;
import com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.election.CourtElectionRunCmd;
import com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.election.CourtElectionVoteCmd;
import com.nicholasdoherty.socialcore.components.courts.commands.courtSubCommands.secretary.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@CommandAlias("court")
@CommandPermission("socialcore.courts")
@Description("Allow a player to use the commands for the court system.")
public class CourtCommandHandler extends BaseCommand {
    private Courts courts;
    private SocialCore sc;

    public CourtCommandHandler(Courts courts, SocialCore sc) {
        this.courts = courts;
        this.sc = sc;
    }

    @Default
    @CommandAlias("help")
    public boolean onCourtCommand(final CommandSender commandSender){
        if(commandSender.hasPermission("socialcore.courts.judge"))
            return sendCourtHelp(commandSender, false, true);
        if(commandSender.hasPermission("socialcore.courts.admin"))
            return sendCourtHelp(commandSender, true, false);
        return sendCourtHelp(commandSender, false, false);
    }


    @Subcommand("officials")
    @CommandPermission("socialcore.courts.officials")
    public boolean onCourtOfficials(final CommandSender sender, final String[] args) {
        CourtOfficialsCmd cmd = new CourtOfficialsCmd(sender, courts.getJudgeManager(), args);
        return cmd.runCommand();
    }


    @Subcommand("policies")
    @CommandPermission("socialcore.courts.policies")
    public boolean onCourtPolicies(final CommandSender sender, final String[] args) {
        CourtPoliciesCmd cmd = new CourtPoliciesCmd(courts, courts.getPolicyManager(), sender, args);
        return cmd.runCommand();
    }


    @Subcommand("policy")
    @CommandPermission("socialcore.courts.policy")
    public boolean onCourtPolicy(final CommandSender sender, final String[] args) {
        CourtPolicyCmd cmd = new CourtPolicyCmd(courts, courts.getPolicyManager(), sender, args);
        return cmd.runCommand();
    }

    @Subcommand("tp")
    @CommandPermission("socialcore.courts.tp")
    public boolean onCourtTeleport(final CommandSender sender) {
        CourtTeleportCmd cmd = new CourtTeleportCmd(courts, sender);
        return cmd.runCommand();
    }

    @Subcommand("cases")
    @CommandPermission("socialcore.courts.cases")
    public boolean onCourtMasterCases(final CommandSender sender) {
        CourtMasterCasesCmd cmd = new CourtMasterCasesCmd(sender);
        return cmd.runCommand();
    }

    @Subcommand("playerinfo")
    @CommandPermission("socialcore.courts.playerinfo")
    public boolean onCourtPlayerInfo(final CommandSender sender, final String[] args) {
        CourtPlayerInfoCmd cmd = new CourtPlayerInfoCmd(sender, args);
        return cmd.runCommand();
    }

    @Subcommand("secretary")
    public class SecretaryHandler extends BaseCommand {
        @Subcommand("desk")
        @CommandPermission("socialcore.courts.secretary.desk")
        public boolean onSecretaryTeleport(final CommandSender commandSender){
            SecretaryTeleportCmd cmd = new SecretaryTeleportCmd(courts, commandSender);
            return cmd.runCommand();
        }

        @Subcommand("accept")
        @CommandPermission("socialcore.courts.secretary.accept")
        public boolean onSecretaryAccept(final CommandSender commandSender){
            SecretaryAcceptCmd cmd = new SecretaryAcceptCmd(courts, courts.getJudgeManager(), commandSender);
            return cmd.runCommand();
        }

        @Subcommand("deny")
        @CommandPermission("socialcore.courts.secretary.deny")
        public boolean onSecretaryDeny(final CommandSender commandSender){
            SecretaryDenyCmd cmd = new SecretaryDenyCmd(courts, courts.getJudgeManager(), commandSender);
            return cmd.runCommand();
        }

        @Subcommand("add")
        @CommandCompletion("@players")
        @CommandPermission("socialcore.courts.judge.secretary.add")
        public boolean onSecretaryAdd(final CommandSender commandSender, final String[] args){
            SecretaryAddCmd cmd = new SecretaryAddCmd(courts, commandSender, courts.getJudgeManager(), args);
            return cmd.runCommand();
        }

        @Subcommand("remove")
        @CommandPermission("socialcore.courts.judge.secretary.remove")
        public boolean onSecretaryRemove(final CommandSender commandSender, final String[] args){
            SecretaryRemoveCmd cmd = new SecretaryRemoveCmd(courts, commandSender, courts.getJudgeManager(), args);
            return cmd.runCommand();
        }

        @Subcommand("list")
        @CommandPermission("socialcore.courts.judge.secretary.list")
        public boolean onSecretaryList(final CommandSender commandSender){
            SecretaryListCmd cmd = new SecretaryListCmd(commandSender, courts.getJudgeManager());
            return cmd.runCommand();
        }
    }

    @Subcommand("election")
    @CommandPermission("socialcore.courts.election")
    public class CourtElectionHandler extends BaseCommand {

        @Default
        public boolean onCourtElection(final CommandSender commandSender){
            return sendElectionHelp(commandSender, false);
        }

        @Subcommand("vote")
        @CommandAlias("status|list")
        @CommandPermission("socialcore.courts.election.vote")
        public boolean onCourtElectionVote(final CommandSender sender) {
            CourtElectionVoteCmd cmd = new CourtElectionVoteCmd(courts, sender, courts.getElectionManager());
            return cmd.runCommand();
        }

        @Subcommand("run")
        @CommandPermission("socialcore.courts.election.run")
        public boolean onCourtElectionRun(final CommandSender sender) {
            CourtElectionRunCmd cmd = new CourtElectionRunCmd(courts, sender, courts.getElectionManager());
            return cmd.runCommand();
        }
    }


    @Subcommand("admin")
    @CommandPermission("socialcore.courts.admin")
    @Description("Manages all admin commands for the court system.")
    public class CourtAdminHandler extends BaseCommand {

        @Default
        public boolean onCourtAdmin(final CommandSender commandSender) {
            sendAdminHelp(commandSender);
            return true;
        }

        @Subcommand("promote")
        @CommandPermission("socialcore.courts.admin.promote")
        public boolean onCourtAdminPromote(final CommandSender commandSender, final String[] args) {
            CourtPromoteCmd cmd = new CourtPromoteCmd(courts, commandSender, args);
            return cmd.runCommand();
        }

        @Subcommand("demote")
        @CommandPermission("socialcore.courts.admin.demote")
        public boolean onCourtAdminDemote(final CommandSender commandSender, final String[] args) {
            CourtDemoteCmd cmd = new CourtDemoteCmd(courts, commandSender, args);
            return cmd.runCommand();
        }

        @Subcommand("silence")
        @CommandPermission("socialcore.courts.admin.silence")
        public boolean onCourtAdminSilence(final CommandSender commandSender) {
            CourtSilenceCmd cmd = new CourtSilenceCmd(courts, commandSender);
            return cmd.runCommand();
        }

        @Subcommand("tocitizen")
        @CommandPermission("socialcore.courts.admin.tocitizen")
        public boolean onCourtAdminToCitizen(final CommandSender commandSender, final String[] args) {
            CourtToCitizenCmd cmd = new CourtToCitizenCmd(courts, commandSender, args);
            return cmd.runCommand();
        }

        @Subcommand("reload")
        @CommandPermission("socialcore.courts.admin.reload")
        public boolean onCourtAdminReload(final CommandSender commandSender) {
            CourtReloadCmd cmd = new CourtReloadCmd(courts, commandSender);
            return cmd.runCommand();
        }

        @Subcommand("endsessions")
        @CommandPermission("socialcore.courts.admin.endsessions")
        public boolean onCourtAdminEndSessions(final CommandSender commandSender) {
            CourtEndSessionsCmd cmd = new CourtEndSessionsCmd(courts, commandSender);
            return cmd.runCommand();
        }

        @Subcommand("stall")
        @CommandPermission("socialcore.courts.admin.stall")
        public class CourtAdminStallHandler extends BaseCommand {

            @Subcommand("remove")
            @CommandPermission("socialcore.courts.admin.stall.remove")
            public boolean onCourtAdminStallRemove(final CommandSender commandSender) {
                CourtStallRemoveCmd cmd = new CourtStallRemoveCmd(courts, commandSender);
                return cmd.runCommand();
            }

            @Subcommand("create")
            @CommandCompletion("master|judge|secretary|citizen")
            @CommandPermission("socialcore.courts.admin.stall.create")
            public boolean onCourtAdminStallCreate(final CommandSender commandSender, final String[] args) {
                CourtStallCreateCmd cmd = new CourtStallCreateCmd(courts, commandSender, args);
                return cmd.runCommand();
            }
        }

        @Subcommand("election")
        @CommandPermission("socialcore.courts.admin.election")
        public class CourtAdminElectionHandler extends BaseCommand {

            @Default
            public boolean onDefault(final CommandSender commandSender) {
                return sendElectionHelp(commandSender, true);
            }

            @Subcommand("addvotes")
            @CommandCompletion("approve,disapprove")
            @CommandPermission("socialcore.courts.admin.election.addvotes")
            public boolean onCourtAdminElectionAddVotes(final CommandSender commandSender, final String[] args) {
                CourtAddVotesCmd cmd = new CourtAddVotesCmd(commandSender, courts.getElectionManager(), args);
                return cmd.runCommand();
            }

            @Subcommand("resetvotes")
            @CommandPermission("socialcore.courts.admin.election.resetvotes")
            public boolean onCourtAdminElectionResetVotes(final CommandSender commandSender, final String[] args) {
                CourtResetVotesCmd cmd = new CourtResetVotesCmd(commandSender, courts.getElectionManager(), args);
                return cmd.runCommand();
            }

            @Subcommand("remove")
            @CommandCompletion("withvotes|@players")
            @CommandPermission("socialcore.courts.admin.election.remove")
            public boolean onCourtAdminElectionRemove(final CommandSender commandSender, final String[] args) {
                if(args[0].equalsIgnoreCase("withvotes")){
                    CourtRemoveWithVotesCmd cmd = new CourtRemoveWithVotesCmd(courts, commandSender, courts.getElectionManager(), args);
                    return cmd.runCommand();
                } else {
                    CourtRemoveNomineeCmd cmd = new CourtRemoveNomineeCmd(commandSender, courts.getElectionManager(), args);
                    return cmd.runCommand();
                }
            }

            @Subcommand("nominate")
            @CommandPermission("socialcore.courts.admin.election.nominate")
            public boolean onCourtAdminElectionNominate(final CommandSender commandSender, final String[] args) {
                CourtNominateCmd cmd = new CourtNominateCmd(courts, commandSender, courts.getElectionManager(),args);
                return cmd.runCommand();
            }

            @Subcommand("start")
            @CommandCompletion("force")
            @CommandPermission("socialcore.courts.admin.election.start")
            public boolean onCourtAdminElectionStart(final CommandSender commandSender, final String[] args) {
                CourtStartCmd cmd = new CourtStartCmd(commandSender, courts.getElectionManager(), args);
                return cmd.runCommand();
            }

            @Subcommand("end")
            @CommandPermission("socialcore.courts.admin.election.end")
            public boolean onCourtAdminElectionEnd(final CommandSender commandSender) {
                CourtEndCmd cmd = new CourtEndCmd(commandSender, courts.getElectionManager());
                return cmd.runCommand();
            }

            @Subcommand("ifelection")
            @CommandCompletion("shouldwait|throw")
            @CommandPermission("socialcore.courts.admin.election.ifelection")
            public boolean onCourtAdminElectionIfElection(final CommandSender commandSender, final String[] args) {
                CourtIfElectionCmd cmd = new CourtIfElectionCmd(courts, commandSender, courts.getElectionManager(), args);
                return cmd.runCommand();
            }
        }
    }


    private boolean sendAdminHelp(final CommandSender commandSender) {
        final Collection<String> helpCommands = new ArrayList<>();
        helpCommands.add(formHelpLine("/court admin election", "View election commands for admins."));
        helpCommands.add(formHelpLine("/court admin silence", "Silences default court room."));
        helpCommands.add(formHelpLine("/court admin endsessions", "End all active court sessions in case they haven't ended themselves."));
        helpCommands.add(formHelpLine("/court admin promote [name] judge", "Promote a player to judge without an election."));
        helpCommands.add(formHelpLine("/court admin promote [name] secretary [judge]", "Promote a player to secretary under the given judge."));
        helpCommands.add(formHelpLine("/court admin demote [name]", "Demote a player from their court position."));
        helpCommands.add(formHelpLine("/court admin stall create [master/judge/secretary/citizen]", "Creates a specific type of court stall using the chest you're looking at."));
        helpCommands.add(formHelpLine("/court admin stall remove", "Removes the court stall you're looking at."));
        helpCommands.add(formHelpLine("/court admin reload", "Reloads config changes for the entire court system."));

        for(final String help : helpCommands) {
            commandSender.sendMessage(help);
        }
        return true;
    }

    public static boolean sendCourtHelp(CommandSender commandSender, boolean isAdmin, boolean isJudge) {
        List<String> helpCommands = new ArrayList<>();

        if (isJudge) {
            helpCommands.add(formHelpLine("/court", "To view a list of all court commands."));
            helpCommands.add(formHelpLine("/court cases", "Brings up a list of every case."));
            helpCommands.add(formHelpLine("/court secretary add [name]", "Sends a request to a player to become your secretary."));
            helpCommands.add(formHelpLine("/court secretary remove [name]", "Removes a player as your secretary."));
            helpCommands.add(formHelpLine("/court secretary list", "Lists out your secretaries."));
            helpCommands.add(formHelpLine("/court secretary [accept/deny]", "The command for your potential secretary to accept or deny the offer."));
            helpCommands.add(formHelpLine("/judge", "View more judge specific commands."));
        } else {
            helpCommands.add(formHelpLine("/court", "To view a list of all court commands."));
            helpCommands.add(formHelpLine("/court policies", "To view a list of all community approved court policies."));
            helpCommands.add(formHelpLine("/court officials", "To view a list of all elected court officials."));
            helpCommands.add(formHelpLine("/court election", "To view information about elections."));
            helpCommands.add(formHelpLine("/court secretary [accept/deny]", "Accept or deny an offered secretary position."));
        }
        if (isAdmin) {
            helpCommands.add(formHelpLine("/court cases", "Brings up a list of every case from the master list."));
            helpCommands.add(formHelpLine("/court admin", "List all admin commands relating to court."));
        }
        for (String help : helpCommands) {
            commandSender.sendMessage(help);
        }
        return true;
    }

    private boolean sendElectionHelp(final CommandSender commandSender, boolean isAdmin) {
        final Collection<String> helpCommands = new ArrayList<>();
        if(isAdmin) {
            helpCommands.add(formHelpLine("/court admin election start", "Start a new election."));
            helpCommands.add(formHelpLine("/court admin election end", "Ends current election."));
            helpCommands.add(formHelpLine("/court admin election remove [name]", "Removes a candidate from the current election."));
            helpCommands.add(formHelpLine("/court admin election remove withvotes [name]", "Removes a candidate from the current election including their votes."));
            helpCommands.add(formHelpLine("/court admin election addvotes <name> <approve/disapprove> <amount>", "Removes a candidate from the current election."));
            helpCommands.add(formHelpLine("/court admin election removevotes <name> <approve/disapprove> <amount>", "Removes a number of votes from a candidate from the current election."));
            helpCommands.add(formHelpLine("/court admin election resetvotes <name>", "Resets all votes for a candidate from the current election."));
        } else {
            helpCommands.add(formHelpLine("/court election run", "Nominate yourself to run in the current election."));
            helpCommands.add(formHelpLine("/court election vote/list/status", "See the list of candidates for the ongoing election."));
        }

        for(final String help : helpCommands) {
            commandSender.sendMessage(help);
        }
        return true;
    }

    private static String formHelpLine(final String command, final String description) {
        return ChatColor.YELLOW + command + ChatColor.GRAY + " - " + ChatColor.BLUE + description;
    }
}
