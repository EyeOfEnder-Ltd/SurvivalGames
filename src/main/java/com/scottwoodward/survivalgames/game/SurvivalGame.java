package com.scottwoodward.survivalgames.game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import com.scottwoodward.survivalgames.SurvivalGames;
import com.scottwoodward.survivalgames.datastore.DataStoreManager;
import com.scottwoodward.survivalgames.players.PlayerManager;
import com.scottwoodward.survivalgames.worlds.WorldManager;

public class SurvivalGame extends Game implements Spectatable{

	private Set<String> players;
	private Set<String> spectators;
	private Map<String, Integer> votes;
	private Map<String, Integer> kills;
	private Map<String, Integer> bounties;
	private Map<String, Set<Bet>> bets;
	Map<String, Location> podiums;
	private Map<String, String> deaths;
	private List<Location> chests;
	private Set<String> crates;
	private int dmRadius;
	private String displayName;
	private List<Location> podiumLocations;
	private BukkitTask chestDropperTask;
	private BukkitTask winnerPerk;

	BukkitTask phaseTimerTask;
	PhaseTimer phaseTimer;
	Phase phase;
	private String winnerName;

	SurvivalGame(int gameNumber, int world, String displayName) {
		super(gameNumber, world);
		this.displayName = displayName;
		players = new HashSet<String>();
		spectators = new HashSet<String>();
		votes = new HashMap<String, Integer>();
		kills = new HashMap<String, Integer>();
		bounties = new HashMap<String, Integer>();
		bets = new HashMap<String, Set<Bet>>();
		chests = new ArrayList<Location>();
		deaths = new HashMap<String, String>();
		crates = new HashSet<String>();
		podiums = new HashMap<String, Location>();
		podiumLocations = DataStoreManager.getInstance().getSpawns(WorldManager.getInstance().getGameWorlds().get(this.getWorld()), "GameWorld" + this.getGameNumber());
		phaseTimer = new PhaseTimer(Phase.PREGAME, this);
		phaseTimerTask = Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), phaseTimer, 0, 20);
		phase = Phase.PREGAME;
	}

	public void startNextPhase(){
		String message = null;
		if(phaseTimerTask != null){
			phaseTimerTask.cancel();
		}
		if(phase == Phase.PREGAME && players.size() > 1){
			phase = Phase.STARTING;
			message = ChatColor.RED + "Game Starts";
		}else if(phase == Phase.PREGAME){
			phase = Phase.PREGAME;
			message = ChatColor.RED + "Waiting For More Players";
		}else if(phase == Phase.STARTING){
			phase = Phase.IN_PROGRESS;
		}else if(phase == Phase.IN_PROGRESS){
			phase = Phase.DEATHMATCH_COUNTDOWN;
			message = ChatColor.RED + "Death Match Begins";
		}else if(phase == Phase.DEATHMATCH_COUNTDOWN){
			phase = Phase.DEATHMATCH_WARMUP;
			message = ChatColor.RED + "Invulnerability Ends";
		}else if(phase == Phase.DEATHMATCH_WARMUP){
			phase = Phase.DEATHMATCH;
			message = ChatColor.RED + "Death Match Ends";
		}else if(phase == Phase.DEATHMATCH){
			phase = Phase.WINNER_SHOWCASE;
			message = ChatColor.RED + "Next Match Sets Up";
		}else if(phase == Phase.WINNER_SHOWCASE){
			phase = Phase.PREGAME;
			message = ChatColor.RED + "Waiting For More Players";
		}
		phaseTimer = new PhaseTimer(phase, this);
		phaseTimerTask = Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), phaseTimer, 0, 20);
		if(phaseTimer.getTimer() > 0){
			for(String name : players){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					BarAPI.setMessage(player, message, phaseTimer.getTimer());
				}
			}
			for(String name : spectators){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					BarAPI.setMessage(player, message, phaseTimer.getTimer());
				}
			}
		}
	}

	public void setupPhase(Phase phase){
		if(phase == Phase.PREGAME){
			setupPreGame();
		}else if(phase == Phase.STARTING){
			setupStarting();
		}else if(phase == Phase.IN_PROGRESS){
			setupInProgress();
		}else if(phase == Phase.DEATHMATCH_COUNTDOWN){
			setupDeathMatchCountDown();
		}else if(phase == Phase.DEATHMATCH_WARMUP){
			setupDeathMatchWarmUp();
		}else if(phase == Phase.DEATHMATCH){
			setupDeathMatch();
		}else if(phase == Phase.WINNER_SHOWCASE){
			setupWinnerShowCase();
		}
	}

	public Phase getPhase(){
		return phase;
	}

	private void setupPreGame() {
		Set<String> temp = new HashSet<String>();
		for(String name : spectators){
			temp.add(name);
		}
		for(String name : temp){
			removeSpectator(name);
			players.add(name);
		}
		spectators.clear();
		temp.clear();
		podiums.clear();
		winnerName = null;
		for(String name : players){
			Player player = Bukkit.getPlayerExact(name);
			if(player != null){
				World world = Bukkit.getWorld("GameLobby" + this.getGameNumber());
				player.teleport(world.getSpawnLocation());
				player.getInventory().setItem(0, new ItemStack(Material.COMPASS));
			}
		}
	}

	private void setupStarting() {
		WorldManager.getInstance().createGameWorld(this.getGameNumber(), this.getWorld());	
		int index = 0;
		List<String> toSpectators = new ArrayList<String>();
		int number = 10;
		try{
			File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			number = config.getInt("ChestPerMap", 10);
		}catch(Exception e){
			e.printStackTrace();
		}
		podiumLocations = DataStoreManager.getInstance().getSpawns(WorldManager.getInstance().getGameWorld(this.getGameNumber()), "GameWorld" + this.getGameNumber());
		GameUtils.distributeChests(number, this);
		for(String player : players){
			Player combatant = Bukkit.getPlayerExact(player);
			if(combatant != null){
				combatant.getInventory().clear();
				combatant.setFoodLevel(20);
				combatant.getInventory().setHelmet(new ItemStack(Material.AIR));
				combatant.getInventory().setBoots(new ItemStack(Material.AIR));
				combatant.getInventory().setChestplate(new ItemStack(Material.AIR));
				combatant.getInventory().setLeggings(new ItemStack(Material.AIR));
				if(index > 23){
					toSpectators.add(combatant.getName());
					combatant.teleport(podiums.get(0).getWorld().getSpawnLocation());
					combatant.sendMessage(ChatColor.YELLOW + "This game is full, you are now spectating");

				}else{
					Location spawn = GameUtils.getCenter(podiumLocations.get(index));
					podiums.put(combatant.getName(), spawn);
					combatant.teleport(spawn);
				}
				index ++;
			}
		}
		for(String name : toSpectators){
			addSpectator(name);
			players.remove(name);
		}
	}

	private void setupInProgress() {
		WorldManager.getInstance().resetDeathMatch(this.getGameNumber());
		WorldManager.getInstance().resetLobby(this.getGameNumber());
		chestDropperTask = Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), new ChestDrop(this), 0, 600);
	}

	private void setupDeathMatchCountDown() {
		if(chestDropperTask != null){
			chestDropperTask.cancel();
		}

	}

	private void setupDeathMatchWarmUp() {
		for(String name : players){
			Player player = Bukkit.getPlayerExact(name);
			if(player != null){
				World world = Bukkit.getWorld("DeathMatch" + this.getGameNumber());
				player.teleport(world.getSpawnLocation());
			}
		}
		for(String name : spectators){
			Player player = Bukkit.getPlayerExact(name);
			if(player != null){
				World world = Bukkit.getWorld("DeathMatch" + this.getGameNumber());
				player.teleport(world.getSpawnLocation());
			}
		}
	}

	private void setupDeathMatch() {

	}

	private void setupWinnerShowCase() {
		if(winnerName != null){
			final Player winner = Bukkit.getPlayerExact(winnerName);
			if(winner != null){
				int bounty = 0;
				if(bounties.get(winnerName) != null){
					bounty = bounties.get(winnerName);
				}
				int betShare = 0;
				Set<Bet> betSet = bets.get(winnerName);
				if(betSet != null){
					for(Bet bet : betSet){
						betShare += (int)(0.10 * bet.getAmount());
						int betterPoints = PlayerManager.getInstance().getPoints(bet.getName());
						PlayerManager.getInstance().setPoints(bet.getName(), betterPoints + (2 * bet.getAmount()));
						Player better = Bukkit.getPlayerExact(bet.getName());
						if(better != null){
							better.sendMessage(ChatColor.GREEN + "Your bet has earned you " + ChatColor.DARK_GREEN + (2 * bet.getAmount()) + " points.");
						}
						SurvivalGames.getInstance().getLogger().log(Level.INFO, bet.getName() + " earned " + (2 * bet.getAmount()) + "points from their bet.");

						int points = PlayerManager.getInstance().getPoints(bet.getName());
						PlayerManager.getInstance().setPoints(bet.getName(), points + (bet.getAmount() * 2));

					}
				}
				if(winner != null){
					winner.sendMessage(ChatColor.GREEN + "You have won the match.");
					if(betShare != 0){
						winner.sendMessage(ChatColor.GREEN + "You have earned " + ChatColor.DARK_GREEN + betShare + ChatColor.GREEN + " points from the betting pool.");
					}
					if(bounty != 0){
						winner.sendMessage(ChatColor.GREEN + "You have earned " + ChatColor.DARK_GREEN + bounty + ChatColor.GREEN + " points from the unclaimed bounties on your head.");
					}
				}
				SurvivalGames.getInstance().getLogger().log(Level.INFO, winnerName + " earned " + (50 + bounty + betShare) + "points for winning");
				PlayerManager.getInstance().setPoints(winnerName, PlayerManager.getInstance().getPoints(winnerName) + 50 + bounty + betShare);
				winnerPerk = Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), new WinnerPerk(winnerName, this), 0, 100);
			}
		}else{
			for(String name : players){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "The death match has been declared a draw!");
				}
			}
			for(String name : spectators){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "The death match has been declared a draw!");
				}
			}
		}

	}

	public int getKills(String name){
		if(kills.containsKey(name)){
			return kills.get(name);
		}else{
			return 0;
		}
	}

	/*@Override
	void startGame() {
		if(isInProgress()){
			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Game " + getGameNumber() + " tried to start twice");
			return;
		}

		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Game " + getGameNumber() + " is starting. Voting will last three minutes");
		for(String player : players){
			Player gamePlayer = Bukkit.getPlayerExact(player);
			if(gamePlayer != null){
				World world = Bukkit.createWorld(new WorldCreator("GameLobby" + getGameNumber()));
				gamePlayer.teleport(world.getSpawnLocation());
			}		
		}
		for(String player : spectators){
			Player gamePlayer = Bukkit.getPlayerExact(player);
			if(gamePlayer != null){
				World world = Bukkit.createWorld(new WorldCreator("GameLobby" + getGameNumber()));
				gamePlayer.teleport(world.getSpawnLocation());
			}
			players.add(gamePlayer.getName());
			gamePlayer.getInventory().setItem(0, new ItemStack(Material.COMPASS));

		}
		for(String name : players){
			Player player = Bukkit.getPlayerExact(name);
			if(player != null){
				player.getInventory().setItem(0, new ItemStack(Material.COMPASS));
			}

		}
		spectators.clear();
		Bukkit.getScheduler().scheduleSyncDelayedTask(SurvivalGames.getInstance(), new VotingPhase(this), 3600);
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Game is done starting");
	}
	 */


	/*@Override
	void endGame() {
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Game is ending");
		isDeathMatch = false;
		deathmatchIsStarting = false;
		this.winnerName = players.iterator().next();
		if(chestDropper != null){
			chestDropper.cancel();
		}
		final Player winner = Bukkit.getPlayerExact(winnerName);
		if(winner != null){
			for(String name : getAllSpectators()){
				Player spectator = Bukkit.getPlayerExact(name);
				if(spectator != null){
					spectator.sendMessage(ChatColor.YELLOW + winner.getName() + " has won the match!");
				}
			}
			World hub = Bukkit.getWorld(WorldManager.getInstance().getHubworld());
			for(Player player : hub.getPlayers()){
				player.sendMessage(ChatColor.GREEN + winner.getName() + ChatColor.RED + " has emerged victorious in " + displayName);
			}
			winner.setAllowFlight(true);
			winner.setFlying(true);
			winnerPerk = Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), new Runnable(){
				public void run(){
					if(winner != null){
						Firework fw = (Firework) winner.getWorld().spawnEntity(winner.getLocation(), EntityType.FIREWORK);
						FireworkMeta fwm = fw.getFireworkMeta();
						fw.setFireworkMeta(getRandomFireWork(fwm));  
					}
				}
			}, 100, 100);
			Bukkit.getScheduler().runTaskLater(SurvivalGames.getInstance(), new Runnable(){
				public void run(){
					if(winnerPerk != null){
						winnerPerk.cancel();
					}
				}
			}, 600);
			for(int i = 0; i < 5; i++){
				Bukkit.getScheduler().runTaskLater(SurvivalGames.getInstance(), new Runnable(){
					public void run(){
						for(Location loc : podiums.values()){
							if(winner != null){
								Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
								FireworkMeta fwm = fw.getFireworkMeta();
								fw.setFireworkMeta(getRandomFireWork(fwm));  
							}
						}
					}
				}, 20 * i);
			}

		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(SurvivalGames.getInstance(), new Runnable(){

			public void run(){
				if(chestDropper != null){
					chestDropper.cancel();
				}
				if(!isInProgress()){
					System.out.println("Preventing double end");
					return;
				}
				setInProgress(false);
				if(deathMatchCountDown != null){
					deathMatchCountDown.cancel();
					deathMatchCountDown = null;
				}

				Set<String> temp = new HashSet<String>();
				for(String name : spectators){
					temp.add(name);
					players.add(name);
				}
				for(String name : temp){
					removeSpectator(name);
				}
				spectators.clear();
				temp.clear();
				temp = null;

				for(String name : players){
					World world = Bukkit.createWorld(new WorldCreator("GameLobby" + getGameNumber()));
					Player player = Bukkit.getPlayerExact(name);
					if(player != null){
						player.teleport(world.getSpawnLocation());
						player.sendMessage(ChatColor.YELLOW + "You are now in lobby " + getGameNumber());
						player.setFoodLevel(20);
						player.setExhaustion(0);
					}
					player.getInventory().clear();
					player.getInventory().setItem(0, new ItemStack(Material.COMPASS));
				}

				spectators.clear();
				kills.clear();
				bounties.clear();
				bets.clear();
				chests.clear();
				deaths.clear();
				crates.clear();
				podiums.clear();
				startingBlocks.clear();
				isDeathMatch = false;
				setInProgress(false);
				deathmatchIsStarting = false;
				if(deathMatchCountDown != null){
					deathMatchCountDown.cancel();
					deathMatchCountDown = null;
				}
				SurvivalGames.getInstance().getLogger().log(Level.INFO, "Game " + getGameNumber() + " has ended.");
				setInProgress(false);
				startGame();
			}
		}, 250);
		SurvivalGames.getInstance().getLogger().log(Level.INFO, "Game is done ending");
	}*/

	@Override
	public void joinGame(String name) {
		Player player = Bukkit.getPlayerExact(name);
		if(player != null){
			SurvivalGames.getInstance().getLogger().log(Level.INFO, player.getName() + " is joining game " + (getGameNumber() + 1));
			if(this.phase != Phase.PREGAME){
				Location loc = Bukkit.getWorld("GameWorld" + getGameNumber()).getSpawnLocation();
				if(this.phase == Phase.DEATHMATCH || this.phase == Phase.DEATHMATCH_WARMUP){
					loc = Bukkit.getWorld("DeathMatch" + getGameNumber()).getSpawnLocation();
				}
				Player first = Bukkit.getPlayerExact(players.iterator().next());
				if(first != null){
					loc = first.getLocation();
				}
				player.teleport(loc);
				addSpectator(name);
			}else{
				players.add(name);
				World world = Bukkit.createWorld(new WorldCreator("GameLobby" + getGameNumber()));
				player.teleport(world.getSpawnLocation());
			}
		}
	}

	/*public void cancelVotingPhase(){
		if(votingCountdown != null){
			votingCountdown.cancel();
		}
	}*/

	public void onPlayerDeath(String name, String killerName){
		SurvivalGames.getInstance().getLogger().log(Level.INFO, name + " died.");
		deaths.put(name, killerName);
		leaveGame(name, true);
		int originalPoints = PlayerManager.getInstance().getPoints(name);
		int change = (int) ((originalPoints * 0.05) + 5);

		SurvivalGames.getInstance().getLogger().log(Level.INFO, name + " lost " + (change) + "points for dying");

		PlayerManager.getInstance().setPoints(name, originalPoints - change);
		if(killerName != null){
			int bounty = 0;
			if(bounties.get(name) != null){
				bounty = bounties.get(name);
			}
			originalPoints = PlayerManager.getInstance().getPoints(killerName);
			SurvivalGames.getInstance().getLogger().log(Level.INFO, killerName + " earned " + (change + bounty) + "points for a kill");

			PlayerManager.getInstance().setPoints(killerName, originalPoints + change + bounty);
			if(kills.containsKey(killerName) && kills.get(killerName) != null){
				kills.put(killerName, kills.get(killerName) + 1);
			}else{
				kills.put(killerName, 1);
			}
			Player killer = Bukkit.getPlayerExact(killerName);
			if(killer != null){
				addKillStreak(killer);
			}
		}
		int playersForDeathMatch = 10;
		try{
			File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			playersForDeathMatch = config.getInt("PlayersRemainingForDeathMatch");
		}catch(Exception e){
			e.printStackTrace();
		}
		if(players.size() < 2){
			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Ending game" + getGameNumber());
			winnerName = players.iterator().next();
			startPhase(Phase.WINNER_SHOWCASE);
		}else if(players.size() < playersForDeathMatch){
			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Death match starting due to few players remaining");
			if(this.phase == Phase.IN_PROGRESS){
				startPhase(Phase.DEATHMATCH_COUNTDOWN);
			}
		}
		SurvivalGames.getInstance().getLogger().log(Level.INFO, name + " is done dying.");
	}

	private void startPhase(Phase phase) {
		if(phaseTimerTask != null){
			phaseTimerTask.cancel();
		}
		this.phase = phase;
		phaseTimer = new PhaseTimer(phase, this);
		phaseTimerTask = Bukkit.getScheduler().runTaskTimer(SurvivalGames.getInstance(), phaseTimer, 0, 20);
	}

	@Override
	public void leaveGame(String name, boolean died) {
		SurvivalGames.getInstance().getLogger().log(Level.INFO, name + " is leaving game.");

		if(name.equalsIgnoreCase(winnerName)){
			if(winnerPerk != null){
				winnerPerk.cancel();
			}
		}
		if(players.contains(name) && this.phase != Phase.PREGAME && !died){
			for(String Playername : players){
				Player player = Bukkit.getPlayerExact(Playername);
				if(player != null){
					player.sendMessage(ChatColor.YELLOW + name + " has left the game, and has been disqualified");
				}
			}
			for(String Spectatorname : spectators){
				Player player = Bukkit.getPlayerExact(Spectatorname);
				if(player != null){
					player.sendMessage(ChatColor.YELLOW + name + " has left the game, and has been disqualified");
				}
			}
		}
		World world = Bukkit.createWorld(new WorldCreator(WorldManager.getInstance().getHubworld()));
		PlayerManager.getInstance().removeGame(name);
		PlayerManager.getInstance().unsetPlayerMap(name);
		Player player = Bukkit.getPlayerExact(name);
		if(player != null){
			player.teleport(world.getSpawnLocation());
		}
		int playersForDeathMatch = 10;
		try{
			File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			playersForDeathMatch = config.getInt("PlayersRemainingForDeathMatch");
		}catch(Exception e){
		}
		if(players.size() < 2 && this.phase != Phase.PREGAME && !died){
			winnerName = players.iterator().next();
			startPhase(Phase.WINNER_SHOWCASE);
		}else if(players.size() < playersForDeathMatch && this.phase != Phase.PREGAME && !died){
			SurvivalGames.getInstance().getLogger().log(Level.INFO, "Death match starting due to few players remaining");
		}
		votes.remove(name);
		players.remove(name);
		if(spectators.contains(name)){
			removeSpectator(name);
		}
		SurvivalGames.getInstance().getLogger().log(Level.INFO, name + " is done leaving game.");
	}


	@Override
	public Set<String> getAllPlayers() {
		return players;
	}

	public void addSpectator(String name) {
		Player spectator = Bukkit.getPlayerExact(name);
		if(spectator == null){
			return;
		}
		spectator.sendMessage(ChatColor.YELLOW + "You are now spectating game " + (getGameNumber() + 1) + ".");
		for(Player player : Bukkit.getOnlinePlayers()){
			player.hidePlayer(spectator);
		}
		spectator.setAllowFlight(true);
		spectator.setFlying(true);
		spectators.add(name);
		players.remove(name);

	}

	public void removeSpectator(String name) {
		Player spectator = Bukkit.getPlayerExact(name);
		if(spectator == null){
			return;
		}
		spectator.sendMessage(ChatColor.YELLOW + "You are no longer spectating game " + (getGameNumber() + 1) + ".");
		for(Player player : Bukkit.getOnlinePlayers()){
			player.showPlayer(spectator);
		}
		spectator.setAllowFlight(false);
		spectator.setFlying(false);
		spectators.remove(name);
	}

	public boolean isSpectator(String name) {
		return spectators.contains(name);
	}

	public Set<String> getAllSpectators() {
		return spectators;
	}

	public Map<String, Integer> getKills(){
		return kills;
	}

	public void addKillStreak(Player player){
		double random = Math.random() * 100;
		int killCount = kills.get(player.getName());
		killCount = Math.min(killCount, 8);

		int crateKills = 0;
		try{
			File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			crateKills = config.getInt("KillsForCarePackage");
		}catch(Exception e){
			e.printStackTrace();
		}

		if(kills.get(player.getName()) == crateKills){
			crates.add(player.getName());
			player.getInventory().addItem(new ItemStack(Material.REDSTONE_TORCH_ON));
			player.sendMessage(ChatColor.RED + "Place the redstone torch to call in a sponsorship crate");
		}

		int duration = (int)(Math.random() * ((killCount * 5) + 5));
		if(player.hasPermission("survivalgames.premium")){
			random += Math.random() * 15;
		}
		PotionEffect pe = null;
		if(random > 90){
			pe = new PotionEffect(PotionEffectType.REGENERATION, duration * 20, 0);
		}else if(random > 80){
			pe = new PotionEffect(PotionEffectType.INVISIBILITY, duration * 20, 0);
		}else if(random > 60){
			pe = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration * 20, 0);
		}else if(random > 40){
			pe = new PotionEffect(PotionEffectType.SPEED, duration * 20, 0);
		}else if(random > 20){
			pe = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration * 20, 0);
		}else{
			pe = new PotionEffect(PotionEffectType.FAST_DIGGING, duration * 20, 0);
		}
		player.addPotionEffect(pe);
	}

	public boolean useCrate(String name){
		if(crates.contains(name)){
			crates.remove(name);
			return true;
		}
		return false;
	}

	public void addBounty(String name, int amount){
		int currentBounty = 0;
		if(bounties.containsKey(name)){
			currentBounty = bounties.get(name);
		}
		bounties.put(name, currentBounty + amount);

		for(String player : players){
			Player combatant = Bukkit.getPlayerExact(player);
			if(combatant != null){
				combatant.sendMessage(ChatColor.RED + "A bounty of " + ChatColor.DARK_GREEN + amount + ChatColor.RED + " points has been put on " + ChatColor.DARK_RED + name + ChatColor.RED + ". (Total: " + (currentBounty + amount) + ")");
			}
		}
		for(String player : spectators){
			Player spectator = Bukkit.getPlayerExact(player);
			if(spectator != null){
				spectator.sendMessage(ChatColor.RED + "A bounty of " + ChatColor.DARK_GREEN + amount + ChatColor.RED + " points has been put on " + ChatColor.DARK_RED + name + ChatColor.RED + ". (Total: " + (currentBounty + amount) + ")");
			}
		}
	}

	public void addBet(String better, String bettee, int amount){
		Bet bet = new Bet(better, amount);
		Set<Bet> betSet = null;
		if(bets.containsKey(bettee)){
			betSet = bets.get(bettee);
		}
		if(betSet == null){
			betSet = new HashSet<Bet>();
		}
		betSet.add(bet);
		bets.put(bettee, betSet);
		Player issuer = Bukkit.getPlayerExact(better);
		if(issuer != null){
			issuer.sendMessage(ChatColor.GREEN + "You have placed a bet of " + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " points on " + ChatColor.DARK_RED + bettee + ".");
		}
		Player recipient = Bukkit.getPlayerExact(bettee);
		if(recipient != null){
			recipient.sendMessage(ChatColor.GREEN + "A bet of " + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " points has been placed on you by " + ChatColor.DARK_RED + better + ".");
		}
	}

	public int getDropTotal(){
		int number = 10;
		try{
			File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			number = config.getInt("ChestPerMap", 10)/2;
		}catch(Exception e){
			e.printStackTrace();
		}
		return number;
	}

	public void addChest(Location loc){
		chests.add(loc);
	}

	public boolean wasOpened(Location loc){
		return chests.contains(loc);
	}

	public boolean didDie(String playername){
		return deaths.containsKey(playername);
	}

	public void resurrect(String name){
		Player player = Bukkit.getPlayerExact(name);
		if(player == null){
			return;
		}
		if(PlayerManager.getInstance().getNumberOfPasses(name) < 1){
			player.sendMessage(ChatColor.YELLOW + "You do not have any resurrection passes.");
		}else{
			PlayerManager.getInstance().removePass(name);
		}
		String killer = deaths.get(name);
		Location spawn = null;
		if(killer == null){
			spawn = Bukkit.getWorld("GameWorld" + getGameNumber()).getSpawnLocation();	
		}else{
			Player slayer = Bukkit.getPlayerExact(killer);
			spawn = GameUtils.getRandomLocation(slayer.getLocation(), 25);
			int highest = Bukkit.getWorld("GameWorld" + getGameNumber()).getHighestBlockYAt(spawn);
			spawn.setY(highest);
		}

		removeSpectator(name);
		players.add(name);
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
		player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		player.getInventory().setItemInHand(new ItemStack(Material.WOOD_SWORD));
		PotionEffect pe = new PotionEffect(PotionEffectType.SPEED, 200, 0);
		player.addPotionEffect(pe);
	}

	public Location resetLocation(String name){
		Player player = Bukkit.getPlayerExact(name);
		return podiums.get(player.getName());
	}

	public int getDMRadius(){
		return dmRadius;
	}

	public void setDMRadius(int dmRadius){
		this.dmRadius = dmRadius;
	}


	public Map<String, Location> getPodiums(){
		return podiums;
	}

	public void cancelWinnerPerk(){
		if(winnerPerk != null){
			winnerPerk.cancel();
		}
	}

	public int getTimer() {
		return phaseTimer.getTimer();
	}
}
