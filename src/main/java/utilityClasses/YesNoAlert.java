/**************************************************
 *                   YesNoAlert                   *
 *                    02/15/25                    *
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class YesNoAlert extends Stage {
    
    // POJOs

    double alertTitleBoxWidth, alertTitleBoxHeight,
           alertHeaderBoxWidth, alertHeaderBoxHeight, 
           alertContextBoxWidth;
    
    String theYes, theNo, theYesOrNo;
    
    // FX classes
    Button btnYes, btnNo;
    Font alertTitleFont, alertHeaderFont, alertContextFont;;
    HBox buttonBox, textAndIcon;
    Image image;
    ImageView imageView;
    Scene alertScene;
    Region[] spacer;
    Stage alertStage;
    Text txtAlertTitle, txtAlertHeader, txtAlertContext;
    VBox root;
     
    public YesNoAlert(String theYes, String theNo,
                      String strAlertTitle,
                      String strAlertHeader,
                      String strAlertContext,   
                      String imagePath,
                      double alertBoxWidth,
                      double alertBoxHeight,
                      double imageOffSetX,
                      double imageOffSetY,
                      int horizImageSpace,
                      int fitWidth) {
        this.theYes = theYes;
        this.theNo = theNo;
        
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

        btnYes  = new Button(theYes);
        btnYes.setStyle("-fx-text-fill: red;");
        btnYes.setOnAction(e -> { 
            theYesOrNo = "Yes";
            alertStage.hide();
        });
        
        btnYes.setPadding(new Insets(10, 20, 10, 20));
        btnYes.setFont(alertContextFont);
        btnNo  = new Button(theNo);
        btnNo.setStyle("-fx-text-fill: red;");
        
        btnNo.setOnAction(e -> { 
            theYesOrNo = "No";
            alertStage.hide();
        });
        btnNo.setPadding(new Insets(10, 20, 10, 20));
        btnNo.setFont(alertContextFont);
        
        setOnCloseRequest((WindowEvent we) -> {
            theYesOrNo = "Cancel";
            alertStage.hide();
        });        
        
        spacer = new Region[5];
        for (int ithSpacer = 0; ithSpacer < 5; ithSpacer++) {
            spacer[ithSpacer] = new Region(); 
            HBox.setHgrow(spacer[ithSpacer], Priority.ALWAYS);
        }
        
        buttonBox = new HBox();

        buttonBox.getChildren().addAll(spacer[0], spacer[1], btnYes, 
                                       spacer[2], btnNo, spacer[3], spacer[4]);

        textAndIcon.getChildren().addAll(txtAlertContext, imageView);
        root.getChildren().addAll(txtAlertTitle, txtAlertHeader, 
                                  textAndIcon, buttonBox);

        root.setSpacing(20);

        //                             w,   h
        alertScene = new Scene(root, alertBoxWidth + horizImageSpace, alertBoxHeight);
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
    
    public String getYesOrNo() { return theYesOrNo; }
}
