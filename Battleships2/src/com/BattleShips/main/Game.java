package com.BattleShips.main;

import java.util.ArrayList;

public class Game {
	
	private ArrayList<Battleship> ShipList = new ArrayList<Battleship>();
	
	public Game(ArrayList<Battleship> SL) {
		ShipList = SL;
	}
	
	public void DisplayShips() {
		for(Battleship i : ShipList) {
			i.print();
		}
	}
	
}
