package com.scottwoodward.survivalgames.players;

import java.util.HashMap;
import java.util.Map;

import com.scottwoodward.survivalgames.datastore.DataStoreManager;
import com.scottwoodward.survivalgames.game.GameManager;

public class PlayerManager {

	private static PlayerManager instance;
	private Map<String, Integer> players;
	private Map<String, Integer> points;
	private Map<String, Integer> passes;
	private Map<String, Integer> maps;


	private PlayerManager(){
		players = new HashMap<String, Integer>();
		points = new HashMap<String, Integer>();
		passes = new HashMap<String, Integer>();
		maps = new HashMap<String, Integer>();
	}

	public static PlayerManager getInstance(){
		if(instance == null){
			instance = new PlayerManager();
		}
		return instance;
	}

	public boolean isInGame(String name){
		return players.containsKey(name);
	}

	public int getGame(String name){
		return players.get(name);
	}

	public void setGame(String name, int game){
		players.put(name, game);
		GameManager.getInstance().getGame(game).joinGame(name);
	}

	public void removeGame(String name){
		players.remove(name);
	}

	public int getPoints(String name){
		if(points.containsKey(name)){
			if(points.get(name) < 0){
				return 0;
			}
			return points.get(name);
		}else{
			return DataStoreManager.getInstance().getPlayerPoints(name);
		}
	}

	public void setPoints(String name, int newPoints){
		if(newPoints < 0){
			newPoints = 0;
		}
		if(points.containsKey(name)){
			points.put(name, newPoints);
		}else{
			DataStoreManager.getInstance().setPlayerPoints(name, newPoints);
		}
	}

	public void removePoints(String name){
		points.remove(name);
	}
	
	public void removePasses(String name){
		passes.remove(name);
	}
	
	public void setPasses(String name, int newPasses){
		passes.put(name, newPasses);
	}
	
	public void addPass(String name){
		int points = getNumberOfPasses(name) + 1;
		passes.put(name, points);
		DataStoreManager.getInstance().setPlayerPasses(name, points);
	}
	
	public void removePass(String name){
		int points = getNumberOfPasses(name) - 1;
		passes.put(name, points);
		DataStoreManager.getInstance().setPlayerPasses(name, points);
	}
	
	public int getNumberOfPasses(String name){
		if(passes.containsKey(name)){
			return passes.get(name);
		}
		return 0;
	}
	
	public void setPlayerMap(String name, int map){
		maps.put(name, map);
	}
	
	public int getPlayerMap(String name){
		return maps.get(name);
	}
	
	public void unsetPlayerMap(String name){
		maps.remove(name);
	}
	
	public boolean hasMapSelected(String name){
		return maps.containsKey(name);
	}
}
