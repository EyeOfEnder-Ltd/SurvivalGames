package com.scottwoodward.survivalgames.game;

public class Bet {
	
	private String name;
	private int amount;
	
	public Bet(String name, int amount){
		this.name = name;
		this.amount = amount;
	}
	
	public String getName(){
		return name;
	}

	public int getAmount(){
		return amount;
	}
}
