package com.BattleShips.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainBattleShips extends Application {
	Stage primaryStage;
	Pane pane;
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Battleships");
		
		showMainView();
		
	}
	
	void showMainView() throws IOException{
		pane = FXMLLoader.load(getClass().getResource("GUI.fxml"));
		Scene scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.setHeight(722);
		primaryStage.setWidth(1346);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	

	
	public static void main(String[] args) {
		launch(args);
	}
	

}
