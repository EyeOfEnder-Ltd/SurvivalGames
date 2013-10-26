package com.scottwoodward.survivalgames;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.scottwoodward.survivalgames.commands.CommandManager;
import com.scottwoodward.survivalgames.disguises.DisguiseManager;
import com.scottwoodward.survivalgames.game.Game;
import com.scottwoodward.survivalgames.game.GameManager;
import com.scottwoodward.survivalgames.game.Phase;
import com.scottwoodward.survivalgames.game.SurvivalGame;
import com.scottwoodward.survivalgames.listeners.BlockBreakListener;
import com.scottwoodward.survivalgames.listeners.BlockExplosionListener;
import com.scottwoodward.survivalgames.listeners.BlockPlaceListener;
import com.scottwoodward.survivalgames.listeners.ChatListener;
import com.scottwoodward.survivalgames.listeners.EntityDropListener;
import com.scottwoodward.survivalgames.listeners.ExplosionListener;
import com.scottwoodward.survivalgames.listeners.FallingChestListener;
import com.scottwoodward.survivalgames.listeners.InventoryClickListener;
import com.scottwoodward.survivalgames.listeners.InventoryOpenListener;
import com.scottwoodward.survivalgames.listeners.MonsterSpawnListener;
import com.scottwoodward.survivalgames.listeners.MonsterTargetListener;
import com.scottwoodward.survivalgames.listeners.PlayerCollideListener;
import com.scottwoodward.survivalgames.listeners.PlayerDamageByPlayerListener;
import com.scottwoodward.survivalgames.listeners.PlayerDamageListener;
import com.scottwoodward.survivalgames.listeners.PlayerDeathListener;
import com.scottwoodward.survivalgames.listeners.PlayerHungerListener;
import com.scottwoodward.survivalgames.listeners.PlayerInteractListener;
import com.scottwoodward.survivalgames.listeners.PlayerJoinListener;
import com.scottwoodward.survivalgames.listeners.PlayerMovementListener;
import com.scottwoodward.survivalgames.listeners.PlayerPickupEntitiesListener;
import com.scottwoodward.survivalgames.listeners.PlayerQuitListener;
import com.scottwoodward.survivalgames.listeners.PlayerRespawnListener;
import com.scottwoodward.survivalgames.players.PlayerManager;

public class SurvivalGames extends JavaPlugin{

	private static SurvivalGames instance;
	private static boolean initialized;

	public void onEnable(){
		initialized = false;
		instance = this;
		registerEvents();
		registerCommands();
		saveDefaultConfig();
		DisguiseManager.getInstance().loadDisguises();
		try{
			File file = new File(SurvivalGames.getInstance().getDataFolder(), "config.yml");
			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			if(config.getBoolean("AutoInitialize")){
				GameManager.getInstance().init();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void onDisable(){

	}

	public static SurvivalGames getInstance(){
		return instance;
	}

	private void registerEvents(){
		Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new MonsterTargetListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerHungerListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerDamageByPlayerListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerMovementListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerPickupEntitiesListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new EntityDropListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new FallingChestListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new InventoryOpenListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerCollideListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new MonsterSpawnListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new ExplosionListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new BlockExplosionListener(), this);
	}

	private void registerCommands(){
		getCommand("bounty").setExecutor(CommandManager.getInstance());
		getCommand("bet").setExecutor(CommandManager.getInstance());
		getCommand("sponsor").setExecutor(CommandManager.getInstance());
		getCommand("setpodium").setExecutor(CommandManager.getInstance());
		getCommand("init").setExecutor(CommandManager.getInstance());
		getCommand("points").setExecutor(CommandManager.getInstance());
		getCommand("setworldspawn").setExecutor(CommandManager.getInstance());
		getCommand("worldtp").setExecutor(CommandManager.getInstance());
		getCommand("resurrect").setExecutor(CommandManager.getInstance());
		getCommand("addpass").setExecutor(CommandManager.getInstance());
		getCommand("fstart").setExecutor(CommandManager.getInstance());
	}

	public void updateTabList(){

		for(Game game : GameManager.getInstance().getGames()){
			SurvivalGame sg = (SurvivalGame)game;
			if(sg.getPhase() != Phase.PREGAME){
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getNewScoreboard();
				Objective obj = board.registerNewObjective("remainingplayers", "dummy");
				obj.setDisplayName(ChatColor.GREEN + "Top Noodles");
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);

				Map<String, Integer> kills = ((SurvivalGame)game).getKills();
				Entry<String, Integer> first = null;
				Entry<String, Integer> second = null;
				Entry<String, Integer> third = null;
				for(Entry<String, Integer> entry : kills.entrySet()){
					if( first == null || entry.getValue() > first.getValue()){
						third = second;
						second = first;
						first = entry;
					}else if(second == null || entry.getValue() > second.getValue()){
						third = second;
						second = entry;
					}else if(third == null || entry.getValue() > third.getValue()){
						third = entry;
					}
				}
				if(first != null){
					Score one = obj.getScore(Bukkit.getOfflinePlayer(first.getKey()));
					one.setScore(first.getValue());
				}
				if(second != null){
					Score two = obj.getScore(Bukkit.getOfflinePlayer(second.getKey()));
					two.setScore(second.getValue());
				}
				if(third != null){
					Score three = obj.getScore(Bukkit.getOfflinePlayer(third.getKey()));
					three.setScore(third.getValue());
				}
				if(sg.getPhase() == Phase.DEATHMATCH_COUNTDOWN || sg.getPhase() == Phase.DEATHMATCH_WARMUP){
					Score deathmatch = obj.getScore(Bukkit.getOfflinePlayer("Deathmatch Timer"));
					deathmatch.setScore(sg.getTimer());
				}
				for(String name : game.getAllPlayers()){
					Bukkit.getPlayerExact(name).setScoreboard(board);
				}
				for(String name : ((SurvivalGame)game).getAllSpectators()){
					Bukkit.getPlayerExact(name).setScoreboard(board);
				}
			}else{
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getNewScoreboard();
				Objective obj = board.registerNewObjective("mapvotes", "dummy");
				obj.setDisplayName(ChatColor.GREEN + "Match Begins");
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);

				Score timer = obj.getScore(Bukkit.getOfflinePlayer("Start Match"));
				int seconds = ((SurvivalGame)game).getTimer();
				timer.setScore(seconds);
				for(String name : game.getAllPlayers()){
					Bukkit.getPlayerExact(name).setScoreboard(board);
				}
			}
		}

		for(Player player : Bukkit.getOnlinePlayers()){
			
			if(!PlayerManager.getInstance().isInGame(player.getName())){
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getNewScoreboard();
				Objective obj = board.registerNewObjective("availablelobbies", "dummy");
				obj.setDisplayName(ChatColor.GREEN + "Lobby Status");
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);
				for(Game game : GameManager.getInstance().getGames()){
					String message = null;
					SurvivalGame sg = (SurvivalGame)game;
					if(sg.getPhase() != Phase.PREGAME){
						message = ChatColor.AQUA + "Lobby " + (game.getGameNumber() + 1);
					}else{
						message = ChatColor.GREEN + "Lobby " + (game.getGameNumber() + 1);
					}
					Score score = obj.getScore(Bukkit.getOfflinePlayer(message));
					score.setScore(game.getAllPlayers().size());
				}
				player.setScoreboard(board);
			}
		}
	}
	
	public boolean getInitialized(){
		return initialized;
	}
	
	public void setInitialized(boolean initialized){
		this.initialized = initialized;
	}
}


