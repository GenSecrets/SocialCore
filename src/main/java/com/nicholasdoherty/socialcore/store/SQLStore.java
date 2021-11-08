package com.nicholasdoherty.socialcore.store;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.components.genders.Gender;
import com.nicholasdoherty.socialcore.components.marriages.types.Divorce;
import com.nicholasdoherty.socialcore.components.marriages.types.Engagement;
import com.nicholasdoherty.socialcore.components.marriages.types.Marriage;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/14/13
 * Time: 1:04
 */
@SuppressWarnings({"SuspiciousMethodCalls", "unused"})
public class SQLStore extends Store {
    SocialCore plugin;
    private Connection connection;
    
    public SQLStore() {
        plugin = SocialCore.plugin;
        getConnection();
    }
    
    public Connection getConnection() {
        try {
            if(connection == null || !connection.isValid(2)) {
                Class.forName("com.mysql.jdbc.Driver");
                final String pass = plugin.getConfig().getString("sql.pass");
                final String user = plugin.getConfig().getString("sql.user");
                final String dbURL = "jdbc:mysql://" + plugin.getConfig().get("sql.host") + '/' + plugin.getConfig().get("sql.dbname");
                connection = DriverManager.getConnection(dbURL, user, pass);
                try {
                    final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS SocialCore (uuid CHAR(70), PRIMARY KEY(uuid), gender CHAR(20), marriedTo CHAR(70), engagedTo CHAR(70), isMarried boolean, isEngaged boolean, petName CHAR(70))");
                    preparedStatement.execute();
                } catch(final Exception e) {
                    e.printStackTrace();
                }
                try {
                    final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS SocialCore_marriages (id CHAR(150), PRIMARY KEY(id), spouse1 CHAR(70), spouse2 CHAR(70), date CHAR(35), marriedBy CHAR(70))");
                    preparedStatement.execute();
                } catch(final Exception e) {
                    e.printStackTrace();
                }
                try {
                    final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS SocialCore_engagements (id CHAR(150), PRIMARY KEY(id), spouse1 CHAR(70), spouse2 CHAR(70), date CHAR(35), time BIGINT(35), whoProposed CHAR(70))");
                    preparedStatement.execute();
                } catch(final Exception e) {
                    e.printStackTrace();
                }
                try {
                    final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS SocialCore_divorces (id CHAR(150), PRIMARY KEY(id), spouse1 CHAR(25), spouse2 CHAR(25), date CHAR(35), filedBy CHAR(25))");
                    preparedStatement.execute();
                } catch(final Exception e) {
                    e.printStackTrace();
                }
                try {
                    final PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS SocialCore_marriage_perks (id CHAR(70), PRIMARY KEY(id), allowPetName boolean, allowShareExp boolean, allowShareInv boolean, allowShareFood boolean, allowPiggyBack boolean, allowTP boolean)");
                    preparedStatement.execute();
                } catch(final Exception e) {
                    e.printStackTrace();
                }

                try {
                    final String sql = "-- Create syntax for TABLE 'courts_case_history'\n" +
                            "CREATE TABLE IF NOT EXISTS `courts_case_history` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `case_id` int(11) DEFAULT NULL,\n" +
                            "  `date` datetime DEFAULT NULL,\n" +
                            "  `case_status` varchar(25) DEFAULT NULL,\n" +
                            "  `responsible` varchar(60) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  KEY `case_id` (`case_id`),\n" +
                            "  KEY `date` (`date`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;\n" +
                            '\n' +
                            "-- Create syntax for TABLE 'courts_case_resolves'\n" +
                            "CREATE TABLE IF NOT EXISTS `courts_case_resolves` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `case_id` int(11) DEFAULT NULL,\n" +
                            "  `resolve` text,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `case_id` (`case_id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;\n" +
                            '\n' +
                            "-- Create syntax for TABLE 'courts_cases'\n" +
                            "CREATE TABLE IF NOT EXISTS `courts_cases` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `case_status` varchar(20) DEFAULT NULL,\n" +
                            "  `plaintiff_id` int(11) DEFAULT NULL,\n" +
                            "  `defendant_id` int(11) DEFAULT NULL,\n" +
                            "  `case_category` varchar(64) DEFAULT NULL,\n" +
                            "  `case_meta` text,\n" +
                            "  `case_book_blob` text,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;\n" +
                            '\n' +
                            "-- Create syntax for TABLE 'courts_citizens'\n" +
                            "CREATE TABLE IF NOT EXISTS `courts_citizens` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `uuid` char(36) DEFAULT NULL,\n" +
                            "  `name` varchar(64) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `uuid` (`uuid`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;\n" +
                            '\n' +
                            "-- Create syntax for TABLE 'courts_court_dates'\n" +
                            "CREATE TABLE IF NOT EXISTS `courts_court_dates` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `case_id` int(11) DEFAULT NULL,\n" +
                            "  `judge_id` int(11) DEFAULT NULL,\n" +
                            "  `date` datetime DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `case_id_2` (`case_id`),\n" +
                            "  KEY `case_id` (`case_id`),\n" +
                            "  KEY `judge_id` (`judge_id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;\n" +
                            '\n' +
                            "-- Create syntax for TABLE 'courts_fines'\n" +
                            "CREATE TABLE IF NOT EXISTS `courts_fines` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `sender_id` int(11) DEFAULT NULL,\n" +
                            "  `rec_id` int(11) DEFAULT NULL,\n" +
                            "  `amount` int(11) DEFAULT NULL,\n" +
                            "  `amount_paid` int(11) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;\n" +
                            '\n' +
                            "-- Create syntax for TABLE 'courts_judges'\n" +
                            "CREATE TABLE IF NOT EXISTS `courts_judges` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `citizen_id` int(11) DEFAULT NULL,\n" +
                            "  `join_date` char(70) DEFAULT '1',\n" +
                            "  `last_online_date` char(70) DEFAULT '1',\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;\n" +
                            '\n' +
                            "-- Create syntax for TABLE 'courts_secretaries'\n" +
                            "CREATE TABLE IF NOT EXISTS `courts_secretaries` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `citizen_id` int(11) DEFAULT NULL,\n" +
                            "  `judge_id` int(11) DEFAULT NULL,\n" +
                            "  `last_online_date` char(70) DEFAULT '1',\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;\n" +
                            '\n' +
                            "-- Create syntax for TABLE 'courts_stalls'\n" +
                            "CREATE TABLE IF NOT EXISTS `courts_stalls` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `type` varchar(20) DEFAULT NULL,\n" +
                            "  `location` varchar(64) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;\n" +
                            '\n' +
                            "-- Create syntax for TABLE 'courts_votes'\n" +
                            "CREATE TABLE IF NOT EXISTS `courts_votes` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `approval` tinyint(1) DEFAULT NULL,\n" +
                            "  `citizen_id` int(11) DEFAULT NULL,\n" +
                            "  `voter_uuid` char(36) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `citizen_id` (`citizen_id`,`voter_uuid`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8;\n" +
                            '\n' +
                            "-- Create syntax for TABLE 'election_candidates'\n" +
                            "CREATE TABLE IF NOT EXISTS `election_candidates` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `citizen_id` int(11) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `citizen_id` (`citizen_id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;";
                    for(final String sqlL : sql.split(";")) {
                        final PreparedStatement preparedStatement = connection.prepareStatement(sqlL);
                        preparedStatement.execute();
                    }
                } catch(final Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    final String sql = "CREATE TABLE IF NOT EXISTS `courts_policies` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `text` text,\n" +
                            "  `author_citizen_id` int(11) NOT NULL,\n" +
                            "  `state` varchar(16) NOT NULL DEFAULT '',\n" +
                            "  `creation_time` timestamp NULL DEFAULT NULL,\n" +
                            "  `confirm_time` timestamp NULL DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;";
                    final PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.execute();
                } catch(final Exception ignored) {
                }
                try {
                    final String sql = "CREATE TABLE IF NOT EXISTS `courts_policies_judge_confirmations` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `judge_citizen_id` int(11) DEFAULT NULL,\n" +
                            "  `policy_id` int(11) DEFAULT NULL,\n" +
                            "  `approve` tinyint(1) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `judge_citizen_id` (`judge_citizen_id`,`policy_id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;";
                    final PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.execute();
                } catch(final Exception ignored) {
                }
                try {
                    final String sql = "CREATE TABLE IF NOT EXISTS `courts_policies_votes` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `policy_id` int(11) DEFAULT NULL,\n" +
                            "  `voter_citizen_id` int(11) DEFAULT NULL,\n" +
                            "  `approve` tinyint(1) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `one_vote_per_citizen` (`policy_id`,`voter_citizen_id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;";
                    final PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.execute();
                } catch(final Exception ignored) {
                }
            }
            return connection;
        } catch(final Exception e) {
            e.printStackTrace();
            SocialCore.plugin.getLogger().severe("SQL Server down!");
        }
        return null;
    }
    
