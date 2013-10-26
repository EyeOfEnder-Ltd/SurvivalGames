package com.scottwoodward.survivalgames.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class PlayerMovementListener implements Listener {

	@EventHandler
	public void onPlayerMove(final PlayerMoveEvent event){
		if(PlayerManager.getInstance().isInGame(event.getPlayer().getName())){
			int index = PlayerManager.getInstance().getGame(event.getPlayer().getName());
			final SurvivalGame game = (SurvivalGame)GameManager.getInstance().getGame(index);
			if(game.getPhase() == Phase.STARTING){
				Location location = game.getPodiums().get(event.getPlayer().getName());
			    if (location == null) {
			      return;
			    }
			    location = location.clone();
			    if (location.distance(event.getTo()) > 1.0D) {
			      location.setPitch(event.getTo().getPitch());
			      location.setYaw(event.getTo().getYaw());
			      event.getPlayer().teleport(location);
			    }
			}else if(!game.isSpectator(event.getPlayer().getName()) && (game.getPhase() == Phase.DEATHMATCH_WARMUP || game.getPhase() == Phase.DEATHMATCH)){
				Location to = event.getTo();
				Location spawn = event.getPlayer().getWorld().getSpawnLocation();
				int radius = game.getDMRadius();
				if(spawn.distanceSquared(to) > radius*radius){
					event.getPlayer().sendMessage("You are too far from spawn!");
					event.getPlayer().getWorld().strikeLightning(event.getPlayer().getLocation());
					event.getPlayer().setHealth(0.00);
				}
			}
		}
	}
}
