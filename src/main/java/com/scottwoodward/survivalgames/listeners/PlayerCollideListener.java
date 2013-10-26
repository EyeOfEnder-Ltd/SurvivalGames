package com.scottwoodward.survivalgames.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.GameUtils;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class PlayerCollideListener implements Listener {

	@EventHandler
	public void onPlayerCollid(VehicleEntityCollisionEvent event){
		if(event.getEntity() instanceof Player && event.getVehicle() instanceof Boat){
			Player player = (Player) event.getEntity();
			if(PlayerManager.getInstance().isInGame(player.getName())){
				int index = PlayerManager.getInstance().getGame(player.getName());
				SurvivalGame game = (SurvivalGame)GameManager.getInstance().getGame(index);
				if(game.isSpectator(player.getName())){
					event.setCancelled(true);
					Location loc = GameUtils.getRandomLocation(player.getLocation(), 10);
					loc.setY(loc.getWorld().getHighestBlockYAt(loc));
					player.teleport(loc);
					player.sendMessage(ChatColor.YELLOW + "Moving you out of way of a boat....");
				}
			}
		}
	}

	@EventHandler
	public void onPlayerCollid(ProjectileHitEvent event){
		if(event.getEntity() instanceof Player){
			Player player = (Player) event.getEntity();
			if(PlayerManager.getInstance().isInGame(player.getName())){
				int index = PlayerManager.getInstance().getGame(player.getName());
				SurvivalGame game = (SurvivalGame)GameManager.getInstance().getGame(index);
				if(game.isSpectator(player.getName())){
					Location arrow = player.getLocation();
					Location loc = GameUtils.getRandomLocation(player.getLocation(), 10);
					loc.setY(loc.getWorld().getHighestBlockYAt(loc));
					player.teleport(loc);
					player.sendMessage(ChatColor.YELLOW + "Moving you out of way of an arrow....");
					Projectile projectile = null;
					if(event.getEntity() instanceof Arrow){
						projectile = event.getEntity().getShooter().launchProjectile(Arrow.class);
					}else if(event.getEntity() instanceof Snowball){
						projectile = event.getEntity().getShooter().launchProjectile(Snowball.class);
					}else if(event.getEntity() instanceof Egg){
						projectile = event.getEntity().getShooter().launchProjectile(Egg.class);
					}else if(event.getEntity() instanceof EnderPearl){
						projectile = event.getEntity().getShooter().launchProjectile(EnderPearl.class);
					}else if(event.getEntity() instanceof Fish){
						projectile = event.getEntity().getShooter().launchProjectile(Fish.class);
					}else if(event.getEntity() instanceof LargeFireball){
						projectile = event.getEntity().getShooter().launchProjectile(LargeFireball.class);
					}else if(event.getEntity() instanceof ThrownExpBottle){
						projectile = event.getEntity().getShooter().launchProjectile(ThrownExpBottle.class);
					}else if(event.getEntity() instanceof ThrownPotion){
						projectile = event.getEntity().getShooter().launchProjectile(ThrownPotion.class);
					}
					projectile.teleport(arrow);
					projectile.setVelocity(event.getEntity().getVelocity());
				}else{
					if(event.getEntity() instanceof Snowball){
						PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, 6 * 20, 0);
						player.addPotionEffect(pe);
					}
				}
			}
		}
	}
}
