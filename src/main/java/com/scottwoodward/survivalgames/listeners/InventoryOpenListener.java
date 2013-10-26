package com.scottwoodward.survivalgames.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.GameUtils;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.loot.LootManager;
import com.scottwoodward.survivalgames.loot.LootTier;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class InventoryOpenListener implements Listener {

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event){
		Inventory inv = event.getInventory();
		if(inv.getType() != InventoryType.CHEST){
			return;
		}
		Player player = (Player) event.getPlayer();
		if(!PlayerManager.getInstance().isInGame(player.getName())){
			return;
		}
		int index = PlayerManager.getInstance().getGame(player.getName());
		SurvivalGame game = (SurvivalGame)GameManager.getInstance().getGame(index);
		Location loc = null;
		if(game.getPhase() == Phase.PREGAME){
			return;
		}
		if (!(event.getInventory().getHolder() instanceof BlockState)){
			return;
		}else{
			loc = ((BlockState) event.getInventory().getHolder()).getLocation();
		}
		if(game.wasOpened(loc)){
			return;
		}else{
			game.addChest(loc);
		}
		if(inv.getName().equalsIgnoreCase("Sponsorship Crate")){
			for(int i = 0; i < 3; i++){
				inv.setItem(GameUtils.getRandomInt(27), LootManager.requestLoot(LootTier.LOWER));
			}
		}else {
			if(inv.contains(Material.GOLD_BLOCK)){
				inv.clear();
				for(int i = 0; i < 5; i++){
					inv.setItem(GameUtils.getRandomInt(27), LootManager.requestLoot(LootTier.LOWER));
				}
				inv.addItem(LootManager.requestLoot(LootTier.HIGHER));
			}
			
		}
	}
}
