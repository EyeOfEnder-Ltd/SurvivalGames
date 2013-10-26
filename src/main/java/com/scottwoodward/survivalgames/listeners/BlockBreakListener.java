package com.scottwoodward.survivalgames.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class BlockBreakListener implements Listener {
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(PlayerManager.getInstance().isInGame(player.getName())){
			List<Integer> breakable = new ArrayList<Integer>();
			try{
				File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
				YamlConfiguration config = new YamlConfiguration();
				config.load(file);
				breakable = config.getIntegerList("BreakableBlocks");
			}catch(Exception e){
				e.printStackTrace();
			}
			if(breakable != null){
				if(breakable.contains(event.getBlock().getTypeId())){
					return;
				}
			}
			event.setCancelled(true);
		}else if(!event.getPlayer().hasPermission("survivalgames.staff")){
			event.setCancelled(true);
		}
	}

}
