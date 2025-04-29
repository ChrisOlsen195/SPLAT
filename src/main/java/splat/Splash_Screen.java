// **************************************************
// *                 Splash_Screen                  *
// *                    02/27/25                    *
// *                      12:00                     *
// *************************************************/
package splat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Splash_Screen {
    
    public void ShowHello(String versText) {
        //System.out.println("24 Splash_Screen");
        Label title1 = new Label("SPLAT");
        title1.getStyleClass().add("dialogTitle");
        Label title2 = new Label("Statistical Package for Learning and Teaching");
        Label space1 = new Label("  ");
        Label add1 = new Label("Chris R Olsen");
        Label add2 = new Label("crolsen@fastmail.com");
        Button closeButton = new Button("Let's get this show on the road!");
        closeButton.setPadding(new Insets(10, 10, 10, 10));
        
        VBox txtPanel = new VBox(5);
        txtPanel.setAlignment(Pos.CENTER);
        txtPanel.setPadding(new Insets(15, 20, 10, 20));
        txtPanel.getStyleClass().add("txtPanel");
        txtPanel.getChildren().addAll(title1, title2, space1,
                add1, add2);
        VBox buttonPanel = new VBox();
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.getStyleClass().add("buttonPanel");
        buttonPanel.getChildren().add(closeButton);
        VBox mainPanel = new VBox();
        mainPanel.getChildren().addAll(txtPanel, buttonPanel);
        Scene introScene = new Scene(mainPanel);
        Stage introStage = new Stage();        
        introStage.setScene(introScene);
        
        closeButton.setOnAction((ActionEvent event) -> { 
            introStage.close();
        });
        
        introScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                introStage.close();
            }
        });
        
        introStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
            }
        });
        
        introStage.setTitle("Hello!");
        introStage.centerOnScreen();
        introStage.showAndWait();
        
    } // ShowHello

} // Splash_Screen
