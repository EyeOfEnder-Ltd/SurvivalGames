package com.scottwoodward.survivalgames.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.worlds.WorldManager;

public class GameManager {

	private static GameManager instance;
	private List<Game> games;

	private GameManager(){
		games = new ArrayList<Game>();
	}

	public static GameManager getInstance(){
		if(instance == null){
			instance = new GameManager();
		}
		return instance;
	}

	public void startNewGame(int world, int number){
		int index = games.size();
		WorldManager.getInstance().setGameWorld(index, world);
		String worldname = WorldManager.getInstance().getGameWorlds().get(world);
		Game game = new SurvivalGame(index, world, worldname + ", lobby " + number);
		WorldManager.getInstance().createLobby(index);
		games.add(game);
		try{
			String name = WorldManager.getInstance().getGameWorld(world);
			File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			int radius = config.getInt(name + ".radius", 50);
			((SurvivalGame)game).setDMRadius(radius);
		}catch(Exception e){
			e.printStackTrace();
		}
		//game.startGame();
	}

	public void init(){
		if(SurvivalGames.getInstance().getInitialized()){
			return;
		}
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Initializing server with " + getNumberOfGamesFromConfig() + " games.");
		for(int i = 0; i < WorldManager.getInstance().getGameWorlds().size(); i++){
			for(int j = 0; j < getNumberOfGamesFromConfig(); j++){
				startNewGame(i, j);

			}
		}
		SurvivalGames.getInstance().setInitialized(true);
		
	}

	public Game getGame(int index){
		return games.get(index);
	}

	public List<Game> getGames(){
		return games;
	}

	public List<Integer> getGamesWithWorld(int world){
		List<Integer> list = new ArrayList<Integer>();
		for(Game game : games){
			if(game.getWorld() == world){
				list.add(game.getGameNumber());
			}
		}
		return list;
	}

	private int getNumberOfGamesFromConfig(){
		int numberOfGames = 0;
		File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
		YamlConfiguration config = new YamlConfiguration();
		try{
			config.load(file);
			numberOfGames = config.getInt("NumberOfGames");
		}catch(Exception e){
			e.printStackTrace();
		}
		return numberOfGames;
	}
}
