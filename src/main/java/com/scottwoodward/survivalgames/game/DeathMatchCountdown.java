package com.scottwoodward.survivalgames.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DeathMatchCountdown{

	/*private int length;
	private SurvivalGame game;
	
	public DeathMatchCountdown(int length, SurvivalGame game){
		this.length = length;
		this.game = game;
	}
	
	public void run(){
		if(length < 1){
			if(game.deathmatchIsStarting){
				game.deathmatchIsStarting = false;
				game.cancelDeathMatchCountdown();
			}else{
				game.beginDeathMatch();
			}
		}else if(length == 60 || length == 45 || length == 30 || length == 15 || length <= 10){
			String message = ChatColor.RED + "The death match begins in " + ChatColor.GREEN + length + ChatColor.RED + " seconds";
			if(game.deathmatchIsStarting){
				message = ChatColor.RED + "Invulnerability ends in " + ChatColor.GREEN + length + ChatColor.RED + " seconds";
			}
			for(String name : game.getAllPlayers()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(message);
				}
			}
			for(String name : game.getAllSpectators()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(message);
				}
			}
		}
		length--;
		game.deathMatchCountDownTimer--;
		
	}
*/
}
