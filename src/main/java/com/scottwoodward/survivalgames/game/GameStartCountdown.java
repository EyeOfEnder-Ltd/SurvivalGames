package com.scottwoodward.survivalgames.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameStartCountdown {
	
	/*SurvivalGame game;
	int timer;
	
	public GameStartCountdown(SurvivalGame game, int timer){
		this.game = game;
		this.timer = timer;
	}

	@Override
	public void run() {
		
		if(timer < 0){
			return;
		}
		if((timer % 5) == 0){
			for(String name : game.getAllPlayers()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "The game will start in " + ChatColor.GREEN + timer + ChatColor.RED + " seconds");
				}
			}
			for(String name : game.getAllSpectators()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "The game will start in " + ChatColor.GREEN + timer + ChatColor.RED + " seconds");
				}
			}
		}
		timer--;

	}*/

}
