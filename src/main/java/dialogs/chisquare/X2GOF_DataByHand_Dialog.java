/**************************************************************************
 *                  X2GOF_DataByHandDialog                                *
 *                        12/13/25                                        *
 *                         15:00                                          *
 *************************************************************************/
package dialogs.chisquare;

import chiSquare.GOF.X2GOF_DataDialogObj;
import chiSquare.GOF.X2GOF_Model;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import smarttextfield.*;
import dialogs.Splat_Dialog;
import utilityClasses.*;

public class X2GOF_DataByHand_Dialog extends Splat_Dialog {
    // POJOs
    boolean equalPropsSelected; 
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    double sumExpectedProps;
    double[] expectedProps;
    
    int nCategories;
    int[] obsValues;

    final String strAdjust = "Adjust";
    final String strFix = "Fix";
    final String strCancel = "Cancel";
    
    // My classes   
    SmartTextFieldsController stf_Initial_Controller, stf_TheGrid_Controller;
    SmartTextFieldDoublyLinkedSTF al_Initial_STF, al_TheGrid_STF;
    
    X2GOF_Model x2GOF_Model;
    X2GOF_DataDialogObj x2GOF_DataDialog_Obj;
    
    // POJOs / FX
    Button btnOkForGOFAnalysis, btnCancelGOFAnalysis,
           btnOkForGOFCategories, btnCancelGOFCategories,
           btnClearGOFCategories;
    
    CheckBox chBoxDoEqualProps;
    ColumnConstraints columnConstraints;
    GridPane  gridPane_GOF;
    HBox hBoxGOFDirectionBtns;
    Pane pane4_GOFButtons;
    Scene scene_GOF_Summary;
    Text txtGOFControlTitle, txtX2Variable, txtNCategories, txtCategoryDescr, 
         txtExpProp, txtObsCount; 
    VBox vBoxVisual, vBoxGOF;
    
    public X2GOF_DataByHand_Dialog(X2GOF_Model x2GOFModel) {
        super();
        if (printTheStuff) {
            System.out.println("*** 74 X2GOF_DataByHand_Dialog, Constructing");
        }
        this.x2GOF_Model = x2GOFModel;
        initialize();
    }
    
