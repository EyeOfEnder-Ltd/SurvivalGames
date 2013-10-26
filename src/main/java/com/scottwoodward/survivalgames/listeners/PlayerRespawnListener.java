package com.scottwoodward.survivalgames.listeners;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.players.PlayerManager;
import com.scottwoodward.survivalgames.worlds.WorldManager;

public class PlayerRespawnListener implements Listener {

	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent event){
		World world = null;
		if(!PlayerManager.getInstance().isInGame(event.getPlayer().getName())){
			world = Bukkit.createWorld(new WorldCreator(WorldManager.getInstance().getHubworld()));
		}else{
			//int index = PlayerManager.getInstance().getGame(event.getPlayer().getName());
			//SurvivalGame game = (SurvivalGame) GameManager.getInstance().getGame(index);
			world = Bukkit.createWorld(new WorldCreator(WorldManager.getInstance().getHubworld()));
		}
		
		//World world = Bukkit.createWorld(new WorldCreator("GameWorld" + game.getGameNumber()));
		//if(event.getPlayer().hasPermission("survivalgames.vip") && game.getAllPlayers().size() > 1){
		//		game.addSpectator(event.getPlayer().getName());
		//}else{
		
		//game.leaveGame(event.getPlayer().getName());
		PlayerManager.getInstance().removeGame(event.getPlayer().getName());
		//}
		world.setTime(0);
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Respawning player in the hub");

		event.setRespawnLocation(world.getSpawnLocation());
		Bukkit.getScheduler().scheduleSyncDelayedTask(SurvivalGames.getInstance(), new Runnable(){
			public void run(){
				event.getPlayer().getInventory().setItemInHand(new ItemStack(Material.COMPASS));
			}
		}, 1L);
	}
}
