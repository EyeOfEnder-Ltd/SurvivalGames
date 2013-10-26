package com.scottwoodward.survivalgames.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class ExplosionListener implements Listener{
	
	@EventHandler
	public void onExplode(ExplosionPrimeEvent event){
		if(event.getEntityType() == EntityType.PRIMED_TNT){
			event.setRadius(3);
		}
	}

}
