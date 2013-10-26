package com.scottwoodward.survivalgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.game.VotingPhase;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class ForceStartCommand {
	public static void execute(CommandSender sender, String[] args){
		if(PlayerManager.getInstance().isInGame(sender.getName())){
			int index = PlayerManager.getInstance().getGame(sender.getName());
			SurvivalGame game = (SurvivalGame)GameManager.getInstance().getGame(index);
			if(game.getPhase() != Phase.PREGAME){
				return;
			}
			game.startNextPhase();
		}
	}
}
