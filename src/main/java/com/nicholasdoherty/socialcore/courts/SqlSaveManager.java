package com.nicholasdoherty.socialcore.courts;

import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.tuple.Tuple2;
import com.nicholasdoherty.socialcore.courts.cases.*;
import com.nicholasdoherty.socialcore.courts.cases.CaseHistory.HistoryEntry;
import com.nicholasdoherty.socialcore.courts.elections.Candidate;
import com.nicholasdoherty.socialcore.courts.elections.Election;
import com.nicholasdoherty.socialcore.courts.fines.Fine;
import com.nicholasdoherty.socialcore.courts.judges.Judge;
import com.nicholasdoherty.socialcore.courts.judges.secretaries.Secretary;
import com.nicholasdoherty.socialcore.courts.objects.ApprovedCitizen;
import com.nicholasdoherty.socialcore.courts.objects.Citizen;
import com.nicholasdoherty.socialcore.courts.policies.Policy;
import com.nicholasdoherty.socialcore.courts.policies.Policy.State;
import com.nicholasdoherty.socialcore.courts.stall.Stall;
import com.nicholasdoherty.socialcore.courts.stall.StallType;
import com.nicholasdoherty.socialcore.time.VoxTimeUnit;
import com.nicholasdoherty.socialcore.utils.SCSerializer;
import com.voxmc.voxlib.VLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by john on 3/4/15.
 */
@SuppressWarnings({"SqlResolve", "UnusedParameters", "SqlDialectInspection"})
public class SqlSaveManager {
    private final Courts courts;
    
    public SqlSaveManager() {
        courts = Courts.getCourts();
    }
    
