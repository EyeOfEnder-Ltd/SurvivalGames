package com.scottwoodward.survivalgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.game.GameManager;

public class InitCommand {

	public static void execute(CommandSender sender, String[] args){
		GameManager.getInstance().init(); //Initializes server's minigames
		Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), new Runnable(){
			public void run(){
				SurvivalGames.getInstance().updateTabList();
			}
		}, 20, 20);
	}
}
