package com.scottwoodward.survivalgames.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.scottwoodward.survivalgames.game.Game;
import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;
import com.scottwoodward.survivalgames.worlds.WorldManager;

public class PlayerInteractListener implements Listener{

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		PlayerManager pm = PlayerManager.getInstance();
		if(player.getItemInHand().getType() == Material.SPIDER_EYE){
			handleSpiderEye(player);
		}
		if(player.getItemInHand().getType() == Material.BLAZE_ROD){
			handleBlazeRod(player);
		}
		if(player.getItemInHand().getTypeId() != Material.COMPASS.getId()){
			return;
		}
		if(pm.isInGame(player.getName())){
			int index = pm.getGame(player.getName());
			SurvivalGame game = (SurvivalGame)GameManager.getInstance().getGame(index);
			if(game.getPhase() != Phase.PREGAME && game.isSpectator(player.getName())){
				event.setCancelled(true);
				showTeleportGUI(player);
			}else if(game.getPhase() == Phase.PREGAME){
				showBackToHubGUI(player);
			}
		}else if(pm.hasMapSelected(player.getName())){
			event.setCancelled(true);
			showLobbySelect(player);
		}else{
			event.setCancelled(true);
			showMapSelect(player);
		}
	}

	private void handleSpiderEye(Player player){
		PlayerManager pm = PlayerManager.getInstance();
		if(pm.isInGame(player.getName())){
			int index = pm.getGame(player.getName());
			SurvivalGame game = (SurvivalGame)GameManager.getInstance().getGame(index);
			if(game.getPhase() != Phase.PREGAME && !game.isSpectator(player.getName())){
				for(String name : game.getAllPlayers()){
					Player target = Bukkit.getPlayerExact(name);
					if(target != null && player.getLocation().distanceSquared(target.getLocation()) <= 625){
						PotionEffect pe = new PotionEffect(PotionEffectType.POISON, 6 * 20, 0);
						target.addPotionEffect(pe);
					}
				}
				player.setItemInHand(new ItemStack(Material.AIR));
			}
		}
	}

	private void handleBlazeRod(Player player){
		PlayerManager pm = PlayerManager.getInstance();
		if(pm.isInGame(player.getName())){
			int index = pm.getGame(player.getName());
			SurvivalGame game = (SurvivalGame)GameManager.getInstance().getGame(index);
			if(game.getPhase() != Phase.PREGAME && !game.isSpectator(player.getName())){
				player.getWorld().strikeLightning(player.getTargetBlock(null, 25).getLocation());
			}
			player.setItemInHand(new ItemStack(Material.AIR));
		}

	}

	private void showMapSelect(Player player){
		List<String> maps = WorldManager.getInstance().getGameWorlds();
		Inventory inv = Bukkit.createInventory(player, slotsNeeded(maps.size()), "Select a map to play on");
		int index = 0;
		for(String map : maps){
			ItemStack item = new ItemStack(Material.EMPTY_MAP);
			ItemMeta meta = item.getItemMeta();
			String name = map.trim().replace("_", " ");
			meta.setDisplayName("Select Map: " + name);
			item.setItemMeta(meta);
			inv.setItem(index, item);
			index++;
		}
		player.openInventory(inv);
	}

	public static void showLobbySelect(Player player){
		GameManager gm = GameManager.getInstance();
		int world = PlayerManager.getInstance().getPlayerMap(player.getName());
		int index = 0;
		for(Game game : gm.getGames()){
			if(game.getWorld() == world){
				index ++;
			}
		}
		Inventory inv = Bukkit.createInventory(player,slotsNeeded(index + 1), "Select a lobby");
		index = 0;
		for(Game game : gm.getGames()){
			SurvivalGame sg = (SurvivalGame)game;
			if(game.getWorld() == world){
				ItemStack item = null;
				String name = "Lobby " + (game.getGameNumber() + 1);
				List<String> lore = new ArrayList<String>();
				if(sg.getPhase() == Phase.DEATHMATCH || sg.getPhase() == Phase.DEATHMATCH_WARMUP){
					item = new ItemStack(Material.DIAMOND_BLOCK);
					lore.add("In Death Match");
					lore.add(sg.getAllPlayers().size() + "/24");
				}else if(sg.getPhase() != Phase.PREGAME){
					item = new ItemStack(Material.IRON_BLOCK);
					lore.add("In Progress");
					lore.add(sg.getAllPlayers().size() + "/24");
				}else{
					item = new ItemStack(Material.GOLD_BLOCK);
					lore.add("Pre Game Lobby");
					lore.add(sg.getAllPlayers().size() + "/24");
				}
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(name);
				meta.setLore(lore);
				item.setItemMeta(meta);
				inv.setItem(index, item);
				index ++;
			}
		}
		ItemStack item = new ItemStack(Material.COMPASS);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Choose another map");
		item.setItemMeta(meta);
		inv.setItem(index, item);
		player.openInventory(inv);
	}


	private void showBackToHubGUI(Player player){
		Inventory inv = Bukkit.createInventory(player, 9, "Return to Hub");
		ItemStack item = new ItemStack(Material.COMPASS);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Return to Hub");
		item.setItemMeta(meta);
		inv.setItem(0, item);
		player.openInventory(inv);
	}

	private void showTeleportGUI(Player player){
		int gameNumber = PlayerManager.getInstance().getGame(player.getName());
		Game game = GameManager.getInstance().getGame(gameNumber);
		Inventory inv = Bukkit.createInventory(player, slotsNeeded(game.getAllPlayers().size() + 1), "Select a player to teleport to");
		int index = 0;
		for(String name : game.getAllPlayers()){
			ItemStack item = new ItemStack(Material.SKULL_ITEM);
			item.setDurability((short)SkullType.PLAYER.ordinal());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			item.setItemMeta(meta);
			inv.setItem(index, item);
			index++;
		}
		ItemStack item = new ItemStack(Material.COMPASS);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Return to hub");
		item.setItemMeta(meta);
		inv.setItem(index, item);
		player.openInventory(inv);
	}

	private static int slotsNeeded(int items){
		return (9 - items % 9) + items;
	}
}
