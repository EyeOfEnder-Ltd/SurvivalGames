package com.scottwoodward.survivalgames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import com.scottwoodward.survivalgames.game.Game;
import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.Spectatable;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class MonsterTargetListener implements Listener {

	@EventHandler
	public void onMonsterTarget(EntityTargetLivingEntityEvent event){
		if(event.getTarget() instanceof Player){
			Player player = (Player)event.getTarget();
			if(PlayerManager.getInstance().isInGame(player.getName())){
				int index = PlayerManager.getInstance().getGame(player.getName());
				SurvivalGame game = (SurvivalGame) GameManager.getInstance().getGame(index);
				if(game.getPhase() != Phase.PREGAME){
					if(game instanceof Spectatable && ((Spectatable)game).isSpectator(player.getName())){
						event.setCancelled(true);
					}else if(game instanceof SurvivalGame && game.getPhase() == Phase.STARTING){
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
}
