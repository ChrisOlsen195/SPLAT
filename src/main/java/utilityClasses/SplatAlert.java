/**************************************************
 *                   SplatAlert                   *
 *                    11/01/23                    *
 *                      12:00                     *
 *************************************************/
package utilityClasses;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SplatAlert extends Stage {
    
    // POJOs
    double alertTitleBoxWidth, alertTitleBoxHeight,
           alertHeaderBoxWidth, alertHeaderBoxHeight, 
           alertContextBoxWidth;
    
    // FX classes
    Button btnGoAway;
    Font alertTitleFont, alertHeaderFont, alertContextFont;;
    HBox textAndIcon;
    Image image;
    ImageView imageView;
    Scene alertScene;
    Stage alertStage;
    Text txtAlertTitle, txtAlertHeader, txtAlertContext;
    VBox root;
     
    public SplatAlert(String strAlertTitle,
                      String strAlertHeader,
                      String strAlertContext,   
                      String imagePath,
                      double alertBoxWidth,
                      double alertBoxHeight,
                      double imageOffSetX,
                      double imageOffSetY,
                      int horizImageSpace,
                      int fitWidth) {
        

        
        alertTitleFont = Font.font("Times New Roman", FontWeight.BOLD, 18);
        alertHeaderFont = Font.font("Times New Roman", FontWeight.BOLD, 18);
        alertContextFont = Font.font("Times New Roman", FontWeight.BOLD, 18);

        alertTitleBoxWidth = alertContextBoxWidth;
        alertHeaderBoxWidth = alertContextBoxWidth;
        alertTitleBoxHeight = 25;
        alertHeaderBoxHeight = 25;

        txtAlertTitle = new Text(alertTitleBoxWidth, alertTitleBoxHeight, strAlertTitle);
        txtAlertTitle.setFont(alertTitleFont);
        txtAlertTitle.setFill(Color.RED);
        txtAlertTitle.setTextAlignment(TextAlignment.LEFT);

        txtAlertHeader = new Text(alertHeaderBoxWidth, alertHeaderBoxHeight, strAlertHeader);
        txtAlertHeader.setFont(alertHeaderFont);
        txtAlertHeader.setFill(Color.RED);
        txtAlertHeader.setTextAlignment(TextAlignment.LEFT);

        txtAlertContext = new Text(alertBoxWidth, alertBoxHeight, strAlertContext);
        txtAlertContext.setFont(alertContextFont);
        txtAlertContext.setFill(Color.BLACK);
        txtAlertContext.setLineSpacing(1.0);   //  Works
        txtAlertContext.setWrappingWidth(0.90 * alertBoxWidth);
        txtAlertContext.setTextAlignment(TextAlignment.LEFT);
        txtAlertContext.setTranslateX(20.);    //  Works
        txtAlertContext.setTranslateY(20.);    //  Works

        textAndIcon = new HBox();

        root = new VBox();
        root.setStyle("-fx-background-color: white");
        root.setAlignment(Pos.CENTER);
        image = new Image(imagePath);
        imageView = new ImageView(image);
        imageView.setFitWidth(fitWidth);
        imageView.setPreserveRatio(true);
        imageView.setTranslateX(imageOffSetX);    //  Works
        imageView.setTranslateY(imageOffSetY);    //  Works

        btnGoAway  = new Button("Oooohhhh, SPLAT, you are SO cool, and SO helpful. \n                  Click to agree and continue.");
        btnGoAway.setStyle("-fx-text-fill: red;");
        btnGoAway.setOnAction(e -> alertStage.close());
        btnGoAway.setPadding(new Insets(20, 20, 20, 20));
        btnGoAway.setFont(alertContextFont);

        textAndIcon.getChildren().addAll(txtAlertContext, imageView);
        root.getChildren().addAll(txtAlertTitle, txtAlertHeader, textAndIcon, btnGoAway);

        root.setSpacing(20);

        //                             w,   h
        alertScene = new Scene(root, alertBoxWidth + horizImageSpace, alertBoxHeight); //  Works
        alertScene.setFill(Color.WHITE);
        alertStage = new Stage();
        
        alertStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) { alertStage.close(); }
        });
        
        alertStage.setScene(alertScene);
        alertStage.setTitle("");
        alertStage.showAndWait();
    }
}