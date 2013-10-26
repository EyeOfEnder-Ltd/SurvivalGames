package com.scottwoodward.survivalgames.game;

import java.util.Set;

public interface Spectatable {

	void addSpectator(String name);
	void removeSpectator(String name);
	public boolean isSpectator(String name);
	public Set<String> getAllSpectators();
	
}
