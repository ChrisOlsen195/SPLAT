/**********************************************************************
 *                       ChooseOneStat_Dialog                         *
 *                             01/08/25                               *
 *                               15:00                                *
 *********************************************************************/
package bootstrapping;

import dialogs.Splat_Dialog;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.WindowEvent;
import javafx.scene.text.Text;
import splat.Data_Manager;
import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;

public class ChooseOneStat_Dialog  extends Splat_Dialog{
    // POJOs
    boolean checked, doAlert;
    protected Boolean[][] checkBoxSettings;
    
    int nGridRows, nGridCols, nBoxesChecked, index, sampleSize, nReplications;
        
    double paneWidth, paneHeight;
    double[][] initWidth, initHeight; 
    
    String strJustClickedOn, strDirections, returnStatus;
    String[] checkBoxDescr;
    String backSpace = String.valueOf('\b');
    
    // Make empty if no-print
    //String waldoFile = "ChooseStats_Dialog";
    String waldoFile = "";
    
    // MyClasses
    Data_Manager dm;
    OneStat_Controller boot_Controller;
    
    // FX POJOs
    Button btnOK, btnCancel;
    CheckBox[][] checkBoxes;
    GridPane gridPane;
    HBox hboxNReps, hBoxButtons;
    Pane root;
    Text txtTitle, txtDirections, txtNReps;
    TextField tfNReps;
    AnchorPane daAnchorPane;
    Scene scene;
    
