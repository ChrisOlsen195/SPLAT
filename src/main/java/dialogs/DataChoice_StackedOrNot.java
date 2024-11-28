/**************************************************
 *              DataChoice_StackedOrNot           *
 *                    11/27/24                    *
 *                      12:00                     *
 *************************************************/
package dialogs;

import anova1.categorical.ANOVA1_Cat_Controller;
import anova1.quantitative.ANOVA1_Quant_Controller;
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
import proceduresManyUnivariate.MultUni_Controller;
import proceduresTwoUnivariate.Explore_2Ind_Controller;
import utilityClasses.MyAlerts;

public class DataChoice_StackedOrNot extends Stage {
    
    // POJOs
    double alertTitleBoxWidth, alertTitleBoxHeight, alertBoxWidth,
           alertBoxHeight;
    
    String strCallingProc;
    
    // FX classes
    Button btnStacked, btnSeparate;
    Font fntAlertTitle, fntAlertContext;;
    HBox hBoxButtons;
    Region spacer1, spacer2, spacer3;
    Scene sceneAlert;
    Stage sceneStage;
    Text txtTitle_Alert, txtContext_Alert;
    VBox root;
    
    ANOVA1_Cat_Controller anova1_Cat_Controller;
    ANOVA1_Quant_Controller anova1_Quant_Controller;
    Explore_2Ind_Controller explore_2Ind_Controller;
    MultUni_Controller multUni_Controller;
     
    public DataChoice_StackedOrNot(ANOVA1_Cat_Controller anova1_Cat_Controller) {
        this.anova1_Cat_Controller = anova1_Cat_Controller;
        anova1_Cat_Controller.setStackedOrSeparate("Bailed"); // The default
        strCallingProc = "ANOVA1_Cat";
        finishTheJob();
    }
    
    public DataChoice_StackedOrNot(ANOVA1_Quant_Controller anova1_Quant_Controller) {
        this.anova1_Quant_Controller = anova1_Quant_Controller;
        anova1_Quant_Controller.setStackedOrSeparate("Bailed"); // The default
        strCallingProc = "ANOVA1_Quant";
        finishTheJob();
    }
    
    public DataChoice_StackedOrNot(Explore_2Ind_Controller explore_2Ind_Controller) {
        this.explore_2Ind_Controller = explore_2Ind_Controller;
        explore_2Ind_Controller.setStackedOrSeparate("Bailed"); // The default
        strCallingProc = "2Ind";
        finishTheJob();
    }
    
    public DataChoice_StackedOrNot(MultUni_Controller multUni_Controller) {
        this.multUni_Controller = multUni_Controller;
        multUni_Controller.setStackedOrSeparate("Bailed"); // The default
        strCallingProc = ">2Ind";
        finishTheJob();
    }
    
    private void finishTheJob() {
        fntAlertTitle = Font.font("Times New Roman", FontWeight.BOLD, 24);
        fntAlertContext = Font.font("Times New Roman", FontWeight.BOLD, 18);

        alertTitleBoxWidth = 500;
        alertTitleBoxHeight = 25;
        alertBoxWidth = 500;
        alertBoxHeight = 25;
        
        String strAlertTitle = " Request for information about your data structure"; 
        
        txtTitle_Alert = new Text(alertTitleBoxWidth, alertTitleBoxHeight, strAlertTitle);
        txtTitle_Alert.setFont(fntAlertTitle);
        txtTitle_Alert.setFill(Color.RED);
        txtTitle_Alert.setTextAlignment(TextAlignment.LEFT);

        String strAlertContext = "In order to maximize the flexibility of your data entry, "
                        + " SPLAT allows two possible strategies.  One strategy is similar "
                        + " to how data is entered in the TI-8x calculators.  Another is to"
                        + " enter group / treatment information in one column and the values "
                        + " in another column.  Please indicate which strategy you have used"
                        + " for the data in this file.  \n\nThank you in advance!\n\n";
        
        txtContext_Alert = new Text(alertBoxWidth, alertBoxHeight, strAlertContext);
        txtContext_Alert.setFont(fntAlertContext);
        txtContext_Alert.setFill(Color.BLACK);
        txtContext_Alert.setLineSpacing(1.0);   //  Works
        txtContext_Alert.setWrappingWidth(0.90 * alertBoxWidth);
        txtContext_Alert.setTextAlignment(TextAlignment.LEFT);
        txtContext_Alert.setTranslateX(20.);    //  Works
        txtContext_Alert.setTranslateY(20.);    //  Works

        root = new VBox();
        root.setStyle("-fx-background-color: white");
        root.setAlignment(Pos.CENTER);

        btnStacked  = new Button("Group Column");
        btnStacked.setStyle("-fx-text-fill: red;");
        btnStacked.setOnAction(e -> {
            //System.out.println("119 DataChoice_StackedOrNot, strCallingProc = " + strCallingProc);
            switch (strCallingProc) {
                case "ANOVA1_Cat":
                    anova1_Cat_Controller.setStackedOrSeparate("Group & Data");
                    break;

                case "ANOVA1_Quant":
                    anova1_Quant_Controller.setStackedOrSeparate("Group & Data");
                    break;

                case "2Ind":
                    explore_2Ind_Controller.setStackedOrSeparate("Group & Data");
                    break;

                case ">2Ind":
                    multUni_Controller.setStackedOrSeparate("Group & Data");
                    break;

                default:
                    String switchFailure = "Switch failure: DataChoice_StackedOrNot 138 " + strCallingProc;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
                    sceneStage.close();
            }
            sceneStage.close();
        });
        
        btnStacked.setPadding(new Insets(5, 5, 5, 5));
        btnStacked.setFont(fntAlertContext);
        
        btnSeparate  = new Button("TI8x-Like");
        btnSeparate .setStyle("-fx-text-fill: red;");
            btnSeparate.setOnAction(e -> {
            switch (strCallingProc) {
                case "ANOVA1_Cat":
                    anova1_Cat_Controller.setStackedOrSeparate("TI8x-Like");
                    break;

                case "ANOVA1_Quant":
                    anova1_Quant_Controller.setStackedOrSeparate("TI8x-Like");
                    break;

                case "2Ind":
                    explore_2Ind_Controller.setStackedOrSeparate("TI8x-Like");
                    break;

                case ">2Ind":
                    multUni_Controller.setStackedOrSeparate("TI8x-Like");
                    break;

                default:
                    String switchFailure = "Switch failure: DataChoice_StackedOrNot 169 " + strCallingProc;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
                    sceneStage.close();
            }
            sceneStage.close();
        });
        btnSeparate .setPadding(new Insets(5, 5, 5, 5));
        btnSeparate .setFont(fntAlertContext);
        
        hBoxButtons= new HBox();
        spacer1 = new Region(); spacer2 = new Region(); spacer3 = new Region();
        
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        
        hBoxButtons.getChildren().addAll(spacer1, btnStacked, spacer2, btnSeparate, spacer3);
        root.getChildren().addAll(txtTitle_Alert, txtContext_Alert, hBoxButtons);

        root.setSpacing(20);

        //                             w,   h
        sceneAlert = new Scene(root, 600, 400); //  Works
        sceneAlert.setFill(Color.WHITE);
        sceneStage = new Stage();
        sceneStage.setScene(sceneAlert);
        sceneStage.setTitle("");
        sceneStage.showAndWait();
    }
}
