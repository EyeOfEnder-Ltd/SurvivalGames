package com.scottwoodward.survivalgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class BetCommand {

	public static void execute(CommandSender sender, String[] args){
		if(sender instanceof Player){
			if(args.length == 2){
				Player target = Bukkit.getPlayer(args[0]);
				if(target != null){
					if(PlayerManager.getInstance().isInGame(sender.getName())){
						int index = PlayerManager.getInstance().getGame(sender.getName());
						SurvivalGame game = (SurvivalGame) GameManager.getInstance().getGame(index);
						if(game.getAllSpectators().contains(sender.getName())){
							sender.sendMessage(ChatColor.YELLOW + "You must be spectating a match to place a bet");
						}else if(game.getAllPlayers().contains(target.getName())){
							if(game.getAllPlayers().size() >= 10){
								if(game.getPhase() != Phase.PREGAME){
									try{
										int amount = Integer.parseInt(args[1]);
										if(amount > 0){
											int currentPoints = PlayerManager.getInstance().getPoints(sender.getName());
											if(currentPoints >= amount){
												PlayerManager.getInstance().setPoints(sender.getName(), currentPoints - amount);
												game.addBet(sender.getName(), target.getName(), amount);
											}else{
												sender.sendMessage(ChatColor.YELLOW + "You do not have enough points");
											}
										}else{
											sender.sendMessage(ChatColor.YELLOW + "Amount must be a positive whole number");
										}
									}catch(Exception e){
										sender.sendMessage(ChatColor.YELLOW + "Amount must be a positive whole number");
									}
								}else{
									sender.sendMessage(ChatColor.YELLOW + "Wait for the game to start.");
								}
							}else{
								sender.sendMessage(ChatColor.YELLOW + "There must be ten active players remaining to place a bet.");
							}
						}else{
							sender.sendMessage(ChatColor.YELLOW + target.getName() + " is not an active player in the same lobby as you.");
						}
					}else{
						sender.sendMessage(ChatColor.YELLOW + "You must be spectating a game to place a bet.");
					}
				}else{
					sender.sendMessage(ChatColor.YELLOW + "Player " + args[0] + " not found.");
				}
			}else{
				sender.sendMessage(ChatColor.YELLOW + "Usage: /bet <Player Name> <Amount>");
			}			
		}else{
			sender.sendMessage(ChatColor.YELLOW + "You must be a player to place a bet.");
		}
	}
}
