package com.scottwoodward.survivalgames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.scottwoodward.survivalgames.players.PlayerManager;

public class AddPassCommand {
	public static void execute(CommandSender sender, String[] args){
		if(args.length == 1){
			PlayerManager.getInstance().addPass(args[0]);
		}else{
			sender.sendMessage(ChatColor.YELLOW + "Usage: /addpass <Player Name>");
		}
	}
}
