package com.scottwoodward.survivalgames.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWorldSpawnCommand {

	public static void execute(CommandSender sender, String[] args){
		if(args.length == 0){
			if(sender instanceof Player){
				World world = ((Player)sender).getWorld();
				Location loc = ((Player)sender).getLocation();
				world.setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
				sender.sendMessage(ChatColor.YELLOW + "World spawn point set");
			}else{
				sender.sendMessage(ChatColor.YELLOW + "You must be in game to execute setworldspawn");
			}
		}else{
			sender.sendMessage(ChatColor.YELLOW + "Usage /setworldspawn");
		}
	}
}
