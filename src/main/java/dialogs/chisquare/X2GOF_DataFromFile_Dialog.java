/*******************************************************************************
 *                  GOF_DataFromFileDialog                                     *
 *                        05/23/24                                             *
 *                         12:00                                               *
 ******************************************************************************/
package dialogs.chisquare;

import chiSquare.GOF.X2GOF_Model;
import utilityClasses.MyAlerts;
import chiSquare.GOF.X2GOF_DataDialogObj;
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
import javafx.scene.control.TextField;

public class X2GOF_DataFromFile_Dialog extends Splat_Dialog {
    // POJOs
    boolean equalPropsSelected;
    
    int nCategories;
    int[] observedValues;
    
    double sumExpectedProps;
    double[] expectedProps;
    
    final String strAdjust = "Adjust";
    final String strFix = "Fix";
    final String strCancel = "Cancel";
    String str_SwitchFailure;
    String[] strGOFCategories;
    
    // My classes
    TextField stf_nGOFCategories;    
    X2GOF_Model x2GOF_Model;
    X2GOF_DataDialogObj x2GOF_DataDialogObj;
   
    // POJOs / FX
    Button btnOkGOFAnalysis, btnCancelGOFAnalysis;
    CheckBox chBox_DoEqualProps;
    ColumnConstraints columnConstraints;
    GridPane  gridPane_GOF; 
    HBox hBoxGOFDirectionBtns;
    Pane pane_GOFButtons;
    Scene scene_GOFSummary;
    
    SmartTextFieldsController stf_Controller;
    DoublyLinkedSTF al_STF;
    
    Text txtGOFControlTitle, txtX2VariableDescr, 
         txtX2VariableName, txtNCategories, txtNCategoriesDescr, 
         txtCategoryDescr, txtExpPropDescr, txtObsCountDescr; 
    Text[] txtGOFCategories;
    TextField[] tfGOFObsCounts;
    VBox vBoxVisual, vBoxGOF;
    
    public X2GOF_DataFromFile_Dialog(X2GOF_Model gof_Model) {
        super();
        System.out.println("75 X2GOF_DataFromFile_Dialog(X2GOF_Model gof_Model), constructing ");
        this.x2GOF_Model = gof_Model;

        nCategories = x2GOF_Model.getNCategories(); 
        stf_Controller = new SmartTextFieldsController();
        // stf_Controller is empty until size is set                          *
        stf_Controller.setSize(nCategories);
        stf_Controller.finish_TF_Initializations();
        al_STF = stf_Controller.getLinkedSTF();
        al_STF.makeCircular();
        
        vBoxVisual = new VBox();
        StackPane root = new StackPane();
        root.getChildren().add(vBoxVisual);
        scene_GOFSummary = new Scene(root, 600, 200);
        setScene(scene_GOFSummary);
        
        setResizable(false);
        setWidth(600);
        setHeight(200);  
        
        hide(); //  ????? 
        System.out.println("97 X2GOF_DataFromFile_Dialog(X2GOF_Model gof_Model), END constructing ");
    }   //  End constructor
        
/****************************************************************************
 *                      Guts of the dialog                                  * 
 ***************************************************************************/
    public void constructDialogGuts() {  
        System.out.println("104 X2GOF_DataFromFile_Dialog, constructDialogGuts()");
        vBoxGOF = new VBox();
        txtGOFControlTitle = new Text("X2 Goodness of Fit");  
        vBoxGOF.getChildren().add(txtGOFControlTitle);
        txtX2VariableDescr = new Text("Variable name: ");
        txtX2VariableName = new Text(x2GOF_Model.getGOFVariable());
        txtNCategoriesDescr = new Text("#Categories: ");
        txtCategoryDescr = new Text("Category "); 
        txtExpPropDescr = new Text("Expected\n   prop");
        txtObsCountDescr = new Text("Observed\n  Count");
        pane_GOFButtons = new Pane();
        System.out.println("130 X2GOF_DataFromFile_Dialog, pre-constructSomeGUI()");
        constructSomeGUI();
        System.out.println("132 X2GOF_DataFromFile_Dialog, post-constructSomeGUI()");
        chBox_DoEqualProps.setStyle("-fx-border-color: black");
        chBox_DoEqualProps.setSelected(false);
        chBox_DoEqualProps.setPadding(new Insets(5, 5, 5, 5)); 

        //stf_gofVariable = new TextField();
        txtNCategories = new Text(String.valueOf(nCategories));
        stf_nGOFCategories = new TextField(); 
        //stfArrayList = new ArrayList<>(nCategories); 

        //  Construct the Grid
        gridPane_GOF = new GridPane();
        columnConstraints = new ColumnConstraints(125);
        gridPane_GOF.getColumnConstraints().add(columnConstraints);
        gridPane_GOF.setPadding(new Insets(10, 10, 10, 10));
        gridPane_GOF.setVgap(10);
        gridPane_GOF.setHgap(10);
        gridPane_GOF.add(txtX2VariableDescr, 0, 0);  //  Variable descr
        
        gridPane_GOF.add(txtX2VariableName, 1, 0); //  Variable name
        gridPane_GOF.add(txtNCategoriesDescr, 2, 0); // N categories descrr        
        gridPane_GOF.add(txtNCategories, 3, 0);
        
        stf_nGOFCategories.setPrefColumnCount(4);
        gridPane_GOF.add(pane_GOFButtons, 3, 1); 
        GridPane.setHalignment(btnOkGOFAnalysis, HPos.CENTER);
        hBoxGOFDirectionBtns = new HBox();
        vBoxVisual.getChildren().addAll(txtGOFControlTitle, gridPane_GOF, hBoxGOFDirectionBtns);
        constructMoreGUI();
        System.out.println("146 X2GOF_DataFromFile_Dialog, END constructDialogGuts()");
    }   //  End constructDialogGuts 
    
