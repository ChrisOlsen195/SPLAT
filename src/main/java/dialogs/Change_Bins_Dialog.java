/************************************************************
 *                     Change_Bins_Dialog                   *
 *                          05/26/24                        *
 *                            15:00                         *
 ***********************************************************/
package dialogs;

import utilityClasses.DataUtilities;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import smarttextfield.*;
import javafx.stage.WindowEvent;
import utilityClasses.MyAlerts;

public class Change_Bins_Dialog extends Stage { 
    
    // POJOs
    boolean boolValuesLeftBlank, boolLeftBinGood, boolRightBinGood, 
            boolAllFieldsGood;
    
    double leftBin, rightBin, minDataRange, maxDataRange;
    
    String strLeftBin, strRightBin, strTitle, strReturnStatus, strTextLabels;
    final String toBlank = "";
    
    // My classes
    DoublyLinkedSTF al_STF;
    SmartTextFieldsController stf_Controller;

    // JavaFX POJOs
    Button btnOK, btnCancel, btnReset;
    Canvas canvas_ithBin;
    GraphicsContext gc;
    Label lblTitle;
    HBox hBoxTextFieldRow, hBoxButtonPanel;
    VBox root, vBoxLabelsAndSTFs;
    Scene scene; 
    Text txtTitle, txtLabels;

    public Change_Bins_Dialog(double minDataRange, double maxDataRange) {
        this.minDataRange = minDataRange;
        this.maxDataRange = maxDataRange;
        strReturnStatus = "OK";
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        stf_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set                          *
        stf_Controller.setSize(2);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();
        
        lblTitle = new Label("Set bins for histogram / dotplot / etc");
        lblTitle.setPadding(new Insets(10, 10, 10, 10));        
        lblTitle.getStyleClass().add("dialogTitle");
        
        lblTitle.setPadding(new Insets(10, 10, 10, 10));
        makeDiagramPanel();
        makeButtonPanel();
        makeLabelsAndSTFsPanel();
   
        root.getChildren().addAll(txtTitle, 
                                  canvas_ithBin,
                                  vBoxLabelsAndSTFs,
                                  hBoxButtonPanel);        
        
        // width, height
        scene = new Scene (root, 300, 450);
        setTitle("Bin format");
        
        setOnCloseRequest((WindowEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });
        setScene(scene);
    }  

    private void makeDiagramPanel() {  
        canvas_ithBin = new Canvas(400, 250);
        gc = canvas_ithBin.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        strTitle = " Determine the 'look and feel' by defining "
                   + "\nthe class limits of an ith bin. It is much"
                   + "\npreferred that the ith bin so defined is"
                   + "\nwithin the range of your data.";
        txtTitle = new Text(50, 150, strTitle);
        txtTitle.setFont(Font.font("Times New Roman", FontWeight.BLACK, 14));
        // x1, y1, x2, y2
        gc.strokeLine(75, 235, 300, 235);
        // x, y, width, height
        gc.setFill(Color.GREEN);
        gc.fillRect(125, 50, 125, 185);
    }
 
    private void makeLabelsAndSTFsPanel() {
        vBoxLabelsAndSTFs = new VBox();
        strTextLabels = "                           Left                   Right";
        txtLabels = new Text(200, 50, strTextLabels);    
        hBoxTextFieldRow = new HBox();
        hBoxTextFieldRow.getChildren().add(txtLabels);
        al_STF.get(0).getTextField().setPrefColumnCount(12);
        al_STF.get(0).getTextField().setMaxWidth(65);
        al_STF.get(0).getTextField().setPadding(new Insets(5, 10, 5, 5));
        al_STF.get(0).getTextField().setText(toBlank);
        al_STF.get(0).getTextField().setId("LeftEnd");
        al_STF.get(0).setSmartTextField_MB_REAL(true);
        
        al_STF.get(0).getTextField().setOnAction(e -> {            
            boolLeftBinGood = DataUtilities.strIsADouble(al_STF.get(0).getText());            
            if (boolLeftBinGood) {
                strLeftBin = al_STF.get(0).getText();
                leftBin = Double.parseDouble(strLeftBin);
                al_STF.get(0).setText(String.valueOf(leftBin));
            }
        });
       
        al_STF.get(1).getTextField().setPrefColumnCount(12);
        al_STF.get(1).getTextField().setMaxWidth(65);
        al_STF.get(1).getTextField().setText(toBlank); 
        al_STF.get(1).getTextField().setId("RightEnd");
        al_STF.get(1).setSmartTextField_MB_REAL(true);
        
        al_STF.get(1).getTextField().setOnAction(e -> {
            boolRightBinGood = DataUtilities.strIsADouble(al_STF.get(1).getText());            
            if (boolRightBinGood) {
                rightBin = Double.parseDouble(al_STF.get(1).getText());
                strRightBin = String.valueOf(rightBin);
                al_STF.get(1).setText(strRightBin);
                boolRightBinGood = true;
            }
        });
        hBoxTextFieldRow.setAlignment(Pos.CENTER);
        hBoxTextFieldRow.getChildren()
                             .addAll(al_STF.get(0).getTextField(),
                                     al_STF.get(1).getTextField());
        hBoxTextFieldRow.setSpacing(25);          
        al_STF.get(0).getTextField().requestFocus();           
        vBoxLabelsAndSTFs.getChildren().addAll(txtLabels, hBoxTextFieldRow, hBoxButtonPanel);
    }
    
    private void makeButtonPanel() { 
        hBoxButtonPanel = new HBox(10);
        hBoxButtonPanel.setAlignment(Pos.CENTER);
        hBoxButtonPanel.setPadding(new Insets(5, 5, 5, 5));
        btnOK = new Button("OK");
        btnCancel = new Button("Cancel");
        btnReset = new Button("Reset");
        btnOK.setOnAction((ActionEvent event) -> { 
            
        doMissingAndOrWrong();
        
        if (boolValuesLeftBlank) {
            MyAlerts.showMustBeNonBlankAlert();  
        }
        else 
        if (!boolAllFieldsGood) {
            MyAlerts.showNotAllFieldsGoodAlert();
        }
        else 
        if (rightBin <= leftBin) {
            MyAlerts.showLeftRightOrderAlert();
        }
        else 
        if (rightBin < minDataRange || maxDataRange < leftBin) {
            MyAlerts.showbadBinRangeAlert();
        }

        else{
            strReturnStatus = "OK";
            close();
        }  
    });
        
        setOnCloseRequest((WindowEvent t) -> {
            strReturnStatus = "Cancel";
            close();
        });
        
        btnCancel.setOnAction((ActionEvent event) -> {
            strReturnStatus = "Cancel";
            close();
        });

        btnReset.setOnAction((ActionEvent event) -> {
            
            for (int ithSTF = 0; ithSTF < 2; ithSTF++) {
                al_STF.get(ithSTF).setText(toBlank); 
            }           
        });
        
        hBoxButtonPanel.getChildren().addAll(btnOK, btnCancel, btnReset);
    }     
    
    private void doMissingAndOrWrong() {
        boolValuesLeftBlank = false;        
        for (int ithSTF = 0; ithSTF < 2; ithSTF++) {
            if (al_STF.get(ithSTF).isEmpty()) 
                boolValuesLeftBlank = true;
        }
        boolAllFieldsGood = boolLeftBinGood && boolRightBinGood;
    }

    public double getDblLeftBin() { return leftBin;}
    public double getDblRightBin() { return rightBin;}          
    public String getReturnStatus() { return strReturnStatus; }
}


