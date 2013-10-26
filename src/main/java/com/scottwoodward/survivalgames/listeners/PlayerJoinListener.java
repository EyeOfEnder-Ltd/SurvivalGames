package com.scottwoodward.survivalgames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.scottwoodward.survivalgames.datastore.DataStoreManager;
import com.scottwoodward.survivalgames.players.PlayerManager;
import com.scottwoodward.survivalgames.worlds.WorldManager;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		World world = Bukkit.createWorld(new WorldCreator(WorldManager.getInstance().getHubworld()));
		world.setDifficulty(Difficulty.PEACEFUL);
		player.teleport(world.getSpawnLocation());
		player.setAllowFlight(false);
		player.setFlying(false);
		player.getInventory().clear();
		player.getInventory().setItem(0, new ItemStack(Material.COMPASS));
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta)book.getItemMeta();
		meta.setDisplayName("How to play");
		meta.addPage("The compass handles teleportation to and from the hub, lobbies, and players.\nWhile in a lobby, you can only chat within that lobby.");
		book.setItemMeta(meta);
		player.getInventory().setItem(1, book);
		player.sendMessage(ChatColor.YELLOW + "Use a compass to select a game.");
		event.setJoinMessage("");
		PlayerManager.getInstance().setPoints(player.getName(), DataStoreManager.getInstance().getPlayerPoints(player.getName()));
		PlayerManager.getInstance().setPasses(player.getName(), DataStoreManager.getInstance().getPlayerPasses(player.getName()));
		
	}
}
