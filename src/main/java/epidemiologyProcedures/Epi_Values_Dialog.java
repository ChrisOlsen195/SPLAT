/****************************************************************************
 *                Epidemiology_Values_Dialog                                * 
 *                          08/19/24                                        *
 *                            00:00                                         *
 ***************************************************************************/
package epidemiologyProcedures;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import dialogs.Splat_Dialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import utilityClasses.MyAlerts;

        
/****************************************************************************
 *        GridPane is (column index, row index)                             *
 *   Var1 categories go DOWN the left column from row 1 to row nCategories  *
 *   Var2 categories go ACROSS the top row from col 1 to col nCategories    *
 ***************************************************************************/

public class Epi_Values_Dialog extends Splat_Dialog {
    //  POJOs
    boolean closeRequested, entriesDiffer;
    
    int indexExposureYes, indexExposureNo, indexOutcomeYes, indexOutcomeNo;

    String[] strEnteredData; 
    ObservableList<String> exposureUniques, outcomesUniques;
    
    // My classes  
    Epi_Model epi_Model;
    
    // POJOs / FX
    AnchorPane anchorPane_DataEntry;
    Button btnClearControl, btnContinue;   
    ChoiceBox<String> chBoxExpNo, chBoxExpYes, chBoxOutNo, chBoxOutYes;
    HBox hBoxWhereToNext;
    Label lblExposure, lblOutcome;
    Label lblExpYes, lblExpNo, lblOutYes, lblOutNo;
    Scene scene_Epi;
    Text txtBottom;  

    public Epi_Values_Dialog(Epi_Model epi_Model) {
        super();
        System.out.println("\n59 Epidemiology_Values_Dialog, Constructing");
        strEnteredData = new String[10];
        VBox root = new VBox();
        this.epi_Model = epi_Model;

        initializeUIComponents();   
        constructObservedValuesPanel();
        constructTheAnchorPane();
        setResizable(true);
        setWidth(750);
        setHeight(350);
        root.getChildren().addAll(anchorPane_DataEntry, hBoxWhereToNext);
        scene_Epi = new Scene(root);
        setScene(scene_Epi); 
        showAndWait();
    }

    private void constructObservedValuesPanel() {
        //System.out.println("77 Epidemiology_Values_Dialog, constructObservedValuesPanel()");
        setWidth(600);
        setHeight(425);  
        setWidth(825);
        armDirectionsButtons();
    }
    
    private void constructTheAnchorPane() {
        //System.out.println("85 Epidemiology_Values_Dialog, constructTheAnchorPane()");
        anchorPane_DataEntry = new AnchorPane();
        
        AnchorPane.setTopAnchor(lblExposure, 170.0);
        AnchorPane.setLeftAnchor(lblExposure, 25.0);
        AnchorPane.setRightAnchor(lblExposure, 450.0);
        anchorPane_DataEntry.getChildren().add(lblExposure);
        
        AnchorPane.setTopAnchor(chBoxExpYes, 130.0);
        AnchorPane.setLeftAnchor(chBoxExpYes, 175.0);
        AnchorPane.setRightAnchor(chBoxExpYes, 450.0);
        anchorPane_DataEntry.getChildren().add(chBoxExpYes);    
        
        AnchorPane.setTopAnchor(lblExpYes, 130.0);
        AnchorPane.setLeftAnchor(lblExpYes, 100.0);
        AnchorPane.setRightAnchor(lblExpYes, 550.0);
        anchorPane_DataEntry.getChildren().add(lblExpYes);        

        AnchorPane.setTopAnchor(chBoxExpNo, 210.0);
        AnchorPane.setLeftAnchor(chBoxExpNo, 175.0);
        AnchorPane.setRightAnchor(chBoxExpNo, 450.0);
        anchorPane_DataEntry.getChildren().add(chBoxExpNo); 
        
        AnchorPane.setTopAnchor(lblExpNo, 210.0);
        AnchorPane.setLeftAnchor(lblExpNo, 100.0);
        AnchorPane.setRightAnchor(lblExpNo, 525.0);
        anchorPane_DataEntry.getChildren().add(lblExpNo);         

        AnchorPane.setTopAnchor(lblOutcome, 30.0);
        AnchorPane.setLeftAnchor(lblOutcome, 450.0);
        AnchorPane.setRightAnchor(lblOutcome, 75.0);
        anchorPane_DataEntry.getChildren().add(lblOutcome);        

        AnchorPane.setTopAnchor(chBoxOutYes, 90.0);
        AnchorPane.setLeftAnchor(chBoxOutYes, 325.0);
        AnchorPane.setRightAnchor(chBoxOutYes, 300.0);
        anchorPane_DataEntry.getChildren().add(chBoxOutYes); 
        
        AnchorPane.setTopAnchor(lblOutYes, 60.0);
        AnchorPane.setLeftAnchor(lblOutYes, 370.0);
        AnchorPane.setRightAnchor(lblOutYes, 265.0);
        anchorPane_DataEntry.getChildren().add(lblOutYes);        

        AnchorPane.setTopAnchor(chBoxOutNo, 90.0);
        AnchorPane.setLeftAnchor(chBoxOutNo, 500.0);
        AnchorPane.setRightAnchor(chBoxOutNo, 125.0);
        anchorPane_DataEntry.getChildren().add(chBoxOutNo); 
        
        AnchorPane.setTopAnchor(lblOutNo, 60.0);
        AnchorPane.setLeftAnchor(lblOutNo, 550.0);
        AnchorPane.setRightAnchor(lblOutNo, 140.0);
        anchorPane_DataEntry.getChildren().add(lblOutNo);         
    }
  