    private void constructSomeGUI() {   //  and CheckBox
        System.out.println("150 X2GOF_DataFromFile_Dialog, constructSomeGUI()");
        chBox_DoEqualProps = new CheckBox("H0 Equal Props");
        chBox_DoEqualProps.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                
                if (chBox_DoEqualProps.isSelected()) {
                    equalPropsSelected = true;  
                    
                    if (al_STF != null) {  //  Resetting from blanks
                        for (int iCategories = 0; iCategories < nCategories; iCategories++) {
                            double daEqualProp = 1.0 / nCategories;
                            al_STF.get(iCategories).setSmartTextDouble(daEqualProp);
                            al_STF.get(iCategories).setText(String.valueOf(daEqualProp));
                            al_STF.get(iCategories).setIsEditable(false);
                        }                          
                    }
                }
                else {
                    equalPropsSelected = false;
                    
                    for (int iCategories = 0; iCategories < nCategories; iCategories++) {
                        al_STF.get(iCategories).setSmartTextDouble(0.0);
                        al_STF.get(iCategories).setText("");
                        al_STF.get(iCategories).setIsEditable(true);
                    }
                }
            }
        });

        btnCancelGOFAnalysis = new Button ("Cancel");
        btnCancelGOFAnalysis.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                strReturnStatus = "Cancel";
                close();
            }
        });
        
        btnOkGOFAnalysis = new Button("Proceed to analysis");
        btnOkGOFAnalysis.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
