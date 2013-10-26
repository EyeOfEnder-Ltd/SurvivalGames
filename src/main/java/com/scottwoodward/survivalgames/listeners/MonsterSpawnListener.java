package com.scottwoodward.survivalgames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class MonsterSpawnListener implements Listener {
	
	@EventHandler
	public void onMonsterSpawn(CreatureSpawnEvent event){
			event.setCancelled(true);
	}

}
