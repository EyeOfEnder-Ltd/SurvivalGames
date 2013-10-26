package com.scottwoodward.survivalgames.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VotingCountDown {

	/*private SurvivalGame game;

	public VotingCountDown(SurvivalGame game){
		this.game = game;
		game.resetGameStartTimer();
	}

	@Override
	public void run() {
		if((game.gameStartTimer == 600 || game.gameStartTimer == 400 || game.gameStartTimer <= 200) && !game.isStarting()){
			for(String name : game.getAllPlayers()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "Waiting " + ChatColor.GREEN + (game.gameStartTimer / 20) + ChatColor.RED + " more seconds for players.");
				}
			}
		}
		game.decrementGameStartTimer();
	}*/

}
