package com.nicholasdoherty.socialcore.courts;

import com.nicholasdoherty.socialcore.courts.cases.*;
import com.nicholasdoherty.socialcore.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.courts.elections.Election;
import com.nicholasdoherty.socialcore.courts.fines.Fine;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.courts.prefix.PrefixListener;
import com.nicholasdoherty.socialcore.courts.stall.Stall;
import com.nicholasdoherty.socialcore.courts.stall.StallType;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import com.nicholasdoherty.socialcore.utils.SerializationUtil;
import com.nicholasdoherty.socialcore.utils.UUIDFetcher;
import com.nicholasdoherty.socialcore.utils.VLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import javax.persistence.PrePersist;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;

/**
 * Created by john on 3/4/15.
 */
public class SqlSaveManager {
    private Courts courts;
    public SqlSaveManager() {
        this.courts = Courts.getCourts();
    }
    public void upgrade() {
        //prevent dupe courts_judges
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("ALTER IGNORE TABLE courts_judges ADD UNIQUE INDEX judge_unique (citizen_id)");
            preparedStatement.execute();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        //prevent dupe secretaries
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("ALTER IGNORE TABLE courts_secretaries ADD UNIQUE INDEX secretary_unique (citizen_id,judge_id)");
            preparedStatement.execute();
        } catch (Exception e) {
            //e.printStackTrace();
        }