    private void initialize() {
        if (printTheStuff) {
            System.out.println("*** 82 X2GOF_DataByHand_Dialog, initialize()");
        }
        nCategories = x2GOF_Model.getNCategories();
        vBoxVisual = new VBox();
        StackPane root = new StackPane();
        root.getChildren().add(vBoxVisual);
        scene_GOF_Summary = new Scene(root, 600, 200);
        setScene(scene_GOF_Summary);
        
        setResizable(true);
        setWidth(600);
        setHeight(200);  
        
        hide();
    }   //  End constructor
        
/****************************************************************************
 *                      Guts of the dialog                                  * 
 ***************************************************************************/
    public void constructDialogGuts() {
        if (printTheStuff) {
            System.out.println("*** 103 X2GOF_DataByHand_Dialog, constructDialogGuts()");
        }
        vBoxGOF = new VBox();
        txtGOFControlTitle = new Text("   X2 Goodness of Fit");  
        vBoxGOF.getChildren().add(txtGOFControlTitle);
        txtX2Variable = new Text("Variable name: ");
        txtNCategories = new Text("#Categories: ");
        txtCategoryDescr = new Text("Category "); 
        txtExpProp = new Text("Expected\n   prop");
        txtObsCount = new Text("Observed\n  Count");
        pane4_GOFButtons = new Pane();
 
        constructButtons();

        chBoxDoEqualProps.setStyle("-fx-border-color: black");
        chBoxDoEqualProps.setSelected(false);
        chBoxDoEqualProps.setPadding(new Insets(5, 5, 5, 5)); 
        
        stf_Initial_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set
        stf_Initial_Controller.setSize(2);
        stf_Initial_Controller.finish_TF_Initializations();
        al_Initial_STF = stf_Initial_Controller.getLinkedSTF();
        al_Initial_STF.makeCircular();

        al_Initial_STF.get(1).setSmartTextField_MB_POSITIVEINTEGER(true);   
        al_Initial_STF.get(0).getTextField().requestFocus();

        //  Construct the Grid
        gridPane_GOF = new GridPane();
        columnConstraints = new ColumnConstraints(125);
        gridPane_GOF.getColumnConstraints().add(columnConstraints);
        gridPane_GOF.setPadding(new Insets(10, 10, 10, 10));
        gridPane_GOF.setVgap(10);
        gridPane_GOF.setHgap(10);
        gridPane_GOF.add(txtX2Variable, 0, 0);  //  Variable name
        
        al_Initial_STF.get(0).setPrefColumnCount(15);
        gridPane_GOF.add(al_Initial_STF.get(0).getTextField(), 1, 0);
        gridPane_GOF.add(txtNCategories, 2, 0);
        
        al_Initial_STF.get(1).setPrefColumnCount(4);
        gridPane_GOF.add(al_Initial_STF.get(1).getTextField(), 3, 0);
              
        gridPane_GOF.add(btnCancelGOFCategories, 2, 1);
        gridPane_GOF.add(pane4_GOFButtons, 3, 1); 

        GridPane.setHalignment(btnOkForGOFCategories, HPos.CENTER);
        GridPane.setHalignment(btnClearGOFCategories, HPos.CENTER);
        GridPane.setHalignment(btnCancelGOFCategories, HPos.CENTER);
        GridPane.setHalignment(btnOkForGOFAnalysis, HPos.CENTER);
        
        pane4_GOFButtons.getChildren().add(btnOkForGOFCategories);

        hBoxGOFDirectionBtns = new HBox();
        vBoxVisual.getChildren().addAll(txtGOFControlTitle, gridPane_GOF, hBoxGOFDirectionBtns);
    } 
    
