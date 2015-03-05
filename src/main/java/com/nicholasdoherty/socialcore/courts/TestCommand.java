package com.nicholasdoherty.socialcore.courts;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by john on 1/6/15.
 */
public class TestCommand implements CommandExecutor {
    Courts courts;

    public TestCommand(Courts courts) {
        this.courts = courts;
        courts.getPlugin().getCommand("ttc").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        /*
        if (args[0].equalsIgnoreCase("cct")) {
            Player p = (Player) commandSender;
            CourtRoom courtRoom = Courts.getCourts().getCourtsConfig().getDefaultCourtRoom();
            Judge judge = courts.getJudgeManager().getJudge(p.getUniqueId());
            Case caze = courts.getCaseManager().newCase(p);
            CourtDate courtDate = new CourtDate(new Date().getTime()+(1000*5), judge,courtRoom);
            caze.setCaseCategory(CaseCategory.DIVORCE);
            caze.setCourtDate(courtDate);
            caze.setCaseStatus(CaseStatus.COURT_DATE_SET, p.getName());
            courts.getCourtSessionManager().registerSession(caze);
            p.sendMessage("euo");
        }
        if (args[0].equalsIgnoreCase("jc")) {
            Player p = (Player) commandSender;
            Location p1 = p.getLocation().add(-100,-20,-100);
            Location p2 = p.getLocation().add(100,20,100);
            ProtectedRegion protectedRegion = new ProtectedCuboidRegion("court-room",new BlockVector(p1.getBlockX(),p1.getBlockY(),p1.getBlockZ()),new BlockVector(p2.getBlockX(),p2.getBlockY(),p2.getBlockZ()));
            CourtRoom courtRoom = new CourtRoom("default",protectedRegion,new VLocation(p.getLocation()),new VLocation(p.getLocation()), new VLocation(p.getLocation()));
            Case caze = courts.getCaseManager().newCase(p);
            Judge judge = courts.getJudgeManager().getJudge(p.getUniqueId());
            CourtDate courtDate = new CourtDate(new Date().getTime(), judge,courtRoom);
            caze.setCaseCategory(CaseCategory.DIVORCE);
            caze.setPlantiff(new Citizen("Notch", UUIDUtil.getUUID("Notch")));
            caze.setCourtDate(courtDate);
            caze.setCaseStatus(CaseStatus.IN_COURT, p.getName());
            CourtSession courtSession = new CourtSession(caze, judge,courtRoom);
            JudgeCourtGUI judgeCourtGUI = new JudgeCourtGUI(courtSession);
            judgeCourtGUI.setPlayer(p);
            judgeCourtGUI.open();
        }
        if (args[0].equalsIgnoreCase("cal")) {
            Player p = (Player) commandSender;
            CalendarGUI.createAndOpen(p,null,null,null,courts.getDefaultDayGetter());
        }
        if (args[0].equalsIgnoreCase("test1")) {
            Player p = (Player) commandSender;
            CaseManager caseManager = courts.getCaseManager();
            for (int i = 0; i < 10; i++) {
                Case caze = caseManager.newCase(p);
            }
            Candidate candidate = new Candidate(p.getName(),p.getUniqueId());
            courts.getJudgeManager().promoteJudge(candidate);
            courts.getJudgeManager().getJudge(p.getUniqueId()).addSecretary(new Secretary(p.getName(),p.getUniqueId(),courts.getJudgeManager().getJudge(p.getUniqueId())));
        }
        if (args[0].equalsIgnoreCase("createcases")) {
            Player p = (Player) commandSender;
            CaseStatus caseStatus = CaseStatus.UNPROCESSED;
            int amount = 1;
            if (args.length > 1) {
                amount = Integer.parseInt(args[1]);
            }
            if (args.length > 2) {
                caseStatus = CaseStatus.valueOf(args[2].toUpperCase());
            }
            CaseManager caseManager = courts.getCaseManager();
            for (int i = 0; i < amount; i++) {
                Case caze = caseManager.newCase(p);
                caze.setCaseStatus(caseStatus,p.getName());
            }
            commandSender.sendMessage(ChatColor.GREEN + "Created cases");
        }
        if (args[0].equalsIgnoreCase("setjudgesec")) {
            Player p = (Player) commandSender;
            Candidate candidate = new Candidate(p.getName(),p.getUniqueId());
            courts.getJudgeManager().promoteJudge(candidate);
            courts.getJudgeManager().getJudge(p.getUniqueId()).addSecretary(new Secretary(p.getName(),p.getUniqueId(),courts.getJudgeManager().getJudge(p.getUniqueId())));
        }
        if (args[0].equalsIgnoreCase("sign")) {
            Player player = (Player) commandSender;
            courts.getPlugin().getInputLib().add(player.getUniqueId(), new InputRunnable() {
                @Override
                public void run(String input) {
                    System.out.println(input);
                }
            });
            courts.getPlugin().getInputLib().clearChat(player);
            courts.getPlugin( ).getInputLib().sendMessage(player,"Hi");
        }
        return true;*/
        return true;
    }
}
