/**********************************************************************
 *                      Generic_AskForInteger                         *
 *                             08/05/26                               *
 *                               18:00                                *
 *********************************************************************/
package dialogs;

import bootstrapping.Boot_Controller;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.WindowEvent;
import javafx.scene.text.Text;
import splat.Data_Manager;
import utilityClasses.*;

public class Generic_AskForInteger  extends Splat_Dialog{
    // POJOs
    boolean checked;
  
    int nBoxesChecked, numberOfReps;
        
    double paneWidth, paneHeight;
    
    String strDirections;
    
    // Make empty if no-print
    //String waldoFile = "Generic_AskForNumber";
    String waldoFile = "";
    
    // My Classes
    Boot_Controller boot_Controller;
    
    // FX POJOs
    Data_Manager dm;
    HBox hboxNReps, hBoxButtons;
    Pane root;
    Text txtTitle, txtDirections, txtNReps;
    TextField tfNReps;
    AnchorPane daAnchorPane;
    Scene scene;
    
    public Generic_AskForInteger(Boot_Controller boot_Controller,   //  Calling class
                                String strTitle, double titleIndent,
                                String strDirections,
                                double width, double height) {
        this.boot_Controller = boot_Controller;
        dm = boot_Controller.getTheDataManager();
        dm.whereIsWaldo(55, waldoFile, " *** ChooseStats_Dialog, Constructing"); 
        root = new Pane();
        root.setPrefSize(width, height);
        strReturnStatus = "OK";
        txtTitle = new Text(strTitle);
        txtTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 24));
        
        this.strDirections = strDirections;
        txtDirections = new Text(strDirections);
        txtDirections.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));

        txtNReps = new Text("NReps: ");
        txtNReps.setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));

        tfNReps = new TextField();
        tfNReps.setPrefColumnCount(5);
        tfNReps.setOnAction(e -> {
            dm.whereIsWaldo(72, waldoFile, " ... Generic_AskForInteger, Constructing"); 
            numberOfReps = Integer.parseInt(tfNReps.getText());
            boot_Controller.setNReps(numberOfReps);
        });
        
        tfNReps.textProperty().addListener(this::changeNReps);
        
        hboxNReps = new HBox();
        hboxNReps.getChildren().addAll(txtNReps, tfNReps);
        
        btnOK = new Button("OK");
        
        btnOK.setOnAction(e -> {  
            System.out.println(" ... 85 Generic_AskForInteger, btnOK Clicked");
            nBoxesChecked = 0;
            boot_Controller.setReturnStatus("OK");
            boot_Controller.setExitStatus("OK");
            strReturnStatus = "OK";
        if (numberOfReps == 0 ) {
            dm.whereIsWaldo(90, waldoFile, " ... Generic_AskForInteger");
            MyAlerts.showZeroReplicationsAlert();
            strReturnStatus = "Cancel";
            boot_Controller.setReturnStatus("Cancel");
            boot_Controller.setExitStatus("Cancel");
        }
        hide();
        });
        
        btnCancel = new Button("Cancel");
        btnCancel.setOnAction(e -> {  
            System.out.println(" ... 102 Generic_AskForInteger, btnCancel Clicked");
            boot_Controller.setReturnStatus("Cancel");
            boot_Controller.setExitStatus("Cancel");
            strReturnStatus = "Cancel";
            hide();
        });
        

        setOnCloseRequest((WindowEvent we) -> {
            System.out.println(" ... 111 Generic_AskForInteger, setOnCloseRequest((WindowEvent we)");
            btnCancel.fire();
        });
        
        hBoxButtons = new HBox();
        hBoxButtons.getChildren().addAll(btnOK, btnCancel);
        hBoxButtons.setSpacing(20.0);
        paneWidth = width;
        paneHeight = height;
        
        daAnchorPane = new AnchorPane(txtTitle, txtDirections, hboxNReps, hBoxButtons);

        AnchorPane.setTopAnchor(txtTitle, 0.05 * paneHeight);
        AnchorPane.setLeftAnchor(txtTitle, titleIndent * paneWidth);
        AnchorPane.setRightAnchor(txtTitle, 0.45 * paneWidth);
        AnchorPane.setBottomAnchor(txtTitle, 0.90 * paneHeight);
        
        AnchorPane.setTopAnchor(txtDirections, 0.20 * paneHeight);
        AnchorPane.setLeftAnchor(txtDirections, 0.45 * paneWidth);
        AnchorPane.setRightAnchor(txtDirections, 0.40 * paneWidth);
        AnchorPane.setBottomAnchor(txtDirections, 0.75 * paneHeight);

        AnchorPane.setTopAnchor(hboxNReps, 0.75 * paneHeight);
        AnchorPane.setLeftAnchor(hboxNReps, 0.85 * paneWidth);
        AnchorPane.setRightAnchor(hboxNReps, 0.10 * paneWidth);
        AnchorPane.setBottomAnchor(hboxNReps, 0.30 * paneHeight);

        AnchorPane.setTopAnchor(hBoxButtons, 1.05 * paneHeight);
        AnchorPane.setLeftAnchor(hBoxButtons, 0.90 * paneWidth);
        AnchorPane.setRightAnchor(hBoxButtons, 0.40 * paneWidth);
        AnchorPane.setBottomAnchor(hBoxButtons, 0.20 * paneHeight);
        
        root.getChildren().add(daAnchorPane);
        
        scene = new Scene(root, 2.25 * width, 1.25 * height);
        this.setScene(scene);
    }
    
    public void changeNReps(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        //dm.whereIsWaldo(152, waldoFile, " --- Generic_AskForInteger, oldValue = " + oldValue);
        //dm.whereIsWaldo(153, waldoFile, " ... Generic_AskForInteger, newValue = " + newValue);
        tfNReps.setText(newValue);
        if (!newValue.isEmpty()){
            if (DataUtilities.strIsAPosInt(newValue)) {
                numberOfReps = Integer.parseInt(newValue);
            } else {
                MyAlerts.showGenericBadNumberAlert("positive integer");
            }
        }
    }
    
    public String getStrReturnStatus() { return strReturnStatus; }
    public int getNReps() { return numberOfReps; }
}


