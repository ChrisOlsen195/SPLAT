/**************************************************
 *         DataStructChoice_Dialog_t_SingleMean   *
 *                    12/13/25                    *
 *                      18:00                     *
 *************************************************/
package dialogs.t_and_z;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import the_t_procedures.Single_t_Controller;

public class DataStructChoice_Dialog_t_Single_Mean extends Stage {
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    double alertTitleBoxWidth, alertTitleBoxHeight, alertBoxWidth,
           alertBoxHeight;
    
    // FX classes
    Button btn_Data, btn_Summary;
    Font alertTitleFont, alertContextFont;;
    HBox buttonBox;
    Region spacer1, spacer2, spacer3;
    Scene alertScene;
    Stage alertStage;
    Text txtAlertTitle, txtAlertContext;
    VBox root;
    
    Single_t_Controller single_t_Controller;
     
    public DataStructChoice_Dialog_t_Single_Mean( Single_t_Controller single_t_Controller) {
        if (printTheStuff) {
            System.out.println("*** 47 DataStructChoice_Dialog_t_Single_Mean, Constructing");
        }
        this.single_t_Controller = single_t_Controller;
        single_t_Controller.setDataOrSummary("Bailed"); // The default
        finishTheJob();
    }
    
    private void finishTheJob() {
        if (printTheStuff) {
            System.out.println("*** 56 DataStructChoice_Dialog_t_Single_Mean, finishTheJob()");
        }
        alertTitleFont = Font.font("Times New Roman", FontWeight.BOLD, 24);
        alertContextFont = Font.font("Times New Roman", FontWeight.BOLD, 18);

        alertTitleBoxWidth = 500;
        alertTitleBoxHeight = 25;
        alertBoxWidth = 500;
        alertBoxHeight = 25;
        
        String strAlertTitle = " Request for information about your data"; 
        
        txtAlertTitle = new Text(alertTitleBoxWidth, alertTitleBoxHeight, strAlertTitle);
        txtAlertTitle.setFont(alertTitleFont);
        txtAlertTitle.setFill(Color.RED);
        txtAlertTitle.setTextAlignment(TextAlignment.LEFT);

        String strAlertContext = "SPLAT allows two possible strategies for performing inference"
                        + " about a population mean. You may have raw data, not yet processed,"
                        + " or you already have the necessary statistics arleady summarized."
                        + " Please indicate which is the case for this analysis.  \n\n";
        
        txtAlertContext = new Text(alertBoxWidth, alertBoxHeight, strAlertContext);
        txtAlertContext.setFont(alertContextFont);
        txtAlertContext.setFill(Color.BLACK);
        txtAlertContext.setLineSpacing(1.0);   //  Works
        txtAlertContext.setWrappingWidth(0.90 * alertBoxWidth);
        txtAlertContext.setTextAlignment(TextAlignment.LEFT);
        txtAlertContext.setTranslateX(20.);    //  Works
        txtAlertContext.setTranslateY(20.);    //  Works

        root = new VBox();
        root.setStyle("-fx-background-color: white");
        root.setAlignment(Pos.CENTER);

        btn_Data  = new Button("Raw data");
        btn_Data.setStyle("-fx-text-fill: red;");
        btn_Data.setOnAction(e -> {
            single_t_Controller.setDataOrSummary("Raw data");       
            alertStage.close();
        });
        btn_Data.setPadding(new Insets(5, 5, 5, 5));
        btn_Data.setFont(alertContextFont);
        
        btn_Summary  = new Button("Summary");
        btn_Summary.setStyle("-fx-text-fill: red;");
            btn_Summary.setOnAction(e -> {
            single_t_Controller.setDataOrSummary("Summary");       
            alertStage.close();
        });
            
        btn_Summary .setPadding(new Insets(5, 5, 5, 5));
        btn_Summary .setFont(alertContextFont);
        
        buttonBox= new HBox();
        spacer1 = new Region();
        spacer2 = new Region();
        spacer3 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        buttonBox.getChildren().addAll(spacer1, btn_Data, spacer2, btn_Summary, spacer3);
        root.getChildren().addAll(txtAlertTitle, txtAlertContext, buttonBox);

        root.setSpacing(20);

        //                             w,   h
        alertScene = new Scene(root, 600, 400); //  Works
        alertScene.setFill(Color.WHITE);
        alertStage = new Stage();
        alertStage.setScene(alertScene);
        alertStage.setTitle("");
        alertStage.showAndWait();
    }
}
