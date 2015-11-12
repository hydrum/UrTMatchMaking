package net.haagenti.urtmatchmaking.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.haagenti.urtmatchmaking.Debug;
import net.haagenti.urtmatchmaking.Debug.TAG;
import net.haagenti.urtmatchmaking.match.Match;
import net.haagenti.urtmatchmaking.player.Player;

public class Database {
	
	private static Connection c = null;
	private static Statement stmt = null;
	
	private Database() { }
	
	public static void initConnection() {
		try {
			Debug.Log(TAG.DATABASE, "Setting up Database");
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:urtmatchmaking.db");
		    c.setAutoCommit(true);
			stmt = c.createStatement();
			initTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void initTable() {
		String sql;
		try {
			Debug.Log(TAG.DATABASE, "Initializing Tables");
			sql = "CREATE TABLE IF NOT EXISTS player (urtauth TEXT PRIMARY KEY, elo INTEGER DEFAULT 1000, elochange INTEGER DEFAULT 0)";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS match (ID INTEGER PRIMARY KEY, server REFERENCES server, starttime INTEGER, map TEXT, red_elo INTEGER, blue_elo INTEGER, red_score INTEGER, blue_score INTEGER)";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS player_in_match (ID INTEGER PRIMARY KEY, MID REFERENCES match, player REFRENCES player, team TEXT)";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS config (key TEXT PRIMARY KEY, value TEXT)";
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int getMatchID() {
		String sql;
		ResultSet rs;
		try {
			sql = "SELECT key, value FROM config WHERE key=\"matchid\"";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String matchid = rs.getString("value");
				sql = "UPDATE config SET value=\"" + matchid + "\" WHERE key=\"matchid\"";
				stmt.executeQuery(sql);
				Debug.Log(TAG.DATABASE, "Retrieved matchid: " + matchid);
				return Integer.valueOf(matchid);
			} else {
				sql = "INSERT INTO config (key, value) VALUES (\"matchid\", \"0\")";
				stmt.executeUpdate(sql);
				Debug.Log(TAG.DATABASE, "Created matchid: 0");
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static void createNewPlayer(Player player) {
		String sql;
		try {
			Debug.Log(TAG.DATABASE, "Creating new player: " + player.getUrTAuth());
			sql = "INSERT INTO player (urtauth) VALUES (\"" + player.getUrTAuth() + "\")";
			System.out.println(sql);
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void createMatch(Match match) {
		String sql;
		try {
			Debug.Log(TAG.DATABASE, "Creating new match: " + match.id);
			sql = "INSERT INTO match (ID, starttime, map, red_elo, blue_elo) VALUES (\"" + match.id + "\", " + match.getStartTime() + ", \"" + match.map.name() + "\", " + match.elored + ", " + match.eloblue + ")";
			stmt.executeUpdate(sql);
			for (Player player : match.teamred) {
				sql = "INSERT INTO player_in_match (MID, player, team) VALUES (" + match.id + ", " + player.getUrTAuth() + ", \"red\")";
				stmt.executeUpdate(sql);
			}
			for (Player player : match.teamblue) {
				sql = "INSERT INTO player_in_match (MID, player, team) VALUES (" + match.id + ", " + player.getUrTAuth() + ", \"blue\")";
				stmt.executeUpdate(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void disconnect() {
		try {
			Debug.Log(TAG.DATABASE, "Disconnecting...");
			stmt.close();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
