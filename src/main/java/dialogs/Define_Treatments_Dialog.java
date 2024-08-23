/*******************************************************************************
 *                 Define_Treatments_Dialog                                    *
 *                        05/26/24                                             *
 *                         15:00                                               *
 ******************************************************************************/
package dialogs;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import randomAssignment.RandomAssignment_Controller;
import smarttextfield.DoublyLinkedSTF;
import smarttextfield.SmartTextFieldsController;
import utilityClasses.MyAlerts;

public class Define_Treatments_Dialog extends Splat_Dialog {    //  which extends from Stage
/*******************************************************************************
*          Define the return object and any necessary ancillary variables.     *
*******************************************************************************/
    // POJOs
    boolean designIsOK, blocksAreComplete, uniqueCategories, blankTreatments;
    int nTreatments, nSubjects;
    RandomAssignment_Controller randomAssignment_Controller;
    Stage stage;
    String theDesign;
    String[] daTreats;
    
    SmartTextFieldsController stf_VarDef_Controller, stf_Treatments_Controller;
    DoublyLinkedSTF al_VarDef_STF, al_Treatments_STF;
    
    // FX
    Button btnOkToAssignTreatments, btnCancelTreatAssignment,
           btnOkToDefineTreatments, btn_NOT_OkToDefineTreatments, 
           btnClearTreatments;

    ColumnConstraints columnConstraints_1;
    GridPane grid_Treatments;
    HBox hBoxTreatmentDirectionBtns;
    Pane pane_DefineTreatButton, pane_DoNotDefineTreatButton;
    Scene sceneDefineTreatments;
    Text txtTitle, treatmentVariable, txt_nTreatments, txtCategoryDescr; 
    VBox vBoxVisual, vBoxTitle;
    
    public Define_Treatments_Dialog(RandomAssignment_Controller randomAssignmentController) {
        super();
        //System.out.println("57 Define_Treatments_Dialog, constructing");
        this.randomAssignment_Controller = randomAssignmentController;
        theDesign = randomAssignmentController.getTheDesign();
        nSubjects = randomAssignmentController.getNSubjects();
        
        if (nSubjects == 0) {
            MyAlerts.showMissingNSubjectsAlert();
            strReturnStatus = "MissingSubjects";
            closeTheDialog();
        }
        initialize();
    }
    
    private void initialize() {
        strReturnStatus = "OK";
        stage = this;   // 'this' worketh not inside the handlers

        vBoxVisual = new VBox();
        StackPane root = new StackPane();
        root.getChildren().add(vBoxVisual);
        sceneDefineTreatments = new Scene(root, 600, 500);
        setScene(sceneDefineTreatments);
        
        setResizable(true);
        setWidth(600);
        setHeight(500);  
        
        hide();
    }
        
    public void constructDialogGuts() {
        vBoxTitle = new VBox();
        txtTitle = new Text("Random assignment of subjects to treatments...");  
        vBoxTitle.getChildren().add(txtTitle);
        treatmentVariable = new Text("Treatment variable: ");
        txt_nTreatments= new Text("#Treatments: ");
        txtCategoryDescr = new Text("Treatment "); 
        
        pane_DefineTreatButton = new Pane();
        pane_DoNotDefineTreatButton = new Pane();
        constructButtons();

        stf_VarDef_Controller = new SmartTextFieldsController();
        /*    stf_VarDef_Controller is empty until size is set    */
        stf_VarDef_Controller.setSize(2);
        stf_VarDef_Controller.finish_TF_Initializations();
        al_VarDef_STF = stf_VarDef_Controller.getLinkedSTF();
        al_VarDef_STF.makeCircular();     
 
        al_VarDef_STF.get(1).setSmartTextField_MB_POSITIVEINTEGER(true);
        al_VarDef_STF.get(0).setText(("Treatment"));
        al_VarDef_STF.get(0).getTextField().requestFocus();
        
        //  Construct the Grid
        grid_Treatments = new GridPane();
        columnConstraints_1 = new ColumnConstraints(125);
        grid_Treatments.getColumnConstraints().add(columnConstraints_1);
        grid_Treatments.setPadding(new Insets(10, 10, 10, 10));
        grid_Treatments.setVgap(10);
        grid_Treatments.setHgap(10);
        grid_Treatments.add(treatmentVariable, 0, 0);  //  Variable name
        al_VarDef_STF.get(0).setPrefColumnCount(15);
        grid_Treatments.add(al_VarDef_STF.get(0).getTextField(), 1, 0);
        grid_Treatments.add(txt_nTreatments, 2, 0);
        
        al_VarDef_STF.get(1).setPrefColumnCount(4);
        grid_Treatments.add(al_VarDef_STF.get(1).getTextField(), 3, 0);
        grid_Treatments.add(pane_DoNotDefineTreatButton, 2, 1);
        grid_Treatments.add(pane_DefineTreatButton, 3, 1); 

        GridPane.setHalignment(btnOkToDefineTreatments, HPos.CENTER);
        GridPane.setHalignment(btnClearTreatments, HPos.CENTER);
        GridPane.setHalignment(btnOkToAssignTreatments, HPos.CENTER);
        GridPane.setHalignment(btn_NOT_OkToDefineTreatments, HPos.CENTER);
        pane_DoNotDefineTreatButton.getChildren().add(btn_NOT_OkToDefineTreatments);
        pane_DefineTreatButton.getChildren().add(btnOkToDefineTreatments);
        hBoxTreatmentDirectionBtns = new HBox();
        vBoxVisual.getChildren().addAll(txtTitle, grid_Treatments, hBoxTreatmentDirectionBtns);
    }   //  constructDialogGuts 
    
