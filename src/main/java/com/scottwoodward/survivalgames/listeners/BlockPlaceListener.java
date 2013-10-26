package com.scottwoodward.survivalgames.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class BlockPlaceListener implements Listener{

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		
		Player player = event.getPlayer();
		if(!PlayerManager.getInstance().isInGame(player.getName())){
			event.setCancelled(true);
			return;
		}
		int index = PlayerManager.getInstance().getGame(player.getName());
		SurvivalGame game = (SurvivalGame) GameManager.getInstance().getGame(index);
		if(game.getPhase() == Phase.PREGAME){
			event.setCancelled(true);
			return;
		}
		if(event.getBlockPlaced().getTypeId() == Material.CHEST.getId()){
			event.setCancelled(true);
			game.addChest(event.getBlockPlaced().getLocation());
		}
		if(event.getBlockPlaced().getTypeId() == Material.REDSTONE_TORCH_ON.getId() && game.useCrate(player.getName())){
			event.getBlockPlaced().setType(Material.AIR);
			Location loc = event.getBlockPlaced().getLocation();
			int newY = loc.getWorld().getHighestBlockYAt(loc);
			loc.setY(newY - 1);
			Byte data = 0x0;
			loc.getWorld().spawnFallingBlock(loc, Material.CHEST.getId(), data);
		}
		if(event.getBlockPlaced().getType() == Material.REDSTONE){
			event.getBlockPlaced().setType(Material.FIRE);
		}
		if(event.getBlockPlaced().getType() == Material.TNT){
			Location loc = event.getBlockPlaced().getLocation();
			event.getBlockPlaced().setType(Material.AIR);
			Entity entity = loc.getWorld().spawnEntity(loc, EntityType.PRIMED_TNT);
		}
	}
}
