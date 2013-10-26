package com.scottwoodward.survivalgames.datastore;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.scottwoodward.survivalgames.SurvivalGames;


public class DataStoreManager {

	private static DataStoreManager instance;
	private static  String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static  String DB_URL = "localhost";
	private static  String USERNAME = "root";
	private static  String PASSWORD = "smw8744mysgin";
	private static  String DATABASE = "eoe_survival_games";
	private static  String DB_PORT = "3306";

	private DataStoreManager(){
		String createPlayers = "CREATE TABLE IF NOT EXISTS PLAYERS (USERNAME VARCHAR(20) NOT NULL PRIMARY KEY, POINTS INT(5), TOKENS INT(5));";
		String createSpawns = "CREATE TABLE IF NOT EXISTS SPAWNS (ID INT(5) NOT NULL AUTO_INCREMENT PRIMARY KEY, WORLD VARCHAR(30), X FLOAT, Y FLOAT, Z FLOAT, PITCH FLOAT, YAW FLOAT);";
		String createMemberships = "CREATE TABLE IF NOT EXISTS noodles_memberships (NAME VARCHAR(20) NOT NULL, COLOR VARCHAR(4));";

		try{
			File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
			YamlConfiguration config = new YamlConfiguration();

			config.load(file);
			DB_URL = config.getString("DatabaseURL");
			DATABASE = config.getString("DatabaseName");
			USERNAME = config.getString("DatabaseUser");
			PASSWORD = config.getString("DatabasePassword");
			DB_PORT = config.getString("DatabasePort");

			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Initializing database");
			Connection con = getConnection();
			Statement statement = con.createStatement();
			statement.execute(createSpawns);
			statement.execute(createPlayers);
			statement.execute(createMemberships);
			con.close();
			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Database initialized");

		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public static DataStoreManager getInstance(){
		if(instance == null){
			instance = new DataStoreManager();
		}
		return instance;
	}

	public boolean isFirstTimePlayer(String playerName){
		try{
			Connection con = getConnection();
			String query = "SELECT POINTS FROM PLAYERS WHERE USERNAME = ?";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, playerName.toLowerCase());
			ResultSet rs = statement.executeQuery();
			if(rs.next()){
				rs.close();
				con.close();
				return false;
			}
			rs.close();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "New Player: " + playerName);
		return true;
	}

	public int getPlayerPoints(String playerName){
		if(isFirstTimePlayer(playerName)){
			return 100;
		}else{
			try{
				Connection con = getConnection();
				String query = "SELECT POINTS FROM PLAYERS WHERE USERNAME = ?";
				PreparedStatement statement = con.prepareStatement(query);
				statement.setString(1, playerName.toLowerCase());
				ResultSet rs = statement.executeQuery();
				while(rs.next()){
					int result = rs.getInt("POINTS");
					rs.close();
					con.close();
					return result;
				}
				rs.close();
				con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return 0;
	}

	public int getPlayerPasses(String playerName){
		if(isFirstTimePlayer(playerName)){
			return 0;
		}else{
			try{
				Connection con = getConnection();
				String query = "SELECT TOKENS FROM PLAYERS WHERE USERNAME = ?";
				PreparedStatement statement = con.prepareStatement(query);
				statement.setString(1, playerName.toLowerCase());
				ResultSet rs = statement.executeQuery();
				while(rs.next()){
					int result = rs.getInt("TOKENS");
					rs.close();
					con.close();
					return result;
				}
				rs.close();
				con.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return 0;
	}

	public void setPlayerPoints(String playerName, int newPoints){
		try{
			Connection con = getConnection();
			String query = "INSERT INTO PLAYERS (username, points) VALUES(?, ?) ON DUPLICATE KEY UPDATE POINTS=VALUES(POINTS);";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, playerName.toLowerCase());
			statement.setInt(2, newPoints);
			statement.executeUpdate();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void setPlayerPasses(String playerName, int newPoints){
		try{
			Connection con = getConnection();
			String query = "INSERT INTO PLAYERS (username, tokens) VALUES(?, ?) ON DUPLICATE KEY UPDATE TOKENS=VALUES(TOKENS);";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, playerName.toLowerCase());
			statement.setInt(2, newPoints);
			statement.executeUpdate();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void addResurrectionPass(String playerName){
		try{
			Connection con = getConnection();
			String query = "UPDATE PLAYERS SET PASSES = ? WHERE USERNAME = ?";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setInt(1, getResurrectionPass(playerName) + 1);
			statement.setString(2, playerName.toLowerCase());
			statement.executeUpdate();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void useResurrectionPass(String playerName){
		try{
			Connection con = getConnection();
			String query = "UPDATE PLAYERS SET PASSES = ? WHERE USERNAME = ?";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setInt(1, getResurrectionPass(playerName) - 1);
			statement.setString(2, playerName.toLowerCase());
			statement.executeUpdate();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private int getResurrectionPass(String playerName){
		try{
			Connection con = getConnection();
			String query = "SELECT PASS FROM PLAYERS WHERE USERNAME = ?";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, playerName.toLowerCase());
			ResultSet rs = statement.executeQuery();
			while(rs.next()){
				int result = rs.getInt("PASSES");
				rs.close();
				con.close();
				return result;
			}
			rs.close();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}

	public Connection getConnection(){
		Connection con = null;
		try{
			Class.forName(DB_DRIVER);
			con = DriverManager.getConnection("jdbc:mysql://" + DB_URL + ":" + DB_PORT + "/" + DATABASE, USERNAME, PASSWORD);
		}catch(Exception e){
			e.printStackTrace();
		}
		return con;
	}

	public void addSpawn(Location loc){
		try{
			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Adding spawn point to world " + loc.getWorld().getName());

			Connection con = getConnection();
			String query = "INSERT INTO SPAWNS(WORLD, X, Y, Z, PITCH, YAW) VALUES(?, ?, ?, ?, ?, ?);";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, loc.getWorld().getName());
			statement.setFloat(2, (float)loc.getX());
			statement.setFloat(3, (float)loc.getY());
			statement.setFloat(4, (float)loc.getZ());
			statement.setFloat(5, (float)loc.getPitch());
			statement.setFloat(6, (float)loc.getYaw());
			statement.execute();
			con.close();
			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Done adding spawn point");

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public List<Location> getSpawns(String worldName, String tempWorld){
		try{
			List<Location> locs = new ArrayList<Location>();
			Connection con = getConnection();
			String query = "SELECT * FROM SPAWNS WHERE WORLD=?";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, worldName);
			ResultSet rs = statement.executeQuery();
			while(rs.next()){
				String world = tempWorld;
				double x = rs.getDouble("X");
				double y = rs.getDouble("Y");
				double z = rs.getDouble("Z");
				float pitch = rs.getFloat("PITCH");
				float yaw = rs.getFloat("YAW");
				Location spawn = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
				locs.add(spawn);
			}

			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Getting " + locs.size() + " spawn points for world " + worldName);
			rs.close();
			con.close();
			return locs;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public String loadNameColor(String name) {
		try{
			Connection con = getConnection();
			String query = "SELECT COLOR FROM noodles_memberships WHERE NAME=?";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, name);
			ResultSet rs = statement.executeQuery();
			while(rs.next()){
				return rs.getString("COLOR");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "&f";

	}
}
