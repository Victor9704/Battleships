package com.BattleShips.main;
import java.util.ArrayList;

public class Battleship {
	
	private int size = 0;
	
	//Coordinates Array
	private int Coordinates[][];
	private int CoordinatesArrayLoc = 0;//Used to keep track of row index
	
	public Battleship(int getsize) {
		size = getsize;
		Coordinates = new int[size][2];
	}

	public void AddCoordinate(int row, int col) {
		Coordinates[CoordinatesArrayLoc][0] = row;
		Coordinates[CoordinatesArrayLoc][1] = col;
		
		CoordinatesArrayLoc++;
	}
	
	public void print() {
		System.out.println("Ship coordinates are : ");
		for(int i =0;i<size;i++) {
			System.out.println("y (row) = " + this.Coordinates[i][0] + " , x (col) = " + this.Coordinates[i][1] + ".");
		}
	}
	
	public boolean CheckIfHit(Point Hit) {
		
		boolean check = false;
		
		for(int i =0;i<size;i++) {
			if(Coordinates[i][1] == Hit.getY() && Coordinates[i][0] == Hit.getX()) {
				check = true;
			}
		}
		
		return check;
	}

}
