package com.scottwoodward.survivalgames.worlds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.minecraft.server.v1_6_R3.MinecraftServer;
import net.minecraft.server.v1_6_R3.RegionFile;
import net.minecraft.server.v1_6_R3.RegionFileCache;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.entity.Player;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class WorldManager {

	private static WorldManager instance;

	private List<String> worlds;
	private String hubWorld;
	private String lobbyWorld;
	private String deathmatchWorld;
	private Map<Integer, Integer> maps;

	@SuppressWarnings("rawtypes")
	protected HashMap regionfiles;
	protected Field rafField;

	private WorldManager(){
		bindRegionFiles();
		worlds = new ArrayList<String>();
		maps = new HashMap<Integer, Integer>();
		loadAllWorlds();
	}

	public static WorldManager getInstance(){
		if(instance == null){
			instance = new WorldManager();
		}
		return instance;
	}

	private void loadAllWorlds(){
		File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
		YamlConfiguration config = new YamlConfiguration();
		try{
			config.load(file);
			worlds = config.getStringList("GameWorlds");
			for(String world : worlds){
				SurvivalGames.getInstance().getLogger().log(Level.INFO, world + ": loaded");
			}
			lobbyWorld = config.getString("LobbyWorld");
			hubWorld = config.getString("HubWorld");
			deathmatchWorld = config.getString("DeathmatchWorld");
			Bukkit.createWorld(new WorldCreator(hubWorld));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getLobbyWorld(){
		return lobbyWorld;
	}

	public String getHubworld(){
		return hubWorld;
	}

	public void setGameWorld(int gameNumber, int worldNumber){
		maps.put(gameNumber, worldNumber);
	}

	public String getGameWorld(int gameNumber){
		int number = maps.get(gameNumber);
		return worlds.get(number);
	}

	public List<Integer> getAllGamesWithMap(int map){
		List<Integer> games = new ArrayList<Integer>();
		for(Entry<Integer, Integer> entry : maps.entrySet()){
			if(entry.getValue() == map){
				games.add(entry.getKey());
			}
		}
		return games;
	}

	public void createLobby(int gameNumber){
		try {
			String name = "GameLobby" + gameNumber;
			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Creating world " + name);
			FileUtils.copyDirectory(new File(lobbyWorld), new File(name));
			if(new File(name + "/uid.dat").delete()){
				System.out.println("uid.dat successfully deleted");
			}
			World world = Bukkit.createWorld(new WorldCreator(name));
			world.setDifficulty(Difficulty.PEACEFUL);
			world.setMonsterSpawnLimit(0);
			world.setAnimalSpawnLimit(0);
		}catch(FileNotFoundException e){

		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resetLobby(final int gameNumber){
		final String name = "GameLobby" + gameNumber;
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Resetting world " + name);
		forceUnloadWorld(Bukkit.getWorld(name));
		//Bukkit.unloadWorld(name, false);
		clearWorldReference(name);
		try{
			FileUtils.deleteDirectory(new File(name));
		}catch(FileNotFoundException e){

		}catch (IOException e){
			e.printStackTrace();
		}

		createLobby(gameNumber);
	}

	public void createGameWorld(int gameNumber, int worldNumber){
		try {
			deleteGameWorld(gameNumber);
			String name = "GameWorld" + gameNumber;
			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Creating world " + name);
			FileUtils.copyDirectory(new File(worlds.get(worldNumber)), new File(name));
			if(new File(name + "/uid.dat").delete()){
				System.out.println("uid.dat successfully deleted");
			}
			World world = Bukkit.createWorld(new WorldCreator(name));
			world.setMonsterSpawnLimit(0);
			world.setAnimalSpawnLimit(0);
		}catch(FileNotFoundException e){

		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteGameWorld(int gameNumber){
		final String name = "GameWorld" + gameNumber;
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Deleting world " + name);
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Unloading world " + name);

		forceUnloadWorld(Bukkit.getWorld(name));
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Clearing references to world " + name);

		//Bukkit.unloadWorld(name, false);
		clearWorldReference(name);
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Deleting directory of world " + name);

		try{
			FileUtils.deleteDirectory(new File(name));
		}catch(FileNotFoundException e){

		}catch (IOException e){
			e.printStackTrace();
		}

	}

	public void createDeathMatch(int gameNumber){
		try {
			String name = "DeathMatch" + gameNumber;
			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Creating world " + name);
			FileUtils.copyDirectory(new File(deathmatchWorld), new File(name));
			if(new File(name + "/uid.dat").delete()){
				System.out.println("uid.dat successfully deleted");
			}
			World world = Bukkit.createWorld(new WorldCreator(name));
			world.setDifficulty(Difficulty.PEACEFUL);
			world.setMonsterSpawnLimit(0);
			world.setAnimalSpawnLimit(0);
		}catch(FileNotFoundException e){

		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void resetDeathMatch(final int gameNumber){
		final String name = "DeathMatch" + gameNumber;
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Resetting world " + name);
		forceUnloadWorld(Bukkit.getWorld(name));
		//Bukkit.unloadWorld(name, false);
		clearWorldReference(name);
		try{
			FileUtils.deleteDirectory(new File(name));
		}catch(FileNotFoundException e){

		}catch (IOException e){
			e.printStackTrace();
		}
		createDeathMatch(gameNumber);
	}

	public List<String> getGameWorlds(){
		return worlds;
	}

	@SuppressWarnings("rawtypes")
	private void bindRegionFiles(){
		try{
			Field a = RegionFileCache.class.getDeclaredField("a"); // obfuscated
			a.setAccessible(true);
			regionfiles = (HashMap) a.get(null);
			rafField = RegionFile.class.getDeclaredField("c"); // obfuscated
			rafField.setAccessible(true);
		}
		catch (Throwable t){
			t.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public synchronized boolean clearWorldReference(String worldName){
		if (regionfiles == null){
			return false;
		}
		if (rafField == null){
			return false;
		}

		ArrayList<Object> removedKeys = new ArrayList<Object>();
		try{
			for (Object o : regionfiles.entrySet()){
				Map.Entry e = (Map.Entry) o;
				File f = (File) e.getKey();

				if (f.toString().startsWith("." + File.separator + worldName)){
					RegionFile file = (RegionFile) e.getValue();
					try{
						RandomAccessFile raf = (RandomAccessFile) rafField.get(file);
						raf.close();
						removedKeys.add(f);
					}
					catch (Exception ex){
						ex.printStackTrace();
					}
				}
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		for (Object key : removedKeys){
			regionfiles.remove(key);
		}

		return true;
	}

	public void forceUnloadWorld(org.bukkit.World world){
		if(world == null){
			System.out.println("Null world, not unloading");
			return;
		}
		world.setAutoSave(false);
		for ( Player player : world.getPlayers() ){
			World hub = Bukkit.createWorld(new WorldCreator(hubWorld));
			player.teleport(hub.getSpawnLocation());
			if(PlayerManager.getInstance().isInGame(player.getName())){
				int index = PlayerManager.getInstance().getGame(player.getName());
				GameManager.getInstance().getGame(index).leaveGame(player.getName(), false);
			}
		}
		CraftServer server = (CraftServer)Bukkit.getServer();
		CraftWorld craftWorld = (CraftWorld)world;

		try{
			Field f = server.getClass().getDeclaredField("worlds");
			f.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<String, org.bukkit.World> worlds = (Map<String, org.bukkit.World>)f.get(server);
			worlds.remove(world.getName().toLowerCase());
			f.setAccessible(false);
		}
		catch ( IllegalAccessException ex ){
		}
		catch  ( NoSuchFieldException ex ){
		}

		MinecraftServer ms = ((CraftServer)Bukkit.getServer()).getServer();
		ms.worlds.remove(ms.worlds.indexOf(craftWorld.getHandle()));
	}
}
