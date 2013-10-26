package com.scottwoodward.survivalgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldTPCommand {
	
	public static void execute(CommandSender sender, String[] args){
		if(args.length == 1){
			if(sender instanceof Player){
				World world = Bukkit.createWorld(new WorldCreator(args[0]));
				((Player)sender).teleport(world.getSpawnLocation());
			}else{
				sender.sendMessage(ChatColor.YELLOW + "You must be in game to execute worldTP");
			}
		}else{
			sender.sendMessage(ChatColor.YELLOW + "Usage: /worldtp <WorldName>");
		}
	}
}