    private void constructButtons() {
        btnClearTreatments = new Button ("Clear treatments");
        btnClearTreatments.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent event) {     
            
            if (nTreatments > 0) {
                for (int ithTreatment = 0; ithTreatment < nTreatments; ithTreatment++) {
                    al_Treatments_STF.get(ithTreatment).setText(""); 
                } 
            }
        }
    });    

        btnCancelTreatAssignment = new Button ("Cancel");
        btnCancelTreatAssignment.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                strReturnStatus = "Cancel";
                closeTheDialog();
            }
        });
        
        btn_NOT_OkToDefineTreatments = new Button("Cancel");
        btn_NOT_OkToDefineTreatments.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {  
                strReturnStatus = "CancelTreatDefine";
                closeTheDialog();
            }   //  end handle
        });   
        
        btnOkToDefineTreatments = new Button("Define treatments...");
        btnOkToDefineTreatments.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {    
                blocksAreComplete = true;   // So far...
                boolean stfIsEmpty = al_VarDef_STF.get(1).isEmpty();
                boolean stfIsBlank = al_VarDef_STF.get(1).isBlank();
                
                if (!stfIsEmpty && !stfIsBlank) {
                    nTreatments = al_VarDef_STF.get(1).getSmartTextInteger();
                    designIsOK = true;  //  so far...

                    if (theDesign.equals("RBD")) {  //  Check for completeness of blocks
                        if(nSubjects % nTreatments != 0) {
                            MyAlerts.showIncompleteBlocksAlert();
                            strReturnStatus = "IncompleteBlocks";
                            blocksAreComplete = false;
                            designIsOK = false;
                            closeTheDialog();
                        }
                    }
                    stf_Treatments_Controller = new SmartTextFieldsController();
                    /*    stf_VarDef_Controller is empty until size is set    */
                    stf_Treatments_Controller.setSize(nTreatments);
                    stf_Treatments_Controller.finish_TF_Initializations();
                    al_Treatments_STF = stf_Treatments_Controller.getLinkedSTF();
                    al_Treatments_STF.makeCircular();    
                    pane_DoNotDefineTreatButton.getChildren().remove(btn_NOT_OkToDefineTreatments);
                    pane_DefineTreatButton.getChildren().remove(btnOkToDefineTreatments);
                    grid_Treatments.add(txtCategoryDescr, 1, 2);
                    GridPane.setHalignment(txtCategoryDescr, HPos.CENTER);
                    setHeight(250 + 40 * nTreatments);
                    
                    for (int ithSTF = 0; ithSTF < nTreatments; ithSTF++) {
                        grid_Treatments.add(al_Treatments_STF.get(ithSTF).getTextField(), 1, ithSTF + 3);
                    }
                    
                    grid_Treatments.getChildren().remove(btnOkToDefineTreatments);
                    grid_Treatments.add(btnCancelTreatAssignment, 1, nTreatments + 5);
                    grid_Treatments.add(btnClearTreatments, 2, nTreatments + 5);
                    grid_Treatments.add(btnOkToAssignTreatments, 3, nTreatments + 5);
                } else {
                    MyAlerts.showMissingNTreatmentsAlert();
                    strReturnStatus = "MissingTreatments";
                }
            }
        }); 
        
        btnOkToAssignTreatments = new Button("Assign treatments...");
        btnOkToAssignTreatments.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
/***************************************************************************
 *                    Check for Data Entry Problems                        *
 **************************************************************************/ 
                uniqueCategories = true;
                blankTreatments = checkForBlankTreatments();
                if (blankTreatments) {
                    MyAlerts.showMissingDataAlert();
                    closeTheDialog();
                }
                
                if (!blankTreatments) {
                    uniqueCategories =  checkForUniqueCategories();
                }
                
                if (!uniqueCategories) {    //  or all are blank
                    MyAlerts.showNonUniqueCategoriesAlert();
                    closeTheDialog();
                }
                
                if (blankTreatments == false && uniqueCategories == true) {
                    strReturnStatus = "OK";
                    closeTheDialog();
                }
            }
        });   
    }
    
    private boolean checkForUniqueCategories() {
        // Unique is necessary for Category Axis
        
        for (int ithTreat = 0; ithTreat < nTreatments - 1; ithTreat++) {    
            
            for (int jthTreat = ithTreat + 1; jthTreat < nTreatments; jthTreat++) {
                String temp1 = al_Treatments_STF.get(ithTreat).getText();
                String temp2 = al_Treatments_STF.get(jthTreat).getText();
                if (temp1.equals(temp2)) {
                    return false;
                } 
            }
        }
        return true;
    }
    
    private boolean checkForBlankTreatments() {
        boolean hasBlankTreats = false;
        
        for (int ithSTF = 0; ithSTF < nTreatments; ithSTF++) {
            String strTreatment = al_Treatments_STF.get(ithSTF).getText();
            
            if (strTreatment.isEmpty() || strTreatment.isBlank()) {
                hasBlankTreats  = true;            
            }
        }       
        return hasBlankTreats;
    }
    
    private void closeTheDialog() {
        randomAssignment_Controller.setReturnStatusTo (strReturnStatus);
        stage.close();
    }
     
    public String getTreatmentVariable() { return al_VarDef_STF.get(0).getText(); }
    
    public int getNTreatments() { return nTreatments; }
    
    public String[] getTreatments() {
        daTreats = new String[nTreatments];
        for (int ithTreat = 0; ithTreat < nTreatments; ithTreat++) {
            daTreats[ithTreat] = al_Treatments_STF.get(ithTreat).getText();
        }
        return daTreats;
    }  
}


