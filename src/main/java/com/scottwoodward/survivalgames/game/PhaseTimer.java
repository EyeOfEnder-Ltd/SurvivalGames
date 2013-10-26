package com.scottwoodward.survivalgames.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PhaseTimer implements Runnable {

	private int seconds;
	private Phase phase;
	private SurvivalGame game;

	PhaseTimer(Phase phase, SurvivalGame game){
		this.phase = phase;
		seconds = this.phase.getTimer();
		this.game = game;
		this.game.setupPhase(phase);
	}
	
	public int getTimer(){
		return seconds;
	}

	@Override
	public void run() {
		if(seconds > 0){
			announce(this.phase);
		}else if(phase != Phase.IN_PROGRESS){
			game.startNextPhase();
		}else if(phase == Phase.IN_PROGRESS && game.getAllPlayers().size() <= 10){
			game.startNextPhase();
		}
		seconds--;
	}

	public void announce(Phase phase){
		if(phase == Phase.PREGAME){
			announcePregame();
		}else if(phase == Phase.STARTING){
			announceStarting();
		}else if(phase == Phase.IN_PROGRESS){
			announceInProgress();
		}else if(phase == Phase.DEATHMATCH_COUNTDOWN){
			announceDeathmatchCountdown();
		}else if(phase == Phase.DEATHMATCH_WARMUP){
			announceDeathmatchWarmup();
		}else if(phase == Phase.DEATHMATCH){
			announceDeathmatch();
		}else if(phase == Phase.WINNER_SHOWCASE){
			announceWinnerShowcase();
		}
	}

	public void announcePregame(){
		if(this.seconds == 60 || this.seconds == 45 || this.seconds == 30 || this.seconds == 15 || (this.seconds > 0 && this.seconds <=10)){
			for(String name : game.getAllPlayers()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "Waiting " + ChatColor.GREEN + this.seconds + ChatColor.RED + " more seconds for players.");
				}
			}
			for(String name : game.getAllSpectators()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "Waiting " + ChatColor.GREEN + this.seconds + ChatColor.RED + " more seconds for players.");
				}
			}
		}
	}

	public void announceStarting(){
		if(this.seconds == 60 || this.seconds == 45 || this.seconds == 30 || this.seconds == 15 || (this.seconds > 0 && this.seconds <=10)){
			for(String name : game.getAllPlayers()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "The game will begin in " + ChatColor.GREEN + this.seconds + ChatColor.RED + "  seconds.");
				}
			}
			for(String name : game.getAllSpectators()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "The game will begin in " + ChatColor.GREEN + this.seconds + ChatColor.RED + "  seconds.");
				}
			}
		}
	}

	public void announceInProgress(){
		//Nothing currently
	}

	public void announceDeathmatchCountdown(){
		if(this.seconds == 60 || this.seconds == 45 || this.seconds == 30 || this.seconds == 15 || (this.seconds > 0 && this.seconds <=10)){
			for(String name : game.getAllPlayers()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "The death match will begin in " + ChatColor.GREEN + this.seconds + ChatColor.RED + "  seconds.");
				}
			}
			for(String name : game.getAllSpectators()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "The death match will begin in " + ChatColor.GREEN + this.seconds + ChatColor.RED + "  seconds.");
				}
			}
		}
	}

	private void announceDeathmatchWarmup() {
		if(this.seconds == 60 || this.seconds == 45 || this.seconds == 30 || this.seconds == 15 || (this.seconds > 0 && this.seconds <=10)){
			for(String name : game.getAllPlayers()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "Invulnerability will end in " + ChatColor.GREEN + this.seconds + ChatColor.RED + "  seconds.");
				}
			}
			for(String name : game.getAllSpectators()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "Invulnerability will end in " + ChatColor.GREEN + this.seconds + ChatColor.RED + "  seconds.");
				}
			}
		}
	}

	private void announceDeathmatch() {
		if(this.seconds == 60 || this.seconds == 45 || this.seconds == 30 || this.seconds == 15 || (this.seconds > 0 && this.seconds <=10)){
			for(String name : game.getAllPlayers()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "The Death Match will end with no with no winner in " + ChatColor.GREEN + this.seconds + ChatColor.RED + " seconds.");
				}
			}
			for(String name : game.getAllSpectators()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					player.sendMessage(ChatColor.RED + "The Death Match will end with no with no winner in " + ChatColor.GREEN + this.seconds + ChatColor.RED + " seconds.");
				}
			}
		}
	}

	private void announceWinnerShowcase() {
		if(this.seconds == 10){
			for(String name : game.getAllPlayers()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){
					
				}
			}
			for(String name : game.getAllSpectators()){
				Player player = Bukkit.getPlayerExact(name);
				if(player != null){

				}
			}
		}
	}
}
