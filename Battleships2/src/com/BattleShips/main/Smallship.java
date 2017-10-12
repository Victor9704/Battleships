package com.BattleShips.main;

public class Smallship extends Battleship {
		
	public Smallship(int size,int row1, int col1, int row2, int col2, int row3, int col3) {
		super(size);
		this.AddCoordinate(row1, col1);
		this.AddCoordinate(row2, col2);
		this.AddCoordinate(row3, col3);
	}		

}