    public SocialPlayer getSocialPlayer(final String uuid) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM SocialCore WHERE uuid = ?");
            preparedStatement.setString(1, uuid);
            final ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                String gender = rs.getString("gender");
                String petName = rs.getString("petName");
                final boolean isMarried = rs.getBoolean("isMarried");
                final boolean isEngaged = rs.getBoolean("isEngaged");
                if(gender == null) {
                    gender = "UNSPECIFIED";
                } else {
                    gender = gender.toUpperCase();
                }
                String marriedTo = rs.getString("marriedTo");
                if(marriedTo == null) {
                    marriedTo = "";
                }
                String engagedTo = rs.getString("engagedTo");
                if(engagedTo == null) {
                    engagedTo = "";
                }

                final SocialPlayer socialPlayer = new SocialPlayer(uuid);
                socialPlayer.setGender(new Gender(gender));
                socialPlayer.setEngaged(isEngaged);
                socialPlayer.setEngagedTo(engagedTo);
                socialPlayer.setMarried(isMarried);
                socialPlayer.setMarriedTo(marriedTo);
                socialPlayer.setPetName(petName);

                conn.close();
                return socialPlayer;
            }
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        create(uuid);
        return new SocialPlayer(uuid);
    }
    
    public List<String> getSocialPlayers() {
        final Connection conn = getConnection();
        final List<String> socialPlayers = new ArrayList<>();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("SELECT uuid FROM SocialCore");
            final ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                socialPlayers.add(rs.getString("uuid"));
            }

            conn.close();
        } catch(final Exception e) {
            e.printStackTrace();
        }
        return socialPlayers;
    }

    public void create(final String uuid) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SocialCore (uuid, gender, marriedTo, engagedTo, isMarried, isEngaged, petName) VALUES(?,?,?,?,?,?,?)");
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, "UNSPECIFIED");
            preparedStatement.setString(3, "");
            preparedStatement.setString(4, "");
            preparedStatement.setBoolean(5, false);
            preparedStatement.setBoolean(6, false);
            preparedStatement.setString(7, "significant other");
            preparedStatement.execute();
            plugin.genders.adjustGenderCache(new Gender("UNSPECIFIED"), false);

            conn.close();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }

    public void syncSocialPlayer(final SocialPlayer socialPlayer) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("UPDATE SocialCore SET gender = ?, marriedTo = ?, engagedTo = ?, isMarried = ?, isEngaged = ?,petName = ? WHERE uuid = ?");
            preparedStatement.setString(1, socialPlayer.getGender().getName());
            preparedStatement.setString(2, socialPlayer.getMarriedTo());
            preparedStatement.setString(3, socialPlayer.getEngagedTo());
            preparedStatement.setBoolean(4, socialPlayer.isMarried());
            preparedStatement.setBoolean(5, socialPlayer.isEngaged());
            preparedStatement.setString(6, socialPlayer.getPetName());
            preparedStatement.setString(7, socialPlayer.getUUID());
            preparedStatement.execute();

            conn.close();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
    }

    public int getGenderStats(String genderName) {
        int genderCount = 14;
        final Connection conn = getConnection();
        try {
            final String sql = "SELECT COUNT(gender) AS 'rows' FROM SocialCore WHERE gender LIKE '"+genderName.toUpperCase()+"'";
            final PreparedStatement preparedStatement = conn.prepareStatement(sql);
            final ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            genderCount = rs.getInt("rows");
            rs.close();

            conn.close();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return genderCount;
    }

    public HashMap<String, Integer> getGenderTotals(List<String> names) {
        final Connection conn = getConnection();
        HashMap<String, Integer> genderTotals = new HashMap<String, Integer>() {{for(String name : names){put(name.toUpperCase(), 0);}}};

        try {
            final String sql = "SELECT gender FROM SocialCore";
            final PreparedStatement preparedStatement = conn.prepareStatement(sql);
            final ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String genderName = rs.getString("gender").toUpperCase();
                if(names.contains(genderName)){
                    genderTotals.put(genderName, genderTotals.get(genderName) +1);
                }
            }

            rs.close();
            conn.close();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return genderTotals;
    }

    public Marriage getMarriage(final SocialPlayer spouse1, final SocialPlayer spouse2) {
        final Marriage marriage = new Marriage(spouse1, spouse2);
        final String marriageName = marriage.getName();
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM SocialCore_marriages WHERE id = ?");
            preparedStatement.setString(1, marriageName);
            final ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                marriage.setDate(rs.getString("date"));
                marriage.setPriest(rs.getString("marriedBy"));
            }

            conn.close();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return marriage;
    }
    
    public void saveMarriage(final Marriage marriage) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SocialCore_marriages (id,spouse1,spouse2,date,marriedBy) values(?,?,?,?,?)");
            preparedStatement.setString(1, marriage.getName());
            preparedStatement.setString(2, marriage.getSpouse1().getUUID());
            preparedStatement.setString(3, marriage.getSpouse2().getUUID());
            preparedStatement.setString(4, marriage.getDate());
            preparedStatement.setString(5, marriage.getPriest());
            preparedStatement.execute();

            conn.close();
        } catch(final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void saveMarriage(final Marriage marriage, final boolean debug) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SocialCore_marriages (id,spouse1,spouse2,date,marriedBy) values(?,?,?,?,?)");
            preparedStatement.setString(1, marriage.getName());
            preparedStatement.setString(2, marriage.getSpouse1().getUUID());
            preparedStatement.setString(3, marriage.getSpouse2().getUUID());
            preparedStatement.setString(4, marriage.getDate());
            preparedStatement.setString(5, marriage.getPriest());
            preparedStatement.execute();

            conn.close();
        } catch(final Exception e) {
            if(debug) {
                e.printStackTrace();
            }
        }
    }

    public List<String> getAllMarriageNames() {
        final List<String> marriageNames = new ArrayList<>();
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("SELECT id FROM SocialCore_marriages");
            final ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                marriageNames.add(rs.getString("id"));
            }

            conn.close();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return marriageNames;
    }
    
    public Engagement getEngagement(final SocialPlayer husband, final SocialPlayer wife) {
        final Engagement engagement = new Engagement(husband, wife);
        final String engagementName = engagement.getName();
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM SocialCore_engagements WHERE id = ?");
            preparedStatement.setString(1, engagementName);
            final ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                engagement.setDate(rs.getString("date"));
                engagement.setTime(rs.getLong("time"));
                engagement.setWhoProposed(rs.getString("whoProposed"));
            }

            conn.close();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return engagement;
    }
    
    public void saveEngagement(final Engagement engagement) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SocialCore_engagements (id, spouse1, spouse2, date, time, whoProposed) values(?,?,?,?,?,?)");
            preparedStatement.setString(1, engagement.getName());
            preparedStatement.setString(2, engagement.getFutureSpouse1().getUUID());
            preparedStatement.setString(3, engagement.getFutureSpouse2().getUUID());
            preparedStatement.setString(4, engagement.getDate());
            preparedStatement.setLong(5, engagement.getTime());
            preparedStatement.setString(6, engagement.getWhoProposed().toString());
            preparedStatement.execute();

            conn.close();
        } catch(final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public List<String> getAllEngagementNames() {
        final List<String> engagementNames = new ArrayList<>();
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("SELECT id FROM SocialCore_engagements");
            final ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                engagementNames.add(rs.getString("id"));
            }

            conn.close();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return engagementNames;
    }
    
    public Divorce getDivorce(final SocialPlayer husband, final SocialPlayer wife) {
        final Divorce divorce = new Divorce(husband, wife);
        final String divorceName = divorce.getName();
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM SocialCore_divorces WHERE id = ?");
            preparedStatement.setString(1, divorceName);
            final ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                divorce.setDate(rs.getString("date"));
                divorce.setFiledBy(rs.getString("filedBy"));
            }

            conn.close();
        } catch(final SQLException e) {
            e.printStackTrace();
        }
        return divorce;
    }
    
    public void saveDivorce(final Divorce divorce) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SocialCore_divorces (id, spouse1, spouse2, date, filedby) values(?,?,?,?,?)");
            preparedStatement.setString(1, divorce.getName());
            preparedStatement.setString(2, divorce.getExSpouse1().getUUID());
            preparedStatement.setString(3, divorce.getExSpouse2().getUUID());
            preparedStatement.setString(4, divorce.getDate());
            preparedStatement.setString(5, divorce.getFiledBy());
            preparedStatement.execute();

            conn.close();
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<String> getAllDivorceNames() {
        final List<String> divorceNames = new ArrayList<>();
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("SELECT id FROM SocialCore_divorces");
            final ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                divorceNames.add(rs.getString("id"));
            }

            conn.close();
        } catch(final SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return divorceNames;
    }
    
    public void deleteDivorce(final Divorce d) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM SocialCore_divorces WHERE id = ?");
            preparedStatement.setString(1, d.getName());
            preparedStatement.execute();

            conn.close();
        } catch(final Exception ignored) {
        }
    }

    public void deleteDivorce(final String s) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM SocialCore_divorces WHERE id = ?");
            preparedStatement.setString(1, s);
            preparedStatement.execute();

            conn.close();
        } catch(final Exception ignored) {
        }
    }
    
    public void deleteMarriage(final Marriage m) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM SocialCore_marriages WHERE id = ?");
            preparedStatement.setString(1, m.getName());
            preparedStatement.execute();

            conn.close();
        } catch(final Exception ignored) {
        }
    }

    public void deleteMarriage(final String m) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM SocialCore_marriages WHERE id = ?");
            preparedStatement.setString(1, m);
            preparedStatement.execute();

            conn.close();
        } catch(final Exception ignored) {
        }
    }
    
    public void deleteEngagement(final Engagement e) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM SocialCore_engagements WHERE id = ?");
            preparedStatement.setString(1, e.getName());
            preparedStatement.execute();

            conn.close();
        } catch(final Exception ignored) {
        }
    }

    public void deleteEngagement(final String e) {
        final Connection conn = getConnection();
        try {
            final PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM SocialCore_engagements WHERE id = ?");
            preparedStatement.setString(1, e);
            preparedStatement.execute();

            conn.close();
        } catch(final Exception ignored) {
        }
    }
}
