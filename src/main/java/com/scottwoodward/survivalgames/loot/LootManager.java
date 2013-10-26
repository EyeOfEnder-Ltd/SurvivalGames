package com.scottwoodward.survivalgames.loot;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.game.GameUtils;

public class LootManager {

	public static ItemStack requestLoot(LootTier tier){
		ItemStack item = new ItemStack(getRandomItem(tier));
		return item;
	}

	private static int getRandomItem(LootTier tier){
		String query = null;
		int item = 0;
		boolean both = false;
		if(tier == LootTier.LOWER){
			query = "TierOneItems";
		}else if(tier == LootTier.HIGHER){
			query = "TierTwoItems";
			both = true;
		}
		try{
			File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			List list = config.getList(query);
			if(both){
				List second = config.getList("TierOneItems");
				for(Object num : second){
					list.add(num);
				}
			}

			int random = GameUtils.getRandomInt(list.size() + 1);
			if(random >= list.size()){
				item = 0;
			}else{
				item = (Integer)list.get(random);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return item;
	}
}
