package com.scottwoodward.survivalgames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class PlayerPickupEntitiesListener implements Listener {

	@EventHandler
	public void onEntityPickup(PlayerPickupItemEvent event){
		Player player = event.getPlayer();
		if(PlayerManager.getInstance().isInGame(player.getName())){
			int index = PlayerManager.getInstance().getGame(player.getName());
			SurvivalGame game = (SurvivalGame) GameManager.getInstance().getGame(index);
			if(game.isSpectator(player.getName())){
				event.setCancelled(true);
			}
		}
	}
}
