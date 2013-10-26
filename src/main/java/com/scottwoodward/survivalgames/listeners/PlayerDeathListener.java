package com.scottwoodward.survivalgames.listeners;

import net.minecraft.server.v1_6_R3.Packet205ClientCommand;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.GameUtils;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class PlayerDeathListener implements Listener {

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent event){
		int index = PlayerManager.getInstance().getGame(event.getEntity().getName());
		final SurvivalGame game = (SurvivalGame) GameManager.getInstance().getGame(index);
		String killerName = null;
		if(event.getEntity().getKiller() instanceof Player){
			killerName = event.getEntity().getKiller().getName();
		}
		game.onPlayerDeath(event.getEntity().getName(), killerName);

		Bukkit.getScheduler().scheduleSyncDelayedTask(SurvivalGames.getInstance(), new Runnable() {
			public void run() {
				Packet205ClientCommand packet = new Packet205ClientCommand();
				packet.a = 1;
				((CraftPlayer) event.getEntity()).getHandle().playerConnection.a(packet); // obfuscated		
			}
		}, 1);
		event.setDeathMessage("");
		if(killerName == null){
			killerName = "an inanimate object";
		}
		String message = GameUtils.getRandomDeathMessage(event.getEntity().getName(), killerName);
		for(String name : game.getAllPlayers()){
			Player player = Bukkit.getPlayerExact(name);
			if(player != null){
				player.sendMessage(message);
			}
		}
		for(String name : game.getAllSpectators()){
			Player player = Bukkit.getPlayerExact(name);
			if(player != null){
				player.sendMessage(message);
			}
		}
	}
}
