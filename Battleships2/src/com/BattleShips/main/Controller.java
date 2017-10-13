package com.BattleShips.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.management.Notification;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class Controller implements Initializable {
	
	//<---- VARIABLES ---->
	
	public int timesHit = 0; //!resetable
	
	//Shiplist
	private ArrayList<Battleship> ShipList = new ArrayList<Battleship>(); //!resetable
	
	//Number of 3 box ships
	private int Smallships = 0; //!resetable
	//Number of 5 box ships
	private int Mediumships = 0; //!resetable
	
	//GridPane height and width
	private final int GRID_HEIGHT = 600;
	private final int GRID_WIDTH = 600;
	
	//GridPane rows and columns
	private final int HEIGHT = 20; // nr. of rows
	private final int WIDTH = 20; // nr. of cols
	
	//"Checkers"
	private boolean placingShips = true; //!resetable
	private boolean Placeable = true; //If boxes are not red, this is true.
	
	//Start with whichever is true
	private boolean PlacingSmallShipsHorizontal = true;//Switch Number 1, resetable
	private boolean PlacingSmallShipsVertical = false;//Switch Number 2, resetable
	
	private boolean PlacingMediumShipsHorizontal = false;//Switch Number 3, resetable
	private boolean PlacingMediumShipsVertical = false;//Switch Number 4, resetable
	
	//TODO Boolean to change GridPanes after game starts
	private boolean ChangeGridPanes = true;
	
	//Boolean to reset the game
	private boolean resetGameBool = true;

	// The AI, initilized in the function with the game Thread down, because it need the ShipList, after the ships are placed!
	private AI AI; //!resetable
	
	//Bool to reset AI
	private boolean resetAI = true; //After the reset button this becomes true until all ships are placed, verified before AI.Hitfunction get's used in run!
	
	//<---- GUI ELEMENTS BY ID ---->
	
	@FXML//Player GridPane
	public GridPane fPlayer_Grid;
	
	@FXML//Computer GridPane
	public GridPane AI_Grid;
	
	@FXML//Rotate Button
	private Button rotateBtn;
	
	@FXML//Ready Button
	private Button readyBtn;
	
	@FXML
	public AnchorPane anchor1;
	
	//<---- SHIP FUNCTIONS ---->
	
	public void SmallShipsHorizontal() {
		
		//Place Ship Event
		fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				
				@Override
				public void handle(MouseEvent e) {
						
						//Check here because if the Horizontal switches we want to not advance further in the function
						//=> PlacingSmallShipsHorizontal = false => function does not go further in the next if!
						if(Smallships > 3) {
							ShipBooleanListener(1,false);
							//PlacingSmallShipsHorizontal = false;
							System.out.println("Horizontal State " + PlacingSmallShipsHorizontal);
						}
						
						Object clickedObj = e.getTarget();
	
						int row = GridPane.getRowIndex((Node)clickedObj);
						int col = GridPane.getColumnIndex((Node)clickedObj);
						
						//Take into account Smallships need 1 free box on margin, else NullPointer (out of the GridPane!)
						if(PlacingSmallShipsHorizontal == true && col+1<WIDTH && col-1>=0) {
							
							Node left = getNodeByRowColumnIndex(row, col-1, fPlayer_Grid);
							Node right = getNodeByRowColumnIndex(row, col+1, fPlayer_Grid);
							
							if(clickedObj instanceof VBox && left instanceof VBox && right instanceof VBox ) {
								
								//Create Placed Ship on GridPane out of Panes (see the function which creates them)
								fPlayer_Grid.getChildren().remove(left);
								fPlayer_Grid.add(createGridPaneCanvas(), col-1, row);
								fPlayer_Grid.getChildren().remove(right);
								fPlayer_Grid.add(createGridPaneCanvas(), col+1, row);
								fPlayer_Grid.getChildren().remove((VBox)clickedObj);
								fPlayer_Grid.add(createGridPaneCanvas(), col, row);
								
								//Create Ship objects and add them to the list
								ShipList.add(new Smallship(3,row,col-1,row,col,row,1+col));
								
								//Verify if all small ships have been placed
								Smallships++;
								//!!!! IMPORTANT, there is a same if at the begginning of the function, but that verifies and uses
								//the listener ONLY AFTER WE CLICK AGAIN, but we need exactly after the condition becomes availible,
								//(Else there is a bug with rotate button) therefore we put it here again!
								if(Smallships > 3) {
									ShipBooleanListener(1,false);
								}
								
							}
							else if(PlacingSmallShipsHorizontal == true){
								//<------ TO DO ------>
								System.out.println("There's already a ship!");
							}					
						}
						else if(PlacingSmallShipsHorizontal == true){
							//<------ TO DO ------>
							System.out.println("Can not place!");
						}
				}
			
		});
		
		//Hover over the GridPane event, create "Skeleton"		
		fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent e) {
				if(placingShips == true && Smallships < 4) {
					Object hoveredObj = e.getTarget();
					
					if(hoveredObj instanceof VBox) {
					
					int row = GridPane.getRowIndex((Node)hoveredObj);
					int col = GridPane.getColumnIndex((Node)hoveredObj);

						if(PlacingSmallShipsHorizontal == true && col+1<WIDTH && col-1>=0) {
							
							Node left = getNodeByRowColumnIndex(row, col-1, fPlayer_Grid);
							Node right = getNodeByRowColumnIndex(row, col+1, fPlayer_Grid);
							
							if(left instanceof VBox && right instanceof VBox) {							
								
								((VBox)hoveredObj).setStyle("-fx-background-color: #0000FF;");
								((VBox)left).setStyle("-fx-background-color: #0000FF;");
								((VBox)right).setStyle("-fx-background-color: #0000FF;");
								
							}
							
						}
					}
				}
			}
			
		});
		
		//Exit previously hovered GridPane area
		//Hover Exit Function (Delete Ships "Skeleton")
		fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent e) {
				if(placingShips == true && Smallships < 4) {
					Object hoveredObj = e.getTarget();
					
					if(hoveredObj instanceof VBox) {
						
						int row = GridPane.getRowIndex((Node)hoveredObj);
						int col = GridPane.getColumnIndex((Node)hoveredObj);
							
							if(PlacingSmallShipsHorizontal == true && col+1<WIDTH && col-1>=0) {
								
								Node left = getNodeByRowColumnIndex(row, col-1, fPlayer_Grid);
								Node right = getNodeByRowColumnIndex(row, col+1, fPlayer_Grid);
								
								if(left instanceof VBox && right instanceof VBox) {							
									
									((VBox)hoveredObj).setStyle("-fx-background-color: #333333;");
									((VBox)left).setStyle("-fx-background-color: #333333;");
									((VBox)right).setStyle("-fx-background-color: #333333;");
									
								}
								
							}
					}
				}
			}		
		});
		
	}
	
	public void SmallShipsVertical() {
		
		//Place Ship Event
		fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				
				@Override
				public void handle(MouseEvent e) {
						
						//Check here because if the Horizontal switches we want to not advance further in the function
						//=> PlacingSmallShipsVertical = false => function does not go further in the next if!
						if(Smallships > 3) {
							ShipBooleanListener(2,false);
							//PlacingSmallShipsVertical = false;
							System.out.println("Vertical State " + PlacingSmallShipsVertical);
						}
					
						Object clickedObj = e.getTarget();
	
						int row = GridPane.getRowIndex((Node)clickedObj);
						int col = GridPane.getColumnIndex((Node)clickedObj);
						
						//Take into account Smallships need 1 free box on margin, else NullPointer (out of the GridPane!)
						if(PlacingSmallShipsVertical == true && row+1<HEIGHT && row-1>=0) {
							
							Node up = getNodeByRowColumnIndex(row-1, col, fPlayer_Grid);
							Node down = getNodeByRowColumnIndex(row+1, col, fPlayer_Grid);
							
							if(clickedObj instanceof VBox && up instanceof VBox && down instanceof VBox ) {
								
								fPlayer_Grid.getChildren().remove(up);
								fPlayer_Grid.add(createGridPaneCanvas(), col, row-1);
								fPlayer_Grid.getChildren().remove(down);
								fPlayer_Grid.add(createGridPaneCanvas(), col, row+1);
								fPlayer_Grid.getChildren().remove((VBox)clickedObj);
								fPlayer_Grid.add(createGridPaneCanvas(), col, row);
								
								//Create Ship objects and add them to the list
								ShipList.add(new Smallship(3,row-1,col,row,col,row+1,col));
								
								//Verify if all small ships have been placed
								Smallships++;
								if(Smallships > 3) {
									ShipBooleanListener(2,false);
								}
								
							}
							else if(PlacingSmallShipsVertical == true){
								//<------ TO DO ------>
								System.out.println("There's already a ship!");
							}					
						}
						else if(PlacingSmallShipsVertical == true){
							//<------ TO DO ------>
							System.out.println("Can not place!");
						}
				}
			
		});
		
		//Hover over the GridPane event, create "Skeleton"	
		fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, new EventHandler<MouseEvent>() {
					
					@Override
					public void handle(MouseEvent e) {
						if(placingShips == true && Smallships < 4) {
							Object hoveredObj = e.getTarget();
							
							if(hoveredObj instanceof VBox) {
							
							int row = GridPane.getRowIndex((Node)hoveredObj);
							int col = GridPane.getColumnIndex((Node)hoveredObj);
								
								if(PlacingSmallShipsVertical == true && row+1<HEIGHT && row-1>=0) {
									
									Node left = getNodeByRowColumnIndex(row-1, col, fPlayer_Grid);
									Node right = getNodeByRowColumnIndex(row+1, col, fPlayer_Grid);
									
									if(left instanceof VBox && right instanceof VBox) {							
										
										((VBox)hoveredObj).setStyle("-fx-background-color: #0000FF;");
										((VBox)left).setStyle("-fx-background-color: #0000FF;");
										((VBox)right).setStyle("-fx-background-color: #0000FF;");
										
									}
									
								}
							}
						}
					}
					
				});
				
				//Exit previously hovered GridPane area
				//Hover Exit Function (Delete Ships "Skeleton")
				fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, new EventHandler<MouseEvent>() {
					
					@Override
					public void handle(MouseEvent e) {
						if(placingShips == true && Smallships < 4) {
							Object hoveredObj = e.getTarget();
							
							if(hoveredObj instanceof VBox) {
								
								int row = GridPane.getRowIndex((Node)hoveredObj);
								int col = GridPane.getColumnIndex((Node)hoveredObj);
									
									if(PlacingSmallShipsVertical == true && row+1<HEIGHT && row-1>=0) {
										
										Node left = getNodeByRowColumnIndex(row-1, col, fPlayer_Grid);
										Node right = getNodeByRowColumnIndex(row+1, col, fPlayer_Grid);
										
										if(left instanceof VBox && right instanceof VBox) {							
											
											((VBox)hoveredObj).setStyle("-fx-background-color: #333333;");
											((VBox)left).setStyle("-fx-background-color: #333333;");
											((VBox)right).setStyle("-fx-background-color: #333333;");
											
										}
										
									}
							}
						}
					}		
				});
	}
	
	//Medium Ship Horizontal
	
	public void MediumShipsHorizontal() {
		
		//Place Ship Event
		fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				
				@Override
				public void handle(MouseEvent e) {
						
						//Check here because if the Horizontal switches we want to not advance further in the function
						//=> PlacingMediumShipsHorizontal = false => function does not go further in the next if!
						if(Mediumships > 2) {
							ShipBooleanListener(3,false);
							System.out.println("Horizontal State " + PlacingMediumShipsHorizontal);
						}
						
						Object clickedObj = e.getTarget();
	
						int row = GridPane.getRowIndex((Node)clickedObj);
						int col = GridPane.getColumnIndex((Node)clickedObj);
						
						//Take into account Mediumships need 2 free boxes on margin, else NullPointer (out of the GridPane!)
						if(PlacingMediumShipsHorizontal == true && col+2<WIDTH && col-2>=0) {
							
							Node left = getNodeByRowColumnIndex(row, col-1, fPlayer_Grid);
							Node leftleft = getNodeByRowColumnIndex(row, col-2, fPlayer_Grid);
							Node right = getNodeByRowColumnIndex(row, col+1, fPlayer_Grid);
							Node rightright = getNodeByRowColumnIndex(row, col+2, fPlayer_Grid);
							
							if(clickedObj instanceof VBox && left instanceof VBox && right instanceof VBox && leftleft instanceof VBox 
									&& rightright instanceof VBox) {
								
								//Create Placed Ship on GridPane out of Panes (see the function which creates them)
								fPlayer_Grid.getChildren().remove(left);
								fPlayer_Grid.add(createGridPaneCanvas(), col-1, row);
								fPlayer_Grid.getChildren().remove(leftleft);
								fPlayer_Grid.add(createGridPaneCanvas(), col-2, row);
								fPlayer_Grid.getChildren().remove(right);
								fPlayer_Grid.add(createGridPaneCanvas(), col+1, row);
								fPlayer_Grid.getChildren().remove(rightright);
								fPlayer_Grid.add(createGridPaneCanvas(), col+2, row);
								fPlayer_Grid.getChildren().remove((VBox)clickedObj);
								fPlayer_Grid.add(createGridPaneCanvas(), col, row);
								
								//Create Ship objects and add them to the list
								ShipList.add(new Mediumship(5,row,col-2,row,col-1,row,col,row,1+col,row,col+2));
								
								//Verify if all small ships have been placed
								Mediumships++;
								
							}
							else if(PlacingMediumShipsHorizontal == true){
								//<------ TO DO ------>
								System.out.println("There's already a ship!");
							}					
						}
						else if(PlacingMediumShipsHorizontal == true){
							//<------ TO DO ------>
							System.out.println("Can not place!");
						}
				}
			
		});
		
		//Hover over the GridPane event, create "Skeleton"	
		fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, new EventHandler<MouseEvent>() {
					
					@Override
					public void handle(MouseEvent e) {
						if(placingShips == true && Mediumships < 3) {
							Object hoveredObj = e.getTarget();
							
							if(hoveredObj instanceof VBox) {
							
							int row = GridPane.getRowIndex((Node)hoveredObj);
							int col = GridPane.getColumnIndex((Node)hoveredObj);
								
								if(PlacingMediumShipsHorizontal == true && col+2<WIDTH && col-2>=0) {
									
									Node left = getNodeByRowColumnIndex(row, col-1, fPlayer_Grid);
									Node leftleft = getNodeByRowColumnIndex(row, col-2, fPlayer_Grid);
									Node right = getNodeByRowColumnIndex(row, col+1, fPlayer_Grid);
									Node rightright = getNodeByRowColumnIndex(row,col+2,fPlayer_Grid);
									
									if(left instanceof VBox && right instanceof VBox && leftleft instanceof VBox && rightright instanceof VBox) {							
										
										((VBox)hoveredObj).setStyle("-fx-background-color: #0000FF;");
										((VBox)left).setStyle("-fx-background-color: #0000FF;");
										((VBox)right).setStyle("-fx-background-color: #0000FF;");
										((VBox)leftleft).setStyle("-fx-background-color: #0000FF;");
										((VBox)rightright).setStyle("-fx-background-color: #0000FF;");
										
									}
									
								}
							}
						}
					}
					
				});
				
				//Exit previously hovered GridPane area
				//Hover Exit Function (Delete Ships "Skeleton")
				fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, new EventHandler<MouseEvent>() {
					
					@Override
					public void handle(MouseEvent e) {
						if(placingShips == true && Mediumships < 3) {
							Object hoveredObj = e.getTarget();
							
							if(hoveredObj instanceof VBox) {
								
								int row = GridPane.getRowIndex((Node)hoveredObj);
								int col = GridPane.getColumnIndex((Node)hoveredObj);
									
									if(PlacingMediumShipsHorizontal == true && col+2<WIDTH && col-2>=0) {
										
										Node left = getNodeByRowColumnIndex(row, col-1, fPlayer_Grid);
										Node leftleft = getNodeByRowColumnIndex(row, col-2, fPlayer_Grid);
										Node right = getNodeByRowColumnIndex(row, col+1, fPlayer_Grid);
										Node rightright = getNodeByRowColumnIndex(row,col+2,fPlayer_Grid);
										
										if(left instanceof VBox && right instanceof VBox && leftleft instanceof VBox && rightright instanceof VBox) {							
											
											((VBox)hoveredObj).setStyle("-fx-background-color: #333333;");
											((VBox)left).setStyle("-fx-background-color: #333333;");
											((VBox)right).setStyle("-fx-background-color: #333333;");
											((VBox)leftleft).setStyle("-fx-background-color: #333333");
											((VBox)rightright).setStyle("-fx-background-color: #333333");
											
										}
										
									}
							}
						}
					}		
				});
	}
	
	//Medium Ship Vertical
	
	public void MediumShipsVertical() {
		
		//Place Ship Event
		fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				
				@Override
				public void handle(MouseEvent e) {
						
						//Check here because if the Horizontal switches we want to not advance further in the function
						//=> PlacingMediumShipsHorizontal = false => function does not go further in the next if!
						if(Mediumships > 2) {
							ShipBooleanListener(4,false);
							System.out.println("Vertical State " + PlacingMediumShipsVertical);
						}
						
						Object clickedObj = e.getTarget();
	
						int row = GridPane.getRowIndex((Node)clickedObj);
						int col = GridPane.getColumnIndex((Node)clickedObj);
						
						//Take into account Mediumships need 2 free boxes on margin, else NullPointer (out of the GridPane!)
						if(PlacingMediumShipsVertical == true && row+2<HEIGHT && row-2>=0) {
							
							Node left = getNodeByRowColumnIndex(row-1, col, fPlayer_Grid);
							Node leftleft = getNodeByRowColumnIndex(row-2, col, fPlayer_Grid);
							Node right = getNodeByRowColumnIndex(row+1, col, fPlayer_Grid);
							Node rightright = getNodeByRowColumnIndex(row+2, col, fPlayer_Grid);
							
							if(clickedObj instanceof VBox && left instanceof VBox && right instanceof VBox && leftleft instanceof VBox 
									&& rightright instanceof VBox) {
								
								//Create Placed Ship on GridPane out of Panes (see the function which creates them)
								fPlayer_Grid.getChildren().remove(left);
								fPlayer_Grid.add(createGridPaneCanvas(), col, row-1);
								fPlayer_Grid.getChildren().remove(leftleft);
								fPlayer_Grid.add(createGridPaneCanvas(), col, row-2);
								fPlayer_Grid.getChildren().remove(right);
								fPlayer_Grid.add(createGridPaneCanvas(), col, row+1);
								fPlayer_Grid.getChildren().remove(rightright);
								fPlayer_Grid.add(createGridPaneCanvas(), col, row+2);
								fPlayer_Grid.getChildren().remove((VBox)clickedObj);
								fPlayer_Grid.add(createGridPaneCanvas(), col, row);
								
								//Create Ship objects and add them to the list
								ShipList.add(new Mediumship(5,row-2,col,row-1,col,row,col,row+1,col,row+2,col));
								
								//Verify if all small ships have been placed
								Mediumships++;
								
							}
							else if(PlacingMediumShipsVertical == true){
								//<------ TO DO ------>
								System.out.println("There's already a ship!");
							}					
						}
						else if(PlacingMediumShipsVertical == true){
							//<------ TO DO ------>
							System.out.println("Can not place!");
						}
				}
			
		});
		
		//Hover over the GridPane event, create "Skeleton"	
				fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, new EventHandler<MouseEvent>() {
							
							@Override
							public void handle(MouseEvent e) {
								if(placingShips == true && Mediumships < 3) {
									Object hoveredObj = e.getTarget();
									
									if(hoveredObj instanceof VBox) {
									
									int row = GridPane.getRowIndex((Node)hoveredObj);
									int col = GridPane.getColumnIndex((Node)hoveredObj);
										
										if(PlacingMediumShipsVertical == true && row+2<HEIGHT && row-2>=0) {
											
											Node left = getNodeByRowColumnIndex(row-1, col, fPlayer_Grid);
											Node leftleft = getNodeByRowColumnIndex(row-2, col, fPlayer_Grid);
											Node right = getNodeByRowColumnIndex(row+1, col, fPlayer_Grid);
											Node rightright = getNodeByRowColumnIndex(row+2,col,fPlayer_Grid);
											
											if(left instanceof VBox && right instanceof VBox && leftleft instanceof VBox && rightright instanceof VBox) {							
												
												((VBox)hoveredObj).setStyle("-fx-background-color: #0000FF;");
												((VBox)left).setStyle("-fx-background-color: #0000FF;");
												((VBox)right).setStyle("-fx-background-color: #0000FF;");
												((VBox)leftleft).setStyle("-fx-background-color: #0000FF;");
												((VBox)rightright).setStyle("-fx-background-color: #0000FF;");
												
											}
											
										}
									}
								}
							}
							
						});
						
						//Exit previously hovered GridPane area
						//Hover Exit Function (Delete Ships "Skeleton")
						fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, new EventHandler<MouseEvent>() {
							
							@Override
							public void handle(MouseEvent e) {
								if(placingShips == true && Mediumships < 3) {
									Object hoveredObj = e.getTarget();
									
									if(hoveredObj instanceof VBox) {
										
										int row = GridPane.getRowIndex((Node)hoveredObj);
										int col = GridPane.getColumnIndex((Node)hoveredObj);
											
											if(PlacingMediumShipsVertical == true && row+2<HEIGHT && row-2>=0) {
												
												Node left = getNodeByRowColumnIndex(row-1, col, fPlayer_Grid);
												Node leftleft = getNodeByRowColumnIndex(row-2, col, fPlayer_Grid);
												Node right = getNodeByRowColumnIndex(row+1, col, fPlayer_Grid);
												Node rightright = getNodeByRowColumnIndex(row+2,col,fPlayer_Grid);
												
												if(left instanceof VBox && right instanceof VBox && leftleft instanceof VBox && rightright instanceof VBox) {							
													
													((VBox)hoveredObj).setStyle("-fx-background-color: #333333;");
													((VBox)left).setStyle("-fx-background-color: #333333;");
													((VBox)right).setStyle("-fx-background-color: #333333;");
													((VBox)leftleft).setStyle("-fx-background-color: #333333");
													((VBox)rightright).setStyle("-fx-background-color: #333333");
													
												}
												
											}
									}
								}
							}		
						});
	}
	
	//<---- AI GRID ---->
	private void mouseOverPosition() {
		
		AI_Grid.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent e) {
				
				Object hoveredObj = e.getTarget();
				
				if(hoveredObj instanceof VBox) {
					((VBox)hoveredObj).setStyle("-fx-background-color: #0000FF");
				}
					
			}
			
		});
		
		AI_Grid.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent e) {
				
				Object hoveredObj = e.getTarget();
				
				if(hoveredObj instanceof VBox) {
					((VBox)hoveredObj).setStyle("-fx-background-color: #333333");
				}
					
			}
			
		});
	
	}
	
	
	//<---- BUTTON FUNCTIONS ---->
	
	private void RotateShip() {

			 rotateBtn.setOnAction(new EventHandler<ActionEvent>() {
		            @Override
		            public void handle(ActionEvent event) {
		        		//Check which ships are currently in use! Currently ships come in order, small to big hardcoded!
		        		//!!!! ALSO IF BOTH ARE FALSE (HORIZONTAL AND VERTICAL CHECKERS) FUNCTION SHOULD NOT RUN TO CHANGE THEIR VALUE!!
		            	//! If there are already 4 small placed, no more rotation for small ship checkers!
		            	if(Smallships < 4 ) {
			            	if(PlacingSmallShipsHorizontal == true) {
			            		PlacingSmallShipsHorizontal = false;
			            		PlacingSmallShipsVertical = true;
			            	}
			            	else {
			            		PlacingSmallShipsHorizontal = true;
			            		PlacingSmallShipsVertical = false;
			            	}
			                System.out.println("Small Horizontal Ship " + PlacingSmallShipsHorizontal);
			                System.out.println("Small Vertical Ship " + PlacingSmallShipsVertical);
		            	}
		            	else if(Mediumships < 3) {
		            		if(PlacingMediumShipsHorizontal == true) {
			            		PlacingMediumShipsHorizontal = false;
			            		PlacingMediumShipsVertical = true;
			            	}
		            		else {
			            		PlacingMediumShipsHorizontal = true;
			            		PlacingMediumShipsVertical = false;
			            	}
		            		System.out.println("Medium Horizontal Ship " + PlacingMediumShipsHorizontal);
			                System.out.println("Medium Vertical Ship " + PlacingMediumShipsVertical);
		            	}
		            }
		     });
	}
	
	//Start game button function 
	
	private void StartGame() {		
		
		//Initialize the AI, ALWAYS LAST FUNCTION IN THE INITIALIZAION!!!!!!!!
		//Always last because we need all the Ships already in the ShipList!!!!
		AI = new AI(HEIGHT, WIDTH, ShipList);
		
		//readyBtn.setOnAction(new EventHandler<ActionEvent>() {
		AI_Grid.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			
            @Override
            public void handle(/*ActionEvent*/MouseEvent event) {
            	
            	
            	/*Runnable task*/ Platform.runLater( new Runnable() {
            		
        			public void run() {
        				
        				
        				//First verify if Player placed all ships
        				
        				if(Smallships < 4 || Mediumships < 3) {
        					System.out.println("Place all Ships first!");
        					return;
        				}
        				
        				//TODO Try to invert grids after game starts       				
        				
        				if(timesHit == 200) {
        					timesHit++;
        					System.out.println("A pause");
        					return;
        				}
        				
        				
        				//while(timesHit < 400)
        				//{
	        			if(Smallships > 3 && Mediumships > 2) {
	        				
	        				resetGameBool = false;
	        				
	        				//If the reset button gets pressed, resetAI will be true => after the reset => after the if() which verifies at the 
	        				//beginning of the function if the ships are placed, the AI will be recreated here and the bool will be false again until
	        				//next reset (and next reset/recreation of the AI!)
	        				if(resetAI == true) {
	        					
	        					AI = new AI(HEIGHT, WIDTH, ShipList);
	        					resetAI = false;
	        					
	        				}
	        				
	        				//This if here is only to prevent the game form continuing if the AI or player (later implemented) wins!
	        				if(AI.checkShipList()) {
	        					System.out.println("Game over! Computer won!");
	        					return;
	        				}
	        			
	        				//PLAYER TURN ---->
	        				
	    					Object hoveredObj = event.getTarget();
	    					
	    					if(hoveredObj instanceof VBox) {
	    						
	    						int row = GridPane.getRowIndex((Node)hoveredObj);
	    						int col = GridPane.getColumnIndex((Node)hoveredObj);
	    						
	    						AI_Grid.getChildren().remove(hoveredObj);
	    						
	    						Pane p = new Pane();
	    						p.setStyle("-fx-background-color: #FF0000");
	    						p.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
	    						AI_Grid.add(p, col, row);
	    					}
	        				
	    					
	    					//AI TURN ---->
	    					
	        				Point HitCoordinates = new Point();
	        				
	        				HitCoordinates = AI.HitFunction();
	        				
	        				Node Hit = getNodeByRowColumnIndex(HitCoordinates.getX(), HitCoordinates.getY(), fPlayer_Grid);
	        				
	        				if(Hit.getStyle() == "-fx-background-color: #00ff00;" || Hit.getStyle() == "-fx-background-color: #000000;") {
	        					Hit.setStyle("-fx-background-color: #000000;");
	        				}
	        				else {
	        					Hit.setStyle("-fx-background-color: #FF0000;");
	        				}
	        				
	        				AI.countByAIDestroyedShips();
	        				
	        				//Only after we are sure in case of game restart that AI is initialized we can check its ShipList!
	        				if(AI.checkShipList()) {
	        					System.out.println("Computer won!");
	        					return;
	        				}
        				}
	        			
	        			//AI.countDestroyedShips();
	        			
        				timesHit++;
        				//}
        				
        				System.out.println(timesHit);
        			}
        			
        		});
        		
        		/*Thread backgroundThread = new Thread(task);
        		backgroundThread.setDaemon(true);
        		backgroundThread.start();*/
            	
            }
		});
		
	}
	
	//Reset Function
	private void resetGame() {
		
		
		readyBtn.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				//!Reset the player grid
				fPlayer_Grid.getChildren().clear();
				populateGridPane(fPlayer_Grid);
				//populateGridPane(fPlayer_Grid);
				
				//!Reset the AI grid
				AI_Grid.getChildren().clear();
				populateGridPane(AI_Grid);
				
				//!Reset values
				timesHit = 0;
				ShipList = new ArrayList<Battleship>();
				Smallships = 0;
				Mediumships = 0;
				placingShips = true;
				PlacingSmallShipsHorizontal = true;
				PlacingSmallShipsVertical = false;
				PlacingMediumShipsHorizontal = false;
				PlacingMediumShipsVertical = false;
				AI = null;
				
				resetGameBool = true;
				resetAI = true;
				
			}
			
		});
		
		
	}
	
	
	//<---- LISTENERS ---->
	
	//Ship placement booleans with Switch
	
	public void ShipBooleanListener(int SwitchNumber, boolean bEnabled)
	{
		switch(SwitchNumber) {
			
		case 1:
		    if(PlacingSmallShipsHorizontal == bEnabled) {
		    	return;
		    }
		    
		    PlacingSmallShipsHorizontal = bEnabled;
		    
		    if(PlacingSmallShipsHorizontal == false && PlacingSmallShipsVertical == false) {
		    	PlacingMediumShipsHorizontal = true;//The conditions becomes availible in the same time with the placement of the last 3 box Ship
		    }									   //BUT won't be place because can;t place over another ship, that's why here PlacingMediumShipsHorizontal = true
		    break;
		    
		case 2:
		    if(PlacingSmallShipsVertical == bEnabled) {
		    	return;
		    }
		    
		    PlacingSmallShipsVertical = bEnabled;
		    
		    if(PlacingSmallShipsHorizontal == false && PlacingSmallShipsVertical == false) {
		    	PlacingMediumShipsVertical = true; //The conditions becomes availible in the same time with the placement of the last 3 box Ship
		    }									   //BUT won't be place because can;t place over another ship, that's why here PlacingMediumShipsVertical = true
		    break;
		    
		case 3:
		    if(PlacingMediumShipsHorizontal == bEnabled) {
		    	return;
		    }
		   
		    PlacingMediumShipsHorizontal = bEnabled;
		    
		    if(PlacingMediumShipsHorizontal == false && PlacingMediumShipsVertical == false) {
		    	//Next size ship if you want to add another 1 later!
		    	
		    }
		    break;
		case 4:
		    if(PlacingMediumShipsVertical == bEnabled) {
		    	return;
		    }
		   
		    PlacingMediumShipsVertical = bEnabled;
		    
		    if(PlacingMediumShipsHorizontal == false && PlacingMediumShipsVertical == false) {
		    	//Next size ship if you want to add another 1 later!
		    	
		    }
		    break;
		default:
			break;
		}
	}
	
	//<---- THREADS + Thread Examples ---->
	
	//Example
	private void CheckShipsPoistions() {
		
		Runnable task = new Runnable() {
			
			public void run() {
				DisplayShips();
			}
			
		};
		
		Thread backgroundThread = new Thread(task);
		backgroundThread.setDaemon(true);
		backgroundThread.start();
		
	}
	
	//<---- CREATE PLAYER GRID ---->
	
	private void setPlayerGrid(GridPane GridPane) {
		
		createGridPane(GridPane);
		populateGridPane(GridPane);
		
	}
	
	//<---- CREATE AI GRID ---->
	
	private void setAIGrid(GridPane GridPane) {
		
		createGridPane(GridPane);
		populateGridPane(GridPane);
		
	}
	
	//<---- CREATE GRID PANE FUNCTIONS ---->
	
	public void createGridPane(GridPane GridPane) {
		
//		GridPane.setMaxHeight(2*GRID_HEIGHT);
//		GridPane.setMaxWidth(2*GRID_WIDTH);
		GridPane.setMinHeight(GRID_HEIGHT);
		GridPane.setMinWidth(GRID_WIDTH);
		GridPane.setPrefHeight(GRID_HEIGHT);
		GridPane.setPrefWidth(GRID_WIDTH);
		GridPane.setGridLinesVisible(false);
		
		for(int i =0;i<WIDTH;i++) {
			ColumnConstraints colConst = new ColumnConstraints();
			colConst.setPercentWidth(100.0 / WIDTH);
			GridPane.getColumnConstraints().add(colConst);
		}
		
		for(int i =0;i<HEIGHT;i++) {
			RowConstraints rowConst = new RowConstraints();
			rowConst.setPercentHeight(100.0 / HEIGHT);
			GridPane.getRowConstraints().add(rowConst);
		}
		
		//!!!HIT FUNCTION!!!
		/*if(placingShips == false) {
			//Click Event for Grid/GridChildren
			fPlayer_Grid.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
				
				@Override
				public void handle(MouseEvent e) {
					
					Object clickedObj = e.getTarget();
					if(clickedObj instanceof VBox) {
						((VBox) clickedObj).setStyle("-fx-background-color: #878787;");
					}
					else{
						//<------ TO DO ------>
						System.out.println("Select a field");
					}
				
				}
			
			});
		}*/
	
	}
	
	//Create Grid Coloured Boxes
	private VBox createGridPaneBoxes() {
		
		VBox box = new VBox();
		box.setStyle("-fx-background-color: #333333;");
		box.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		return box;
		
	}
	
	//Create Pane to replace VBoxes when ships are placed
	private Pane createGridPaneCanvas() {
		
		Pane pane = new Pane();
		pane.setStyle("-fx-background-color: #00ff00;");
		pane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		return pane;
		
	}
	
	//Fill the Grid with Coloured Boxes
	private void populateGridPane(GridPane GridPane) {
		
		for(int x =0;x<20;x++) {
			
			for(int y=0;y<20;y++) {
				
				GridPane.add(createGridPaneBoxes(), x, y);
				
			}
			
		}

	}
	
	//<---- FUNCTIONS ---->
	
	//!!!Search Node in GridPane by coordinates function
	public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
	    
		Node result = null;
	    ObservableList<Node> childrens = gridPane.getChildren();

	    for (Node node : childrens) {
	        if(gridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null && gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
	            result = node;
	            break;
	        }
	    }

	    return result;
	    
	}
	
	@FXML
	private void DisplayBoard() {
		
		int[][] Board = AI.GetBoard();
		
		for(int i = 0; i<20; i++) {
			for(int j = 0; j<20; j++) {
				System.out.print("[ "+Board[i][j]+" ]");
			}
			System.out.println("");
		}
	
	}
	
	@FXML
	public void DisplayShips() {
		for(Battleship i : ShipList) {
			i.print();
		}
	}
	
	//<---- INITIALIZATION FUNCTION ---->
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setPlayerGrid(fPlayer_Grid);
		setAIGrid(AI_Grid);
		RotateShip();
		SmallShipsHorizontal();
		SmallShipsVertical();
		MediumShipsHorizontal();
		MediumShipsVertical();
		mouseOverPosition();
		resetGame();
		StartGame();

		
	}
	
}