    public ChooseOneStat_Dialog(OneStat_Controller boot_Controller) {
        this.boot_Controller = boot_Controller;
        dm = boot_Controller.getTheDataManager();
        doAlert = true;
        nReplications = 0;
        dm.whereIsWaldo(65, waldoFile, "\nChooseStats_Dialog, Constructing"); 
        root = new Pane();
        root.setPrefSize(1200, 500);
        returnStatus = "OK";
        txtTitle = new Text("Bootstrapping the statistics!");
        txtTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 24));
        
        strDirections = "                     Easy Peasy Directions:" + 
                        "\n\n Please indicate the number of repetitions and" +
                        "\n choose the statistic (singular) you wish to bootstrap.";
        txtDirections = new Text(strDirections);
        txtDirections.setFont(Font.font("Times New Roman", FontWeight.BOLD, 18));

        txtNReps = new Text("NReps: ");
        txtNReps.setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));

        tfNReps = new TextField();
        tfNReps.setPrefColumnCount(4);
        tfNReps.setOnAction(e -> {
            String strTxtNReps = tfNReps.getText();
            if (DataUtilities.strIsAPosInt(strTxtNReps)) {
                nReplications = Integer.parseInt(strTxtNReps);
                boot_Controller.setNReps(nReplications);
            }
        });
        
        tfNReps.textProperty().addListener(this::changeNReps);
        
        hboxNReps = new HBox();
        hboxNReps.getChildren().addAll(txtNReps, tfNReps);
        
        gridPane = new GridPane();
        
        btnOK = new Button("OK");

        btnOK.setOnAction(e -> {  
            nBoxesChecked = 0;
            boot_Controller.setReturnStatus("OK");
            for (int ithRow = 0; ithRow < nGridRows; ithRow++) {
                for (int jthCol = 0; jthCol < nGridCols; jthCol++) {
                    index = ithRow * nGridCols + jthCol;
                    checked = checkBoxes[ithRow][jthCol].selectedProperty().getValue();
                    boot_Controller.setACheckBoxValue(index, checked);
                    if (checked) {
                        nBoxesChecked++;
                    }
                }
            } 
            if (nBoxesChecked == 0) {
                returnStatus = "Cancel";
            }
            close();
        });
        
        btnCancel = new Button("Cancel");
        btnCancel.setOnAction(e -> {            
            returnStatus = "Cancel";
            close();
        });

        hBoxButtons = new HBox();
        hBoxButtons.getChildren().addAll(btnOK, btnCancel);
        hBoxButtons.setSpacing(20.0);
        paneWidth = 1000.;
        paneHeight = 450.;
        
        daAnchorPane = new AnchorPane(txtTitle, txtDirections, /*hboxSampleSize,*/ hboxNReps, gridPane, hBoxButtons);

        AnchorPane.setTopAnchor(txtTitle, 0.05 * paneHeight);
        AnchorPane.setLeftAnchor(txtTitle, 0.30 * paneWidth);
        AnchorPane.setRightAnchor(txtTitle, 0.45 * paneWidth);
        AnchorPane.setBottomAnchor(txtTitle, 0.90 * paneHeight);
        
        AnchorPane.setTopAnchor(txtDirections, 0.20 * paneHeight);
        AnchorPane.setLeftAnchor(txtDirections, 0.25 * paneWidth);
        AnchorPane.setRightAnchor(txtDirections, 0.40 * paneWidth);
        AnchorPane.setBottomAnchor(txtDirections, 0.75 * paneHeight);

        AnchorPane.setTopAnchor(hboxNReps, 0.45 * paneHeight);
        AnchorPane.setLeftAnchor(hboxNReps, 0.35 * paneWidth);
        AnchorPane.setRightAnchor(hboxNReps, 0.10 * paneWidth);
        AnchorPane.setBottomAnchor(hboxNReps, 0.40 * paneHeight);
        
        AnchorPane.setTopAnchor(gridPane, 0.55 * paneHeight);
        AnchorPane.setLeftAnchor(gridPane, 0.05 * paneWidth);
        AnchorPane.setRightAnchor(gridPane, 0.10 * paneWidth);
        AnchorPane.setBottomAnchor(gridPane, 0.35 * paneHeight);
        
        AnchorPane.setTopAnchor(hBoxButtons, 0.85 * paneHeight);
        AnchorPane.setLeftAnchor(hBoxButtons, 0.40 * paneWidth);
        AnchorPane.setRightAnchor(hBoxButtons, 0.40 * paneWidth);
        AnchorPane.setBottomAnchor(hBoxButtons, 0.20 * paneHeight);
        
        root.getChildren().add(daAnchorPane);
        
        nGridRows = 5;
        nGridCols = 3;
        
        checkBoxDescr = boot_Controller.getRepAndStatCheckBoxDescriptions();
        checkBoxSettings = new Boolean[nGridRows][nGridCols];
        
        initWidth = new double[nGridRows][nGridCols];
        initHeight = new double[nGridRows][nGridCols];
  
        checkBoxes = new CheckBox[nGridRows][nGridCols];  
        
        for (int ithRow = 0; ithRow < nGridRows; ithRow++) {
            for (int jthCol = 0; jthCol < nGridCols; jthCol++) {
                index = ithRow * nGridCols + jthCol;
                initWidth[ithRow][jthCol] = 750; 
                initHeight[ithRow][jthCol] = 350;
                checkBoxSettings[ithRow][jthCol] = false;
            }
        }
        
        for (int ithRow = 0; ithRow < nGridRows; ithRow++) {
            for (int jthCol = 0; jthCol < nGridCols; jthCol++) {
                index = ithRow * nGridCols + jthCol;
                checkBoxes[ithRow][jthCol] = new CheckBox();
                checkBoxes[ithRow][jthCol].setText(checkBoxDescr[index ]);
                checkBoxes[ithRow][jthCol].setId(checkBoxDescr[index ]);    
                checkBoxes[ithRow][jthCol].setPrefSize(175, 30);
                if (checkBoxes[ithRow][jthCol].isSelected()) {
                    checkBoxes[ithRow][jthCol].setTextFill(Color.GREEN);
                }
                else 
                {
                    checkBoxes[ithRow][jthCol].setTextFill(Color.RED);
                }
                
                checkBoxes[ithRow][jthCol].setOnAction(e->{
                    CheckBox tb = ((CheckBox) e.getTarget());            
                    String daID = tb.getId();
                    Boolean checkValue = tb.selectedProperty().getValue();
                    // Reset selected color
                    if (checkValue == true) {
                        tb.setTextFill(Color.GREEN);
                        strJustClickedOn = daID.trim();
                    }
                    else {
                        tb.setTextFill(Color.RED);
                    }
                });
            }
        }
                
        for (int ithRow = 0; ithRow < nGridRows; ithRow++) {
            for (int jthCol = 0; jthCol < nGridCols; jthCol++) {
                gridPane.add(checkBoxes[ithRow][jthCol], ithRow, jthCol);
            }
        }
        
        setOnCloseRequest((WindowEvent event) -> {            
            for (int ithRow = 0; ithRow < nGridRows; ithRow++) {
                for (int jthCol = 0; jthCol < nGridCols; jthCol++) {
                    index = ithRow * nGridCols + jthCol;
                    boot_Controller.setACheckBoxValue(index, checkBoxes[ithRow][jthCol].selectedProperty().getValue());
                }
            }           
        });
        
        scene = new Scene(root, 900, 500);
        this.setScene(scene);
    }
    
    public void changeNReps(ObservableValue<? extends String> prop,
        String oldValue,
        String newValue) {
        tfNReps.setText(newValue);
        String strTxtNReps = tfNReps.getText();
        if(!DataUtilities.strIsAPosInt(strTxtNReps)) {
            if (doAlert) {
                MyAlerts.showGenericBadNumberAlert(" a positive integer ");
            }
            // Eliminate double alert due to blanking tnReps
            doAlert = false;
            tfNReps.setText("");
            nReplications = 0;
            doAlert = true;
        } else { 
            nReplications = Integer.parseInt(newValue);
        }
    }
    
    public String getReturnStatus() { return returnStatus; }
    public int getSampleSize() { return sampleSize; }
    public int getNReps() { return nReplications; }
    public int getNStatsChecked() { return nBoxesChecked; }   
}

