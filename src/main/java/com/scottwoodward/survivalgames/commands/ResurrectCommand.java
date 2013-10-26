package com.scottwoodward.survivalgames.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.scottwoodward.survivalgames.disguises.Disguise;
import com.scottwoodward.survivalgames.disguises.DisguiseManager;
import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class ResurrectCommand {
	
	public static void execute(CommandSender sender, String[] args){
		return;
		/*if(args.length == 0){
			if(sender instanceof Player){
				Player player = (Player)sender;
				if(PlayerManager.getInstance().isInGame(player.getName())){
					int index = PlayerManager.getInstance().getGame(player.getName());
					SurvivalGame game = (SurvivalGame)GameManager.getInstance().getGame(index);
					if(!game.isInProgress()){
						sender.sendMessage(ChatColor.YELLOW + "The game is not in progress.");
					}else if(game.isDeathMatch()){
						sender.sendMessage(ChatColor.YELLOW + "You cannot resurrect during a deathmatch.");
					}else if(game.didDie(player.getName())){
						//game.resurrect(player.getName());
						displayRessurectInventory(player);
					}else{
						sender.sendMessage(ChatColor.YELLOW + "You must be spectating a game you played to resurrect.");
					}
				}else{
					sender.sendMessage(ChatColor.YELLOW + "You must be spectating a game you played to resurrect.");

				}
			}else{
				sender.sendMessage(ChatColor.YELLOW + "You must be a player to resurrect.");

			}
		}else{
			sender.sendMessage(ChatColor.YELLOW + "Usage: /resurrect");
		}*/
	}
	
	private static void displayRessurectInventory(Player player){
		if(DisguiseManager.getInstance().getDisguises() == null || DisguiseManager.getInstance().getDisguises().size() == 0){
			return;
		}
		int slots = slotsNeeded(DisguiseManager.getInstance().getDisguises().size());
		Inventory inv = Bukkit.createInventory(player, slots);
		int index = 0;
		for(Disguise disguise : DisguiseManager.getInstance().getDisguises()){
			if(disguise.getPermission() == null || player.hasPermission(disguise.getPermission())){
				ItemStack icon = disguise.getIcon();
				ItemMeta meta = icon.getItemMeta();
				meta.setDisplayName("Resurrect as a " + disguise.getType().getName());
				icon.setItemMeta(meta);
				inv.setItem(index, icon);
			}else{
				ItemStack icon = new ItemStack(Material.BEDROCK);
				ItemMeta meta = icon.getItemMeta();
				meta.setDisplayName("Visit our webpage to unlock more resurrection kits");
				icon.setItemMeta(meta);
				inv.setItem(index, icon);
			}
			index ++;
		}
	}
	
	private static int slotsNeeded(int items){
		return (9 - items % 9) + items;
	}

}
