/****************************************************************************
 *                Epidemiology_Summary_Dialog                               * 
 *                          08/19/24                                        *
 *                            00:00                                         *
 ***************************************************************************/
package epidemiologyProcedures;

import java.util.ArrayList;
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
import smarttextfield.*;
import dialogs.Splat_Dialog;
import javafx.scene.layout.AnchorPane;
import utilityClasses.MyAlerts;
        
/****************************************************************************
 *        GridPane is (column index, row index)                             *
 *   Var1 categories go DOWN the left column from row 1 to row nCategories  *
 *   Var2 categories go ACROSS the top row from col 1 to col nCategories    *
 ***************************************************************************/

public class Epi_SummaryDialog extends Splat_Dialog {
    //  POJOs
    boolean closeRequested;

    ArrayList<String> strEnteredData; 
    
    // My classes  
    ArrayList<SmartTextField> stf_al_Epi;
    Epi_Model epi_Model;
    DoublyLinkedSTF stf_al;
    SmartTextFieldsController stf_Controller;
    
    // POJOs / FX
    AnchorPane anchorPane_DataEntry;
    Button btnClearControl, btnContinue;   
    HBox hBoxWhereToNext;
    Scene scene_Epi;
    Text txtBottom;  

    public Epi_SummaryDialog(Epi_Model epi_Model) {
        super();
        System.out.println("\n53 Epidemiology_SummaryDialog, Constructing");
        VBox root = new VBox();
        this.epi_Model = epi_Model;
        stf_al_Epi = new ArrayList<>();
        strEnteredData = new ArrayList<>();
        stf_Controller = new SmartTextFieldsController();
        stf_Controller.setSize(10);
        stf_Controller.finish_TF_Initializations();
        stf_al = stf_Controller.getLinkedSTF();
        stf_al.makeCircular();
        initializeUIComponents();   
        constructObservedValuesPanel();
        constructTheAnchorPane();
        setResizable(true);
        setWidth(540);
        setHeight(325);
        root.getChildren().addAll(anchorPane_DataEntry, hBoxWhereToNext);
        scene_Epi = new Scene(root);
        setScene(scene_Epi); 
    }
    
    public void doShowAndWait() { showAndWait(); }
    
    private void constructObservedValuesPanel() {
        setWidth(600);
        setHeight(425);  
        setWidth(625);
        armDirectionsButtons();
    }
    
