package com.scottwoodward.survivalgames.listeners;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;
import com.scottwoodward.survivalgames.worlds.WorldManager;

public class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		event.setCancelled(true);
		String log = null;
		Player player = event.getPlayer();
		if(PlayerManager.getInstance().isInGame(player.getName())){
			int index = PlayerManager.getInstance().getGame(player.getName());
			SurvivalGame game = (SurvivalGame)GameManager.getInstance().getGame(index);
			if(game.isSpectator(player.getName())){
				for(String name : game.getAllSpectators()){
					Player other = Bukkit.getPlayerExact(name);
					if(other != null){
						if(game.getPhase() != Phase.PREGAME){
							log = "[" + ChatColor.GREEN + PlayerManager.getInstance().getPoints(player.getName()) + ChatColor.WHITE + "] " + PlayerManager.getInstance().getColor(player.getName()) + player.getName() + ChatColor.WHITE + ": " + event.getMessage();

						}else{
							log = "[" + ChatColor.GREEN + PlayerManager.getInstance().getPoints(player.getName()) + ChatColor.WHITE + "] " + PlayerManager.getInstance().getColor(player.getName()) + player.getName() + ChatColor.WHITE + ": " + event.getMessage();
						}
						other.sendMessage(ChatColor.translateAlternateColorCodes('&', log));
					}
				}
			}else{
				for(String name : game.getAllPlayers()){
					Player other = Bukkit.getPlayerExact(name);
					if(other != null){
						if(game.getPhase() != Phase.PREGAME){
							log = "[" + ChatColor.GREEN + PlayerManager.getInstance().getPoints(player.getName()) + ChatColor.WHITE + "] " + PlayerManager.getInstance().getColor(player.getName()) + player.getName() + ChatColor.WHITE + ": " + event.getMessage();

						}else{
							log = "[" + ChatColor.GREEN + PlayerManager.getInstance().getPoints(player.getName()) + ChatColor.WHITE + "] " + PlayerManager.getInstance().getColor(player.getName()) + player.getName() + ChatColor.WHITE + ": " + event.getMessage();
						}
						other.sendMessage(ChatColor.translateAlternateColorCodes('&', log));
					}
				}
			}
		}else{
			World world = Bukkit.getWorld(WorldManager.getInstance().getHubworld());
			for(Player other : world.getPlayers()){
				log = "[" + ChatColor.GREEN + PlayerManager.getInstance().getPoints(player.getName()) + ChatColor.WHITE + "] " + PlayerManager.getInstance().getColor(player.getName()) + player.getName() + ChatColor.WHITE + ": " + event.getMessage();
				other.sendMessage(ChatColor.translateAlternateColorCodes('&', log));
			}
		}
		SurvivalGames.getInstance().getLogger().log(Level.INFO, ChatColor.translateAlternateColorCodes('&', log));
	}
}
