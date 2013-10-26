package com.scottwoodward.survivalgames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockExplosionListener implements Listener{
	
	@EventHandler
	public void onBlockExplosion(EntityExplodeEvent event){
		if(event.blockList().size() > 0){
			event.blockList().clear();
		}
	}

}