    private void constructTheAnchorPane() {
        anchorPane_DataEntry = new AnchorPane();

        AnchorPane.setTopAnchor(stf_al.get(3).getTextField(), 20.0);
        AnchorPane.setLeftAnchor(stf_al.get(3).getTextField(), 275.0);
        AnchorPane.setRightAnchor(stf_al.get(3).getTextField(), 175.0);
        anchorPane_DataEntry.getChildren().add(stf_al.get(3).getTextField());        

        AnchorPane.setTopAnchor(stf_al.get(4).getTextField(), 60.0);
        AnchorPane.setLeftAnchor(stf_al.get(4).getTextField(), 225.0);
        AnchorPane.setRightAnchor(stf_al.get(4).getTextField(), 225.0);
        anchorPane_DataEntry.getChildren().add(stf_al.get(4).getTextField());        

        AnchorPane.setTopAnchor(stf_al.get(5).getTextField(), 60.0);
        AnchorPane.setLeftAnchor(stf_al.get(5).getTextField(), 350.0);
        AnchorPane.setRightAnchor(stf_al.get(5).getTextField(), 100.0);
        anchorPane_DataEntry.getChildren().add(stf_al.get(5).getTextField());        

        AnchorPane.setTopAnchor(stf_al.get(0).getTextField(), 140.0);
        AnchorPane.setLeftAnchor(stf_al.get(0).getTextField(), 25.0);
        AnchorPane.setRightAnchor(stf_al.get(0).getTextField(), 450.0);
        anchorPane_DataEntry.getChildren().add(stf_al.get(0).getTextField());        

        AnchorPane.setTopAnchor(stf_al.get(1).getTextField(), 100.0);
        AnchorPane.setLeftAnchor(stf_al.get(1).getTextField(), 100.0);
        AnchorPane.setRightAnchor(stf_al.get(1).getTextField(), 350.0);
        anchorPane_DataEntry.getChildren().add(stf_al.get(1).getTextField());        

        AnchorPane.setTopAnchor(stf_al.get(2).getTextField(), 180.0);
        AnchorPane.setLeftAnchor(stf_al.get(2).getTextField(), 100.0);
        AnchorPane.setRightAnchor(stf_al.get(2).getTextField(), 350.0);
        anchorPane_DataEntry.getChildren().add(stf_al.get(2).getTextField());       

        AnchorPane.setTopAnchor(stf_al.get(6).getTextField(), 100.0);
        AnchorPane.setLeftAnchor(stf_al.get(6).getTextField(), 260.0);
        AnchorPane.setRightAnchor(stf_al.get(6).getTextField(), 240.0);
        anchorPane_DataEntry.getChildren().add(stf_al.get(6).getTextField());        
        
        AnchorPane.setTopAnchor(stf_al.get(7).getTextField(), 100.0);
        AnchorPane.setLeftAnchor(stf_al.get(7).getTextField(), 385.0);
        AnchorPane.setRightAnchor(stf_al.get(7).getTextField(), 115.0);
        anchorPane_DataEntry.getChildren().add(stf_al.get(7).getTextField());        

        AnchorPane.setTopAnchor(stf_al.get(8).getTextField(), 180.0);
        AnchorPane.setLeftAnchor(stf_al.get(8).getTextField(), 260.0);
        AnchorPane.setRightAnchor(stf_al.get(8).getTextField(),240.0);
        anchorPane_DataEntry.getChildren().add(stf_al.get(8).getTextField());        

        AnchorPane.setTopAnchor(stf_al.get(9).getTextField(), 180.0);
        AnchorPane.setLeftAnchor(stf_al.get(9).getTextField(), 385.0);
        AnchorPane.setRightAnchor(stf_al.get(9).getTextField(), 115.0);
        anchorPane_DataEntry.getChildren().add(stf_al.get(9).getTextField());        
    }
  
