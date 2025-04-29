/************************************************************
 *                             App                          *
 *                          04/02/22                        *
 *                            15:00                         *
 ***********************************************************/
package com.mycf.splat_maven;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import splat.Data_Manager;
import splat.Splash_Screen;
import splat.MainMenu;

public class App extends Application {

    // POJOs
    int initialGridColumns = 6;
    int initialGridRows = 14;
    
    // My classes
    Data_Manager dm;
    
    // POJOs / FX
    
    @Override
    public void start(Stage primaryStage) {

        //                    initialGridCases, initialGridVars
        dm = new Data_Manager(initialGridRows, initialGridColumns);  
        
        dm.setDataAreClean(false);
        
        Splash_Screen introMessage = new Splash_Screen();
        introMessage.ShowHello(STYLESHEET_MODENA);
        Label fileLabel = new Label("File: ");
        MainMenu myMenus = new MainMenu(this, dm, fileLabel);
        BorderPane myGrid = new BorderPane();
        myGrid = dm.getMainPane();
        VBox mainPane = new VBox();
        mainPane.getChildren().addAll(myMenus, myGrid, fileLabel);
        Scene mainScene = new Scene(mainPane);
        String css = getClass().getClassLoader().getResource("DataManager.css").toExternalForm();
        mainScene.getStylesheets().add(css);
          
        primaryStage.setScene(mainScene);
  
        primaryStage.setTitle("SPLAT: StatisticsPackageForLearningAndTeaching");
        primaryStage.getIcons().add(new Image(getClass().getResource("/SplatJPG.jpg").toExternalForm())); 
        
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        mainScene.widthProperty().addListener((obs, oldVal, newVal) -> {

            dm.getMainPane().setPrefWidth((double)newVal);          
            int newMaxColumnCount = (int)((double)newVal / 100);
            dm.setMaxVisVars(newMaxColumnCount);
            dm.resizeColumnHeaderCellsArray(newMaxColumnCount);
            dm.resizeGrid(dm.getMaxVisCases(), dm.getMaxVisVars());
            dm.sendDataStructToGrid(0, 0);
        });

        mainScene.heightProperty().addListener((obs, oldVal, newVal) -> {

            dm.getMainPane().setPrefHeight((double)newVal - 32);
            
            int newMaxRowCount = (int)((double)newVal / 31);
            
            dm.resizeRowHeaderCellsArray(newMaxRowCount);
            dm.setMaxVisCases(newMaxRowCount);
            dm.resizeGrid(newMaxRowCount, dm.getMaxVisVars());
            dm.sendDataStructToGrid(0, 0);   
        });
    
    } // void start
        public static void main(String[] args) {
        launch(args);
    }

}