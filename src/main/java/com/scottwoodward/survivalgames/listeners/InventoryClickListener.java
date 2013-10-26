package com.scottwoodward.survivalgames.listeners;


import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;
import com.scottwoodward.survivalgames.worlds.WorldManager;


public class InventoryClickListener implements Listener{

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		Inventory inv = event.getInventory();
		if(!(inv.getHolder() instanceof Player)){
			return;
		}
		final Player player = (Player) inv.getHolder();
		final PlayerManager pm = PlayerManager.getInstance();
		int clicked = event.getRawSlot();
		int topSize = event.getView().getTopInventory().getSize();
		if(clicked + 1 > topSize || clicked == -999){
			return;
		}
		if(inv.getItem(clicked) == null || inv.getItem(clicked).getTypeId() == Material.AIR.getId()){
			return;
		}
		if(pm.isInGame(player.getName())){
			int index = pm.getGame(player.getName());
			SurvivalGame game = (SurvivalGame) GameManager.getInstance().getGame(index);
			if(game.isSpectator(player.getName())){
				if(event.getInventory().getItem(clicked).getType() == Material.COMPASS){
					player.sendMessage("Teleporting to the hub");
					World world = Bukkit.createWorld(new WorldCreator(WorldManager.getInstance().getHubworld()));
					player.teleport(world.getSpawnLocation());
				}else{
					String name = inv.getItem(clicked).getItemMeta().getDisplayName();
					player.sendMessage("Teleporting to " + name);
					Player target = Bukkit.getPlayerExact(ChatColor.stripColor(name));
					if(target != null){
						player.teleport(target);
					}
				}
				event.setCancelled(true);
			}else if(game.getPhase() == Phase.PREGAME && inv.getItem(clicked).getType() == Material.COMPASS){
				game.leaveGame(player.getName(), false);
			}
		}else{
			if(pm.hasMapSelected(player.getName())){
				if(inv.getItem(clicked).getTypeId() == Material.COMPASS.getId()){
					pm.unsetPlayerMap(player.getName());
				}else{
					ItemStack item = inv.getItem(clicked);
					String name = item.getItemMeta().getDisplayName();
					int number = 0;
					try{
						number = Integer.valueOf(name.split(" ")[1]);

					}catch(Exception e){
						return;
					}
					SurvivalGames.getInstance().getLogger().log(Level.INFO, player.getName() + " is joining lobby " + (number - 1));
					GameManager.getInstance().getGame(number - 1).joinGame(player.getName());
					pm.setGame(player.getName(), number - 1);
				}
				event.setCancelled(true);
			}else if(WorldManager.getInstance().getGameWorlds().size() >= clicked + 1){
				event.setCancelled(true);
				pm.setPlayerMap(player.getName(), clicked);
				player.sendMessage(ChatColor.YELLOW + "Selected Map " + WorldManager.getInstance().getGameWorlds().get(clicked).replace("_", " "));
				SurvivalGames.getInstance().getLogger().log(Level.INFO, "Selected " + clicked + " " + WorldManager.getInstance().getGameWorlds().get(clicked));
			}
		}
		if(event.isCancelled()){
			Bukkit.getScheduler().scheduleSyncDelayedTask(SurvivalGames.getInstance(), new Runnable(){
				public void run(){
					player.closeInventory();
				}
			}, 1);
		}
		if(event.isCancelled() && !pm.isInGame(player.getName()) && pm.hasMapSelected(player.getName())){
			Bukkit.getScheduler().scheduleSyncDelayedTask(SurvivalGames.getInstance(), new Runnable(){
				public void run(){
					PlayerInteractListener.showLobbySelect(player);
				}
			}, 5);
		}
	}
}