    private void initializeUIComponents() {    
    // **********************   Buttons  ***********************************
        btnCancel.setText("Return to Menu");
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                epi_Model.setCleanReturnFromSummaryDialog("Cancel");
                epi_Model.closeTheSummaryDialog(false);
                strReturnStatus = "Cancel";
                close();
            }
        });
        
        btnClearControl = new Button("Clear Entries");
        btnClearControl.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) { 
                stf_al.get(0).setText("Exposure");
                stf_al.get(1).setText("Yes");
                stf_al.get(2).setText("No");
                stf_al.get(3).setText("Outcome");
                stf_al.get(4).setText("Yes");
                stf_al.get(5).setText("No");
                stf_al.get(6).setText("");
                stf_al.get(7).setText("");
                stf_al.get(8).setText("");
                stf_al.get(9).setText("");
            }   
        });
               
        btnContinue = new Button("Continue");
        btnContinue.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {   
                
                if (checkOKCategoriesGrid() == true) {
                    strEnteredData.add(stf_al.get(0).getText());
                    strEnteredData.add(stf_al.get(1).getText());
                    strEnteredData.add(stf_al.get(2).getText());
                    strEnteredData.add(stf_al.get(3).getText());
                    strEnteredData.add(stf_al.get(4).getText());
                    strEnteredData.add(stf_al.get(5).getText());
                    strEnteredData.add(stf_al.get(6).getText());
                    strEnteredData.add(stf_al.get(7).getText());
                    strEnteredData.add(stf_al.get(8).getText());
                    strEnteredData.add(stf_al.get(9).getText());

                    epi_Model.setCleanReturnFromSummaryDialog("OK");
                    epi_Model.setEpiArrayList(strEnteredData);
                    hide();
                }
            }
        });     
        
        hBoxWhereToNext = new HBox();
        hBoxWhereToNext.setAlignment(Pos.CENTER);
        // Insets top, right, bottom, left
        Insets margin = new Insets(20, 10, 0, 10);
        HBox.setMargin(btnCancel, margin);
        HBox.setMargin(btnClearControl, margin);
        HBox.setMargin(btnContinue, margin);
        hBoxWhereToNext.getChildren().addAll(btnCancel, btnClearControl, btnContinue);

        // **********************   Strings and Text  ***********************************
        txtBottom = new Text("\n\n      ******  In the fields below, enter the observed values.  *****\n\n");

        // **********************   TextFields  ***********************************   

        //         gridAssociation.add(lbl_Var2, 0, 3); 
        stf_al.get(0).setText("Exposure");
        stf_al.get(0).setIsEditable(true);

        // stf_Yes_Exposure = new SmartTextField (stf_Handler_Epi, 0, 2);  
        stf_al.get(1).setText("Yes");
        stf_al.get(1).setIsEditable(true); 

        // stf_No_Exposure = new SmartTextField (stf_Handler_Epi, 1, 3);
        stf_al.get(2).setText("No");
        stf_al.get(2).setIsEditable(true); 

        // stf_Outcome = new SmartTextField (stf_Handler_Epi, 2, 4);
        stf_al.get(3).setText("Outcome");
        stf_al.get(3).getTextField().setMinWidth(100.);
        stf_al.get(3).getTextField().setMaxWidth(100.);
        stf_al.get(3).setIsEditable(true);

        // stf_Yes_Outcome = new SmartTextField (stf_Handler_Epi, 3, 5); 
        stf_al.get(4).setText("Yes");
        stf_al.get(4).setIsEditable(true);     

        // stf_No_Disease = new SmartTextField (stf_Handler_Epi, 4, 6);
        stf_al.get(5).setText("No");
        stf_al.get(5).setIsEditable(true);     

        // stf_Exposed_YesOutcome = new SmartTextField (stf_Handler_Epi, 5, 7); 
        stf_al.get(6).setSmartTextField_MB_POSITIVEINTEGER(true);
        stf_al.get(6).setIsEditable(true);

        // stf_Exposed_NoOutcome = new SmartTextField (stf_Handler_Epi, 6, 8);
        stf_al.get(7).setSmartTextField_MB_POSITIVEINTEGER(true);
        stf_al.get(7).setIsEditable(true);    

        // stf_NoExpose_YesOutcome = new SmartTextField (stf_Handler_Epi, 7, 9); 
        stf_al.get(8).setSmartTextField_MB_POSITIVEINTEGER(true);
        stf_al.get(8).setIsEditable(true);    

        // stf_NoExpose_NoOutcome = new SmartTextField (stf_Handler_Epi, 8, 0);  
        stf_al.get(9).setSmartTextField_MB_POSITIVEINTEGER(true);
        stf_al.get(9).setIsEditable(true);     

     // **********************   HBoxes, VBoxes *******************************   
        hBoxWhereToNext = new HBox();
        hBoxWhereToNext.setAlignment(Pos.CENTER);
        // Insets top, right, bottom, left
        HBox.setMargin(btnCancel, margin);
        HBox.setMargin(btnClearControl, margin);
        HBox.setMargin(btnContinue, margin);
        hBoxWhereToNext.getChildren().addAll(btnCancel, btnClearControl, btnContinue);

         // *************************   Misc  ***********************************  
        Font CourierNew_14 = Font.font("Courier New", FontWeight.BOLD, FontPosture.REGULAR, 14);
        txtBottom.setFont(CourierNew_14);

    }   // initializeUIComponents    

    private boolean checkOKCategoriesGrid() {
        boolean okToContinue = true;
        int nCatsToCheck = stf_al_Epi.size();
        for (int ithVar1 = 0; ithVar1 < nCatsToCheck; ithVar1++) {            
            if (stf_al_Epi.get(ithVar1).isEmpty()) {
                okToContinue = false;
            }
        }
        
        if (!okToContinue) {
            MyAlerts.showMustBeNonBlankAlert();
            return false;
        }        
        return true;
    }
    
    public String getReturnStatus() { return strReturnStatus;  }   
    public void armDirectionsButtons() {
        btnCancel.arm(); btnClearControl.arm(); btnContinue.arm(); 
    }      
    public ArrayList<String> getEpiDialogInfo() { return strEnteredData; }
    public boolean getCloseRequested() { return closeRequested; }    
}
