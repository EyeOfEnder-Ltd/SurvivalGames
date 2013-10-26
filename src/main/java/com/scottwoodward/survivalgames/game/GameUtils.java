package com.scottwoodward.survivalgames.game;

import java.io.File;
import java.security.SecureRandom;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.scottwoodward.survivalgames.SurvivalGames;

public class GameUtils {

	private static SecureRandom random = new SecureRandom();

	public static Location getRandomLocation(Location loc, int range){
		Location randomized = loc.clone();
		double deltaX = generatRandomPositiveNegitiveValue(range);
		double deltaZ = generatRandomPositiveNegitiveValue(range);
		System.out.println(loc.getX()  + " + " + deltaX);
		randomized.setX(loc.getX() + deltaX);
		System.out.println(loc.getZ()  + " + " + deltaZ);
		randomized.setZ(loc.getZ() + deltaZ);
		randomized.setY(255);
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Randomized location determined: X:" + randomized.getX() + " Z:" + randomized.getZ());
		return randomized;
	}
	private static int generatRandomPositiveNegitiveValue(int max) {
		int rand = random.nextInt(max);
		if(random.nextBoolean()){
			rand = rand * -1;
		}
		return rand;
	}

	public static Player getRandomPlayer(Set<String> players){
		Player player = null;
		int size = players.size();
		int item = random.nextInt(size);
		int i = 0;
		for(String name : players){
			if (i == item){
				player = Bukkit.getPlayerExact(name);
				if(player != null){
					System.out.println(player.getName());
					return player;
				}else{
					return getRandomPlayer(players);
				}
			}
			i = i + 1;
		}
		return null;
	}

	public static int getRandomInt(int max){
		return random.nextInt(max);
	}

	public static boolean getRandomBoolean(){
		return random.nextBoolean();
	}
	public static String getRandomDeathMessage(String name, String killerName) {
		String message = null;
		try{
			File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			List<String> list = config.getStringList("RandomDeathMessages");
			message = list.get(random.nextInt(list.size()));
			message = ChatColor.YELLOW + message;
			message = message.replace("PLAYER", ChatColor.RED + name + ChatColor.YELLOW);
			message = message.replace("KILLER", ChatColor.GREEN + killerName + ChatColor.YELLOW);
		}catch(Exception e){
			e.printStackTrace();
		}
		return message;
	}

	static Color getColor(int i) {
		Color c = null;
		if(i==1){
			c=Color.AQUA;
		}else if(i==2){
			c=Color.BLACK;
		}else if(i==3){
			c=Color.BLUE;
		}else if(i==4){
			c=Color.FUCHSIA;
		}else if(i==5){
			c=Color.GRAY;
		}else if(i==6){
			c=Color.GREEN;
		}else if(i==7){
			c=Color.LIME;
		}else if(i==8){
			c=Color.MAROON;
		}else if(i==9){
			c=Color.NAVY;
		}else{
			c=Color.OLIVE;
		}
		return c;
	}

	public static Location getCenter(Location loc){
		double x = Math.floor(loc.getX());
		double z = Math.floor(loc.getZ());
		x += 0.5;
		z += 0.5;
		return new Location(loc.getWorld(), x, loc.getY(), z);
	}
	
	public static void distributeChests(int number, Game game){
		for(int i = 0; i < (number/2); i++){
			if(game.getAllPlayers().size() < 1){
				return;
			}
			Player player = GameUtils.getRandomPlayer(game.getAllPlayers());
			if(player == null){
				return;
			}
			Location loc = GameUtils.getRandomLocation(player.getLocation(), 100);
			World world = Bukkit.getWorld("GameWorld" + game.getGameNumber());
			Byte data = 0x0;
			world.spawnFallingBlock(loc, Material.CHEST.getId(), data);
		}
	}
}