    private void initializeUIComponents() {    
        //System.out.println("140 Epidemiology_Values_Dialog, initializeUIComponents()");
        lblExposure = new Label("Exposure"); lblOutcome = new Label("Outcome");
        
        lblExpYes = new Label("Yes"); lblExpNo = new Label("No"); 
        lblOutYes = new Label("Yes");  lblOutNo = new Label("No");
    
        btnCancel.setText("Return to Menu");
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                epi_Model.setCleanReturnFromSummaryDialog("Cancel");
                epi_Model.closeTheSummaryDialog(false);
                strReturnStatus = "Cancel";
                close();
            }
        });
        
        // Not clear that clear is needed
        btnClearControl = new Button("Clear Entries");
        btnClearControl.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) { 

            }   
        });
               
        btnContinue = new Button("Continue");
        btnContinue.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {   
                strEnteredData[0] = lblExposure.getText();
                strEnteredData[1] = chBoxExpYes.getValue();
                strEnteredData[2] = chBoxExpNo.getValue();
                strEnteredData[3] = lblOutcome.getText();
                strEnteredData[4] = chBoxOutYes.getValue();
                strEnteredData[5] = chBoxOutNo.getValue();
                
                if (checkEntriesAreOK()) {
                    epi_Model.setCleanReturnFromSummaryDialog("OK");
                    hide();
                } else {
                    MyAlerts.showHomogeneousVariableAlert();
                    epi_Model.setCleanReturnFromSummaryDialog("NO");
                }
            }
        });     
        
        hBoxWhereToNext = new HBox();
        hBoxWhereToNext.setAlignment(Pos.CENTER);
        // Insets top, right, bottom, left
        Insets margin = new Insets(25, 10, 0, 10);
        HBox.setMargin(btnCancel, margin);
        HBox.setMargin(btnClearControl, margin);
        HBox.setMargin(btnContinue, margin);
        hBoxWhereToNext.getChildren().addAll(btnCancel, btnClearControl, btnContinue);

        // **********************   Strings and Text  ***********************************
        txtBottom = new Text("\n\n      ******  In the fields below, enter the observed values.  *****\n\n");
        
        // ******************   Dropdowns  ***********************
        exposureUniques = FXCollections.<String>observableArrayList(epi_Model.getExposureUniques());
        outcomesUniques = FXCollections.<String>observableArrayList(epi_Model.getOutcomeUniques());
        
        chBoxExpYes = new ChoiceBox<>(exposureUniques);
        chBoxExpYes.setPrefWidth(400.0);
        chBoxExpNo = new ChoiceBox<>(exposureUniques);
        chBoxExpNo.setPrefWidth(400.0);
        chBoxOutYes = new ChoiceBox<>(outcomesUniques); 
        chBoxOutYes.setPrefWidth(400.0);
        chBoxOutNo = new ChoiceBox<>(outcomesUniques);
        chBoxOutNo.setPrefWidth(400.0);
          
        hBoxWhereToNext = new HBox();
        hBoxWhereToNext.setAlignment(Pos.CENTER);
        // Insets top, right, bottom, left
        HBox.setMargin(btnCancel, margin);
        HBox.setMargin(btnClearControl, margin);
        HBox.setMargin(btnContinue, margin);
        hBoxWhereToNext.getChildren().addAll(btnCancel, btnClearControl, btnContinue);
 
        Font CourierNew_14 = Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14);
        txtBottom.setFont(CourierNew_14);
    } 

    private boolean checkEntriesAreOK() {
        //System.out.println("222 Epidemiology_Values_Dialog, checkEntriesAreOK()"); 
        indexExposureYes = getIndexValueExposuresYes();
        indexExposureNo = getIndexValueExposuresNo();
        indexOutcomeYes = getIndexValueOutcomesYes();
        indexOutcomeNo = getIndexValueOutcomesNo();
        entriesDiffer = (indexExposureYes != indexExposureNo)
                        && (indexOutcomeYes != indexOutcomeNo);
        return entriesDiffer;
    }
    
    public String getExposureLabel() { 
        //System.out.println("233 Epi_Values_Dialog, lblExposure.getText(); = " + lblExposure.getText());
        return lblExposure.getText();
    }
    
    public String getValueExposuresYes() { 
        //System.out.println("238 Epi_Values_Dialog, chBoxExpYes.getValue() = " + chBoxExpYes.getValue());
        return chBoxExpYes.getValue(); 
    }
    
    public String getValueExposuresNo() { 
        //System.out.println("243 Epi_Values_Dialog, chBoxExpNo.getValue() = " + chBoxExpNo.getValue());
        return chBoxExpNo.getValue(); 
    }
    
    public String getOutcomeLabel() { 
        //System.out.println("248 Epi_Values_Dialog, lblOutcome.getText(); = " + lblOutcome.getText());
        return lblExposure.getText();
    }

    public String getValueOutcomesYes() { 
        //System.out.println("253 Epi_Values_Dialog, chBoxOutYes.getValue() = " + chBoxOutYes.getValue());
        return chBoxOutYes.getValue();
    }
    
    public String getValueOutcomesNo() { 
        //System.out.println("258 Epi_Values_Dialog, chBoxOutNo.getValue() = " + chBoxOutNo.getValue());
        return chBoxOutNo.getValue(); 
    }
    
    public int getIndexValueExposuresNo() { 
        int valueToReturn = 0;
        String strTheValue = chBoxExpNo.getValue();
        for (int ithValue = 0; ithValue < exposureUniques.size(); ithValue++) {
            if (exposureUniques.get(ithValue).equals(strTheValue)) {
                indexExposureNo = ithValue;
                valueToReturn = ithValue;
            }
        }
        return valueToReturn;
    }
    
    public int getIndexValueExposuresYes() { 
        int valueToReturn = 0;
        String strTheValue = chBoxExpYes.getValue();
        for (int ithValue = 0; ithValue < exposureUniques.size(); ithValue++) {
            if (exposureUniques.get(ithValue).equals(strTheValue)) {
                indexExposureYes = ithValue;
                valueToReturn = ithValue;
            }
        }
        return valueToReturn;
    }    
    
    public int getIndexValueOutcomesNo() { 
        int valueToReturn = 0;
        String strTheValue = chBoxOutNo.getValue();
        for (int ithValue = 0; ithValue < outcomesUniques.size(); ithValue++) {
            if (outcomesUniques.get(ithValue).equals(strTheValue)) {
                indexOutcomeNo  = ithValue;
                valueToReturn = ithValue;
            }
        }
        return valueToReturn;
    }
    
    public int getIndexValueOutcomesYes() { 
        int valueToReturn = 0;
        String strTheValue = chBoxOutYes.getValue();
        for (int ithValue = 0; ithValue < outcomesUniques.size(); ithValue++) {
            if (outcomesUniques.get(ithValue).equals(strTheValue)) {
                indexOutcomeYes = ithValue;
                valueToReturn = ithValue;
            }
        }
        return valueToReturn;
    } 
    
    public String[] getFileResponseListAsStrings() {
        String[] strList = new String[10];
        
        for (int ithStr = 0; ithStr < 6; ithStr++) {
            strList[ithStr] = strEnteredData[ithStr];
        }
        return strList;
    }

    public String getReturnStatus() { return strReturnStatus;  }   
    public void armDirectionsButtons() {
        btnCancel.arm(); btnClearControl.arm(); btnContinue.arm(); 
    }      
    public String[] getEpiDialogInfo() { return strEnteredData; }
    public boolean getCloseRequested() { return closeRequested; }    
}


