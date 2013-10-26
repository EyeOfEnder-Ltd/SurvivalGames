package com.scottwoodward.survivalgames.listeners;

import org.bukkit.entity.Egg;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class PlayerDamageByPlayerListener implements Listener {

	@EventHandler
	public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Snowball && event.getEntity() instanceof LivingEntity){
			PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, 6 * 20, 0);
			((LivingEntity)event.getEntity()).addPotionEffect(pe);
		}else if(event.getDamager() instanceof Egg && event.getEntity() instanceof LivingEntity){
			PotionEffect pe = new PotionEffect(PotionEffectType.CONFUSION, 6 * 20, 0);
			((LivingEntity)event.getEntity()).addPotionEffect(pe);
		}
	}
}
