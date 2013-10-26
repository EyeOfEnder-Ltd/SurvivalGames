package com.scottwoodward.survivalgames.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.datastore.DataStoreManager;
import com.scottwoodward.survivalgames.worlds.WorldManager;

public class VotingPhase {

	/*private SurvivalGame game;

	public VotingPhase(Game game){
		this.game = (SurvivalGame) game;
		this.game.cancelVotingPhase();
		BukkitTask votingPhase = Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), new VotingCountDown((SurvivalGame) game), 20, 20);
		this.game.setVotingCountDown(votingPhase);
	}

	@Override
	public void run() {
		long timeout = 5;
		int number = 10;
		int secondsToStart = 10;
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Game " + game.getGameNumber() +  " has " + game.getAllPlayers().size() + " players.");
		if(game.getAllPlayers().size() < 2){
			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Game " + game.getGameNumber() + " did not have enough players.");
			for(String player : game.getAllPlayers()){
				Player combatant = Bukkit.getPlayerExact(player);
				if(combatant != null){
					combatant.sendMessage(ChatColor.YELLOW + "Not enough players to start a match, waiting for more.");
				}
			}
			game.startGame();
			return;
		}
		try{
			File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			timeout = 20 * 60 * config.getLong("TotalTimeUntilDeathMatch", 5);
			number = config.getInt("ChestPerMap", 10);
			secondsToStart = config.getInt("SecondsBeforeGameStarts", 10);
		}catch(Exception e){
			e.printStackTrace();
		}
		game.setInProgress(true);
		if(game.winnerPerk != null){
			game.winnerPerk.cancel();
		}
		WorldManager.getInstance().createGameWorld(game.getGameNumber(), game.getWorld());
		WorldManager.getInstance().resetDeathMatch(game.getGameNumber());
		String name = "GameWorld" + game.getGameNumber();
		World world = Bukkit.createWorld(new WorldCreator(name));
		world.setTime(0);
		final List<Location> podiums = DataStoreManager.getInstance().getSpawns(WorldManager.getInstance().getGameWorlds().get(game.getWorld()), name);
		int index = 0;
		List<String> toSpectators = new ArrayList<String>();
		for(String player : game.getAllPlayers()){
			Player combatant = Bukkit.getPlayerExact(player);
			if(combatant != null){
				if(index > 23){
					toSpectators.add(combatant.getName());
					combatant.sendMessage(ChatColor.YELLOW + "This game is full, you are now spectating");
					combatant.setFoodLevel(20);
					combatant.getInventory().setHelmet(new ItemStack(Material.AIR));
					combatant.getInventory().setBoots(new ItemStack(Material.AIR));
					combatant.getInventory().setChestplate(new ItemStack(Material.AIR));
					combatant.getInventory().setLeggings(new ItemStack(Material.AIR));
				}else{
					Location spawn = getCenter(podiums.get(index));
					game.podiums.put(combatant.getName(), spawn);
					combatant.teleport(spawn);
					combatant.getInventory().clear();
					combatant.getInventory().setHelmet(new ItemStack(Material.AIR));
					combatant.getInventory().setBoots(new ItemStack(Material.AIR));
					combatant.getInventory().setChestplate(new ItemStack(Material.AIR));
					combatant.getInventory().setLeggings(new ItemStack(Material.AIR));
				}
				index ++;
			}
		}
		for(String spec : toSpectators){
			game.addSpectator(spec);
		}
		toSpectators.clear();
		toSpectators = null;
		for(String playerName : game.getAllPlayers()){
			Player player = Bukkit.getPlayerExact(playerName);
			if(player != null){
				BarAPI.setMessage(player, ChatColor.RED + "Match Begins", secondsToStart);
			}	
		}
		for(String playerName : game.getAllSpectators()){
			Player player = Bukkit.getPlayerExact(playerName);
			if(player != null){

				BarAPI.setMessage(player, ChatColor.RED + "Match Begins", secondsToStart);
			}	
		}
		
		
		final long timeoutF = timeout;
		final int numberF = number;
		game.setIsStarting(true);

		final BukkitTask startAnnouncer = Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), new GameStartCountdown(game, secondsToStart), 20, 20);
		

		Bukkit.getScheduler().scheduleSyncDelayedTask(SurvivalGames.getInstance(), new Runnable(){
			public void run(){
				if(startAnnouncer != null){
					startAnnouncer.cancel();
				}
				int index = 0;
				for(String player : game.getAllPlayers()){
					Player combatant = Bukkit.getPlayerExact(player);
					if(combatant != null){
						combatant.setAllowFlight(false);
						combatant.setFlying(false);
						combatant.teleport(podiums.get(index));
						combatant.getInventory().clear();
						index ++;
					}
				}
				for(String player : game.getAllPlayers()){
					Player combatant = Bukkit.getPlayerExact(player);
					if(combatant != null){
						combatant.sendMessage(ChatColor.YELLOW + "The game has started!");
					}
				}
				for(String player : game.getAllSpectators()){
					Player combatant = Bukkit.getPlayerExact(player);
					if(combatant != null){
						combatant.sendMessage(ChatColor.YELLOW + "The game has started!");
					}
				}
				game.cancelVotingPhase();
				long timer = (long)timeoutF/(numberF/2);
				BukkitTask task = Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), new ChestDrop(game), 20, timer);
				game.setChestDropper(task);
				game.setIsStarting(false);
				if(game.getAllPlayers().size() <= 10){
					((SurvivalGame)game).startDeathMatch();
				}

			}
		}, (secondsToStart * 20) + 5);
		WorldManager.getInstance().resetLobby(game.getGameNumber());
		game.setIsDeathMatch(false);
		distributeChests(number);
		game.cancelVotingPhase();
	}


	public void distributeChests(int number){
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
	
	public Location getCenter(Location loc){

		    double x = Math.floor(loc.getX());
		    double z = Math.floor(loc.getZ());
		        x += 0.5;
		        z += 0.5;
		    return new Location(loc.getWorld(), x, loc.getY(), z);
	}*/

}
