package com.scottwoodward.survivalgames.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ChestDrop implements Runnable{

	private SurvivalGame game;
	private int dropped;
	
	public ChestDrop(SurvivalGame game){
		this.game = game;
	}
	
	public void run(){
		Player player = GameUtils.getRandomPlayer(game.getAllPlayers());
		if(player == null){
			return;
		}
		Location loc = GameUtils.getRandomLocation(player.getLocation(), 10);
		World world = Bukkit.getWorld("GameWorld" + game.getGameNumber());
		System.out.println("Calling in a chest at " + loc.getX() + " " + loc.getZ());
		int newY = world.getHighestBlockYAt(loc);
		loc.setY(newY - 1);
		if(loc.getBlock().getType() != Material.AIR){
			Byte data = 0x0;
			world.spawnFallingBlock(loc, Material.CHEST.getId(), data);
		}
		
	}
}
