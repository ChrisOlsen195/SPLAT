/**************************************************
 *         DataStructChoice_Dialog_t_Indep_Means  *
 *                    06/15/24                    *
 *                      12:00                     *
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
import the_t_procedures.Indep_t_Controller;

public class DataStructChoice_Dialog_t_Indep_Means extends Stage {
    
    // POJOs
    double alertTitleBoxWidth, alertTitleBoxHeight, alertBoxWidth,
           alertBoxHeight;

    // FX classes
    Button btn_Stacked, btn_Separate, btn_Summary;
    Font alertTitleFont, alertContextFont;;
    HBox buttonBox;
    Region spacer1, spacer2, spacer3, spacer4;
    Scene alertScene;
    Stage alertStage;
    Text txtAlertTitle, txtAlertContext;
    VBox root;
    
    Indep_t_Controller indep_t_Controller;
     
    public DataStructChoice_Dialog_t_Indep_Means(Indep_t_Controller indep_t_Controller) {
        //System.out.println("43 DataStructChoice_Dialog_t_Indep_Means, constructing");
        this.indep_t_Controller = indep_t_Controller;
        indep_t_Controller.setDataOrSummary("Bailed"); // The default
        finishTheJob();
    }
    
    private void finishTheJob() {
        //System.out.println("50 DataStructChoice_Dialog_t_Indep_Means, finishTheJob()");
        alertTitleFont = Font.font("Times New Roman", FontWeight.BOLD, 24);
        alertContextFont = Font.font("Times New Roman", FontWeight.BOLD, 18);

        alertTitleBoxWidth = 500;
        alertTitleBoxHeight = 25;
        alertBoxWidth = 500;
        alertBoxHeight = 25;
        
        String strAlertTitle = " Request for information about your data structure"; 
        
        txtAlertTitle = new Text(alertTitleBoxWidth, alertTitleBoxHeight, strAlertTitle);
        txtAlertTitle.setFont(alertTitleFont);
        txtAlertTitle.setFill(Color.RED);
        txtAlertTitle.setTextAlignment(TextAlignment.LEFT);

        String strAlertContext = "In order to maximize the flexibility of your data entry, "
                        + " SPLAT allows two possible strategies.  One strategy is similar "
                        + " to how data is entered in the TI-8x calculators.  Another is to"
                        + " enter group / treatment information in one column and the values "
                        + " in another column.  And finally, it is possible to enter previously"
                        + " calculated summary statistics.  Please indicate which strategy you"
                        + " have decided to use.  \n\nThank you in advance!\n\n";
        
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

        btn_Stacked  = new Button("Group Column");
        btn_Stacked.setStyle("-fx-text-fill: red;");
        btn_Stacked.setOnAction(e -> {
            indep_t_Controller.setDataOrSummary("Group & Data");
                alertStage.close();
        });
        btn_Stacked.setPadding(new Insets(5, 5, 5, 5));
        btn_Stacked.setFont(alertContextFont);
        
        btn_Separate  = new Button("TI8x-Like");
        btn_Separate.setStyle("-fx-text-fill: red;");
        btn_Separate.setOnAction(e -> {
            indep_t_Controller.setDataOrSummary("Data");
            alertStage.close();
        });
        btn_Separate.setPadding(new Insets(5, 5, 5, 5));
        btn_Separate.setFont(alertContextFont);
        
        btn_Summary  = new Button("Summary");
        btn_Summary.setStyle("-fx-text-fill: red;");
            btn_Summary.setOnAction(e -> {
            indep_t_Controller.setDataOrSummary("Summary");       
            alertStage.close();
        });
        btn_Summary .setPadding(new Insets(5, 5, 5, 5));
        btn_Summary .setFont(alertContextFont);
        
        buttonBox= new HBox();
        spacer1 = new Region();
        spacer2 = new Region();
        spacer3 = new Region();
        spacer4 = new Region();
        
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        HBox.setHgrow(spacer4, Priority.ALWAYS);
        
        buttonBox.getChildren().addAll(spacer1, btn_Stacked, spacer2, btn_Separate, spacer3, btn_Summary, spacer4);
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

