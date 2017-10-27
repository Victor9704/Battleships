package com.BattleShips.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.management.Notification;

import org.controlsfx.control.Notifications;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.net.www.http.PosterOutputStream;

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
	//TODO WAS TRUE FOMR THE START OF THE APP!
	private boolean resetAI = false; //After the reset button this becomes true until all ships are placed, verified before AI.Hitfunction get's used in run!
	
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
							
							//Verify neighbour nodes
							Node upLeft = null;
							Node downLeft = null;
							Node upClickedObj = null;
							Node downClickedObj = null;
							Node upRight = null;
							Node downRight = null;
							Node leftMargin = null;
							Node rightMargin = null;
							
							if( row-1>=0 ) {
								upLeft = getNodeByRowColumnIndex(row-1, col-1,fPlayer_Grid);
								upClickedObj = getNodeByRowColumnIndex(row-1, col,fPlayer_Grid);
								upRight = getNodeByRowColumnIndex(row-1, col+1,fPlayer_Grid);
							}
							
							if( row+1<HEIGHT ) {
								downLeft = getNodeByRowColumnIndex(row+1, col-1,fPlayer_Grid);
								downClickedObj = getNodeByRowColumnIndex(row+1, col,fPlayer_Grid);
								downRight = getNodeByRowColumnIndex(row+1,col+1,fPlayer_Grid);
							}
							
							if( col+2<WIDTH) {
								rightMargin = getNodeByRowColumnIndex(row, col+2,fPlayer_Grid);
							}
							
							if( col-2<WIDTH) {
								leftMargin = getNodeByRowColumnIndex(row, col-2,fPlayer_Grid);
							}
							
							if(clickedObj instanceof VBox && left instanceof VBox && right instanceof VBox 
							  && (upLeft instanceof VBox || upLeft==null) && (upClickedObj instanceof VBox || upClickedObj == null)
							  && (upRight instanceof VBox || upRight == null) && (downLeft instanceof VBox || downLeft == null)
							  && (downClickedObj instanceof VBox || downClickedObj == null) && (downRight instanceof VBox || downRight == null)
							  && (rightMargin instanceof VBox || rightMargin == null) && (leftMargin instanceof VBox || leftMargin == null)) {
								
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
								Platform.runLater(new Runnable() {
									
									@Override
									public void run() {
										Notifications.create().darkStyle().title("There's already a ship!").text("Place the ship atleast at 1 square away.").position(Pos.CENTER).showWarning();
										//Notifications.create().darkStyle().title("Eroare").text("Hei i'm an info").showInformation();
										//Notifications.create().darkStyle().title("Eroare").text("Hei i'm a meesage").show();
									}
								});
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
							
							//Verify neighbour nodes
							Node rightUp = null;
							Node rightClickedObj = null;
							Node rightDown = null;
							Node leftUp = null;
							Node leftClickedObj = null;
							Node leftDown = null;
							Node marginUp = null;
							Node marginDown = null;
							
							if(col -1 >= 0) {
								leftUp = getNodeByRowColumnIndex(row-1, col-1, fPlayer_Grid);
								leftClickedObj = getNodeByRowColumnIndex(row, col-1, fPlayer_Grid);
								leftDown = getNodeByRowColumnIndex(row+1, col-1, fPlayer_Grid);
							}
							if(col +1 <WIDTH) {
								rightUp = getNodeByRowColumnIndex(row-1, col+1, fPlayer_Grid);
								rightClickedObj = getNodeByRowColumnIndex(row, col+1, fPlayer_Grid);
								rightDown = getNodeByRowColumnIndex(row+1, col+1, fPlayer_Grid);
							}
							if(row+2<HEIGHT) {
								marginDown = getNodeByRowColumnIndex(row+2, col, fPlayer_Grid);
							}
							if(row-2>=0) {
								marginUp = getNodeByRowColumnIndex(row-2, col, fPlayer_Grid);
							}
							
							if(clickedObj instanceof VBox && up instanceof VBox && down instanceof VBox
							  && (leftUp instanceof VBox || leftUp == null) && (leftClickedObj instanceof VBox || leftClickedObj == null)
							  && (leftDown instanceof VBox || leftDown == null) && (rightUp instanceof VBox || rightUp == null)
							  && (rightClickedObj instanceof VBox || rightClickedObj == null) && (rightDown instanceof VBox || rightDown == null)
							  && (marginDown instanceof VBox || marginDown == null) && (marginUp instanceof VBox || marginUp == null)) {
								
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
							
							//Verify neighbour nodes
							Node upLeft = null;
							Node upLeftLeft = null;
							Node downLeft = null;
							Node downLeftLeft = null;
							Node upClickedObj = null;
							Node downClickedObj = null;
							Node upRight = null;
							Node upRightRight = null;
							Node downRight = null;
							Node downRightRight = null;
							Node leftMargin = null;
							Node rightMargin = null;
							
							if( row-1>=0 ) {
								upLeft = getNodeByRowColumnIndex(row-1, col-1,fPlayer_Grid);
								upLeftLeft = getNodeByRowColumnIndex(row-1, col-2,fPlayer_Grid);
								upClickedObj = getNodeByRowColumnIndex(row-1, col,fPlayer_Grid);
								upRight = getNodeByRowColumnIndex(row-1, col+1,fPlayer_Grid);
								upRightRight = getNodeByRowColumnIndex(row-1, col+2,fPlayer_Grid);
							}
							
							if( row+1<HEIGHT ) {
								downLeft = getNodeByRowColumnIndex(row+1, col-1,fPlayer_Grid);
								downLeftLeft = getNodeByRowColumnIndex(row+1, col-2,fPlayer_Grid);
								downClickedObj = getNodeByRowColumnIndex(row+1, col,fPlayer_Grid);
								downRight = getNodeByRowColumnIndex(row+1,col+1,fPlayer_Grid);
								downRightRight = getNodeByRowColumnIndex(row+1,col+2,fPlayer_Grid);
							}
							
							if( col+3<WIDTH) {
								rightMargin = getNodeByRowColumnIndex(row, col+3,fPlayer_Grid);
							}
							
							if( col-3<WIDTH) {
								leftMargin = getNodeByRowColumnIndex(row, col-3,fPlayer_Grid);
							}
							
							if(clickedObj instanceof VBox && left instanceof VBox && right instanceof VBox && leftleft instanceof VBox 
									&& rightright instanceof VBox && (upLeft instanceof VBox || upLeft==null) 
									&& (upClickedObj instanceof VBox || upClickedObj == null) && (upRight instanceof VBox || upRight == null) 
									&& (downLeft instanceof VBox || downLeft == null)&& (downClickedObj instanceof VBox || downClickedObj == null)
									&& (downRight instanceof VBox || downRight == null) && (rightMargin instanceof VBox || rightMargin == null) 
									&& (leftMargin instanceof VBox || leftMargin == null) && (downLeftLeft instanceof VBox || downLeftLeft == null)
									&& (downRightRight instanceof VBox || downRightRight == null) && (upLeftLeft instanceof VBox || upLeftLeft == null)
									&& (upRightRight instanceof VBox || upRightRight == null)) {
								
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
							
							//Verify neighbour nodes
							Node rightUp = null;
							Node rightRightUp = null;
							Node rightClickedObj = null;
							Node rightDown = null;
							Node rightRightDown = null;
							Node leftUp = null;
							Node leftLeftUp = null;
							Node leftClickedObj = null;
							Node leftDown = null;
							Node leftLeftDown = null;
							Node marginUp = null;
							Node marginDown = null;
							
							if(col -1 >= 0) {
								leftLeftUp = getNodeByRowColumnIndex(row-2, col-1, fPlayer_Grid);
								leftUp = getNodeByRowColumnIndex(row-1, col-1, fPlayer_Grid);
								leftClickedObj = getNodeByRowColumnIndex(row, col-1, fPlayer_Grid);
								leftDown = getNodeByRowColumnIndex(row+1, col-1, fPlayer_Grid);
								leftLeftDown = getNodeByRowColumnIndex(row+2, col-1, fPlayer_Grid);
							}
							if(col +1 <WIDTH) {
								rightRightUp = getNodeByRowColumnIndex(row-2, col+1, fPlayer_Grid);
								rightUp = getNodeByRowColumnIndex(row-1, col+1, fPlayer_Grid);
								rightClickedObj = getNodeByRowColumnIndex(row, col+1, fPlayer_Grid);
								rightDown = getNodeByRowColumnIndex(row+1, col+1, fPlayer_Grid);
								rightRightDown = getNodeByRowColumnIndex(row+2, col+1, fPlayer_Grid);
							}
							if(row+3<HEIGHT) {
								marginDown = getNodeByRowColumnIndex(row+3, col, fPlayer_Grid);
							}
							if(row-3>=0) {
								marginUp = getNodeByRowColumnIndex(row-3, col, fPlayer_Grid);
							}
							
							if(clickedObj instanceof VBox && left instanceof VBox && right instanceof VBox 
							  && leftleft instanceof VBox && rightright instanceof VBox
							  && (leftUp instanceof VBox || leftUp == null) && (leftClickedObj instanceof VBox || leftClickedObj == null)
							  && (leftDown instanceof VBox || leftDown == null) && (rightUp instanceof VBox || rightUp == null)
							  && (rightClickedObj instanceof VBox || rightClickedObj == null) && (rightDown instanceof VBox || rightDown == null)
							  && (marginDown instanceof VBox || marginDown == null) && (marginUp instanceof VBox || marginUp == null)
							  && (rightRightUp instanceof VBox || rightRightUp == null) && (leftLeftUp instanceof VBox || leftLeftUp == null)
							  && (leftLeftDown instanceof VBox || leftLeftDown == null) && (rightRightDown instanceof VBox || rightRightDown == null)) {
								
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
		AI.placeRandomShips();
		
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
	        					AI.placeRandomShips();
	        					resetAI = false;
	        					
	        				}
	        				
	        				//This if here is only to prevent the game form continuing if the AI or player (later implemented) wins!
	        				if(AI.checkShipList()) {
	        					System.out.println("Game over! Computer has won the game! Restart to play again.");
	        					return;
	        				}
	        			
	        				//PLAYER TURN ---->
	        				
	        				//Current HOVERED VBox
	    					Object hoveredObj = event.getTarget();
	    					
	    					//Coordinates of the current HOVERED VBox
	    					int row = GridPane.getRowIndex((Node)hoveredObj);
    						int col = GridPane.getColumnIndex((Node)hoveredObj);
	    					
    						Point playerHit = new Point();
    						playerHit.setX(col);
    						playerHit.setY(row);
    						
	    					//Register hit in AI Matrix if position is not already hit, else hit again elsewhere before going further!
	    					if(!AI.playerCheckIfAlreadyHit(playerHit)) {
	    						System.out.println("Already hit! Hit an other position!");
	    						return;
	    					}
	    					
	    					if(!AI.playerHit(playerHit)) {
	    						//If you hit a VBox make it red
		    					//if(hoveredObj instanceof VBox) {

		    						AI_Grid.getChildren().remove(hoveredObj);
		    						
		    						Pane p = new Pane();
		    						p.setStyle("-fx-background-color: #FF0000");
		    						p.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
		    						AI_Grid.add(p, col, row);
		    					//}
	    					}
	    					else {
	    						
	    						//!Make child on this position in the grid a black pane!
	    						AI_Grid.getChildren().remove(hoveredObj);
	    						Pane p = new Pane();
	    						p.setStyle("-fx-background-color: #000000");
	    						p.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
	    						AI_Grid.add(p, col, row);
	    					}
	        				
	    					//We will verify if the player destroyed all AI ships before the AI turn!
	    					
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
	private void DisplayAIBoard() {
		
		int[][] AIBoard = AI.GetAIBoard();
		
		for(int i = 0; i<20; i++) {
			for(int j = 0; j<20; j++) {
				System.out.print("[ "+AIBoard[i][j]+" ]");
			}
			System.out.println("");
		}
		
	}
	
	@FXML
	public void DisplayShips() {
		for(Battleship i : AI.AIShipList) {
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
		//! Start Game always last function, see function comments!
		StartGame();

		
	}
	
}