package com.scottwoodward.survivalgames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.Spectatable;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class PlayerHungerListener implements Listener {

	@EventHandler
	public void onPlayerFoodLevelChange(FoodLevelChangeEvent event){
		Player player = (Player)event.getEntity();
		if(PlayerManager.getInstance().isInGame(player.getName())){
			int index = PlayerManager.getInstance().getGame(player.getName());
			SurvivalGame game = (SurvivalGame) GameManager.getInstance().getGame(index);
			if(game.getPhase() != Phase.PREGAME){
				if(game instanceof Spectatable && ((Spectatable)game).isSpectator(player.getName())){
					event.setCancelled(true);
				}
			}else{
				event.setCancelled(true);
			}
		}else{
			event.setCancelled(true);
		}
	}
}
