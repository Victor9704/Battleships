package com.BattleShips.main;

public class Mediumship extends Battleship {
	
	public Mediumship(int size, int row1, int col1, int row2, int col2, int row3, int col3, int row4, int col4, int row5, int col5) {
		super(size);
		this.AddCoordinate(row1, col1);
		this.AddCoordinate(row2, col2);
		this.AddCoordinate(row3, col3);
		this.AddCoordinate(row4, col4);
		this.AddCoordinate(row5, col5);
	}
	
}
