package com.scottwoodward.survivalgames.listeners;

import org.bukkit.entity.Egg;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class PlayerDamageByPlayerListener implements Listener {

	@EventHandler
	public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event){
		/*if(event.getDamager() instanceof Player){
			Player damager = (Player)event.getDamager();
			if(!PlayerManager.getInstance().isInGame(damager.getName())){
				event.setCancelled(true);
			}else{
				int index = PlayerManager.getInstance().getGame(damager.getName());
				SurvivalGame game = (SurvivalGame) GameManager.getInstance().getGame(index);
				if(game.isSpectator(damager.getName())){
					event.setCancelled(true);
				}else if(game.getPhase() != Phase.DEATHMATCH || game.getPhase() != Phase.IN_PROGRESS || game.getPhase() != Phase.DEATHMATCH_COUNTDOWN){
					event.setCancelled(true);
				}
			}
		}else*/
		if(event.getDamager() instanceof Snowball && event.getEntity() instanceof LivingEntity){
			PotionEffect pe = new PotionEffect(PotionEffectType.SLOW, 6 * 20, 0);
			((LivingEntity)event.getEntity()).addPotionEffect(pe);
		}else if(event.getDamager() instanceof Egg && event.getEntity() instanceof LivingEntity){
			PotionEffect pe = new PotionEffect(PotionEffectType.CONFUSION, 6 * 20, 0);
			((LivingEntity)event.getEntity()).addPotionEffect(pe);
		}
	}
}
