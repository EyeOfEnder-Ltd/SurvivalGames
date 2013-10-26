package com.scottwoodward.survivalgames.game;

import java.util.Set;

public abstract class Game {

	private int gameNumber;
	private int world;
	
	Game(int gameNumber, int world){
		this.gameNumber = gameNumber;
		this.world = world;
	}
	
	public abstract void joinGame(String name);
	public abstract void leaveGame(String name, boolean died);
	public abstract Set<String> getAllPlayers();
	
	public int getGameNumber(){
		return gameNumber;
	}

	
	public int getWorld(){
		return world;
	}
	
	
	
}