        //prevent dupe candidates
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("ALTER IGNORE TABLE election_candidates ADD UNIQUE INDEX canidate_unique (citizen_id)");
            preparedStatement.execute();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        //Election should start
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("CREATE TABLE `courts_election` (\n" +
                    "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                    "  `time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "  `is_active` tinyint(1) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            preparedStatement.execute();
        }catch (Exception e) {}
    }
    public void clean() {
        //remove invalid secretaries
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_secretaries WHERE judge_id NOT IN (SELECT courts_judges.id FROM courts_judges)");
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_secretaries WHERE citizen_id = judge_id");
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //fix court dates
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_court_dates WHERE judge_id NOT IN (SELECT courts_judges.id from courts_judges)");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Integer> courtDatesToDelete = new ArrayList<>();
            while (resultSet.next()) {
                int caseId = resultSet.getInt("case_id");
                int id = resultSet.getInt("id");
                List<Case> cazes = getCases();
                Case caze = null;
                for (Case caze1 : cazes) {
                    if (caze1.getId() == caseId) {
                        caze = caze1;
                    }
                }
                courtDatesToDelete.add(id);
                if (caze != null) {
                    caze.setCaseStatus(CaseStatus.PROCESSED,"server");
                }
            }
            resultSet.close();
            for (int id : courtDatesToDelete) {
                PreparedStatement deleteStatement = getConnection().prepareStatement("DELETE FROM courts_court_dates WHERE id = ?");
                deleteStatement.setInt(1,id);
                deleteStatement.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void purgeVotes() {
        long purgeMillis = VoxTimeUnit.TICK.toMillis(Courts.getCourts().getCourtsConfig().getSupportVoteDecayTick());
        Set<UUID> allVoterUUIDs = new HashSet<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT DISTINCT voter_uuid from courts_votes");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("voter_uuid"));
                allVoterUUIDs.add(uuid);
            }
        }catch (Exception e) {e.printStackTrace();}
        long currentTime = new Date().getTime();
        allVoterUUIDs.stream().map(Bukkit::getOfflinePlayer)
                .filter(offlinePlayer -> (currentTime - offlinePlayer.getLastPlayed()) >= purgeMillis).forEach(p -> {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_votes WHERE voter_uuid = ?");
                preparedStatement.setString(1,p.getUniqueId().toString());
                preparedStatement.execute();
            }catch (Exception e) {e.printStackTrace();}
        });
    }

    public Citizen getCitizen(UUID uuid) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("select * from courts_citizens WHERE uuid = ?");
            preparedStatement.setString(1,uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return resultSetToCitizen(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Citizen getCitizen(String name) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("select * from courts_citizens WHERE name = ?");
            preparedStatement.setString(1,name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return resultSetToCitizen(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void endElection() {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE courts_election SET is_active = 0");
            preparedStatement.execute();
            preparedStatement = getConnection().prepareStatement("DELETE FROM election_candidates");
            preparedStatement.execute();
        }catch (Exception e ) {
            e.printStackTrace();
        }
    }
    public void startElection() {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_election VALUES(DEFAULT,DEFAULT,1)");
            preparedStatement.execute();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isElection() {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * from courts_election WHERE is_active = 1");
            return preparedStatement.executeQuery().next();
        }catch (Exception e) { return false;}
    }

    private Connection getConnection() {
        return courts.getPlugin().store.getConnection();
    }

    public Citizen getCitizen(int id) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("select * from courts_citizens WHERE id = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return resultSetToCitizen(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void updateCitizen(Citizen citizen) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("update courts_citizens SET name = ? WHERE uuid = ?");
            preparedStatement.setString(1,citizen.getName());
            preparedStatement.setString(2,citizen.getUuid().toString());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Citizen createCitizen(UUID uuid, String name) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_citizens (name,uuid) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,uuid.toString());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int id = resultSet.getInt(1);
            return new Citizen(id,name,uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Set<Judge> getJudges() {
        Set<Judge> judges = new HashSet<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * from courts_judges");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int judgeId = resultSet.getInt("id");
                int citizenId = resultSet.getInt("citizen_id");
                Judge judge = getJudge(judgeId,citizenId);
                judges.add(judge);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return judges;
    }
    public void updateVote(Citizen citizen, UUID uuid, boolean approve) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("REPLACE INTO courts_votes (citizen_id,voter_uuid,approval) VALUES(?,?,?)");
            preparedStatement.setInt(1,citizen.getId());
            preparedStatement.setString(2,uuid.toString());
            preparedStatement.setBoolean(3,approve);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ApprovedCitizen getApprovedCitizen(Citizen citizen) {
        Set<UUID> approvals = new HashSet<>();
        Set<UUID> disapprovals = new HashSet<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * from courts_votes WHERE citizen_id = ?");
            preparedStatement.setInt(1,citizen.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UUID voterUUID = UUID.fromString(resultSet.getString("voter_uuid"));
                boolean approves = resultSet.getBoolean("approval");
                if (approves) {
                    approvals.add(voterUUID);
                }else {
                    disapprovals.add(voterUUID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ApprovedCitizen(citizen,approvals,disapprovals);
    }
    private void setJudgeSecretaries(Judge judge) {
        Set<Secretary> secretaries = new HashSet<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * from courts_secretaries WHERE judge_id = ?");
            preparedStatement.setInt(1,judge.getJudgeId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int citizenId = resultSet.getInt("citizen_id");
                int secretaryId = resultSet.getInt("id");
                Citizen citizen = getCitizen(citizenId);
                if (citizen != null) {
                    secretaries.add(new Secretary(secretaryId,citizen,judge));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        judge.setSecretaries(secretaries);
    }
    public Judge createJudge(ApprovedCitizen approvedCitizen) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_judges (citizen_id) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,approvedCitizen.getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int judgeId = resultSet.getInt(1);
            Judge judge = new Judge(approvedCitizen,judgeId);
            judge.setSecretaries(new HashSet<Secretary>());
            return judge;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void removeSecretary(Secretary secretary) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE from courts_secretaries WHERE id = ?");
            preparedStatement.setInt(1,secretary.getSecretaryId());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Secretary createSecretary(Judge judge, Citizen citizen) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_secretaries (citizen_id,judge_id) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,citizen.getId());
            preparedStatement.setInt(2,judge.getJudgeId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int secretaryId = resultSet.getInt(1);
            return new Secretary(secretaryId,citizen,judge);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Judge getJudge(int judgeId, int citizenId) {
        Citizen citizen = getCitizen(citizenId);
        ApprovedCitizen approvedCitizen = getApprovedCitizen(citizen);
        Judge judge = new Judge(approvedCitizen,judgeId);
        setJudgeSecretaries(judge);
        return judge;
    }
    public void removeJudge(Judge judge) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE from courts_judges WHERE id = ?");
            preparedStatement.setInt(1,judge.getJudgeId());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeVotes(Citizen citizen) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE from courts_votes WHERE citizen_id = ?");
            preparedStatement.setInt(1,citizen.getId());
            preparedStatement.execute();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Citizen resultSetToCitizen(ResultSet resultSet) {
        try {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            Citizen citizen = new Citizen(id,name,uuid);
            return citizen;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Case createNewCase() {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_cases (case_status) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, CaseStatus.INITIAL.toString());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int caseId = resultSet.getInt(1);
            Case caze = new Case(caseId,CaseStatus.INITIAL);
            return caze;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void addCaseHistoryEntry(Case caze, CaseHistory.HistoryEntry historyEntry) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_case_history (date,case_id,case_status,responsible) VALUES(?,?,?,?)");
            preparedStatement.setTimestamp(1, new Timestamp(historyEntry.getDate()));
            preparedStatement.setInt(2, caze.getId());
            preparedStatement.setString(3, historyEntry.getCaseStatus().toString());
            preparedStatement.setString(4,historyEntry.getResponsible());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public CaseHistory getCaseHistory(Case caze) {
        List<CaseHistory.HistoryEntry> historyEntries = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * from courts_case_history WHERE case_id = 1 ORDER BY date ASC");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long date = resultSet.getTimestamp("date").getTime();
                String resp = resultSet.getString("responsible");
                String caseStatus = resultSet.getString("case_status");
                historyEntries.add(new CaseHistory.HistoryEntry(date,CaseStatus.valueOf(caseStatus),resp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new CaseHistory(historyEntries);
    }
    public void updateCourtDate(Case caze) {
        if (caze.getCourtDate() == null) {
            removeCourtDate(caze);
            return;
        }
        CourtDate courtDate = caze.getCourtDate();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("REPLACE INTO courts_court_dates (judge_id,case_id,date) VALUES(?,?,?)");
            preparedStatement.setInt(1,courtDate.getJudgeId());
            preparedStatement.setInt(2,caze.getId());
            preparedStatement.setTimestamp(3, new Timestamp(courtDate.getTime()));
            preparedStatement.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    } 
    public void removeCourtDate(Case caze) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_court_dates WHERE case_id = ?");
            preparedStatement.setInt(1,caze.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public CourtDate getCourtDate(Case caze) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * from courts_court_dates WHERE case_id = ?");
            preparedStatement.setInt(1,caze.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            int judgeId = resultSet.getInt("judge_id");
            long date = resultSet.getTimestamp("date").getTime();
            return new CourtDate(date,judgeId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void updateResolve(Case caze) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("REPLACE INTO courts_case_resolves (case_id,resolve) VALUES(?,?)");
            preparedStatement.setInt(1, caze.getId());
            if (caze.getResolve() != null) {
                preparedStatement.setString(2, SerializationUtil.serialize(caze.getResolve()));
            }else {
                preparedStatement.setNull(2, Types.LONGNVARCHAR);
            }
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Resolve getResolve(Case caze) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * from courts_case_resolves where case_id = ?");
            preparedStatement.setInt(1,caze.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            String resolveString = resultSet.getString("resolve");
            if (resolveString == null)
                return null;
            Resolve resolve = (Resolve) SerializationUtil.deserialize(resolveString);
            return resolve;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
    public void updateCase(Case caze) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE courts_cases SET case_status = ?, plaintiff_id = ?, defendant_id = ?, case_category = ?, case_book_blob = ?, case_meta = ? WHERE id = ?");
            if (caze.getCaseStatus() != null) {
                preparedStatement.setString(1, caze.getCaseStatus().toString());
            }else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if (caze.getPlantiff() != null) {
                preparedStatement.setInt(2,caze.getPlantiff().getId());
            }else  {
                preparedStatement.setNull(2, Types.INTEGER);
            }
            if (caze.getDefendent() != null) {
                preparedStatement.setInt(3,caze.getDefendent().getId());
            }else {
                preparedStatement.setNull(3,Types.INTEGER);
            }
            if (caze.getCaseCategory() != null) {
                preparedStatement.setString(4,caze.getCaseCategory().toString());
            }else {
                preparedStatement.setNull(4,Types.VARCHAR);
            }
            if (caze.getCaseBook() != null) {
                preparedStatement.setString(5, SerializationUtil.serialize(caze.getCaseBook()));
            }else {
                preparedStatement.setNull(5,Types.VARCHAR);
            }
            if (caze.getCaseMeta() != null) {
                preparedStatement.setString(6, SerializationUtil.serialize(caze.getCaseMeta()));
            }else {
                preparedStatement.setNull(6, Types.LONGNVARCHAR);
            }
            preparedStatement.setInt(7,caze.getId());
            updateResolve(caze);
            updateCourtDate(caze);
            preparedStatement.execute();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Election election() {
        if (!isElection()) {
            return null;
        }
        Election election = new Election();
        Set<Candidate> candidates = new HashSet<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * from election_candidates");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int candidateId = resultSet.getInt("id");
                int citizenId = resultSet.getInt("citizen_id");
                Citizen citizen = getCitizen(citizenId);
                ApprovedCitizen approvedCitizen = getApprovedCitizen(citizen);
                Candidate candidate = new Candidate(approvedCitizen,candidateId);
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        election.setCandidateSet(candidates);
        return election;
    }
    public Candidate createCandidate(Citizen citizen) {
        ApprovedCitizen approvedCitizen = getApprovedCitizen(citizen);
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO election_candidates (citizen_id) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,citizen.getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int id = resultSet.getInt(1);
            return new Candidate(approvedCitizen,id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Stall resultSetToStall(ResultSet resultSet) {
        try {
            int id = resultSet.getInt("id");
            StallType stallType = StallType.valueOf(resultSet.getString("type"));
            VLocation vLocation = VLocation.fromString(resultSet.getString("location"));
            return Stall.createStall(id,stallType,vLocation);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Set<Stall> getStalls() {
        Set<Stall> stalls = new HashSet<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * from courts_stalls");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Stall stall = resultSetToStall(resultSet);
                stalls.add(stall);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stalls;
    }
    public Fine addFine(Citizen sender, Citizen rec, int amount) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_fines (sender_id,rec_id,amount) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1,sender.getId());
            preparedStatement.setInt(2,rec.getId());
            preparedStatement.setInt(3,amount);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int id = resultSet.getInt(1);
            return new Fine(id,sender,rec,amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Fine resultSetToFine(ResultSet resultSet) {
        try {
            int senderId = resultSet.getInt("sender_id");
            int recId = resultSet.getInt("rec_id");
            int amount = resultSet.getInt("amount");
            int amountPaid = resultSet.getInt("amount_paid");
            int id = resultSet.getInt("id");
            Citizen sender = getCitizen(senderId);
            Citizen rec = getCitizen(recId);
            return new Fine(id,sender,rec,amount,amountPaid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Set<Fine> getFines() {
        Set<Fine> fines = new HashSet<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * from courts_fines");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Fine fine = resultSetToFine(resultSet);
                fines.add(fine);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }
    public void updateFine(Fine fine) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE courts_fines SET amount_paid = ? WHERE id = ?");
            preparedStatement.setInt(1, (int) fine.getAmountPaid());
            preparedStatement.setInt(2, fine.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Stall addStall(StallType stallType, VLocation vLocation) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_stalls (type,location) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,stallType.toString());
            preparedStatement.setString(2,vLocation.toString());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int id = resultSet.getInt(1);
            return Stall.createStall(id,stallType,vLocation);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void removeStall(Stall stall) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_stalls WHERE id = ?");
            preparedStatement.setInt(1,stall.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeFine(Fine fine) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_fines WHERE id = ?");
            preparedStatement.setInt(1,fine.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeCandidate(Candidate candidate) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM election_candidates WHERE id = ?");
            preparedStatement.setInt(1,candidate.getCandidateId());
            preparedStatement.execute();
            preparedStatement = getConnection().prepareStatement("DELETE FROM election_candidates WHERE citizen_id = ?");
            preparedStatement.setInt(1,candidate.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Case resultSetToCase(ResultSet resultSet) {
        try {
            int id = resultSet.getInt("id");
            String caseStatusString = resultSet.getString("case_status");
            CaseStatus caseStatus = null;
            if (caseStatusString != null) {
                caseStatus = CaseStatus.valueOf(caseStatusString);
            }
            
            Citizen plaintiff = null;
            int plaintiffId = resultSet.getInt("plaintiff_id");
            if (plaintiffId != 0) {
                plaintiff = getCitizen(plaintiffId);
            }
            Citizen defendant = null;
            int defendantId = resultSet.getInt("defendant_id");
            if (defendantId != 0) {
                defendant = getCitizen(defendantId);
            }
            CaseCategory caseCategory = null;
            String caseCategoryString = resultSet.getString("case_category");
            if (caseCategoryString != null) {
                caseCategory = CaseCategory.valueOf(caseCategoryString);
            }
            CaseMeta caseMeta = null;
            String caseMetaString = resultSet.getString("case_meta");
            if (caseMetaString != null) {
                caseMeta = (CaseMeta) SerializationUtil.deserialize(caseMetaString);
            }

            ItemStack caseBook = new ItemStack(Material.BOOK_AND_QUILL);
            String caseBookString = resultSet.getString("case_book_blob");
            if (caseBookString != null) {
                caseBook = (ItemStack) SerializationUtil.deserialize(caseBookString);
            }
            Case caze = new Case(id,caseStatus,plaintiff,defendant,caseBook,false,caseCategory,caseMeta);
            CourtDate courtDate = getCourtDate(caze);
            caze.setCourtDate(courtDate);
            
            CaseHistory caseHistory = getCaseHistory(caze);
            caze.setCaseHistory(caseHistory);
            
            Resolve resolve = getResolve(caze);
            caze.setResolve(resolve);
            return caze;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Case> getCases() {
        List<Case> cases = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("select * from courts_cases ORDER BY id ASC");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Case caze = resultSetToCase(resultSet);
                cases.add(caze);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return cases;
    }
}