    private void constructButtons() {   //  and CheckBox
        if (printTheStuff) {
            System.out.println("*** 163 X2GOF_DataByHand_Dialog, constructButtons()");
        }
        chBoxDoEqualProps = new CheckBox("H0 Equal Props");
        chBoxDoEqualProps.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                
                if (chBoxDoEqualProps.isSelected()) {
                    equalPropsSelected = true;  
                    
                    if ( al_TheGrid_STF != null) {  //  Resetting from blanks
                        
                        for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {
                            double daEqualProp = 1.0 / nCategories;
                            int tempPosition = 3 * ithCategory + 1;
                            al_TheGrid_STF.get(tempPosition).setSmartTextDouble(daEqualProp);
                            al_TheGrid_STF.get(tempPosition).setText(String.valueOf(daEqualProp));
                            al_TheGrid_STF.get(tempPosition).setIsEditable(false);
                        }                          
                    }
                }
                else {
                    equalPropsSelected = false;
                    
                    for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {
                        int tempPosition = 3 * ithCategory + 1;
                        al_TheGrid_STF.get(tempPosition).setSmartTextDouble(0.0);
                        al_TheGrid_STF.get(tempPosition).setText("");
                        al_TheGrid_STF.get(tempPosition).setIsEditable(true);
                    }
                }
            }
        });
        
        btnClearGOFCategories = new Button ("Clear categories");
        
        btnClearGOFCategories.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent event) {
        //  ****************     Clear the props   **********************        
            if (chBoxDoEqualProps.isSelected()) {
                equalPropsSelected = true;                  
                if (al_TheGrid_STF != null) {  //  Resetting from blanks                    
                    for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {
                        double daEqualProp = 1.0 / nCategories;
                        int tempPosition = 3 * ithCategory + 1;
                        al_TheGrid_STF.get(tempPosition).setSmartTextDouble(daEqualProp);
                        al_TheGrid_STF.get(tempPosition).setText(String.valueOf(daEqualProp));
                        al_TheGrid_STF.get(tempPosition).setIsEditable(false);
                    }                          
                }
            }
            else {
                equalPropsSelected = false;                
                for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {
                    int tempPosition = 3 * ithCategory + 1;
                    al_TheGrid_STF.get(tempPosition).setSmartTextDouble(0.0);
                    al_TheGrid_STF.get(tempPosition).setText("");
                    al_TheGrid_STF.get(tempPosition).setIsEditable(true);
                }
            }
            
            //  ****************     Clear the rest   **********************            
            for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {
                int tempPosition1 = 3 * ithCategory;
                int tempPosition2 = 3 * ithCategory + 1;
                al_TheGrid_STF.get(tempPosition1).setText("");
                al_TheGrid_STF.get(tempPosition2).setText("");  
            }   
        }
    });    
        
    btnCancelGOFCategories = new Button ("Back to Menu");
    btnCancelGOFCategories.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent event) {
            strReturnStatus = "BackToMenu";
            close();
        }
    });

    btnCancelGOFAnalysis = new Button ("Cancel");
    btnCancelGOFAnalysis.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent event) {
            strReturnStatus = "Cancel";
            close();
        }
    });
        
    btnOkForGOFCategories = new Button("Proceed to categories");
        
        btnOkForGOFCategories.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {      
                //  true -> goodToGo
                int tempPosition;
                
                if (!al_Initial_STF.get(0).isEmpty() && !al_Initial_STF.get(1).isEmpty()) {
                    nCategories = al_Initial_STF.get(1).getSmartTextInteger();       
                    stf_TheGrid_Controller = new SmartTextFieldsController();
                    // The Controller is empty until size is set
                    stf_TheGrid_Controller.setSize(3 * nCategories);
                    stf_TheGrid_Controller.finish_TF_Initializations();
                    al_TheGrid_STF = stf_TheGrid_Controller.getLinkedSTF();
                    al_TheGrid_STF.makeCircular();                    
                    obsValues = new int[nCategories];
                    expectedProps = new double[nCategories];
                    gridPane_GOF.add(chBoxDoEqualProps, 0, 1);
                    pane4_GOFButtons.getChildren().remove(btnOkForGOFCategories);
                    gridPane_GOF.add(txtCategoryDescr, 1, 2);
                    GridPane.setHalignment(txtCategoryDescr, HPos.CENTER);
                    gridPane_GOF.add(txtExpProp, 2, 2);
                    GridPane.setHalignment(txtExpProp, HPos.LEFT);
                    gridPane_GOF.add(txtObsCount, 3, 2);
                    GridPane.setHalignment(txtObsCount, HPos.LEFT);
                    setHeight(250 + 40 * nCategories);

                    for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {
                        tempPosition =  3 * ithCategory;
                        gridPane_GOF.add(al_TheGrid_STF.get(tempPosition).getTextField(), 1, ithCategory + 3);

                        tempPosition =  3 * ithCategory + 1;
                        al_TheGrid_STF.get(tempPosition).setSmartTextField_MB_PROBABILITY(true); 
                        al_TheGrid_STF.get(tempPosition).getTextField().setMaxWidth(50);
                        gridPane_GOF.add(al_TheGrid_STF.get(tempPosition).getTextField(), 2, ithCategory + 3);

                        tempPosition =  3 * ithCategory + 2;
                        al_TheGrid_STF.get(tempPosition).setSmartTextField_MB_POSITIVEINTEGER(true);
                        
                        al_TheGrid_STF.get(tempPosition).getSmartTextField().getTextField().setMaxWidth(50);
                        gridPane_GOF.add(al_TheGrid_STF.get(tempPosition).getTextField(), 3, ithCategory + 3);

                        if (equalPropsSelected == true) {
                            double daEqualProp = 1.0 / (double)nCategories;
                            tempPosition =  3 * ithCategory + 1;
                            al_TheGrid_STF.get(tempPosition).setSmartTextDouble(daEqualProp);
                            al_TheGrid_STF.get(tempPosition).setText(String.valueOf(daEqualProp));
                        }    
                    }

                    gridPane_GOF.getChildren().remove(btnCancelGOFCategories);
                    gridPane_GOF.getChildren().remove(btnOkForGOFCategories);
                    gridPane_GOF.add(btnCancelGOFAnalysis, 1, nCategories + 3);
                    gridPane_GOF.add(btnClearGOFCategories, 2, nCategories + 3);
                    gridPane_GOF.add(btnOkForGOFAnalysis, 3, nCategories + 3);

                    al_TheGrid_STF.get(0).getTextField().requestFocus();
                }
                else {
                    MyAlerts.showMissingDataAlert();
                }
            }
        });        
        
        btnOkForGOFAnalysis = new Button("Proceed to analysis");
        
        btnOkForGOFAnalysis.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
