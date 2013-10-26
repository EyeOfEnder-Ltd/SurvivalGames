package com.scottwoodward.survivalgames.game;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class WinnerPerk implements Runnable {
	
	private String name;
	private int launches;
	private SurvivalGame game;
	
	public WinnerPerk(String name, SurvivalGame game){
		this.name = name;
		launches = 0;
		this.game = game;
	}

	@Override
	public void run() {
		Player player = Bukkit.getPlayerExact(name);
		if(player != null){
			Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
			FireworkMeta fwm = fw.getFireworkMeta();
			fw.setFireworkMeta(getRandomFireWork(fwm));  
		}
		launches ++;
		if(launches > 10){
			game.cancelWinnerPerk();
		}
	}

	private FireworkMeta getRandomFireWork(FireworkMeta fwm){

		int rt = GameUtils.getRandomInt(5);

		Type type = Type.BALL;       
		if (rt == 1) type = Type.BALL;
		if (rt == 2) type = Type.BALL_LARGE;
		if (rt == 3) type = Type.BURST;
		if (rt == 4) type = Type.CREEPER;
		if (rt == 5) type = Type.STAR;

		int r1i = GameUtils.getRandomInt(18);
		int r2i = GameUtils.getRandomInt(18);
		Color c1 = GameUtils.getColor(r1i);
		Color c2 = GameUtils.getColor(r2i);

		FireworkEffect effect = FireworkEffect.builder().flicker(GameUtils.getRandomBoolean()).withColor(c1).withFade(c2).with(type).trail(GameUtils.getRandomBoolean()).build();

		fwm.addEffect(effect);

		int rp = GameUtils.getRandomInt(3);
		fwm.setPower(rp);
		return fwm;
	}
}
