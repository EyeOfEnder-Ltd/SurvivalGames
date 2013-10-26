package com.scottwoodward.survivalgames.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.scottwoodward.survivalgames.datastore.DataStoreManager;

public class SetPodiumCommand {

	public static void execute(CommandSender sender, String[] args){
		if(sender instanceof Player){
			if(args.length == 0){
					Player player = (Player) sender;
					Location loc = player.getLocation();
					DataStoreManager.getInstance().addSpawn(loc);
			}else{
				sender.sendMessage(ChatColor.YELLOW + "Usage: /setpodium");
			}
		}else{
			sender.sendMessage(ChatColor.YELLOW + "You must be a player to set a podium location");
		}
	}
}
