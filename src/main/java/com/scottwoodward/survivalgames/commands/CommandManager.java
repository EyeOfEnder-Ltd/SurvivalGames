package com.scottwoodward.survivalgames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor{
	private static CommandManager instance;
	private CommandManager(){
		
	}
	
	public static CommandManager getInstance(){
		if(instance == null){
			instance = new CommandManager();
		}
		return instance;
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("bounty")){
			BountyCommand.execute(sender, args);
		}else if(cmd.getName().equalsIgnoreCase("bet")){
			BetCommand.execute(sender, args);
		}else if(cmd.getName().equalsIgnoreCase("sponsor")){
			SponsorCommand.execute(sender, args);
		}else if(cmd.getName().equalsIgnoreCase("setpodium")){
			SetPodiumCommand.execute(sender, args);
		}else if(cmd.getName().equalsIgnoreCase("init")){
			InitCommand.execute(sender, args);
		}else if(cmd.getName().equalsIgnoreCase("points")){
			PointsCommand.execute(sender, args);
		}else if(cmd.getName().equalsIgnoreCase("worldtp")){
			WorldTPCommand.execute(sender, args);
		}else if(cmd.getName().equalsIgnoreCase("setworldspawn")){
			SetWorldSpawnCommand.execute(sender, args);
		}else if(cmd.getName().equalsIgnoreCase("resurrect")){
			ResurrectCommand.execute(sender, args);
		}else if(cmd.getName().equalsIgnoreCase("resurrect")){
			ResurrectCommand.execute(sender, args);
		}else if(cmd.getName().equalsIgnoreCase("addpass")){
			ResurrectCommand.execute(sender, args);
		}else if(cmd.getName().equalsIgnoreCase("fstart")){
			ForceStartCommand.execute(sender, args);
		}
		return true;
	}
}