    public void upgrade() {
        //prevent dupe courts_judges
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("ALTER IGNORE TABLE courts_judges ADD UNIQUE INDEX judge_unique (citizen_id)");
            preparedStatement.execute();
        } catch(final Exception e) {
            //e.printStackTrace();
        }
        //prevent dupe secretaries
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("ALTER IGNORE TABLE courts_secretaries ADD UNIQUE INDEX secretary_unique (citizen_id,judge_id)");
            preparedStatement.execute();
        } catch(final Exception e) {
            //e.printStackTrace();
        }
        
        //prevent dupe candidates
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("ALTER IGNORE TABLE election_candidates ADD UNIQUE INDEX canidate_unique (citizen_id)");
            preparedStatement.execute();
        } catch(final Exception e) {
            //e.printStackTrace();
        }
        //Election should start
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("CREATE TABLE `courts_election` (\n" +
                    "  `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,\n" +
                    "  `time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "  `is_active` TINYINT(1) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            preparedStatement.execute();
        } catch(final Exception ignored) {
        }
    }
    
    public Optional<Policy> setJudgeConfirmation(final Judge judge, final Policy policy, final boolean approve) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("REPLACE INTO courts_policies_judge_confirmations (judge_citizen_id,policy_id,approve) VALUES (?,?,?)");
            preparedStatement.setInt(1, judge.getId());
            preparedStatement.setInt(2, policy.getId());
            preparedStatement.setBoolean(3, approve);
            preparedStatement.execute();
            return updatePolicy(policy);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public Optional<Policy> createPolicy(final String text, final Citizen author) {
        final int id = getDatabase().update("INSERT INTO courts_policies (`text`,author_citizen_id,state,creation_time) VALUES(?,?,?,?)")
                .parameters(text, author.getId(), State.UNCONFIRMED.toString(), new Timestamp(new Date().getTime()))
                .returnGeneratedKeys()
                .getAs(Integer.class).toBlocking().single();
        return getPolicy(id);
    }
    
    public Optional<Policy> deletePolicy(final Long id) {
        getDatabase().update("DELETE FROM courts_policies WHERE id = ?")
                .parameters(id).execute();
        return Optional.empty();
    }
    
    public Optional<Policy> updatePolicyState(final Policy policy, final State state) {
        getDatabase().update("UPDATE courts_policies SET state = ? WHERE id = ?")
                .parameters(state.toString(), policy.getId())
                .count().toBlocking().single();
        if(state == State.MAIN_VOTING) {
            getDatabase().update("UPDATE courts_policies SET confirm_time = ? WHERE id = ?")
                    .parameters(state, new Timestamp(new Date().getTime()))
                    .count().toBlocking().single();
        }
        return updatePolicy(policy);
    }
    
    public Optional<Policy> setCitizenVote(final Citizen citizen, final Policy policy, final boolean approve) {
        getDatabase()
                .update("REPLACE INTO courts_policies_votes (voter_citizen_id,policy_id,approve) VALUES (?,?,?)")
                .parameters(citizen.getId(), policy.getId(), approve)
                .count().toBlocking().single();
        return updatePolicy(policy);
    }
    
    public Optional<Policy> updatePolicy(final Policy policy) {
        policy.setStale(true);
        return getPolicy(policy.getId());
    }
    
    public Optional<Policy> getPolicy(final int policyId) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_policies WHERE id = ?");
            preparedStatement.setInt(1, policyId);
            final ResultSet policyResult = preparedStatement.executeQuery();
            if(policyResult.next()) {
                final String text = policyResult.getString("text");
                final Citizen author = getCitizen(policyResult.getInt("author_citizen_id"));
                final State state = State.valueOf(policyResult.getString("state"));
                final Timestamp creationTime = policyResult.getTimestamp("creation_time");
                final Optional<Timestamp> confirmTime = Optional.ofNullable(policyResult.getTimestamp("confirm_time"));
                final List<Tuple2<Citizen, Boolean>> votes = getDatabase()
                        .select("SELECT voter_citizen_id,approve FROM courts_policies_votes WHERE policy_id = ?")
                        .parameter(policyId).getAs(Integer.class, Boolean.class)
                        .map(uuidS -> new Tuple2<>(courts.getCitizenManager().getCitizen(uuidS._1()), uuidS._2()))
                        .toList().toBlocking().single();
                final Set<Citizen> approvals = votes.stream().filter(Tuple2::value2)
                        .map(Tuple2::_1).collect(Collectors.toSet());
                final Set<Citizen> disapprovals = votes.stream().filter(vote -> !vote._2())
                        .map(Tuple2::_1).collect(Collectors.toSet());
                final Set<Citizen> confirmApprovals = new HashSet<>(getDatabase()
                        .select("SELECT judge_citizen_id from courts_policies_judge_confirmations WHERE policy_id = ? AND approve = 1")
                        .parameter(policyId).getAs(Integer.class)
                        .map(courts.getCitizenManager()::getCitizen)
                        .toList().toBlocking().single());
                return Optional.of(new Policy(policyId,
                        text,
                        author,
                        confirmApprovals,
                        approvals,
                        disapprovals,
                        state,
                        creationTime,
                        confirmTime));
            }
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public List<Long> allPoliciesIds() {
        return getDatabase().select("SELECT id from courts_policies")
                .getAs(Long.TYPE).toList().toBlocking().single();
    }
    
    public void clean() {
        //remove invalid secretaries
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_secretaries WHERE judge_id NOT IN (SELECT courts_judges.id FROM courts_judges)");
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_secretaries WHERE citizen_id = judge_id");
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        
        //fix court dates
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_court_dates WHERE judge_id NOT IN (SELECT courts_judges.id FROM courts_judges)");
            final ResultSet resultSet = preparedStatement.executeQuery();
            final Collection<Integer> courtDatesToDelete = new ArrayList<>();
            while(resultSet.next()) {
                final int caseId = resultSet.getInt("case_id");
                final int id = resultSet.getInt("id");
                final List<Case> cazes = getCases();
                Case caze = null;
                for(final Case caze1 : cazes) {
                    if(caze1.getId() == caseId) {
                        caze = caze1;
                    }
                }
                courtDatesToDelete.add(id);
                if(caze != null) {
                    caze.setCaseStatus(CaseStatus.PROCESSED, "server");
                }
            }
            resultSet.close();
            for(final int id : courtDatesToDelete) {
                final PreparedStatement deleteStatement = getConnection().prepareStatement("DELETE FROM courts_court_dates WHERE id = ?");
                deleteStatement.setInt(1, id);
                deleteStatement.execute();
            }
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void purgeVotes() {
        final long purgeMillis = VoxTimeUnit.TICK.toMillis(Courts.getCourts().getCourtsConfig().getSupportVoteDecayTick());
        final Collection<UUID> allVoterUUIDs = new HashSet<>();
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT DISTINCT voter_uuid FROM courts_votes");
            final ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                final UUID uuid = UUID.fromString(resultSet.getString("voter_uuid"));
                allVoterUUIDs.add(uuid);
            }
        } catch(final Exception e) {
            e.printStackTrace();
        }
        final long currentTime = new Date().getTime();
        allVoterUUIDs.stream().map(Bukkit::getOfflinePlayer)
                .filter(offlinePlayer -> currentTime - offlinePlayer.getLastPlayed() >= purgeMillis).forEach(p -> {
            try {
                final PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_votes WHERE voter_uuid = ?");
                preparedStatement.setString(1, p.getUniqueId().toString());
                preparedStatement.execute();
            } catch(final Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public Citizen getCitizen(final UUID uuid) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_citizens WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()) {
                return null;
            }
            return resultSetToCitizen(resultSet);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Citizen getCitizen(final String name) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_citizens WHERE name = ?");
            preparedStatement.setString(1, name);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()) {
                return null;
            }
            return resultSetToCitizen(resultSet);
        } catch(final SQLException e) {
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
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void startElection() {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_election VALUES(DEFAULT,DEFAULT,1)");
            preparedStatement.execute();
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean isElection() {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_election WHERE is_active = 1");
            return preparedStatement.executeQuery().next();
        } catch(final Exception e) {
            return false;
        }
    }
    
    private Connection getConnection() {
        return courts.getPlugin().store.getConnection();
    }
    
    private Database getDatabase() {
        return Database.from(getConnection());
    }
    
    public Citizen getCitizen(final int id) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_citizens WHERE id = ?");
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()) {
                return null;
            }
            return resultSetToCitizen(resultSet);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void updateCitizen(final Citizen citizen) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE courts_citizens SET name = ? WHERE uuid = ?");
            preparedStatement.setString(1, citizen.getName());
            preparedStatement.setString(2, citizen.getUuid().toString());
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Citizen createCitizen(final UUID uuid, final String name) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_citizens (name,uuid) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            final int id = resultSet.getInt(1);
            return new Citizen(id, name, uuid);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Set<Judge> getJudges() {
        final Set<Judge> judges = new HashSet<>();
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_judges");
            final ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                final int judgeId = resultSet.getInt("id");
                final int citizenId = resultSet.getInt("citizen_id");
                final Judge judge = getJudge(judgeId, citizenId);
                judges.add(judge);
            }
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return judges;
    }
    
    public void updateVote(final Citizen citizen, final UUID uuid, final boolean approve) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("REPLACE INTO courts_votes (citizen_id,voter_uuid,approval) VALUES(?,?,?)");
            preparedStatement.setInt(1, citizen.getId());
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.setBoolean(3, approve);
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public ApprovedCitizen getApprovedCitizen(final Citizen citizen) {
        final Set<UUID> approvals = new HashSet<>();
        final Set<UUID> disapprovals = new HashSet<>();
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_votes WHERE citizen_id = ?");
            preparedStatement.setInt(1, citizen.getId());
            final ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                final UUID voterUUID = UUID.fromString(resultSet.getString("voter_uuid"));
                final boolean approves = resultSet.getBoolean("approval");
                if(approves) {
                    approvals.add(voterUUID);
                } else {
                    disapprovals.add(voterUUID);
                }
            }
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return new ApprovedCitizen(citizen, approvals, disapprovals);
    }
    
    private void setJudgeSecretaries(final Judge judge) {
        final Set<Secretary> secretaries = new HashSet<>();
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_secretaries WHERE judge_id = ?");
            preparedStatement.setInt(1, judge.getJudgeId());
            final ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                final int citizenId = resultSet.getInt("citizen_id");
                final int secretaryId = resultSet.getInt("id");
                final Citizen citizen = getCitizen(citizenId);
                if(citizen != null) {
                    secretaries.add(new Secretary(secretaryId, citizen, judge));
                }
            }
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        judge.setSecretaries(secretaries);
    }
    
    public Judge createJudge(final ApprovedCitizen approvedCitizen) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_judges (citizen_id) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, approvedCitizen.getId());
            preparedStatement.executeUpdate();
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            final int judgeId = resultSet.getInt(1);
            final Judge judge = new Judge(approvedCitizen, judgeId);
            judge.setSecretaries(new HashSet<>());
            return judge;
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void removeSecretary(final Secretary secretary) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_secretaries WHERE id = ?");
            preparedStatement.setInt(1, secretary.getSecretaryId());
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Secretary createSecretary(final Judge judge, final Citizen citizen) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_secretaries (citizen_id,judge_id) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, citizen.getId());
            preparedStatement.setInt(2, judge.getJudgeId());
            preparedStatement.executeUpdate();
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            final int secretaryId = resultSet.getInt(1);
            return new Secretary(secretaryId, citizen, judge);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private Judge getJudge(final int judgeId, final int citizenId) {
        final Citizen citizen = getCitizen(citizenId);
        final ApprovedCitizen approvedCitizen = getApprovedCitizen(citizen);
        final Judge judge = new Judge(approvedCitizen, judgeId);
        setJudgeSecretaries(judge);
        return judge;
    }
    
    public void removeJudge(final Judge judge) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_judges WHERE id = ?");
            preparedStatement.setInt(1, judge.getJudgeId());
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void removeVotes(final Citizen citizen) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_votes WHERE citizen_id = ?");
            preparedStatement.setInt(1, citizen.getId());
            preparedStatement.execute();
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }
    
    private Citizen resultSetToCitizen(final ResultSet resultSet) {
        try {
            final int id = resultSet.getInt("id");
            final String name = resultSet.getString("name");
            final UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            return new Citizen(id, name, uuid);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Case createNewCase() {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_cases (case_status) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, CaseStatus.INITIAL.toString());
            preparedStatement.executeUpdate();
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            final int caseId = resultSet.getInt(1);
            return new Case(caseId, CaseStatus.INITIAL);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void addCaseHistoryEntry(final Case caze, final HistoryEntry historyEntry) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_case_history (date,case_id,case_status,responsible) VALUES(?,?,?,?)");
            preparedStatement.setTimestamp(1, new Timestamp(historyEntry.getDate()));
            preparedStatement.setInt(2, caze.getId());
            preparedStatement.setString(3, historyEntry.getCaseStatus().toString());
            preparedStatement.setString(4, historyEntry.getResponsible());
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public CaseHistory getCaseHistory(final Case caze) {
        final List<HistoryEntry> historyEntries = new ArrayList<>();
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_case_history WHERE case_id = ? ORDER BY date ASC");
            preparedStatement.setInt(1, caze.getId());
            final ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                final long date = resultSet.getTimestamp("date").getTime();
                final String resp = resultSet.getString("responsible");
                final String caseStatus = resultSet.getString("case_status");
                historyEntries.add(new HistoryEntry(date, CaseStatus.valueOf(caseStatus), resp));
            }
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return new CaseHistory(historyEntries);
    }
    
    public void updateCourtDate(final Case caze) {
        if(caze.getCourtDate() == null) {
            removeCourtDate(caze);
            return;
        }
        final CourtDate courtDate = caze.getCourtDate();
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("REPLACE INTO courts_court_dates (judge_id,case_id,date) VALUES(?,?,?)");
            preparedStatement.setInt(1, courtDate.getJudgeId());
            preparedStatement.setInt(2, caze.getId());
            preparedStatement.setTimestamp(3, new Timestamp(courtDate.getTime()));
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void removeCourtDate(final Case caze) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_court_dates WHERE case_id = ?");
            preparedStatement.setInt(1, caze.getId());
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public CourtDate getCourtDate(final Case caze) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_court_dates WHERE case_id = ?");
            preparedStatement.setInt(1, caze.getId());
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()) {
                return null;
            }
            final int judgeId = resultSet.getInt("judge_id");
            final long date = resultSet.getTimestamp("date").getTime();
            return new CourtDate(date, judgeId);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void updateResolve(final Case caze) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("REPLACE INTO courts_case_resolves (case_id,resolve) VALUES(?,?)");
            preparedStatement.setInt(1, caze.getId());
            if(caze.getResolve() != null) {
                preparedStatement.setString(2, SCSerializer.serialize(caze.getResolve()));
            } else {
                preparedStatement.setNull(2, Types.LONGNVARCHAR);
            }
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Resolve getResolve(final Case caze) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_case_resolves WHERE case_id = ?");
            preparedStatement.setInt(1, caze.getId());
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()) {
                return null;
            }
            final String resolveString = resultSet.getString("resolve");
            if(resolveString == null) {
                return null;
            }
            return (Resolve) SCSerializer.deserialize(resolveString);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void updateCase(final Case caze) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE courts_cases SET case_status = ?, plaintiff_id = ?, defendant_id = ?, case_category = ?, case_book_blob = ?, case_meta = ? WHERE id = ?");
            if(caze.getCaseStatus() != null) {
                preparedStatement.setString(1, caze.getCaseStatus().toString());
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }
            if(caze.getPlantiff() != null) {
                preparedStatement.setInt(2, caze.getPlantiff().getId());
            } else {
                preparedStatement.setNull(2, Types.INTEGER);
            }
            if(caze.getDefendent() != null) {
                preparedStatement.setInt(3, caze.getDefendent().getId());
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }
            if(caze.getCaseCategory() != null) {
                preparedStatement.setString(4, caze.getCaseCategory().toString());
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }
            if(caze.getCaseBook() != null) {
                preparedStatement.setString(5, SCSerializer.serialize(caze.getCaseBook()));
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if(caze.getCaseMeta() != null) {
                preparedStatement.setString(6, SCSerializer.serialize(caze.getCaseMeta()));
            } else {
                preparedStatement.setNull(6, Types.LONGNVARCHAR);
            }
            preparedStatement.setInt(7, caze.getId());
            updateResolve(caze);
            updateCourtDate(caze);
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Election election() {
        if(!isElection()) {
            return null;
        }
        final Election election = new Election();
        final Set<Candidate> candidates = new HashSet<>();
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM election_candidates");
            final ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                final int candidateId = resultSet.getInt("id");
                final int citizenId = resultSet.getInt("citizen_id");
                final Citizen citizen = getCitizen(citizenId);
                final ApprovedCitizen approvedCitizen = getApprovedCitizen(citizen);
                final Candidate candidate = new Candidate(approvedCitizen, candidateId);
                candidates.add(candidate);
            }
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        election.setCandidateSet(candidates);
        return election;
    }
    
    public Candidate createCandidate(final Citizen citizen) {
        final ApprovedCitizen approvedCitizen = getApprovedCitizen(citizen);
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO election_candidates (citizen_id) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, citizen.getId());
            preparedStatement.executeUpdate();
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            final int id = resultSet.getInt(1);
            return new Candidate(approvedCitizen, id);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Stall resultSetToStall(final ResultSet resultSet) {
        try {
            final int id = resultSet.getInt("id");
            final StallType stallType = StallType.valueOf(resultSet.getString("type"));
            final VLocation vLocation = VLocation.fromString(resultSet.getString("location"));
            return Stall.createStall(id, stallType, vLocation);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Set<Stall> getStalls() {
        final Set<Stall> stalls = new HashSet<>();
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_stalls");
            final ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                final Stall stall = resultSetToStall(resultSet);
                stalls.add(stall);
            }
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return stalls;
    }
    
    public Fine addFine(final Citizen sender, final Citizen rec, final int amount) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_fines (sender_id,rec_id,amount) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, sender.getId());
            preparedStatement.setInt(2, rec.getId());
            preparedStatement.setInt(3, amount);
            preparedStatement.executeUpdate();
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            final int id = resultSet.getInt(1);
            return new Fine(id, sender, rec, amount);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Fine resultSetToFine(final ResultSet resultSet) {
        try {
            final int senderId = resultSet.getInt("sender_id");
            final int recId = resultSet.getInt("rec_id");
            final int amount = resultSet.getInt("amount");
            final int amountPaid = resultSet.getInt("amount_paid");
            final int id = resultSet.getInt("id");
            final Citizen sender = getCitizen(senderId);
            final Citizen rec = getCitizen(recId);
            return new Fine(id, sender, rec, amount, amountPaid);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Set<Fine> getFines() {
        final Set<Fine> fines = new HashSet<>();
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_fines");
            final ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                final Fine fine = resultSetToFine(resultSet);
                fines.add(fine);
            }
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return fines;
    }
    
    public void updateFine(final Fine fine) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("UPDATE courts_fines SET amount_paid = ? WHERE id = ?");
            preparedStatement.setInt(1, (int) fine.getAmountPaid());
            preparedStatement.setInt(2, fine.getId());
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Stall addStall(final StallType stallType, final VLocation vLocation) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO courts_stalls (type,location) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, stallType.toString());
            preparedStatement.setString(2, vLocation.toString());
            preparedStatement.executeUpdate();
            final ResultSet resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            final int id = resultSet.getInt(1);
            return Stall.createStall(id, stallType, vLocation);
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void removeStall(final Stall stall) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_stalls WHERE id = ?");
            preparedStatement.setInt(1, stall.getId());
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void removeFine(final Fine fine) {
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM courts_fines WHERE id = ?");
            preparedStatement.setInt(1, fine.getId());
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void removeCandidate(final Candidate candidate) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("DELETE FROM election_candidates WHERE id = ?");
            preparedStatement.setInt(1, candidate.getCandidateId());
            preparedStatement.execute();
            preparedStatement = getConnection().prepareStatement("DELETE FROM election_candidates WHERE citizen_id = ?");
            preparedStatement.setInt(1, candidate.getId());
            preparedStatement.execute();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Case resultSetToCase(final ResultSet resultSet) {
        try {
            final int id = resultSet.getInt("id");
            final String caseStatusString = resultSet.getString("case_status");
            CaseStatus caseStatus = null;
            if(caseStatusString != null) {
                caseStatus = CaseStatus.valueOf(caseStatusString);
            }
            
            Citizen plaintiff = null;
            final int plaintiffId = resultSet.getInt("plaintiff_id");
            if(plaintiffId != 0) {
                plaintiff = getCitizen(plaintiffId);
            }
            Citizen defendant = null;
            final int defendantId = resultSet.getInt("defendant_id");
            if(defendantId != 0) {
                defendant = getCitizen(defendantId);
            }
            CaseCategory caseCategory = null;
            final String caseCategoryString = resultSet.getString("case_category");
            if(caseCategoryString != null) {
                caseCategory = CaseCategory.valueOf(caseCategoryString);
            }
            CaseMeta caseMeta = null;
            final String caseMetaString = resultSet.getString("case_meta");
            if(caseMetaString != null) {
                caseMeta = (CaseMeta) SCSerializer.deserialize(caseMetaString);
            }
            
            ItemStack caseBook = new ItemStack(Material.BOOK_AND_QUILL);
            final String caseBookString = resultSet.getString("case_book_blob");
            if(caseBookString != null) {
                caseBook = (ItemStack) SCSerializer.deserialize(caseBookString);
            }
            final Case caze = new Case(id, caseStatus, plaintiff, defendant, caseBook, false, caseCategory, caseMeta);
            final CourtDate courtDate = getCourtDate(caze);
            caze.setCourtDate(courtDate);
            
            final CaseHistory caseHistory = getCaseHistory(caze);
            caze.setCaseHistory(caseHistory);
            
            final Resolve resolve = getResolve(caze);
            caze.setResolve(resolve);
            return caze;
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Case> getCases() {
        final List<Case> cases = new ArrayList<>();
        try {
            final PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM courts_cases ORDER BY id ASC");
            final ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                final Case caze = resultSetToCase(resultSet);
                cases.add(caze);
            }
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return cases;
    }
}
