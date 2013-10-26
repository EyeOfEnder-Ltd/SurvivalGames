package com.scottwoodward.survivalgames.listeners;

import net.minecraft.server.v1_6_R3.TileEntityChest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import com.scottwoodward.survivalgames.SurvivalGames;

public class FallingChestListener implements Listener{

	@EventHandler
    public void onBlockFall(EntityChangeBlockEvent event) {
        if ((event.getEntityType() == EntityType.FALLING_BLOCK)) {
        	System.out.println("falling block changing");
            if(event.getTo().getId() == Material.CHEST.getId()){
            	final Location loc = event.getBlock().getLocation();
            	Bukkit.getScheduler().runTaskLater(SurvivalGames.getInstance(), new Runnable(){
            		public void run(){
            			Block block = loc.getBlock();
            			block.setTypeId(Material.CHEST.getId());
            			TileEntityChest nmsChest = (TileEntityChest)((CraftWorld)block.getWorld()).getTileEntityAt(block.getX(), block.getY(), block.getZ());
            			nmsChest.a("Sponsorship Crate");
            			block.getWorld().playSound(loc, Sound.FALL_BIG, 1, 1);
            		}
            	},1);
            	
            }
    
        }
 
    }
}