/***************************************************************************
 *                    Check for Data Entry Problems                        *
 **************************************************************************/    
                if(checkOKForAnalysis() == true) {
    /***************************************************************************
     *                    Wrap up final calculations                       *
     **************************************************************************/
                    sumExpectedProps = 0.0;
                    for (int iCategories = 0; iCategories < nCategories; iCategories++) {                    
                        expectedProps[iCategories] = al_STF.get(iCategories).getSmartTextDouble(); 
                        sumExpectedProps += expectedProps[iCategories];
                    }

                    if (Math.abs(sumExpectedProps - 1.0) < .01) {   //  .01 Arbrtrary!!!
                        
                        for (int iExpProps = 0; iExpProps < nCategories; iExpProps++) {
                            expectedProps[iExpProps] /= sumExpectedProps;
                        }  

    /***************************************************************************
     *              Construct the object and send it to Parent                 *
     **************************************************************************/
                    x2GOF_DataDialogObj = new X2GOF_DataDialogObj(nCategories);
                    x2GOF_DataDialogObj.setGOFVariable(x2GOF_Model.getGOFVariable());

                    String[] theGOFCats = new String[nCategories];
                    
                    for (int i = 0; i < nCategories; i++) {
                        theGOFCats[i] = txtGOFCategories[i].getText();
                    }

                    x2GOF_DataDialogObj.setTheGOFCategories(theGOFCats);

                    expectedProps = new double[nCategories];
                    
                    for (int i = 0; i < nCategories; i++) {
                        expectedProps[i] = al_STF.get(i).getSmartTextDouble();
                    } 
                    
                    x2GOF_DataDialogObj.setExpectedProps(expectedProps);   
                    x2GOF_DataDialogObj.setObservedValues(observedValues); 
                    strReturnStatus = "OK";
                    close();
                    
                    } else {  // Math.abs(sumExpectedProps - 1.0) >=.01)
                        
                        String choice = doSumExpPropsDialog();

                        switch (choice) {
                            case strAdjust:
                            {
                                for (int iExpProps = 0; iExpProps < nCategories; iExpProps++) {
                                    expectedProps[iExpProps] /= sumExpectedProps;
                                    al_STF.get(iExpProps).setText(String.valueOf(expectedProps[iExpProps]));
                                    al_STF.get(iExpProps).setSmartTextDouble(expectedProps[iExpProps]);
                                } 
                                
                                sumExpectedProps = 1.0;
                                btnOkGOFAnalysis.fire();
                                break;
                            }

                            case strFix:
                            {
                                for (int iCategories = 0; iCategories < nCategories; iCategories++) {
                                    al_STF.get(iCategories).setText("");
                                }
                                break;
                            }

                            case strCancel:
                                strReturnStatus = "Cancel";
                                if (strReturnStatus.equals("Cancel")) {  //  Making the
                                    return;                          // compiler happy
                                }
                            break;

                            default:
                                str_SwitchFailure = "Switch failure: X2Assoc_SummDial 225 " + choice;
                                MyAlerts.showUnexpectedErrorAlert(str_SwitchFailure);
                        }    
                    }   //  end // Math.abs(sumExpectedProps - 1.0) >=.01) 
                }
            }   //  end handle btnOkGOFAnalysis
        });  
        System.out.println("275 X2GOF_DataFromFile_Dialog, END constructSomeGUI()");
    }
    
    public void constructMoreGUI() {   
        System.out.println("279 X2GOF_DataFromFile_Dialog, constructMoreGUI()");
        strGOFCategories = new String[nCategories];
        strGOFCategories = x2GOF_Model.getObservedValuesFromFile();

        txtGOFCategories = new Text[nCategories];
        
        for (int ithCat = 0; ithCat < nCategories; ithCat++) {
            txtGOFCategories[ithCat] = new Text(strGOFCategories[ithCat]);
        }

        //stf_gofExpProps = new SmartTextField[nCategories];
        tfGOFObsCounts = new TextField[nCategories];
        observedValues = new int[nCategories];
        observedValues = x2GOF_Model.getObservedCountsFromFile();
        expectedProps = new double[nCategories];

        for (int ithCat = 0; ithCat < nCategories; ithCat++) {
            tfGOFObsCounts[ithCat] = new TextField();
            tfGOFObsCounts[ithCat].setText(String.valueOf(observedValues[ithCat]));
        }

        gridPane_GOF.add(chBox_DoEqualProps, 0, 1);
        gridPane_GOF.add(txtCategoryDescr, 1, 2);
        GridPane.setHalignment(txtCategoryDescr, HPos.CENTER);
        gridPane_GOF.add(txtExpPropDescr, 2, 2);
        GridPane.setHalignment(txtExpPropDescr, HPos.LEFT);
        gridPane_GOF.add(txtObsCountDescr, 3, 2);
        GridPane.setHalignment(txtObsCountDescr, HPos.LEFT);
        setHeight(250 + 40 * nCategories);

        for (int iCategories = 0; iCategories < nCategories; iCategories++) {
            gridPane_GOF.add(txtGOFCategories[iCategories], 1, iCategories + 3);

            al_STF.get(iCategories).setSmartTextField_MB_PROBABILITY(true);
            //stfArrayList.add(iCategories, al_STF.get(iCategories)); 
            al_STF.get(iCategories).getTextField().setMaxWidth(50);
            gridPane_GOF.add(al_STF.get(iCategories).getTextField(), 2, iCategories + 3);

            //listPosition =  3 * iCategories + 4;
            tfGOFObsCounts[iCategories].setMaxWidth(50);
            gridPane_GOF.add(tfGOFObsCounts[iCategories], 3, iCategories + 3);

            if (equalPropsSelected) {
                double daEqualProp = 1.0 / nCategories;
                al_STF.get(iCategories).setSmartTextDouble(daEqualProp);
                al_STF.get(iCategories).setText(String.valueOf(daEqualProp));
            }    
        }

        gridPane_GOF.add(btnCancelGOFAnalysis, 1, nCategories + 3);
        gridPane_GOF.add(btnOkGOFAnalysis, 3, nCategories + 3);

        al_STF.get(0).getTextField().requestFocus();
        System.out.println("332 X2GOF_DataFromFile_Dialog, END constructMoreGUI()");
    }   //  end whatever
    
    private boolean checkOKForAnalysis() { 
        System.out.println("336 X2GOF_DataFromFile_Dialog, checkOKForAnalysis()");
        boolean okToContinue = true;
        
        for (int ithCategory = 0; ithCategory < al_STF.getSize(); ithCategory++) {
            
            if (al_STF.get(ithCategory).isEmpty()) {
                okToContinue = false;
            }        
        }       
        
        if (!okToContinue) {
            MyAlerts.showMissingDataAlert();
            return false;
        }
        // Check for unique categories.  Necessary for Category Axis
        for (int ithCat = 0; ithCat < nCategories - 1; ithCat++) {    
            for (int jthCat = ithCat + 1; jthCat < nCategories; jthCat++) {
                String temp1 = txtGOFCategories[ithCat].getText();
                String temp2 = txtGOFCategories[jthCat].getText();
                if (temp1.equals(temp2)) {
                    MyAlerts.showNonUniqueCategoriesAlert();
                    return false;
                } 
            }
        }
        System.out.println("361 X2GOF_DataFromFile_Dialog, END checkOKForAnalysis()");
        return true;
    }