/***************************************************************************
 *                    Check for Data Entry Problems                        *
 **************************************************************************/ 
                boolGoodToGo = checkForBlanks();
                if (boolGoodToGo) {
                    boolGoodToGo =  checkForUniqueCategories();
                }
                if (boolGoodToGo){
                    sumExpectedProps = 0.0;
                    
                    for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {
                        obsValues[ithCategory] = al_TheGrid_STF.get(3 * ithCategory + 2).getSmartTextInteger();                     
                        expectedProps[ithCategory] = al_TheGrid_STF.get(3 * ithCategory + 1).getSmartTextDouble(); 
                        sumExpectedProps += expectedProps[ithCategory];
                    }

                    if (Math.abs(sumExpectedProps - 1.0) < .01) {   //  .01 Arbrtrary!!!
                        // Adjusts to sum of expected props = 1.0 no matter what                        
                        for (int iExpProps = 0; iExpProps < nCategories; iExpProps++) {
                            expectedProps[iExpProps] /= sumExpectedProps;
                        } 

                        sumExpectedProps = 1.0;

    /***************************************************************************
     *                          Construct the object                           *
     **************************************************************************/
                        x2GOF_DataDialog_Obj = new X2GOF_DataDialogObj(nCategories);
                        x2GOF_DataDialog_Obj.setGOFVariable(al_Initial_STF.get(0).getText());

                        String[] theGOFCats = new String[nCategories];                        
                        for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {
                            theGOFCats[ithCategory] = al_TheGrid_STF.get( 3 * ithCategory).getText();
                        }
                        
                        x2GOF_DataDialog_Obj.setTheGOFCategories(theGOFCats);
                        expectedProps = new double[nCategories];                        
                        for (int i = 0; i < nCategories; i++) {
                            expectedProps[i] = al_TheGrid_STF.get(3 * i + 1).getSmartTextDouble();
                        } 
                        
                        x2GOF_DataDialog_Obj.setExpectedProps(expectedProps);   
                        obsValues = new int[nCategories];
                        
                        for (int i = 0; i < nCategories; i++) {
                            obsValues[i] = al_TheGrid_STF.get(3 * i + 2).getSmartTextInteger();
                        } 
                        
                        x2GOF_DataDialog_Obj.setObservedValues(obsValues);   
                        strReturnStatus = "OK";
                        close();

                    } else {    // Math.abs(sumExpectedProps - 1.0) > .01
                        String choice = doSumExpPropsDialog();
                        switch (choice) {
                            case strAdjust: 
                            {
                                for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {
                                    expectedProps[ithCategory] /= sumExpectedProps;
                                    int listPosition = 3 * ithCategory + 1;
                                    al_TheGrid_STF.get(listPosition ).setText(String.valueOf(expectedProps[ithCategory]));
                                    al_TheGrid_STF.get(listPosition ).setSmartTextDouble(expectedProps[ithCategory]);
                                } 
                                sumExpectedProps = 1.0;
                                btnOkForGOFAnalysis.fire(); //  Actually re-fire
                                break;
                            }

                            case strFix:
                            {
                                for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {
                                    al_TheGrid_STF.get(3 * ithCategory + 1).setText("");
                                }
                                break;
                            }

                            case strCancel:
                                strReturnStatus = "Cancel";
                                close(); 
                            break;

                            default:
                                String switchFailure = "Switch failure: 399 X2GOF_DataByHand " + choice;
                                MyAlerts.showUnexpectedErrorAlert(switchFailure);
                        }    
                    }   //  end else 
                }
            }   //  End handle
        });  
    }
    
    private boolean checkForUniqueCategories() {
        String[] catsToCheck = new String[nCategories];
        for (int ithCat = 0; ithCat < nCategories; ithCat++) {
            catsToCheck[ithCat] = al_TheGrid_STF.get(3 * ithCat).getText();
            if (printTheStuff) {
                System.out.println("*** 413 X2GOF_DataByHand.checkForUniqueCategories, " + catsToCheck[ithCat]);
            }
        }
        return StringUtilities.checkForUniqueStrings(catsToCheck);
    }
    
    private boolean checkForBlanks() {
        if (printTheStuff) {
            System.out.println("*** 421 GOF_DataByHand_Dialog, checkForBlanks()");
        }
        boolGoodToGo = true;
        
        for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {            
            if (al_TheGrid_STF.get(3 * ithCategory).getText().isEmpty()) {
                boolGoodToGo = false;
            }
        }
        
        if (!boolGoodToGo) {
            MyAlerts.showEmptyCategoriesAlert();
            return boolGoodToGo;
        }
        
        for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {            
            if (al_TheGrid_STF.get(3 * ithCategory + 1).isEmpty()) {
                boolGoodToGo = false;
            }        
        }  
        
        if (!boolGoodToGo) {
            MyAlerts.showEmptyExpectedPropsAlert();
            return false;
        }
        
        for (int ithCategory = 0; ithCategory < nCategories; ithCategory++) {            
            if (al_TheGrid_STF.get(3 * ithCategory + 2).isEmpty()) {
                boolGoodToGo = false;
            }        
        }   
        
        if (!boolGoodToGo) {
            MyAlerts.showEmptyObservedValuesAlert();
            return false;
        }        
        return true;
    }

