package com.scottwoodward.survivalgames.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.scottwoodward.survivalgames.datastore.DataStoreManager;
import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class PlayerQuitListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(PlayerManager.getInstance().isInGame(player.getName())){
			int index = PlayerManager.getInstance().getGame(player.getName());
			SurvivalGame game = (SurvivalGame) GameManager.getInstance().getGame(index);
			game.leaveGame(player.getName(), false);
			game.removeSpectator(player.getName());
			PlayerManager.getInstance().removeGame(player.getName());
		}
		DataStoreManager.getInstance().setPlayerPoints(player.getName(), PlayerManager.getInstance().getPoints(player.getName()));
		DataStoreManager.getInstance().setPlayerPasses(player.getName(), PlayerManager.getInstance().getNumberOfPasses(player.getName()));
		event.setQuitMessage("");
		PlayerManager.getInstance().removePoints(player.getName());
		PlayerManager.getInstance().unsetPlayerMap(player.getName());
		PlayerManager.getInstance().removePasses(player.getName());
		PlayerManager.getInstance().unloadNameColor(player.getName());
	}
}