/*******************************************************************************
*                          Ancillary routines                                  *
* @return 
*******************************************************************************/    
    
    public String doSumExpPropsDialog() {
        System.out.println("371 X2GOF_DataFromFile_Dialog, doSumExpPropsDialog()");
        String returnString;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Uh-oh, possible problem here...");
        alert.setHeaderText("There is a difficulty with your numbers.");
        alert.setContentText("The sum of your expected proportions is different from 1.0."
                           + "\nThis could be due to roundoff error, in which case your  "
                           + "\nproportions can be (slightly) adjusted.  Or, if "
                           + "\nyour proportions are  in deeper doo-doo, you can fix them."
                           + "\nYour call. "
        );

        ButtonType bt_Adjust = new ButtonType("Adjust & Continue");
        ButtonType bt_Fix = new ButtonType("I will Fix them");
        ButtonType bt_Cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(bt_Adjust, bt_Fix, bt_Cancel);
        boolean goodToGo = true;
        do {
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == bt_Adjust){
                returnString = strAdjust;
                goodToGo = false;
            } else if (result.get() == bt_Fix) {
                returnString = strFix;
                goodToGo = false;
            } else {
                returnString = strCancel;
                goodToGo = false;
            }
        } while (goodToGo == true);      
        System.out.println("402 X2GOF_DataFromFile_Dialog, END doSumExpPropsDialog()");
        return returnString;
    }   //  end doSumExpPropsDialog()

    public X2GOF_DataDialogObj getTheDialogObject() { return x2GOF_DataDialogObj; }
}


