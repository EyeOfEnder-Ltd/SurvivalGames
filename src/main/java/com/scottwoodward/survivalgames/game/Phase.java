package com.scottwoodward.survivalgames.game;

public enum Phase {
	
	PREGAME(180), STARTING(30), IN_PROGRESS(-1), DEATHMATCH_COUNTDOWN(60), DEATHMATCH_WARMUP(10), DEATHMATCH(180), WINNER_SHOWCASE(10);
	
	private int timer;
	
	private Phase(int seconds){
		timer = seconds;
	}
	
	int getTimer(){
		return timer;
	}

}
