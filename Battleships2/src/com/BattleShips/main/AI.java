package com.BattleShips.main;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class AI {
	
	//Ship List
	ArrayList<Battleship> ShipList;
	
	//Last Hit Position Stack
	private Stack<Point> HitStack;
	
	//Guess tries list
	private ArrayList<Integer> GuessArray;
	
	List<Integer>previousChoices = null;//4th time is guaranteed, that it will hit an other part of the ship
	
	//State
	private boolean Kill = false;
	
	//Random Kill Direction Checkers;
	
	//Kill Direction Checkers
	boolean goLeft = false;
	boolean goRight = false;
	
	boolean wasRight = false;
	boolean wasLeft = false;
	
	boolean goUp = false;
	boolean goDown = false;
	
	boolean wasUp = false;
	boolean wasDown = false;
	
	//Ship Direction boolean Checkers
	boolean isHorizontal = false;
	boolean isVertical = false;
	
	//Guessing
	boolean isGuessing = false;
	
	//Destroyed Ships
	int smallshipsDestroyed = 0;
	int mediumshipsDestroyed = 0;
	
	//Destroyed Ships boolean
	boolean destroyShip = false;
	
	private String name = "Johnny";
	private int gameBoard[][];
	private int nrColumns;
	private int nrRows;
	
	public AI(int rows, int cols, ArrayList<Battleship> SL) {
		gameBoard = new int[cols][rows];
		HitStack = new Stack<Point>();
		GuessArray = new ArrayList<Integer>();
		nrColumns = cols;
		nrRows = rows;
		ShipList = SL;
		}
	
	//<---- COUNT DESTROYED SHIPS ---->
	public void countDestroyedShips() {
		
		//Check all positions of all ships if hit!
		for(int i = 0; i<ShipList.size();i++) {
			
			Battleship b = ShipList.get(i);
			
			//Verify each small ship
			if(b instanceof Smallship) {
				
				//Get Ship Positions
				Point p1 = b.getCoordinates(0);
				Point p2 = b.getCoordinates(1);
				Point p3 = b.getCoordinates(2);
				
				//If all positions Hit remove ship
				if(getPosition(p1.getX(), p1.getY()) == 1 && getPosition(p2.getX(), p2.getY()) == 1 && getPosition(p3.getX(), p3.getY()) == 1) {
					
					System.out.println("Small ship Destroyed!");
					smallshipsDestroyed += 1;
					ShipList.remove(b);
					
				}
				
			}
			
			//Verify each medium ship
			if(b instanceof Mediumship) {
				
				//Get Ship Positions
				Point p1 = b.getCoordinates(0);
				Point p2 = b.getCoordinates(1);
				Point p3 = b.getCoordinates(2);
				Point p4 = b.getCoordinates(3);
				Point p5 = b.getCoordinates(4);
				
				//If all positions Hit remove ship
				if(getPosition(p1.getX(), p1.getY()) == 1 && getPosition(p2.getX(), p2.getY()) == 1 && getPosition(p3.getX(), p3.getY()) == 1 && getPosition(p4.getX(),p4.getY()) == 1 &&
						getPosition(p5.getX(),p5.getY()) == 1) {
					
					System.out.println("Medium ship Destroyed");
					smallshipsDestroyed += 1;
					ShipList.remove(b);
					
				}
				
			}
		}
		
	}
	
	//<---- CHECK IF ALL SHEAPS ARE DESTROYED ---->
	
	public boolean checkShipList() {
		
		if(ShipList.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
		
	}
	
	//<---- HIT FUNCTION ---->
	
	public Point HitFunction() {
		
		Point Hit = new Point();
		
		//Check the state of the AI first, hunt or kill!
		//Hunting mode
		if(Kill == false) {
			Hit = HuntFunction();
		}
		//Killing mode
		else if(isGuessing == true){
			Hit = RandomKillFunctionDirection(HitStack.firstElement());   					
		}
		else if(Kill == true && isGuessing == false) {
			Hit = KillFunction();
		}
		return Hit;
		
	}
	
	
	
	//<---- HUNT "STATE" FUNCTION ---->
	
	//Hunt function which uses RecursiveHit function and returns the Hit point when the RecursiveHit function hits a non-hit Point on the
	//Grid Pane, as long as Kill is false (condition set in Controller)
	//Hunt/Hit State
	public Point HuntFunction() {
		
		Point Hit = new Point();
		int randomcol = generateRandomCol();
		int randomrow = generateRandomRow();
		
		Hit = RecursiveHit(randomcol,randomrow);
		
		//Check if Hit(Point) is actually a position of a Ship
		if(CheckHit(Hit)) {
			
			System.out.println("Hit a Ship!");
			
			//Add the hit to the stack
			HitStack.push(Hit);
			
			//Change State
			Kill = true;
			//Start Guessing
			isGuessing = true;
					
		}
		
		return Hit;
		
	}
	
	//Hits Randomly Up, Down, Right, Left after it hits a ship, to check if it is horizontal or vertical
	public Point RandomKillFunctionDirection(Point Hit){
		
		Point direction = new Point();
		
		Random random = new Random();
		int randomnr = random.nextInt(4)+1;
		
		//Check if already tried
		if(GuessArray.isEmpty() == false) {
			for(int i : GuessArray) {
				//If already in the List, search recursively another 1
				if(randomnr == i) {
					return RandomKillFunctionDirection(Hit);
				}
			}
		}
		
		//Check Left
		if(randomnr == 1) {
			
			direction.setX(Hit.getX());
			direction.setY(Hit.getY()-1);
			
			GuessArray.add(randomnr);
			
			//Check if Out of gridpane or already hit
			if(direction.getY() >=0 && getPosition(direction.getX(), direction.getY()) != 1) {
				if(CheckHit(direction)) {
					//System.out.println("Horizontal Ship Detected");
					isHorizontal = true;
					goLeft = true;
					isGuessing = false;
					GuessArray.clear();
					HitStack.add(direction);
					RecursiveHit(direction.getX(),direction.getY());
					return direction;
				}
				else {
					RecursiveHit(direction.getX(),direction.getY());
					return direction;
				}
			}
			else {
				//Search another unhit position (out of the 4 possible) recursively
				return RandomKillFunctionDirection(Hit);
			}			
		}
		//Check right
		else if(randomnr == 2) {
			
			direction.setX(Hit.getX());
			direction.setY(Hit.getY()+1);
			
			GuessArray.add(randomnr);
			
			if(direction.getY() <20 && getPosition(direction.getX(), direction.getY()) != 1) {
				if(CheckHit(direction)) {
					//System.out.println("Horizontal Ship Detected");
					isHorizontal = true;
					goRight = true;
					isGuessing = false;
					GuessArray.clear();
					HitStack.add(direction);
					RecursiveHit(direction.getX(),direction.getY());
					return direction;
				}
				else {
					RecursiveHit(direction.getX(),direction.getY());
					return direction;
				}
			}
			else {
				return RandomKillFunctionDirection(Hit);
			}
			
		}
		//Check Up
		else if(randomnr == 3) {
			
			direction.setX(Hit.getX()-1);
			direction.setY(Hit.getY());
			
			GuessArray.add(randomnr);
			
			if(direction.getX() >=0 && getPosition(direction.getX(), direction.getY()) != 1) {
				if(CheckHit(direction)) {
					//System.out.println("Vertical Ship Detected");
					isVertical = true;
					goUp = true;
					isGuessing = false;
					GuessArray.clear();
					HitStack.add(direction);
					RecursiveHit(direction.getX(),direction.getY());
					return direction;
				}
				else {
					RecursiveHit(direction.getX(),direction.getY());
					return direction;
				}
			}
			else {
				return RandomKillFunctionDirection(Hit);
			}
			
		}
		else if(randomnr == 4) {
			
			direction.setX(Hit.getX()+1);
			direction.setY(Hit.getY());
			
			GuessArray.add(randomnr);
			
			if(direction.getX() <20 && getPosition(direction.getX(), direction.getY()) != 1) {
				if(CheckHit(direction)) {
					//System.out.println("Vertical Ship Detected");
					isVertical = true;
					goDown = true;
					isGuessing = false;
					GuessArray.clear();
					HitStack.add(direction);
					RecursiveHit(direction.getX(),direction.getY());
					return direction;
				}
				else {
					RecursiveHit(direction.getX(),direction.getY());
					return direction;
				}
			}
			else {
				return RandomKillFunctionDirection(Hit);
			}
			
		}
		
		return new Point();
		
	}
	
	//<---- KILL STATE FUNCTIONS ---->
	
	//Kill Function
	public Point KillFunction() {
		
		Point Hit = new Point();
		
		if(isHorizontal == true)
		{
			Hit = HorizontalKill();
		}
		else if(isVertical == true) {
			Hit = VerticalKill();
		}
		
		return Hit;
		
	}
	
	
	//<---- HORIZONTAL KILL STATE FUNCTIONS ---->
	
	//Kill Horizontal Function
	private Point HorizontalKill() {
		
		Point Hit = new Point();
		
		if(goRight == true && wasRight == false) {
			Hit = KillRight();
		}
		else if(goLeft == true && wasLeft == false) {
			Hit = KillLeft();
		}
		else if(wasRight == true && wasLeft == true) {
			Kill = false;
			wasRight = false;
			wasLeft = false;
			goLeft = false;
			goRight = false;
			HitStack.clear();
			isHorizontal = false;
			Hit = HitFunction();//Return another Hit, after Kill is done(False)
			//System.out.println("Ship is Dead");
		}
		
		return Hit;
		
	}
	
	private Point KillRight() {
		
		Point previousHit = HitStack.lastElement();
		Point Hit = new Point();
		
		Hit.setX(previousHit.getX());
		Hit.setY(previousHit.getY()+1);
		
		//Check if right is out of Grid or Point already Hit.
		//It can cause a random hit before continuing the actual kill, because it always need to hit something that is not hit!
		if(Hit.getY()<20 && getPosition(Hit.getX(), Hit.getY()) != 1) {
			Hit = RecursiveHit(Hit.getX(),Hit.getY());
			HitStack.push(Hit);
			if(!CheckHit(Hit)) {
				//System.out.println("Change to Left");
				
				goRight = false;
				goLeft = true;
				wasRight = true;
				
				Point temp = HitStack.firstElement();
				HitStack.clear();
				HitStack.push(temp);
				
			}
			return Hit;
		}
		//If Right is out of Grid or already Hit, go Left if you weren't already, or continue to Hit Function=> Reset every bool
		//related to KillFunction AND ALSO THE STACK and start hunting again.
		else {
			
			//System.out.println("Hit margin right");
			//Kill = false;
			goRight = false;
			goLeft = true;
			wasRight = true;
				
			Point temp = HitStack.firstElement();
			HitStack.clear();
			HitStack.push(temp);
				
			if(wasLeft == false) {
				//System.out.println("Go Left");
				Hit = KillLeft();
			}
			else {
				//System.out.println("Already was left :(");
				Hit = HitFunction();
			}
				
		}
		return Hit;
		
	}
	
	private Point KillLeft() {
		
		Point previousHit = HitStack.lastElement();
		Point Hit = new Point();
		
		Hit.setX(previousHit.getX());
		Hit.setY(previousHit.getY()-1);//Verifica daca e deja lovit
		//System.out.println("X = " + Hit.getX() + " Y = " + Hit.getY());
		
		//Check if right is out of Grid or Point already Hit.
		//It can cause a random hit before continuing the actual kill, because it always need to hit something that is not hit!
		if(Hit.getY() >= 0 && getPosition(Hit.getX(), Hit.getY()) != 1) {
			Hit = RecursiveHit(Hit.getX(),Hit.getY());
			HitStack.push(Hit);
			if(!CheckHit(Hit)) {
				//System.out.println("Change to right");
				goLeft = false;
				goRight = true;
				wasLeft = true;
				
				Point temp = HitStack.firstElement();
				HitStack.clear();
				HitStack.push(temp);
				
			}
			return Hit;
		}
		else {
			//System.out.println("Hit margin left");

			goLeft = false;
			goRight = true;
			wasLeft = true;
			
			Point temp = HitStack.firstElement();
			HitStack.clear();
			HitStack.push(temp);
			
			if(wasRight == false) {
				//System.out.println("Go Right");
				Hit = KillRight();
			}
			else {
				//System.out.println("Already was right :(");
				Hit = HitFunction();
			}
			
		}
		return Hit;
		
	}
	
	//<---- VERTICAL KILL STATE FUNCTIONS ---->
		
	//Kill Horizontal Function
	private Point VerticalKill() {
		
		Point Hit = new Point();
		
		if(goUp == true && wasUp == false) {
			Hit = KillUp();
		}
		else if(goDown == true && wasDown == false) {
			Hit = KillDown();
		}
		else if(wasUp == true && wasDown == true) {
			Kill = false;
			wasUp = false;
			wasDown = false;
			goDown = false;
			goUp = false;
			HitStack.clear();
			isVertical = false;
			Hit = HitFunction();//Return another Hit, after Kill is done(False)
			//System.out.println("Ship is Dead");
		}
		
		return Hit;
		
	}
	
	private Point KillUp() {
		
		Point previousHit = HitStack.lastElement();
		Point Hit = new Point();
		
		Hit.setX(previousHit.getX()-1);
		Hit.setY(previousHit.getY());
		
		//Check if right is out of Grid or Point already Hit.
		//It can cause a random hit before continuing the actual kill, because it always need to hit something that is not hit!
		if(Hit.getX()>=0 && getPosition(Hit.getX(), Hit.getY()) != 1) {
			Hit = RecursiveHit(Hit.getX(),Hit.getY());
			HitStack.push(Hit);
			if(!CheckHit(Hit)) {
				//System.out.println("Change to Down");
				
				goUp = false;
				goDown = true;
				wasUp = true;
				
				Point temp = HitStack.firstElement();
				HitStack.clear();
				HitStack.push(temp);
				
			}
			return Hit;
		}
		//If Right is out of Grid or already Hit, go Left if you weren't already, or continue to Hit Function=> Reset every bool
		//related to KillFunction AND ALSO THE STACK and start hunting again.
		else {
			
			//System.out.println("Hit margin Up");
			//Kill = false;
			goUp = false;
			goDown = true;
			wasUp = true;
				
			Point temp = HitStack.firstElement();
			HitStack.clear();
			HitStack.push(temp);
				
			if(wasDown == false) {
				//System.out.println("Go Down");
				Hit = KillDown();
			}
			else {
				//System.out.println("Already was down :(");
				Hit = HitFunction();
			}
				
		}
		return Hit;
		
	}
	
	private Point KillDown() {
		
		Point previousHit = HitStack.lastElement();
		Point Hit = new Point();
		
		Hit.setX(previousHit.getX()+1);
		Hit.setY(previousHit.getY());//Verifica daca e deja lovit
		
		//Check if right is out of Grid or Point already Hit.
		//It can cause a random hit before continuing the actual kill, because it always need to hit something that is not hit!
		if(Hit.getX() < 20 && getPosition(Hit.getX(), Hit.getY()) != 1) {
			Hit = RecursiveHit(Hit.getX(),Hit.getY());
			HitStack.push(Hit);
			if(!CheckHit(Hit)) {
				//System.out.println("Change to up");
				goDown = false;
				goUp = true;
				wasDown = true;
				
				Point temp = HitStack.firstElement();
				HitStack.clear();
				HitStack.push(temp);
				
			}
			return Hit;
		}
		else {
			//System.out.println("Hit margin down");

			goDown = false;
			goUp = true;
			wasDown = true;
			
			Point temp = HitStack.firstElement();
			HitStack.clear();
			HitStack.push(temp);
			
			if(wasUp == false) {
				//System.out.println("Go Up");
				Hit = KillUp();
			}
			else {
				//System.out.println("Already was Up :(");
				Hit = HitFunction();
			}
			
		}
		return Hit;
		
	}
	
	
	//The recursive function 
	
	public Point RecursiveHit (int col, int row) {
		
		if(gameBoard[col][row] == 0) {			
			
			Point p = new Point();
			gameBoard[col][row] = 1;
			p.setX(col);
			p.setY(row);
			
			//If we hit a ship AND THE AI KNOWS IT IS HORIZONTAL, hit neighbours too, so that computer won;t hit the neighbours ever again ( he knows ship can;t be one next to another)
			if(isHorizontal == true) {
				if(CheckHit(p)) {
					//Also hit the neighbours of first Hit of the ship, which is always first on stack, it can not be empty if isHorizontal or is Vertical is true!
					if(col+1 < 20) {
						gameBoard[col+1][row] = 1;
					}
					if(col-1 >= 0) {
						gameBoard[col-1][row] = 1;
					}
				}
			}			
			//If we hit a ship AND THE AI KNOWS IT IS VERTICAL, hit neighbours too, so that computer won;t hit the neighbours ever again ( he knows ship can;t be one next to another)
			if(isVertical == true) {
				if(CheckHit(p)) {
					if(row+1 < 20) {
						gameBoard[col][row+1] = 1;
					}
					if(row-1 >= 0) {
						gameBoard[col][row-1] = 1;
					}
				}
			}
			
			return p;
			
		}
		else {
			int randomcol = generateRandomCol();
			int randomrow = generateRandomRow();
			return RecursiveHit(randomcol,randomrow);
		}
		
	}
	
	////Check if Hit(Point) is actually a position of a Ship FUNCTION
	public boolean CheckHit(Point Hit) {
		
		boolean check = false;
		
		for(Battleship i : ShipList) {
			check = i.CheckIfHit(Hit);
			//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			//IF A TRUE POSITION IS FOUND, STOP SEARCHING AND RETURN
			//THERE CAN"T BE PREVIOUS HIT POSITIONS BECAUSE WE CHECK BEFORE IF THE POSITION IS ALREADY HIT!
			if(check == true) {
				return check;
			}
		}
		
		return check;
	}
	
	//<---- STATE FUNCTIONS ---->
	
	public boolean getState() {
		return Kill;
	}
	
	//<---- STACK FUNCTIONS ---->
	
	public Point returnLastHit() {
		
		return HitStack.lastElement();
		
	}
	
	public void addToStack(Point Hit) {
		
		HitStack.push(Hit);
		
	}
	
	//<---- MATH FUNCTIONS ---->
	
	private int generateRandomCol() {
		
		Random RandomGenerator = new Random();
		
		int r =  RandomGenerator.nextInt(20) + 0;
		
		return r;
	}
	
	private int generateRandomRow() {
		
		Random RandomGenerator = new Random();
		
		int r =  RandomGenerator.nextInt(20) + 0;
		
		return r;
	}
	
	//<---- SCRAP ---->
	
	/*public void DisplayShips() {
		for(Battleship i : ShipList) {
			i.print();
		}
	}*/
	
	public int getPosition(int col, int row) {
		
		return gameBoard[col][row];
		
	}
	
	//TODO Check with recursive hit Function
	public Point hitPosition(int col, int row) {
		
		Point trytohit = new Point();
		
		if(gameBoard[col][row] == 0){
			gameBoard[col][row] = 1;
			trytohit.setX(col);
			trytohit.setY(row);
			return trytohit;
		}
		else {
			//System.out.println("Already hit" + " Y = " + col + " X = " + row );
			//If already hit search another position.
			System.out.println("Another uncolored Position :(");
			trytohit = RecursiveHit(col,row);
			gameBoard[trytohit.getX()][trytohit.getY()] = 1;
			return trytohit;
		}
	}
	
	public int[][] GetBoard(){
		
		return gameBoard;
		
	}
	
}