/*******************************************************************************
*                          Ancillary routines                                  *
*******************************************************************************/    
    
    public String doSumExpPropsDialog() {
        if (printTheStuff) {
            System.out.println("*** 466 GOF_DataByHandDialog.doSumExpPropsDialog()");
        }

        String returnString;
        Alert nonSumToOneAlert = new Alert(Alert.AlertType.CONFIRMATION);   
        nonSumToOneAlert.setTitle("Uh-oh, possible problem here...");
        nonSumToOneAlert.setHeaderText("There is a problem with your hypothesized proportions.");
        nonSumToOneAlert.setContentText("The sum of your expected proportions is different from 1.0. This could be due to roundoff error, in which"
                                     + "\ncase your proportions can be (only slightly) adjusted for the chi square calculations.  If this difference"
                                     + "\nindicates deeper doo-doo problems, you can fix them yourself. Your call.");         

        ButtonType bt_Adjust = new ButtonType("SPLAT, please adjust & continue");
        ButtonType bt_Fix = new ButtonType("I, human, will fix them myself");
        ButtonType bt_Cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        nonSumToOneAlert.getButtonTypes().setAll(bt_Adjust, bt_Fix, bt_Cancel);
        
        boolean keepGoing = true;
        do {
            Optional<ButtonType> result = nonSumToOneAlert.showAndWait();            
            if (result.get() == bt_Adjust){
                returnString = strAdjust;
                keepGoing = false;
            } else if (result.get() == bt_Fix) {
                returnString = strFix;
                keepGoing = false;
            } else {
                returnString = strCancel;
                keepGoing = false;
            }
        } while (keepGoing == true);      
        return returnString;
    }
    public X2GOF_DataDialogObj getTheDialogObject() { return x2GOF_DataDialog_Obj; }
}

