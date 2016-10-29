package com.nicholasdoherty.socialcore.store;

import com.nicholasdoherty.socialcore.SocialCore;
import com.nicholasdoherty.socialcore.SocialPlayer;
import com.nicholasdoherty.socialcore.marriages.Divorce;
import com.nicholasdoherty.socialcore.marriages.Engagement;
import com.nicholasdoherty.socialcore.marriages.Marriage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: john
 * Date: 11/14/13
 * Time: 1:04
 * To change this template use File | Settings | File Templates.
 */
public class SQLStore extends Store {
 	private Connection connection;
	SocialCore plugin;
	public SQLStore() {
		plugin = SocialCore.plugin;
		getConnection();
	}
	public Connection getConnection() {
		try {
			if (connection == null || !connection.isValid(2)) {
				Class.forName("com.mysql.jdbc.Driver");
				String pass = plugin.getConfig().getString("sql.pass");
				String user = plugin.getConfig().getString("sql.user");
				String dbURL = "jdbc:mysql://"+ plugin.getConfig().get("sql.host") +"/" + plugin.getConfig().get("sql.dbname");
				this.connection = DriverManager.getConnection(dbURL, user, pass);
				try {
					PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS SocialCore (name CHAR(25), PRIMARY KEY(name), race CHAR(25), lastChange BIGINT, gender CHAR(20), marriedTo CHAR(30), engagedTo CHAR(30), isMarried boolean, isEngaged boolean)");
					preparedStatement.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS SocialCore_marriages (name CHAR(70), PRIMARY KEY(name), husband CHAR(25), wife CHAR(25), date CHAR(35), priest CHAR(35))");
					preparedStatement.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS SocialCore_engagements (name CHAR(70), PRIMARY KEY(name), husband CHAR(25), wife CHAR(25), date CHAR(35), time BIGINT(35))");
					preparedStatement.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS SocialCore_divorces (name CHAR(70), PRIMARY KEY(name), husband CHAR(25), wife CHAR(25), date CHAR(35), filedBy CHAR(35))");
					preparedStatement.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					PreparedStatement preparedStatement = connection.prepareStatement("ALTER TABLE SocialCore ADD (gender CHAR(20), marriedTo CHAR(30), engagedTo CHAR(30), isMarried boolean, isEngaged boolean);");
					preparedStatement.execute();
				}catch (Exception e) {}

				try {
					PreparedStatement preparedStatement = connection.prepareStatement("alter ignore table SocialCore ADD (pet_name char(30));");
					preparedStatement.execute();
				}catch (Exception e) {}
                try {
                    String sql = "-- Create syntax for TABLE 'courts_case_history'\n" +
                            "CREATE TABLE `courts_case_history` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `case_id` int(11) DEFAULT NULL,\n" +
                            "  `date` datetime DEFAULT NULL,\n" +
                            "  `case_status` varchar(25) DEFAULT NULL,\n" +
                            "  `responsible` varchar(60) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  KEY `case_id` (`case_id`),\n" +
                            "  KEY `date` (`date`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;\n" +
                            "\n" +
                            "-- Create syntax for TABLE 'courts_case_resolves'\n" +
                            "CREATE TABLE `courts_case_resolves` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `case_id` int(11) DEFAULT NULL,\n" +
                            "  `resolve` text,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `case_id` (`case_id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;\n" +
                            "\n" +
                            "-- Create syntax for TABLE 'courts_cases'\n" +
                            "CREATE TABLE `courts_cases` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `case_status` varchar(20) DEFAULT NULL,\n" +
                            "  `plaintiff_id` int(11) DEFAULT NULL,\n" +
                            "  `defendant_id` int(11) DEFAULT NULL,\n" +
                            "  `case_category` varchar(64) DEFAULT NULL,\n" +
                            "  `case_meta` text,\n" +
                            "  `case_book_blob` text,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;\n" +
                            "\n" +
                            "-- Create syntax for TABLE 'courts_citizens'\n" +
                            "CREATE TABLE `courts_citizens` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `uuid` char(36) DEFAULT NULL,\n" +
                            "  `name` varchar(64) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `uuid` (`uuid`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;\n" +
                            "\n" +
                            "-- Create syntax for TABLE 'courts_court_dates'\n" +
                            "CREATE TABLE `courts_court_dates` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `case_id` int(11) DEFAULT NULL,\n" +
                            "  `judge_id` int(11) DEFAULT NULL,\n" +
                            "  `date` datetime DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `case_id_2` (`case_id`),\n" +
                            "  KEY `case_id` (`case_id`),\n" +
                            "  KEY `judge_id` (`judge_id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;\n" +
                            "\n" +
                            "-- Create syntax for TABLE 'courts_fines'\n" +
                            "CREATE TABLE `courts_fines` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `sender_id` int(11) DEFAULT NULL,\n" +
                            "  `rec_id` int(11) DEFAULT NULL,\n" +
                            "  `amount` int(11) DEFAULT NULL,\n" +
                            "  `amount_paid` int(11) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;\n" +
                            "\n" +
                            "-- Create syntax for TABLE 'courts_judges'\n" +
                            "CREATE TABLE `courts_judges` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `citizen_id` int(11) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;\n" +
                            "\n" +
                            "-- Create syntax for TABLE 'courts_secretaries'\n" +
                            "CREATE TABLE `courts_secretaries` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `citizen_id` int(11) DEFAULT NULL,\n" +
                            "  `judge_id` int(11) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;\n" +
                            "\n" +
                            "-- Create syntax for TABLE 'courts_stalls'\n" +
                            "CREATE TABLE `courts_stalls` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `type` varchar(20) DEFAULT NULL,\n" +
                            "  `location` varchar(64) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;\n" +
                            "\n" +
                            "-- Create syntax for TABLE 'courts_votes'\n" +
                            "CREATE TABLE `courts_votes` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `approval` tinyint(1) DEFAULT NULL,\n" +
                            "  `citizen_id` int(11) DEFAULT NULL,\n" +
                            "  `voter_uuid` char(36) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `citizen_id` (`citizen_id`,`voter_uuid`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=141 DEFAULT CHARSET=utf8;\n" +
                            "\n" +
                            "-- Create syntax for TABLE 'election_candidates'\n" +
                            "CREATE TABLE `election_candidates` (\n" +
                            "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,\n" +
                            "  `citizen_id` int(11) DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`),\n" +
                            "  UNIQUE KEY `citizen_id` (`citizen_id`)\n" +
                            ") ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;";
                    for (String sqlL : sql.split(";")) {
                        PreparedStatement preparedStatement = connection.prepareStatement(sqlL);
                        preparedStatement.execute();
                    }

                }catch (Exception e) {}
				return connection;
			}else {
				return connection;
			}
		}catch (Exception e) {
			e.printStackTrace();
			SocialCore.plugin.getLogger().severe("SQL Server down!");
		}
		return null;
	}
	public void fixTables() {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("alter table SocialCore_engagements modify name VARCHAR(70)");
			preparedStatement.execute();
		}catch (Exception e) {}
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("alter table SocialCore_divorces modify name VARCHAR(70)");
			preparedStatement.execute();
		}catch (Exception e) {}
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("alter table SocialCore_marriages modify name VARCHAR(70)");
			preparedStatement.execute();
		}catch (Exception e) {}
	}
	public long getLastRaceChange(String name) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT lastChange FROM SocialCore WHERE name = ?");
			preparedStatement.setString(1,name);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				return rs.getLong("lastChange");
			}
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return 0;
	}
	public SocialPlayer getSocialPlayer(String name) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM SocialCore WHERE name = ?");
			preparedStatement.setString(1,name);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				String race = rs.getString("race");
				if (race != null && race == "")
					race = null;
				long lastChange = rs.getLong("lastChange");
				String gender = rs.getString("gender");
				if (gender == null) {
					gender = "UNSPECIFIED";
				}else {
					gender = gender.toUpperCase();
				}
				String marriedTo = rs.getString("marriedTo");
				if (marriedTo == null)
					marriedTo = "";
				String engagedTo = rs.getString("engagedTo");
				if (engagedTo == null)
					engagedTo = "";
				boolean isMarried = rs.getBoolean("isMarried");
				boolean isEngaged = rs.getBoolean("isEngaged");
				SocialPlayer socialPlayer = new SocialPlayer(name);
				socialPlayer.setEngaged(isEngaged);
				socialPlayer.setEngagedTo(engagedTo);
				socialPlayer.setMarried(isMarried);
				socialPlayer.setMarriedTo(marriedTo);
				socialPlayer.setGender(SocialCore.Gender.valueOf(gender));
				socialPlayer.setRace(race);
				socialPlayer.setLastRaceChange(lastChange);
				socialPlayer.setPetName(rs.getString("pet_name"));
				return socialPlayer;
			}
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
        this.create(name);
        return new SocialPlayer(name);
	}
	public List<String> getSocialPlayers() {
		Connection conn = getConnection();
		List<String> socialPlayers = new ArrayList<String>();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT name FROM SocialCore");
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				socialPlayers.add(rs.getString("name"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return socialPlayers;
	}

	/*public String getRace(String name) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT race FROM SocialCore WHERE name = ?");
			preparedStatement.setString(1,name);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				String race = rs.getString("race");
				if (race == null || race.equals(""))
					return null;
				return rs.getString("race");
			}
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return null;
	}*/
	public void create(String name) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SocialCore (name,race) VALUES(?,?)");
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, "DEFAULT");
			preparedStatement.execute();
		} catch (SQLException e) {
		}
	}
	/*public void setRace(String name, String race) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("UPDATE SocialCore SET race = ? WHERE name = ?");
			preparedStatement.setString(1, race);
			preparedStatement.setString(2, name);
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}*/
	/*public void setLastRaceChange(String name, long time) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("UPDATE SocialCore SET lastChange = ? WHERE name = ?");
			preparedStatement.setLong(1, time);
			preparedStatement.setString(2, name);
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	} */
	public void syncSocialPlayer(SocialPlayer socialPlayer) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("UPDATE SocialCore SET race = ?, lastChange = ?, gender = ?, marriedTo = ?, engagedTo = ?, isMarried = ?, isEngaged = ?,pet_name = ? WHERE name = ?");
			String race = null;
			if (socialPlayer.getRace() != null)
				race = socialPlayer.getRace().getName().toLowerCase();
			preparedStatement.setString(1, race);
			preparedStatement.setLong(2, socialPlayer.getLastRaceChange());
			preparedStatement.setString(3, socialPlayer.getGender().toString());
			preparedStatement.setString(4, socialPlayer.getMarriedTo());
			preparedStatement.setString(5, socialPlayer.getEngagedTo());
			preparedStatement.setBoolean(6, socialPlayer.isMarried());
			preparedStatement.setBoolean(7, socialPlayer.isEngaged());
			preparedStatement.setString(8, socialPlayer.getPetName());
			preparedStatement.setString(9, socialPlayer.getPlayerName());
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}
	public Marriage getMarriage(SocialPlayer husband, SocialPlayer wife) {
		Marriage marriage = new Marriage(husband,wife);
		String marriageName = husband.getPlayerName()+Marriage.NAME_DELIMITER+wife.getPlayerName();
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM SocialCore_marriages WHERE name = ?");
			preparedStatement.setString(1,marriageName);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				marriage.setDate(rs.getString("date"));
				marriage.setPriest(rs.getString("priest"));
			}
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return marriage;
	}
	public void saveMarriage(Marriage marriage) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SocialCore_marriages (name,husband,wife,priest,date) values(?,?,?,?,?)");
			preparedStatement.setString(1, marriage.getName());
			preparedStatement.setString(2, marriage.getHusband().getPlayerName());
			preparedStatement.setString(3, marriage.getWife().getPlayerName());
			preparedStatement.setString(4, marriage.getPriest());
			preparedStatement.setString(5, marriage.getDate());
			preparedStatement.execute();
		} catch (Exception e) {
		}
	}
	public void saveMarriage(Marriage marriage, boolean debug) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SocialCore_marriages (name,husband,wife,priest,date) values(?,?,?,?,?)");
			preparedStatement.setString(1, marriage.getName());
			preparedStatement.setString(2, marriage.getHusband().getPlayerName());
			preparedStatement.setString(3, marriage.getWife().getPlayerName());
			preparedStatement.setString(4, marriage.getPriest());
			preparedStatement.setString(5, marriage.getDate());
			preparedStatement.execute();
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
		}
	}
	public List<String> getAllMarriageNames() {
		List<String> marriageNames = new ArrayList<String>();
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT name FROM SocialCore_marriages");
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				marriageNames.add(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return marriageNames;
	}
	public Engagement getEngagement(SocialPlayer husband, SocialPlayer wife) {
		Engagement engagement = new Engagement(husband, wife);
		String engagementName = husband.getPlayerName()+Marriage.NAME_DELIMITER+wife.getPlayerName();
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM SocialCore_engagements WHERE name = ?");
			preparedStatement.setString(1,engagementName);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				engagement.setDate(rs.getString("date"));
				engagement.setTime(rs.getLong("time"));
			}
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return engagement;
	}
	public void saveEngagement(Engagement engagement) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SocialCore_engagements (name, husband, wife, date, time) values(?,?,?,?,?)");
			preparedStatement.setString(1, engagement.getName());
			preparedStatement.setString(2, engagement.getFHusband().getPlayerName());
			preparedStatement.setString(3, engagement.getFWife().getPlayerName());
			preparedStatement.setString(4, engagement.getDate());
			preparedStatement.setLong(5, engagement.getTime());
			preparedStatement.execute();
		} catch (Exception e) {
		}
	}
	public List<String> getAllEngagementNames() {
		List<String> engagementNames = new ArrayList<String>();
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT name FROM SocialCore_engagements");
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				engagementNames.add(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return engagementNames;
	}

	public Divorce getDivorce(SocialPlayer husband, SocialPlayer wife) {
		Divorce divorce= new Divorce(husband, wife);
		String divorceName = husband.getPlayerName()+Marriage.NAME_DELIMITER+wife.getPlayerName();
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM SocialCore_divorces WHERE name = ?");
			preparedStatement.setString(1,divorceName);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				divorce.setDate(rs.getString("date"));
				divorce.setFiledBy(rs.getString("filedBy"));
			}
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return divorce;
	}
	public void saveDivorce(Divorce divorce) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SocialCore_divorces (name, husband, wife, date, filedby) values(?,?,?,?,?)");
			preparedStatement.setString(1, divorce.getName());
			preparedStatement.setString(2, divorce.getExhusband().getPlayerName());
			preparedStatement.setString(3, divorce.getExwife().getPlayerName());
			preparedStatement.setString(4, divorce.getDate());
			preparedStatement.setString(5, divorce.getFiledBy());
			preparedStatement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public List<String> getAllDivorceNames() {
		List<String> divorceNames = new ArrayList<String>();
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT name FROM SocialCore_divorces");
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				divorceNames.add(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return divorceNames;
	}
	public void deleteDivorce(Divorce divorce) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM SocialCore_divorces WHERE name = ?");
			preparedStatement.setString(1, divorce.getName());
			preparedStatement.execute();
		}catch (Exception e) {}
	}
	public void deleteMarriage(Marriage marriage) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM SocialCore_marriages WHERE name = ?");
			preparedStatement.setString(1, marriage.getName());
			preparedStatement.execute();
		}catch (Exception e) {}
	}
	public void deleteEngagement(Engagement engagement) {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM SocialCore_engagements WHERE name = ?");
			preparedStatement.setString(1, engagement.getName());
			preparedStatement.execute();
		}catch (Exception e) {}
	}
	public void fixEngagements() {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT name, gender, engagedTo FROM SocialCore WHERE isEngaged=1");
			ResultSet rs = preparedStatement.executeQuery();
			List<String> peopleCovered = new ArrayList<String>();
			while (rs.next()) {
				String name = rs.getString("name");
				String engagedTo = rs.getString("engagedTo");
				String genderString = rs.getString("gender");
				SocialCore.Gender gender = SocialCore.Gender.valueOf(genderString);
				Engagement engagement = null;
				if (gender.equals(SocialCore.Gender.MALE)) {
					SocialPlayer husband = plugin.save.getSocialPlayer(name);
					SocialPlayer wife = plugin.save.getSocialPlayer(engagedTo);
					if (husband != null && wife != null) {
						engagement = new Engagement(husband, wife);
						String dateBuilder = getMonth()+" "+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+", "+Calendar.getInstance().get(Calendar.YEAR);
						engagement.setDate(dateBuilder);
					}
				}else {
					SocialPlayer wife = plugin.save.getSocialPlayer(name);
					SocialPlayer husband = plugin.save.getSocialPlayer(engagedTo);
					if (husband != null && wife != null) {
						engagement = new Engagement(husband, wife);
						String dateBuilder = getMonth()+" "+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+", "+Calendar.getInstance().get(Calendar.YEAR);
						engagement.setDate(dateBuilder);
					}
				}
				if (engagement != null) {
					if (!peopleCovered.contains(engagement.getFWife()) && !peopleCovered.contains(engagement.getFHusband())) {
						saveEngagement(engagement);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String getMonth() {
		switch (Calendar.getInstance().get(Calendar.MONTH)) {
			case 0 : return "January";
			case 1 : return "February";
			case 2: return "March";
			case 3 : return "April";
			case 4 : return "May";
			case 5 : return "June";
			case 6 : return "July";
			case 7 : return "August";
			case 8 : return "September";
			case 9 : return "October";
			case 10 : return "November";
			case 11 : return "December";
			default : return "ERROR";
		}
	}
	public void fixMarriages() {
		Connection conn = getConnection();
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("SELECT name, gender, marriedTo FROM SocialCore WHERE isMarried=1");
			ResultSet rs = preparedStatement.executeQuery();
			List<String> peopleCovered = new ArrayList<String>();
			while (rs.next()) {
				String name = rs.getString("name");
				String marriedTo = rs.getString("marriedTo");
				String genderString = rs.getString("gender");
				SocialCore.Gender gender = SocialCore.Gender.valueOf(genderString);
				Marriage marriage = null;
				if (gender.equals(SocialCore.Gender.MALE)) {
					SocialPlayer husband = plugin.save.getSocialPlayer(name);
					SocialPlayer wife = plugin.save.getSocialPlayer(marriedTo);
					if (husband != null && wife != null) {
						marriage = new Marriage(husband, wife);
						String dateBuilder = getMonth()+" "+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+", "+Calendar.getInstance().get(Calendar.YEAR);
						marriage.setDate(dateBuilder);
						marriage.setPriest("Unknown");
					}
				}else {
					SocialPlayer wife = plugin.save.getSocialPlayer(name);
					SocialPlayer husband = plugin.save.getSocialPlayer(marriedTo);
					if (husband != null && wife != null) {
						marriage = new Marriage(husband, wife);
						String dateBuilder = getMonth()+" "+ Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+", "+Calendar.getInstance().get(Calendar.YEAR);
						marriage.setDate(dateBuilder);
						marriage.setPriest("Unknown");
					}
				}
				if (marriage != null) {
					if (!peopleCovered.contains(marriage.getWife()) && !peopleCovered.contains(marriage.getHusband())) {
						saveMarriage(marriage, true);
						peopleCovered.add(marriedTo);
						peopleCovered.add(name);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}






}
