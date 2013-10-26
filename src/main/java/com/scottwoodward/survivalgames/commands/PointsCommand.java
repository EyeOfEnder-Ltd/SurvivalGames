package com.scottwoodward.survivalgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.scottwoodward.survivalgames.players.PlayerManager;

public class PointsCommand {
	
	public static void execute(CommandSender sender, String[] args){
		if(args.length == 1){
			Player player = Bukkit.getPlayer(args[0]);
			if(player != null){
				int points = PlayerManager.getInstance().getPoints(player.getName());
				sender.sendMessage(ChatColor.YELLOW + player.getName() + " has " + points + " points.");
			}else{
				sender.sendMessage(ChatColor.YELLOW + "Player " + args[0] + " not found");
			}
		}else if(sender instanceof Player){
			Player player = (Player) sender;
			if(player != null){
				int points = PlayerManager.getInstance().getPoints(player.getName());
				sender.sendMessage(ChatColor.YELLOW + "You have " + points + " points.");
			}
		}else{
			sender.sendMessage(ChatColor.YELLOW + "Usage: /points <Player Name>");
		}
	}

}
